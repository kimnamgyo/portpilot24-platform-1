import { useState, useEffect } from 'react';
import apiClient from '../api/axios';
import useAuthStore from '../store/authStore';
import useNotificationStore from '../store/notificationStore';
import { Box, Typography, TextField, Button, List, ListItem, ListItemText, Divider, CircularProgress } from '@mui/material';

function CommentSection({ postId }) {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(true);
  const { user } = useAuthStore();
  const { showNotification } = useNotificationStore();

  const fetchComments = async () => {
    try {
      // 1. API 경로 변경
      const response = await apiClient.get(`/comments/${postId}`);
      setComments(response.data);
    } catch (error) { /* ... */ }
  };

  useEffect(() => {
    fetchComments();
  }, [postId]);

  const handleCommentSubmit = async () => {
    if (!newComment.trim()) return;
    try {
      // 2. API 경로 변경
      await apiClient.post(`/comments/${postId}`, { content: newComment });
      setNewComment('');
      showNotification('댓글이 성공적으로 작성되었습니다.', 'success');
      fetchComments();
    } catch (error) { /* ... */ }
  };

  if (loading) {
    return <CircularProgress />;
  }

  return (
    <Box sx={{ mt: 4 }}>
      <Typography variant="h6" gutterBottom>댓글</Typography>
      <Box component="form" sx={{ display: 'flex', gap: 1, mb: 2 }}>
        <TextField
          fullWidth
          label="댓글을 입력하세요"
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
        />
        <Button variant="contained" onClick={handleCommentSubmit}>등록</Button>
      </Box>
      <List>
        // return 문 안의 comments.map 부분을 아래와 같이 수정
        {comments.map((comment, index) => (
          <Box key={comment.id}>
            <ListItem alignItems="flex-start">
              <ListItemText
                // 3. 백엔드에서 보내주는 authorName, content 사용
                primary={comment.authorName}
                secondary={comment.content}
              />
              {/* 4. 백엔드에서 주는 mine 필드로 본인 댓글 여부 확인 */}
              {comment.mine && (
                <Box>
                  <Button size="small">수정</Button>
                  <Button size="small" color="error">삭제</Button>
                </Box>
              )}
            </ListItem>
            {index < comments.length - 1 && <Divider />}
          </Box>
        ))}
      </List>
    </Box>
  );
}

export default CommentSection;