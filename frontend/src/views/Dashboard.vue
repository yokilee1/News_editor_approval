<!-- src/views/Dashboard.vue -->
<template>
  <el-container class="dashboard">
    <el-main>
      <h2>欢迎回来, {{ userName }}</h2>
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card shadow="hover" class="stats-card">
            <template #header>
              <div class="card-header">
                <span>概览</span>
              </div>
            </template>
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stats-item">
                  <div class="stats-title">待审批</div>
                  <div class="stats-value">{{ stats.pending || 0 }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stats-item">
                  <div class="stats-title">已审批</div>
                  <div class="stats-value">{{ stats.approved || 0 }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stats-item">
                  <div class="stats-title">已拒绝</div>
                  <div class="stats-value">{{ stats.rejected || 0 }}</div>
                </div>
              </el-col>
            </el-row>
          </el-card>
          
          <el-card v-if="userRole === 'editor'" shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>我的稿件</span>
                <el-button type="primary" size="small" plain @click="$router.push('/content')">
                  查看全部
                </el-button>
              </div>
            </template>
            <draft-list limit="5" />
          </el-card>
          
          <el-card v-if="userRole === 'approver'" shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>待审批稿件</span>
                <el-button type="primary" size="small" plain @click="$router.push('/approvals')">
                  查看全部
                </el-button>
              </div>
            </template>
            <approval-list limit="5" />
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <hotspot-news />
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import DraftList from '@/components/DraftList.vue';
import ApprovalList from '@/components/ApprovalList.vue';
import HotspotNews from '@/components/HotspotNews.vue';
import { ElMessage } from 'element-plus';

export default {
  name: 'Dashboard',
  components: { 
    DraftList, 
    ApprovalList,
    HotspotNews
  },
  setup() {
    const store = useStore();
    const stats = ref({
      pending: 0,
      approved: 0,
      rejected: 0
    });

    const userName = computed(() => store.getters.userName || '用户');
    const userRole = computed(() => store.getters.userRole || '');

    const fetchStats = async () => {
      try {
        const response = await store.state.$http.get('/approval/stats');
        stats.value = response.data || {
          pending: 0,
          approved: 0,
          rejected: 0
        };
      } catch (error) {
        ElMessage.error('获取统计数据失败');
      }
    };

    onMounted(fetchStats);

    return {
      stats,
      userName,
      userRole
    };
  }
};
</script>

<style scoped>
.dashboard {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stats-card {
  margin-bottom: 20px;
}
.stats-item {
  text-align: center;
  padding: 10px;
}
.stats-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.stats-value {
  font-size: 24px;
  font-weight: bold;
}
</style>