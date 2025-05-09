<!-- src/views/ApprovalManagement.vue -->
<template>
  <div class="approval-management">
    <div class="page-header">
      <h2 class="page-title">审批管理</h2>
      <div class="header-actions">
        <el-select v-model="activeTab" class="tab-selector">
          <el-option label="待审批" value="pending"></el-option>
          <el-option label="已处理" value="processed"></el-option>
        </el-select>
      </div>
    </div>
    
    <el-card class="approval-card" shadow="hover">
      <el-tabs v-model="activeTab" class="approval-tabs">
        <el-tab-pane label="待审批" name="pending">
          <approval-list 
            ref="pendingListRef"
            :status="'pending'"
            @view-detail="navigateToDetail"
          />
        </el-tab-pane>
        <el-tab-pane label="已处理" name="processed">
          <approval-list 
            ref="processedListRef"
            :status="'processed'"
            @view-detail="navigateToDetail"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 加载错误时的提示 -->
    <el-card v-if="loadError" class="error-card">
      <template #header>
        <div class="error-header">
          <i class="el-icon-warning"></i> 
          <span>数据加载失败</span>
        </div>
      </template>
      <div class="error-content">
        <p>{{ errorMessage }}</p>
        <el-button type="primary" @click="retryLoad">重新加载</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import ApprovalList from '@/components/ApprovalList.vue';
import { ElMessage } from 'element-plus';

export default {
  name: 'ApprovalManagement',
  components: { ApprovalList },
  setup() {
    const router = useRouter();
    const activeTab = ref('pending');
    const pendingListRef = ref(null);
    const processedListRef = ref(null);
    const loadError = ref(false);
    const errorMessage = ref('');
    
    const navigateToDetail = (contentId) => {
      if (contentId) {
        router.push(`/approvals/${contentId}`);
      }
    };
    
    // 监听标签页变化
    watch(activeTab, (newVal) => {
      checkLoadStatus();
    });
    
    // 检查列表加载状态
    const checkLoadStatus = () => {
      const currentListRef = activeTab.value === 'pending' ? pendingListRef.value : processedListRef.value;
      
      if (!currentListRef) {
        loadError.value = true;
        errorMessage.value = '列表组件加载失败，请刷新页面重试。';
        return;
      }
      
      if (currentListRef.loadError) {
        loadError.value = true;
        errorMessage.value = currentListRef.errorMessage || '获取审批列表失败，可能是网络问题或服务器故障。';
      } else {
        loadError.value = false;
      }
    };
    
    // 重试加载数据
    const retryLoad = () => {
      loadError.value = false;
      const currentListRef = activeTab.value === 'pending' ? pendingListRef.value : processedListRef.value;
      
      if (currentListRef && currentListRef.fetchApprovals) {
        currentListRef.fetchApprovals().catch(error => {
          loadError.value = true;
          errorMessage.value = error.message || '重新加载失败';
          ElMessage.error('重新加载失败');
        });
      } else {
        ElMessage.error('组件未就绪，请刷新页面');
      }
    };
    
    onMounted(() => {
      setTimeout(() => {
        checkLoadStatus();
      }, 1000);
    });
    
    return {
      activeTab,
      navigateToDetail,
      pendingListRef,
      processedListRef,
      loadError,
      errorMessage,
      retryLoad
    };
  }
};
</script>

<style scoped>
.approval-management {
  padding: 0;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.tab-selector {
  display: none;
}

.approval-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.approval-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12) !important;
}

.approval-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.approval-tabs :deep(.el-tabs__nav) {
  border-radius: 4px;
}

.approval-tabs :deep(.el-tabs__item) {
  font-size: 14px;
  font-weight: 500;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
}

.error-card {
  margin-top: 40px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.error-header {
  display: flex;
  align-items: center;
  color: #f56c6c;
  gap: 8px;
  font-weight: 500;
}

.error-content {
  text-align: center;
  padding: 20px 0;
}

.error-content p {
  margin-bottom: 20px;
  color: #606266;
}

@media (max-width: 768px) {
  .approval-tabs {
    display: none;
  }
  
  .tab-selector {
    display: block;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .header-actions {
    width: 100%;
  }
}
</style>