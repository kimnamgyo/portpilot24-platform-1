import React, { useState } from "react";
import axios from "axios";

const YoloAnalyzer = () => {
  const [videoFile, setVideoFile] = useState(null);
  const [streamReady, setStreamReady] = useState(false);

  const handleFileChange = (e) => {
    setVideoFile(e.target.files[0]);
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!videoFile) return;

    const formData = new FormData();
    formData.append("file", videoFile);

    try {
      await axios.post("http://localhost:8000/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setStreamReady(true); // 업로드 성공 시 스트리밍 준비
    } catch (err) {
      console.error("Upload failed:", err);
    }
  };

  return (
    <div style={{ padding: "2rem" }}>
      <h2>YOLOv8 영상 분석</h2>

      <form onSubmit={handleUpload}>
        <input type="file" accept="video/mp4" onChange={handleFileChange} />
        <button type="submit">업로드 및 분석 시작</button>
      </form>

      {streamReady && (
        <div style={{ marginTop: "2rem" }}>
          <h3>🔍 분석 결과 스트리밍</h3>
          <img
            src="http://localhost:8000/stream"
            alt="YOLO Stream"
            style={{ border: "1px solid #ccc", width: "640px" }}
          />
        </div>
      )}
    </div>
  );
};

export default YoloAnalyzer;