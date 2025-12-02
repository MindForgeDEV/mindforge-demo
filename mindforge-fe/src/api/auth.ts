import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';

// API base URL
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

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
}

export interface UserLogin {
  username: string;
  password: string;
}

export interface AuthToken {
  username: string;
  token: string;
}

export interface UserInfo {
  id: number;
  username: string;
  role: string;
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
    return response.data;
  },

  // Get current user info
  me: async (): Promise<UserInfo> => {
    const response: AxiosResponse<UserInfo> = await apiClient.get('/auth/me');
    return response.data;
  },
};

export default apiClient;