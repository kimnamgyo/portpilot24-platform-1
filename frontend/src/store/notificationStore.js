import { create } from 'zustand';

const useNotificationStore = create((set) => ({
  open: false,
  message: '',
  severity: 'info', // 'success', 'error', 'warning', 'info'
  showNotification: (message, severity = 'info') =>
    set({ open: true, message, severity }),
  hideNotification: () => set({ open: false }),
}));

export default useNotificationStore;