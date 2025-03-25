<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <template #header>
            <div class="stat-header">
              <el-icon><Document /></el-icon>
              待审批稿件
            </div>
          </template>
          <div class="stat-value">{{ stats.pendingApproval }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <template #header>
            <div class="stat-header">
              <el-icon><CircleCheck /></el-icon>
              已通过稿件
            </div>
          </template>
          <div class="stat-value">{{ stats.approved }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <template #header>
            <div class="stat-header">
              <el-icon><CircleClose /></el-icon>
              已驳回稿件
            </div>
          </template>
          <div class="stat-value">{{ stats.rejected }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <template #header>
            <div class="stat-header">
              <el-icon><Edit /></el-icon>
              草稿箱
            </div>
          </template>
          <div class="stat-value">{{ stats.draft }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
              <el-button text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="todoList" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.type === 'approval' ? 'warning' : 'info'">
                  {{ row.type === 'approval' ? '待审批' : '待修改' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="date" label="时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>AI推荐热点</span>
              <el-button text>查看全部</el-button>
            </div>
          </template>
          <el-table :data="hotNews" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="heat" label="热度" width="100">
              <template #default="{ row }">
                <el-progress
                  :percentage="row.heat"
                  :color="customColors"
                  :show-text="false"
                />
              </template>
            </el-table-column>
            <el-table-column prop="trend" label="趋势" width="100">
              <template #default="{ row }">
                <el-tag :type="row.trend === 'up' ? 'success' : 'danger'">
                  {{ row.trend === 'up' ? '上升' : '下降' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  Document,
  CircleCheck,
  CircleClose,
  Edit
} from '@element-plus/icons-vue'

const stats = ref({
  pendingApproval: 0,
  approved: 0,
  rejected: 0,
  draft: 0
})

const todoList = ref([])
const hotNews = ref([])

const customColors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 }
]

// 获取统计数据
const fetchStats = async () => {
  try {
    // TODO: 调用后端API获取统计数据
    stats.value = {
      pendingApproval: 5,
      approved: 12,
      rejected: 3,
      draft: 8
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取待办事项
const fetchTodoList = async () => {
  try {
    // TODO: 调用后端API获取待办事项
    todoList.value = [
      { title: '新闻稿件审批', type: 'approval', date: '2024-01-20 10:30:00' },
      { title: '专题报道修改', type: 'revision', date: '2024-01-20 09:15:00' },
      { title: '突发新闻审批', type: 'approval', date: '2024-01-20 08:45:00' }
    ]
  } catch (error) {
    console.error('获取待办事项失败:', error)
  }
}

// 获取热点新闻
const fetchHotNews = async () => {
  try {
    // TODO: 调用后端API获取热点新闻
    hotNews.value = [
      { title: '科技创新助力经济发展', heat: 85, trend: 'up' },
      { title: '环保政策新动向', heat: 70, trend: 'up' },
      { title: '教育改革最新进展', heat: 60, trend: 'down' }
    ]
  } catch (error) {
    console.error('获取热点新闻失败:', error)
  }
}

onMounted(() => {
  fetchStats()
  fetchTodoList()
  fetchHotNews()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.stat-card {
  .stat-header {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .stat-value {
    font-size: 24px;
    font-weight: bold;
    text-align: center;
    color: #409EFF;
    margin-top: 10px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>