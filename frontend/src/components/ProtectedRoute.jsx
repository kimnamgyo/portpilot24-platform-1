// src/components/ProtectedRoute.jsx
import { Navigate, Outlet } from 'react-router-dom';
import useAuthStore from '../store/authStore';

function ProtectedRoute() {
  const { isLoggedIn } = useAuthStore();

  if (!isLoggedIn) {
    // 로그인하지 않았다면 /login 경로로 강제 이동
    return <Navigate to="/login" replace />;
  }

  // 로그인했다면 요청한 페이지를 보여줌
  return <Outlet />;
}

export default ProtectedRoute;