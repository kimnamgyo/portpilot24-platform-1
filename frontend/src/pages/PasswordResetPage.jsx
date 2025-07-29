// src/pages/PasswordResetPage.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// import apiClient from '../api/axios'; // 나중에 API 연동 시 주석 해제
import { Container, Box, Typography, TextField, Button, Stack } from '@mui/material';

function PasswordResetPage() {
  const [token, setToken] = useState(''); // 실제로는 URL 파라미터에서 가져옵니다.
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (newPassword !== confirmPassword) {
      alert('새 비밀번호가 일치하지 않습니다.');
      return;
    }
    
    // 이 부분은 백엔드 연동 시 실제 API 호출 로직으로 대체합니다.
    try {
      // await apiClient.post('/users/reset-password', { token, newPassword });
      alert('비밀번호가 성공적으로 변경되었습니다. 로그인 페이지로 이동합니다.');
      navigate('/login');
    } catch (error) {
      alert('비밀번호 변경 중 오류가 발생했습니다.');
      console.error('Password reset error:', error);
    }
  };

  return (
    <Container component="main" maxWidth="xs" sx={{ mt: 8 }}>
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography component="h1" variant="h5">
          새 비밀번호 설정
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3, width: '100%' }}>
          <Stack spacing={2}>
            <TextField
              required
              fullWidth
              name="token"
              label="이메일로 받은 토큰"
              id="token"
              value={token}
              onChange={(e) => setToken(e.target.value)}
            />
            <TextField
              required
              fullWidth
              name="newPassword"
              label="새 비밀번호"
              type="password"
              id="newPassword"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
            />
            <TextField
              required
              fullWidth
              name="confirmPassword"
              label="새 비밀번호 확인"
              type="password"
              id="confirmPassword"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
          </Stack>
          <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
            비밀번호 변경하기
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

export default PasswordResetPage;