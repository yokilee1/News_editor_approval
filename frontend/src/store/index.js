// src/store/index.js
import { createStore } from 'vuex';
import axios from 'axios';

export default createStore({
    state: {
        auth: {
            token: localStorage.getItem('token') || null,
            user: JSON.parse(localStorage.getItem('user')) || null,
        },
        $http: axios.create({
            baseURL: '/api',
        }),
    },
    getters: {
        isAuthenticated: (state) => !!state.auth.token,
        userRole: (state) => state.auth.user?.role || '',
        currentUser: (state) => state.auth.user,
        userName: (state) => state.auth.user?.name || '',
    },
    mutations: {
        SET_AUTH(state, { token, user }) {
            state.auth.token = token;
            state.auth.user = {
                userId: user.userId,
                username: user.username,
                name: user.name,
                role: user.role.toLowerCase(),
            };
            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify(state.auth.user));
            state.$http.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        },
        CLEAR_AUTH(state) {
            state.auth.token = null;
            state.auth.user = null;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            delete state.$http.defaults.headers.common['Authorization'];
        },
    },
    actions: {
        async login({ commit }, credentials) {
            try {
                const response = await axios.post('/api/auth/login', credentials);
                const { token, userId, username, name, role } = response.data;
                commit('SET_AUTH', { token, user: { userId, username, name, role } });
                return response.data;
            } catch (error) {
                throw new Error(error.response?.data?.message || '登录失败');
            }
        },
        async validateToken({ commit, state }) {
            try {
                const response = await axios.post('/api/auth/validate', null, {
                    headers: { Authorization: `Bearer ${state.auth.token}` },
                });
                if (!response.data.valid) {
                    commit('CLEAR_AUTH');
                }
                return response.data.valid;
            } catch (error) {
                commit('CLEAR_AUTH');
                return false;
            }
        },
        logout({ commit }) {
            commit('CLEAR_AUTH');
        },
    },
});