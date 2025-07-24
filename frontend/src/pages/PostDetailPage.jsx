import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiClient from '../api/axios';
import useAuthStore from '../store/authStore';
import useNotificationStore from '../store/notificationStore';
import CommentSection from '../components/CommentSection';
import ConfirmationDialog from '../components/ConfirmationDialog';
import {
  Container,
  Typography,
  Box,
  CircularProgress,
  Paper,
  Button,
} from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';

function PostDetailPage() {
  const { id } = useParams();
  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const { showNotification } = useNotificationStore();

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const response = await apiClient.get(`/posts/${id}`);
        setPost(response.data);
      } catch (error) {
        console.error('Failed to fetch post:', error);
        showNotification('게시글을 불러오는 데 실패했습니다.', 'error');
      } finally {
        setLoading(false);
      }
    };
    fetchPost();
  }, [id, showNotification]);

  const handleDelete = async () => {
    setDialogOpen(false);
    try {
      await apiClient.delete(`/posts/${id}`);
      showNotification('게시글이 삭제되었습니다.', 'success');
      navigate('/posts');
    } catch (error) {
      showNotification('게시글 삭제에 실패했습니다.', 'error');
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!post) {
    return (
      <Container sx={{ mt: 4 }}>
        <Typography>게시글을 찾을 수 없습니다.</Typography>
      </Container>
    );
  }

  // API 응답의 userId와 현재 로그인한 사용자의 id를 비교
  const isAuthor = user?.id === post.userId;

  return (
    <>
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Paper sx={{ p: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            {post.title}
          </Typography>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            작성자: {post.name}
          </Typography>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            작성일: {new Date(post.createdAt).toLocaleString()}
          </Typography>

          <Box sx={{ my: 4, whiteSpace: 'pre-wrap', minHeight: '200px' }}>
            <Typography variant="body1">{post.content}</Typography>
          </Box>

          {/* fileAttached가 1이면 첨부파일 링크 표시 */}
          {post.fileAttached === 1 && (
            <Box sx={{ mb: 4, mt: 2, p: 2, border: '1px solid #eee', borderRadius: 1 }}>
              <Typography variant="h6" gutterBottom>첨부파일</Typography>
              <Button
                startIcon={<DownloadIcon />}
                // 백엔드의 파일 다운로드 API 경로로 연결해야 합니다.
                href={`http://localhost:8080/api/posts/attachments/${post.serverFileName}`}
                target="_blank"
              >
                {post.originalFileName}
              </Button>
            </Box>
          )}

          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
            <Button variant="outlined" onClick={() => navigate('/posts')}>목록으로</Button>
            {isAuthor && (
              <>
                <Button variant="contained" color="primary" onClick={() => navigate(`/posts/${id}/edit`)}>수정</Button>
                <Button variant="contained" color="error" onClick={() => setDialogOpen(true)}>삭제</Button>
              </>
            )}
          </Box>
        </Paper>
        
        <CommentSection postId={id} />
      </Container>
      
      <ConfirmationDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        onConfirm={handleDelete}
        title="게시글 삭제"
        message="정말 이 게시글을 삭제하시겠습니까? 되돌릴 수 없습니다."
      />
    </>
  );
}

export default PostDetailPage;