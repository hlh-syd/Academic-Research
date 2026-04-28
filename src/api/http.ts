import axios from 'axios';

export const http = axios.create({
  baseURL: '/api',
  timeout: 8000,
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(error),
);
