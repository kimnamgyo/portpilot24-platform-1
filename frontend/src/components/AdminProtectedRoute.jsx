import { Navigate, Outlet } from 'react-router-dom';
import useAuthStore from '../store/authStore';

function AdminProtectedRoute() {
  const { isLoggedIn, user } = useAuthStore();

  if (!isLoggedIn) {
    return <Navigate to="/login" replace />;
  }

  // 로그인했지만 관리자가 아닌 경우, 게시판 페이지로 이동
  if (user?.role !== 'ADMIN' && user?.role !== 'ROOT') {
    return <Navigate to="/posts" replace />;
  }

  return <Outlet />;
}

export default AdminProtectedRoute;