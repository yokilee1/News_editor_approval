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
      const response = await authAPI.login(credentials);
      const { token, user } = response.data;
      commit('setToken', token);
      commit('setUser', user);
      return user;
    },
    
    // 注册
    async register(_, userData) {
      const response = await authAPI.register(userData);
      return response.data;
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
