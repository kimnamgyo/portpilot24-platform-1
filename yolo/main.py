from fastapi import FastAPI, UploadFile, File, Request
from fastapi.responses import StreamingResponse, HTMLResponse, RedirectResponse
from ultralytics import YOLO
import cv2
import os

app = FastAPI()

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

model = YOLO(r"C:\Users\User\portpilot24-platform\yolo\models\best.pt")

video_path_global = None  # 추론할 영상 경로를 저장하는 전역 변수

@app.get("/")
def form():
    return HTMLResponse(content="""
        <html>
        <head><title>YOLOv8 영상 업로드</title></head>
        <body>
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

@app.get("/stream")
def video_stream():
    if not video_path_global or not os.path.exists(video_path_global):
        return HTMLResponse("<h3>❌ 영상이 업로드되지 않았습니다.</h3>")

    def generate_frames():
        cap = cv2.VideoCapture(video_path_global)

        while True:
            ret, frame = cap.read()
            if not ret:
                break

            results = model.predict(source=frame, stream=False, verbose=False)
            annotated = results[0].plot()

            _, buffer = cv2.imencode(".jpg", annotated)
            frame_bytes = buffer.tobytes()

            yield (b"--frame\r\n"
                   b"Content-Type: image/jpeg\r\n\r\n" + frame_bytes + b"\r\n")

        cap.release()

    return StreamingResponse(generate_frames(), media_type="multipart/x-mixed-replace; boundary=frame")
