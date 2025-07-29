import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import useAuthStore from '../store/authStore';
import useNotificationStore from '../store/notificationStore'; // 알림 스토어 import
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  Grid,
  Link,
  CircularProgress, // 로딩 스피너 import
} from '@mui/material';

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false); // 로딩 상태 추가
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const { showNotification } = useNotificationStore(); // 알림 함수 가져오기

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true); // 로딩 시작
    try {
      await login(email, password);
      showNotification('로그인 되었습니다.', 'success'); // 성공 알림
      navigate('/posts');
    } catch (error) {
      showNotification('이메일 또는 비밀번호가 일치하지 않습니다.', 'error'); // 실패 알림
      console.error('Login error:', error);
    } finally {
      setLoading(false); // 로딩 종료
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Typography component="h1" variant="h5">
          로그인
        </Typography>
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="email"
            label="이메일 주소"
            name="email"
            autoComplete="email"
            autoFocus
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="비밀번호"
            type="password"
            id="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
            disabled={loading} // 로딩 중일 때 버튼 비활성화
          >
            {loading ? <CircularProgress size={24} color="inherit" /> : '로그인'}
          </Button>
          <Grid container justifyContent="flex-end" columnGap={2}>
            <Grid>
              <Link component={RouterLink} to="/request-password-reset" variant="body2">
                비밀번호를 잊으셨나요?
              </Link>
            </Grid>
            <Grid>
              <Link component={RouterLink} to="/signup" variant="body2">
                계정이 없으신가요? 회원가입
              </Link>
            </Grid>
          </Grid>
        </Box>
      </Box>
    </Container>
  );
}

export default LoginPage;