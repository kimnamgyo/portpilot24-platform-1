import React from 'react'
import ReactDOM from 'react-dom/client'
import Router from './Router.jsx' // App 대신 Router를 가져옵니다.
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Router /> 
  </React.StrictMode>,
)