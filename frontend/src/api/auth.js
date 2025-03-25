import axios from 'axios';

const API_URL = '/api/auth';

export default {
  login(credentials) {
    return axios.post(`${API_URL}/login`, credentials);
  },
  register(userData) {
    return axios.post(`${API_URL}/register`, userData);
  }
};
