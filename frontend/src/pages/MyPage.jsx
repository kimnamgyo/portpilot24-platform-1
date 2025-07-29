import { useState, useEffect } from 'react';
import apiClient from '../api/axios';
import useNotificationStore from '../store/notificationStore';
import {
  Container,
  Box,
  Typography,
  Paper,
  CircularProgress,
  Button,
  Stack,
  TextField,
} from '@mui/material';

function MyPage() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false); // 수정 모드 상태
  const [editName, setEditName] = useState('');
  const [editPassword, setEditPassword] = useState('');
  const { showNotification } = useNotificationStore();

  // 사용자 정보 불러오기
  const fetchUserData = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get('/users/me');
      setUser(response.data);
      setEditName(response.data.name); // 수정 필드 초기값 설정
    } catch (error) {
      console.error('Failed to fetch user data:', error);
      showNotification('사용자 정보를 불러오는 데 실패했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUserData();
  }, []);

  // 정보 저장 핸들러
  // handleSave 함수 부분을 아래 내용으로 교체하세요.
const handleSave = async () => {
  setLoading(true);
  try {
    // 이름 변경 API 호출 (필요 시)
    // await apiClient.patch('/users/me', { name: editName });

    // 비밀번호 변경 API 호출
    if (editPassword) {
      // API 명세서에 currentPassword 필드가 필요하므로, 사용자에게 현재 비밀번호를 입력받는 UI가 추가되어야 합니다.
      // 여기서는 임시로 'currentPassword1234'를 사용합니다.
      await apiClient.patch('/users/password', {
        currentPassword: 'currentPassword1234', // 실제로는 사용자 입력값
        newPassword: editPassword,
      });
    }
    
    showNotification('정보가 성공적으로 수정되었습니다.', 'success');
    setIsEditing(false);
    fetchUserData();
  } catch (error) {
    showNotification('정보 수정에 실패했습니다.', 'error');
  } finally {
    setLoading(false);
  }
};

  


  if (loading && !isEditing) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;
  }

  return (
    <Container component="main" maxWidth="sm" sx={{ mt: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography component="h1" variant="h4" gutterBottom>
          내 정보
        </Typography>
        {isEditing ? (
          // --- 수정 모드 ---
          <Stack spacing={2} sx={{ mt: 2 }}>
            <TextField
              label="이름"
              fullWidth
              value={editName}
              onChange={(e) => setEditName(e.target.value)}
            />
            <TextField
              label="새 비밀번호 (선택)"
              type="password"
              fullWidth
              placeholder="변경할 경우에만 입력하세요"
              value={editPassword}
              onChange={(e) => setEditPassword(e.target.value)}
            />
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 2 }}>
              <Button variant="outlined" onClick={() => setIsEditing(false)}>
                취소
              </Button>
              <Button variant="contained" onClick={handleSave} disabled={loading}>
                {loading ? <CircularProgress size={24} /> : '저장'}
              </Button>
            </Box>
          </Stack>
        ) : (
          // --- 보기 모드 ---
          <Box sx={{ mt: 2 }}>
            {user ? (
              <>
                <Typography variant="h6">이름: {user.name}</Typography>
                <Typography variant="h6" sx={{ mt: 1 }}>이메일: {user.email}</Typography>
                <Typography variant="h6" sx={{ mt: 1 }}>역할: {user.role}</Typography>
                <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
                  <Button variant="contained" onClick={() => setIsEditing(true)}>
                    정보 수정
                  </Button>
                </Box>
              </>
            ) : (
              <Typography>사용자 정보를 찾을 수 없습니다.</Typography>
            )}
          </Box>
        )}
      </Paper>
    </Container>
  );
}

export default MyPage;