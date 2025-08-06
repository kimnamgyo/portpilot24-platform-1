import { useState, useEffect } from "react";

function YoloAnalyzer() {
  const [videoFile, setVideoFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [streamReady, setStreamReady] = useState(false);
  const [unprotectedCount, setUnprotectedCount] = useState(null);  // ✅ 단일 수치 상태

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

    setLoading(false);
    setStreamReady(true);
  };

  useEffect(() => {
    let intervalId;

    if (streamReady) {
      intervalId = setInterval(async () => {
        try {
          const res = await fetch("http://localhost:8000/yolo/status");
          const data = await res.json();
          setUnprotectedCount(data.unprotected_person);  // ✅ 단일 필드만 추출
        } catch (err) {
          console.error("상태 업데이트 실패:", err);
        }
      }, 3000);
    }

    return () => {
      clearInterval(intervalId);
    };
  }, [streamReady]);

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

          {/* ✅ 안전보호구 미착용자 수 표시 */}
          <div className="mt-4 text-center">
            <h4 className="text-lg font-semibold">🚨 보호구 미착용자 수</h4>
            {unprotectedCount === null ? (
              <p>감지 중입니다...</p>
            ) : (
              <p className="text-2xl font-bold text-red-600">
                {unprotectedCount}명
              </p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default YoloAnalyzer;