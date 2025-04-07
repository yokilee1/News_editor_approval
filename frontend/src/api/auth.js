import request from '@/utils/axios';

export default {
  login(credentials) {
    console.log('登录请求数据:', credentials);  // 添加日志
    return request.post('/api/auth/login', credentials).then(response => {
      console.log('登录响应:', response.data);  // 添加日志
      return response.data;
    });
  },
  register(userData) {
    console.log('注册请求数据:', userData);  // 添加日志
    return request.post('/api/auth/register', userData).then(response => {
      console.log('注册响应:', response.data);  // 添加日志
      return response.data;
    });
  }
};