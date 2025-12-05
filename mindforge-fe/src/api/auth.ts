import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';

// API base URL
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081';

// Create axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Types based on OpenAPI spec
export interface UserRegister {
  username: string;
  password: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  avatarUrl?: string;
}

export interface UserLogin {
  username: string;
  password: string;
}

export interface AuthToken {
  username: string;
  token: string;
  refreshToken?: string;
}

export interface UserInfo {
  id: number;
  username: string;
  role: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  avatarUrl?: string;
}

// API methods
export const authApi = {
  // Register a new user
  register: async (userData: UserRegister): Promise<void> => {
    const response: AxiosResponse<void> = await apiClient.post('/auth/register', userData);
    return response.data;
  },

  // Login user
  login: async (credentials: UserLogin): Promise<AuthToken> => {
    const response: AxiosResponse<AuthToken> = await apiClient.post('/auth/login', credentials);
    const data = response.data;
    // Store tokens
    if (data.token) localStorage.setItem('jwt', data.token);
    if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken);
    return data;
  },

  // Refresh token
  refresh: async (): Promise<AuthToken> => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) throw new Error('No refresh token');

    const response: AxiosResponse<AuthToken> = await apiClient.post('/auth/refresh', { refreshToken });
    const data = response.data;
    if (data.token) localStorage.setItem('jwt', data.token);
    if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken);
    return data;
  },

  // Get current user info
  me: async (): Promise<UserInfo> => {
    const response: AxiosResponse<UserInfo> = await apiClient.get('/auth/me');
    return response.data;
  },

  // Update user profile
  updateProfile: async (userData: UserRegister): Promise<UserInfo> => {
    const response: AxiosResponse<UserInfo> = await apiClient.put('/auth/profile', userData);
    return response.data;
  },

  // Delete user account
  deleteUser: async (username: string): Promise<void> => {
    const response: AxiosResponse<void> = await apiClient.delete(`/auth/users/${username}`);
    return response.data;
  },
};

export default apiClient;