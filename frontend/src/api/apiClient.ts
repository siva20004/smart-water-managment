import axios from 'axios';
import type { AxiosError, AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import type { ApiError } from '../types/health';

const BASE_URL: string = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export const apiClient: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

// Request interceptor: attach tracing/token if present in later modules
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Prepared for JWT bearer tokens in upcoming authentication module
    const token = localStorage.getItem('auth_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: unknown) => Promise.reject(error)
);

// Response interceptor: standardize error format
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError<{ message?: string; details?: string }>) => {
    const formattedError: ApiError = {
      message: error.response?.data?.message || error.message || 'An unexpected network error occurred.',
      statusCode: error.response?.status,
      details: error.response?.data?.details || (error.code ? `Error Code: ${error.code}` : undefined),
      timestamp: new Date().toISOString(),
    };
    return Promise.reject(formattedError);
  }
);

export default apiClient;
