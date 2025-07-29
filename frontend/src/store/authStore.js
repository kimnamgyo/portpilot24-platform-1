import { create } from 'zustand';
import apiClient from '../api/axios';

const useAuthStore = create((set, get) => ({
  isLoggedIn: !!localStorage.getItem('access_token'), // accessToken 기준으로 변경
  user: null,
  
  login: async (email, password) => {
    const response = await apiClient.post('/users/login', { email, password });
    const { accessToken, refreshToken } = response.data;
    
    localStorage.setItem('access_token', accessToken);
    localStorage.setItem('refresh_token', refreshToken); // refreshToken 저장
    
    set({ isLoggedIn: true });
    await get().fetchUser();
  },

  logout: () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token'); // refreshToken 삭제
    set({ isLoggedIn: false, user: null });
  },
  
  fetchUser: async () => {
    try {
      const response = await apiClient.get('/users/me');
      set({ user: response.data });
    } catch (error) {
      console.error("Failed to fetch user:", error);
      get().logout();
    }
  }
}));

// 앱 시작 시 토큰이 있으면 사용자 정보 미리 가져오기
if (localStorage.getItem('access_token')) {
  useAuthStore.getState().fetchUser();
}

export default useAuthStore;