import { useState, useEffect } from 'react';
import apiClient from '../../api/axios';
import useNotificationStore from '../../store/notificationStore';
import { Container, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, CircularProgress, Box, Button, Stack } from '@mui/material';

function UserManagementPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const { showNotification } = useNotificationStore();

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await apiClient.get('/admin/users');
      setUsers(response.data.content); // Page 객체에 맞게 수정
    } catch (error) {
      console.error('Failed to fetch users:', error);
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    fetchUsers();
  }, []);

  const handleToggleActivate = async (userId, isActive) => {
    const action = isActive ? 'inactivate' : 'activate';
    try {
      await apiClient.patch(`/admin/users/${userId}/${action}`);
      showNotification(`사용자 상태가 변경되었습니다.`, 'success');
      fetchUsers(); // 목록 새로고침
    } catch (error) {
      showNotification('상태 변경에 실패했습니다.', 'error');
    }
  };
  
  if (loading) { /* ... */ }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>사용자 관리</Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>이름</TableCell>
              <TableCell>이메일</TableCell>
              <TableCell>역할</TableCell>
              <TableCell>상태</TableCell>
              <TableCell align="center">관리</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>
                <TableCell>{user.name}</TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>{user.role}</TableCell>
                <TableCell>{user.isActive ? '활성' : '비활성'}</TableCell>
                <TableCell align="center">
                  <Stack direction="row" spacing={1} justifyContent="center">
                    <Button
                      variant="contained"
                      size="small"
                      color={user.isActive ? 'warning' : 'success'}
                      onClick={() => handleToggleActivate(user.id, user.isActive)}
                    >
                      {user.isActive ? '비활성화' : '활성화'}
                    </Button>
                    <Button variant="contained" size="small" color="error">삭제</Button>
                  </Stack>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Container>
  );
}

export default UserManagementPage;