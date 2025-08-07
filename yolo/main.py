from fastapi import FastAPI, UploadFile, File
from fastapi.responses import StreamingResponse, HTMLResponse, RedirectResponse, JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from pathlib import Path
from ultralytics import YOLO
from collections import deque
import cv2
import os

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

model_path = Path(__file__).parent / "model" / "best.pt"
model = YOLO(str(model_path))
video_path_global = None

def letterbox_image(image, desired_size=(960, 540)):
    ih, iw = image.shape[:2]
    w, h = desired_size
    scale = min(w / iw, h / ih)
    nw = int(iw * scale)
    nh = int(ih * scale)

    image_resized = cv2.resize(image, (nw, nh))

    top = (h - nh) // 2
    bottom = h - nh - top
    left = (w - nw) // 2
    right = w - nw - left

    color = (0, 0, 0)
    new_image = cv2.copyMakeBorder(image_resized, top, bottom, left, right, cv2.BORDER_CONSTANT, value=color)
    return new_image

# 최근 30프레임의 미착용자 수 저장
unprotected_count_deque = deque(maxlen=30)


# 유틸 함수: IoU 계산
def iou(box1, box2):
    x1, y1, x2, y2 = box1.xyxy[0]
    x1g, y1g, x2g, y2g = box2.xyxy[0]

    xi1 = max(x1, x1g)
    yi1 = max(y1, y1g)
    xi2 = min(x2, x2g)
    yi2 = min(y2, y2g)

    inter_width = max(0, xi2 - xi1)
    inter_height = max(0, yi2 - yi1)
    inter_area = inter_width * inter_height

    box1_area = (x2 - x1) * (y2 - y1)
    box2_area = (x2g - x1g) * (y2g - y1g)

    union_area = box1_area + box2_area - inter_area

    if union_area == 0:
        return 0.0
    return inter_area / union_area


# 업로드 및 분석
@app.get("/")
def form():
    return HTMLResponse(content="""
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta charset="UTF-8">
            <title>YOLO 영상 업로드</title>
        </head>
        <body style="margin: 20px; font-family: sans-serif;">
            <h2>YOLOv8 영상 업로드</h2>
            <form action="/upload" enctype="multipart/form-data" method="post">
                <input name="file" type="file" accept="video/mp4"/>
                <input type="submit" value="업로드 및 분석 시작"/>
            </form>
        </body>
        </html>
    """)

@app.post("/upload")
async def upload(file: UploadFile = File(...)):
    global video_path_global

    filename = os.path.join(UPLOAD_DIR, file.filename)
    with open(filename, "wb") as f:
        f.write(await file.read())

    video_path_global = filename
    return RedirectResponse(url="/stream", status_code=303)


# 스트리밍 + 미착용자 감지
@app.get("/stream")
def video_stream():
    if not video_path_global or not os.path.exists(video_path_global):
        return HTMLResponse("<h3>❌ 영상이 업로드되지 않았습니다.</h3>")

    def generate_frames():
        cap = cv2.VideoCapture(video_path_global)

        frame_skip = 3
        frame_count = 0

        while True:
            ret, frame = cap.read()
            if not ret:
                break

            frame_count += 1
            if frame_count % frame_skip != 0:
                continue

            results = model.predict(
                source=frame,
                stream=False,
                verbose=False,
                conf=0.4,
                iou=0.3
            )

            boxes = results[0].boxes
            names = model.names

            persons = [b for b in boxes if names[int(b.cls)] == "person"]
            helmets = [b for b in boxes if names[int(b.cls)] == "helmet"]
            vests = [b for b in boxes if names[int(b.cls)] == "vest"]

            unprotected = 0
            for p in persons:
                has_helmet = any(iou(p, h) > 0.3 for h in helmets)
                has_vest = any(iou(p, v) > 0.3 for v in vests)
                if not has_helmet and not has_vest:
                    unprotected += 1

            unprotected_count_deque.append(unprotected)

            annotated = results[0].plot()
            if annotated is None or annotated.size == 0:
                continue

            annotated = letterbox_image(annotated, desired_size=(960, 540))

            _, buffer = cv2.imencode(".jpg", annotated)
            frame_bytes = buffer.tobytes()

            yield (b"--frame\r\n"
                b"Content-Type: image/jpeg\r\n\r\n" + frame_bytes + b"\r\n")

        cap.release()


    return StreamingResponse(generate_frames(), media_type="multipart/x-mixed-replace; boundary=frame")


# 감지 상태 API (프론트에 표시)
@app.get("/yolo/status")
def detection_status():
    if not unprotected_count_deque:
        return {"unprotected_person": 0}

    avg = int(sum(unprotected_count_deque) / len(unprotected_count_deque))
    return {"unprotected_person": avg}