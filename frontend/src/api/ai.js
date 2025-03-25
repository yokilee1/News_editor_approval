import axios from 'axios'

const baseURL = '/api'

// 生成新闻标题
export const generateTitle = (content) => {
  return axios.post(`${baseURL}/ai/generate-title`, { content })
}

// 生成新闻摘要
export const generateSummary = (content) => {
  return axios.post(`${baseURL}/ai/generate-summary`, { content })
}

// 提取关键词
export const extractKeywords = (content) => {
  return axios.post(`${baseURL}/ai/extract-keywords`, { content })
}

// 智能纠错
export const checkContent = (content) => {
  return axios.post(`${baseURL}/ai/check-content`, { content })
}

// 获取相似新闻
export const getSimilarNews = (content) => {
  return axios.post(`${baseURL}/ai/similar-news`, { content })
}

// 获取热点话题建议
export const getHotTopics = () => {
  return axios.get(`${baseURL}/ai/hot-topics`)
}