import axios from 'axios'

const baseURL = '/api'

// 获取待审批列表
export const getPendingApprovals = (params) => {
  return axios.get(`${baseURL}/approval/pending`, { params })
}

// 获取已审批列表
export const getApprovedList = (params) => {
  return axios.get(`${baseURL}/approval/approved`, { params })
}

// 获取审批详情
export const getApprovalDetail = (id) => {
  return axios.get(`${baseURL}/approval/${id}`)
}

// 提交审批
export const submitApproval = (id, data) => {
  return axios.post(`${baseURL}/approval/${id}/submit`, data)
}

// 撤回审批
export const revokeApproval = (id) => {
  return axios.post(`${baseURL}/approval/${id}/revoke`)
}

// 获取审批历史
export const getApprovalHistory = (contentId) => {
  return axios.get(`${baseURL}/approval/history/${contentId}`)
}