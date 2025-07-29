import { Link as RouterLink, useNavigate } from 'react-router-dom';
import useAuthStore from '../store/authStore';
import useNotificationStore from '../store/notificationStore';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';

function Header() {
  const { isLoggedIn, user, logout } = useAuthStore();
  const { showNotification } = useNotificationStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    showNotification('로그아웃 되었습니다.', 'success');
    navigate('/login');
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component={RouterLink} to={isLoggedIn ? "/dashboard" : "/"} sx={{ flexGrow: 1, textDecoration: 'none', color: 'inherit' }}>
          PortPilot24
        </Typography>
        {isLoggedIn ? (
          <Box>
            {/* 관리자일 경우 관리자 페이지 링크 표시 */}
            {(user?.role === 'ADMIN' || user?.role === 'ROOT') && (
              <Button color="inherit" component={RouterLink} to="/admin/users">
                관리자
              </Button>
            )}
            <Button color="inherit" component={RouterLink} to="/posts">
              게시판
            </Button>
            <Button color="inherit" component={RouterLink} to="/me">
              내 정보
            </Button>
            <Button color="inherit" onClick={handleLogout}>
              로그아웃
            </Button>
          </Box>
        ) : (
          <Box>
            <Button color="inherit" component={RouterLink} to="/login">
              로그인
            </Button>
            <Button color="inherit" component={RouterLink} to="/signup">
              회원가입
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
}

export default Header;