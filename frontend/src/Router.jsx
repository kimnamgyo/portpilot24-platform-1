import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import SignupPage from './pages/SignupPage';
import LoginPage from './pages/LoginPage';
import MyPage from './pages/MyPage';
import ProtectedRoute from './components/ProtectedRoute';
import AdminProtectedRoute from './components/AdminProtectedRoute';
import PasswordRequestPage from './pages/PasswordRequestPage';
import PasswordResetPage from './pages/PasswordResetPage';
import BoardListPage from './pages/BoardListPage';
import PostDetailPage from './pages/PostDetailPage';
import PostFormPage from './pages/PostFormPage';
import DashboardPage from './pages/DashboardPage';
import UserManagementPage from './pages/admin/UserManagementPage';
import ContainerMonitoringPage from './pages/ContainerMonitoringPage';

function Router() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/request-password-reset" element={<PasswordRequestPage />} />
          <Route path="/reset-password" element={<PasswordResetPage />} />

          {/* 로그인이 필요한 페이지 */}
          <Route element={<ProtectedRoute />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/me" element={<MyPage />} />
            <Route path="/posts" element={<BoardListPage />} />
            <Route path="/posts/new" element={<PostFormPage />} />
            <Route path="/posts/:id" element={<PostDetailPage />} />
            <Route path="/posts/:id/edit" element={<PostFormPage />} />
            <Route path="/monitoring" element={<ContainerMonitoringPage />} />
          </Route>

          {/* 관리자만 접근 가능한 페이지 */}
          <Route element={<AdminProtectedRoute />}>
            <Route path="/admin/users" element={<UserManagementPage />} />
            {/* 다른 관리자 페이지들도 여기에 추가 */}
          </Route>

        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default Router;