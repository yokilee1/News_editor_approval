import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
});

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 如果是二进制数据，直接返回
    if (response.request.responseType === 'blob') {
      return response;
    }
    
    const res = response.data;
    // 根据后端返回格式调整
    return res;
  },
  error => {
    console.error('响应错误:', error);
    
    if (error.response) {
      // 请求已发出，但服务器响应状态码不在 2xx 范围内
      switch (error.response.status) {
        case 401:
          ElMessage.error('认证失败，请重新登录');
          localStorage.removeItem('token');
          router.push('/login');
          break;
        case 403:
          ElMessage.error('没有权限访问');
          break;
        case 404:
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          ElMessage.error('服务器内部错误');
          break;
        default:
          ElMessage.error(error.response.data.message || '请求失败');
      }
    } else {
      // 请求未发出或者网络错误
      ElMessage.error('网络错误，请检查您的网络连接');
    }
    
    return Promise.reject(error);
  }
);

export default request; 