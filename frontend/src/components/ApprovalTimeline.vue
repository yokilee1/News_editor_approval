<template>
  <div class="approval-timeline">
    <el-timeline>
      <el-timeline-item
        v-for="(item, index) in approvalHistory"
        :key="index"
        :type="getStatusType(item.status)"
        :timestamp="item.timestamp"
      >
        <div class="timeline-content">
          <h4>{{ item.approverName }} - {{ getStatusText(item.status) }}</h4>
          <p v-if="item.comment" class="comment">{{ item.comment }}</p>
        </div>
      </el-timeline-item>
    </el-timeline>

    <div v-if="currentStep" class="current-step">
      <el-alert
        :title="`当前审批步骤：${currentStep.approverName}`"
        :type="getAlertType(currentStep.status)"
        show-icon
      >
        <template #description>
          <p>开始时间：{{ currentStep.startTime }}</p>
          <p v-if="currentStep.deadline">截止时间：{{ currentStep.deadline }}</p>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  approvalHistory: {
    type: Array,
    default: () => []
  },
  currentStep: {
    type: Object,
    default: null
  }
})

const getStatusType = (status) => {
  const statusMap = {
    approved: 'success',
    rejected: 'danger',
    pending: 'warning',
    processing: 'primary'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    approved: '已通过',
    rejected: '已拒绝',
    pending: '待审批',
    processing: '审批中'
  }
  return statusMap[status] || '未知状态'
}

const getAlertType = (status) => {
  const typeMap = {
    pending: 'warning',
    processing: 'info',
    approved: 'success',
    rejected: 'error'
  }
  return typeMap[status] || 'info'
}
</script>

<style scoped>
.approval-timeline {
  padding: 20px;
}

.timeline-content {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.timeline-content h4 {
  margin: 0;
  font-size: 14px;
  color: #303133;
}

.comment {
  margin: 8px 0 0;
  font-size: 13px;
  color: #606266;
}

.current-step {
  margin-top: 20px;
}
</style>