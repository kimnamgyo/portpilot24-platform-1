// src/pages/PasswordRequestPage.jsx
import { useState } from 'react';
import apiClient from '../api/axios';
import { Container, Box, Typography, TextField, Button } from '@mui/material';

function PasswordRequestPage() {
  const [email, setEmail] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      // API 명세서의 '비밀번호 재발급 요청' API 호출
      await apiClient.post('/users/reset-password/request', { email });
      alert('입력하신 이메일로 재발급 안내 메일을 전송했습니다.');
    } catch (error) {
      alert('요청 중 오류가 발생했습니다.');
      console.error('Password reset request error:', error);
    }
  };

  return (
    <Container component="main" maxWidth="xs" sx={{ mt: 8 }}>
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography component="h1" variant="h5">
          비밀번호 재발급
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <TextField
            required
            fullWidth
            id="email"
            label="가입한 이메일 주소"
            name="email"
            autoComplete="email"
            autoFocus
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
            재발급 링크 받기
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

export default PasswordRequestPage;