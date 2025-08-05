import { useState } from "react";

function YoloAnalyzer() {
  const [videoFile, setVideoFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [streamReady, setStreamReady] = useState(false);

  const handleUpload = async () => {
    if (!videoFile) return;

    const formData = new FormData();
    formData.append("file", videoFile);

    setLoading(true);
    setStreamReady(false);

    await fetch("http://localhost:8000/upload", {
      method: "POST",
      body: formData,
    });

    // YOLO 백엔드는 업로드 후 바로 /stream 으로 리다이렉트하니까,
    // 클라이언트도 바로 /stream을 보여주면 돼
    setLoading(false);
    setStreamReady(true);
  };

  return (
    <div className="p-4">
      <input
        type="file"
        accept="video/mp4"
        onChange={(e) => setVideoFile(e.target.files[0])}
      />
      <button onClick={handleUpload} className="mt-2 p-2 bg-blue-500 text-white">
        분석 시작
      </button>

      {loading && <p className="mt-4 text-blue-600">분석 중입니다...</p>}

      {streamReady && (
        <div style={{ marginTop: "2rem" }}>
          <h3>🔍 분석 결과 스트리밍</h3>
          <img
            src="http://localhost:8000/stream"
            alt="YOLO Stream"
              style={{
                width: "100%",
                maxWidth: "960px",
                border: "1px solid #ccc",
                display: "block",
                margin: "0 auto",
              }}
          />
        </div>
      )}
    </div>
  );
}

export default YoloAnalyzer;