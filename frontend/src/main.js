// src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建应用实例
const app = createApp(App);

// 配置axios
const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 添加认证令牌
http.interceptors.request.use(
  config => {
    const token = store.state.auth.token;
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

// 响应拦截器 - 处理常见错误
http.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 处理认证错误
          store.dispatch('logout');
          router.push('/login');
          ElMessage.error('会话已过期，请重新登录');
          break;
        case 403:
          ElMessage.error('您没有权限执行此操作');
          break;
        case 404:
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          ElMessage.error('服务器内部错误，请稍后再试');
          break;
        default:
          ElMessage.error(error.response.data.message || '操作失败');
      }
    } else if (error.request) {
      // 请求已发出但未收到响应
      ElMessage.error('网络连接失败，请检查您的网络连接');
    } else {
      // 请求配置出错
      ElMessage.error('请求错误: ' + error.message);
    }
    return Promise.reject(error);
  }
);

// 挂载到全局，使其可以在组件中通过 store.state.$http 访问
store.state.$http = http;

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('应用错误:', err);
  console.error('错误信息:', info);
  ElMessage.error('应用发生错误，请刷新页面或联系管理员');
};

// 挂载app组件
app.use(router);
app.use(store);
app.use(ElementPlus);
app.mount('#app');