import { createStore } from 'vuex';
import authAPI from '@/api/auth';

export default createStore({
  state: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null
  },
  mutations: {
    setUser(state, user) {
      state.user = user;
      if (user) {
        localStorage.setItem('user', JSON.stringify(user));
      } else {
        localStorage.removeItem('user');
      }
    },
    setToken(state, token) {
      state.token = token;
      if (token) {
        localStorage.setItem('token', token);
      } else {
        localStorage.removeItem('token');
      }
    }
  },
  actions: {
// 登录
async login({ commit }, credentials) {
  try {
    console.log('开始登录，发送数据:', credentials);
    const response = await authAPI.login(credentials);
    console.log('登录响应:', response);
    
    if (!response) {
      throw new Error('登录响应数据格式错误');
    }
    
    const { token, userId, username, name, role } = response;
    const user = { userId, username, name, role };
    
    commit('setToken', token);
    commit('setUser', user);
    return user;
  } catch (error) {
    console.error('登录错误:', error);
    throw error;
  }
},
    
    // 注册
    async register(_, userData) {
      try {
        console.log('开始注册，发送数据:', userData);  // 添加日志
        const response = await authAPI.register({
          username: userData.username,
          name: userData.name,
          email: userData.email,
          password: userData.password,
          role: userData.role
        });
        console.log('注册响应:', response);  // 添加日志
        return response;
      } catch (error) {
        console.error('注册错误:', error);  // 添加错误日志
        throw error;
      }
    },
    
    // 登出
    async logout({ commit }) {
      commit('setToken', null);
      commit('setUser', null);
    }
  },
  getters: {
    isAuthenticated: state => !!state.token,
    currentUser: state => state.user
  }
});
