import axios from 'axios'

const baseURL = '/api'

// 获取审批流程列表
export const getApprovalFlowList = () => {
  return axios.get(`${baseURL}/approval-flow/list`)
}

// 获取单个审批流程详情
export const getApprovalFlowDetail = (id) => {
  return axios.get(`${baseURL}/approval-flow/${id}`)
}

// 创建审批流程
export const createApprovalFlow = (data) => {
  return axios.post(`${baseURL}/approval-flow`, data)
}

// 更新审批流程
export const updateApprovalFlow = (id, data) => {
  return axios.put(`${baseURL}/approval-flow/${id}`, data)
}

// 删除审批流程
export const deleteApprovalFlow = (id) => {
  return axios.delete(`${baseURL}/approval-flow/${id}`)
}

// 获取可用审批人列表
export const getApproverList = () => {
  return axios.get(`${baseURL}/approval-flow/approvers`)
}

// 设置审批流程状态（启用/禁用）
export const setApprovalFlowStatus = (id, status) => {
  return axios.put(`${baseURL}/approval-flow/${id}/status`, { status })
}