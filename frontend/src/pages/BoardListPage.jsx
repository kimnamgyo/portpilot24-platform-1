import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axios';
import {
  Container, Typography, Button, Box, Paper, TableContainer, Table,
  TableHead, TableBody, TableRow, TableCell, Chip, CircularProgress, Pagination, TextField,
} from '@mui/material';
import AttachmentIcon from '@mui/icons-material/Attachment';

function BoardListPage() {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();




  // useEffect 부분을 아래 내용으로 교체하세요.
useEffect(() => {
  const fetchPosts = async () => {
    setLoading(true);
    try {
      // 1. API 경로를 '/posts/paging'으로 변경
      const response = await apiClient.get('/posts/paging', {
        params: { page: page - 1, size: 10, query: searchQuery }
      });
      // 2. 응답 데이터 구조에 맞춰 posts와 totalPages 설정
      setPosts(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error('Failed to fetch posts:', error);
    } finally {
      setLoading(false);
    }
  };
  fetchPosts();
}, [page, searchQuery]);



  const handleSearch = () => {
    setPage(1); // 검색 시 첫 페이지로 초기화
    // searchQuery state가 변경되면서 useEffect가 자동으로 다시 실행됨
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h4" component="h1">게시판</Typography>
        <Button variant="contained" onClick={() => navigate('/posts/new')}>글 작성</Button>
      </Box>

      <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2, gap: 1 }}>
        <TextField 
          label="검색" 
          variant="outlined" 
          size="small" 
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
        />
        <Button variant="contained" onClick={handleSearch}>검색</Button>
      </Box>

      {loading ? <CircularProgress /> : (
        <>
          <TableContainer component={Paper}>
            {/* Table 내용은 이전과 동일 */}
          </TableContainer>
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
            <Pagination count={totalPages} page={page} onChange={(e, value) => setPage(value)} color="primary" />
          </Box>
        </>
      )}
    </Container>
  );
}

export default BoardListPage;