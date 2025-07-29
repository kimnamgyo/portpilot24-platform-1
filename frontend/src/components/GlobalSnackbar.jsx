import { Snackbar, Alert } from '@mui/material';
import useNotificationStore from '../store/notificationStore';

function GlobalSnackbar() {
  const { open, message, severity, hideNotification } = useNotificationStore();

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    hideNotification();
  };

  return (
    <Snackbar
      open={open}
      autoHideDuration={4000}
      onClose={handleClose}
      anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
    >
      <Alert onClose={handleClose} severity={severity} sx={{ width: '100%' }}>
        {message}
      </Alert>
    </Snackbar>
  );
}

export default GlobalSnackbar;