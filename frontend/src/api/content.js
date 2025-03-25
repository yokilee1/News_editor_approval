import axios from 'axios'

const baseURL = '/api'

// 获取新闻列表
export const getNewsList = (params) => {
  return axios.get(`${baseURL}/content/list`, { params })
}

// 获取单个新闻详情
export const getNewsDetail = (id) => {
  return axios.get(`${baseURL}/content/${id}`)
}

// 创建新闻
export const createNews = (data) => {
  return axios.post(`${baseURL}/content`, data)
}

// 更新新闻
export const updateNews = (id, data) => {
  return axios.put(`${baseURL}/content/${id}`, data)
}

// 删除新闻
export const deleteNews = (id) => {
  return axios.delete(`${baseURL}/content/${id}`)
}

// 获取热点新闻列表
export const getHotNewsList = () => {
  return axios.get(`${baseURL}/content/hot`)
}