import { useState, useEffect } from "react";

function YoloAnalyzer() {
  const [videoFile, setVideoFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [streamReady, setStreamReady] = useState(false);
  const [unprotectedCount, setUnprotectedCount] = useState(null);

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
          setUnprotectedCount(data.unprotected_person);
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
    <div className="min-h-screen bg-gray-100 p-8">
      <div className="max-w-4xl mx-auto bg-white shadow-lg rounded-lg p-6">
        <h1 className="text-2xl font-bold text-gray-800 mb-4">
          🛡️ YOLOv8 안전보호구 미착용 감지 시스템
        </h1>

        <div className="flex items-center gap-4 mb-6">
          <input
            type="file"
            accept="video/mp4"
            onChange={(e) => setVideoFile(e.target.files[0])}
            className="file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
          />
          <button
            onClick={handleUpload}
            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded transition duration-200"
          >
            분석 시작
          </button>
        </div>

        {loading && (
          <div className="text-blue-600 font-semibold animate-pulse mb-4">
            분석 중입니다...
          </div>
        )}

        {streamReady && (
          <div className="mt-6">
            <h3 className="text-lg font-semibold mb-2 text-gray-700">
             📷 분석 영상 스트리밍
            </h3>
            <img
              src="http://localhost:8000/stream"
              alt="YOLO Stream"
              className="w-full max-w-3xl mx-auto border border-gray-300 rounded"
            />

            <div className="mt-6 flex justify-center">
              <div className="bg-white shadow-md rounded p-4 text-center">
                <h4 className="text-md font-medium text-gray-700 mb-2">
                 📝 보호구 미착용자 수
                </h4>
                {unprotectedCount === null ? (
                  <p className="text-gray-500">감지 중입니다...</p>
                ) : (
                  <p className="text-3xl font-bold text-red-600">
                    {unprotectedCount}명
                  </p>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default YoloAnalyzer;