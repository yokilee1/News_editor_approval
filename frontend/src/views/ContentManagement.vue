<!-- src/views/ContentManagement.vue -->
<template>
  <div class="content-management">
    <div class="page-header">
      <h2 class="page-title">内容管理</h2>
      <div class="actions">
        <el-button
            type="primary"
            icon="el-icon-plus"
            @click="$router.push('/content/create')"
            class="create-button"
        >
          创建草稿
        </el-button>
      </div>
    </div>
    
    <draft-list ref="draftListRef" />
    
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
import { ref, onMounted } from 'vue';
import DraftList from '@/components/DraftList.vue';
import { ElMessage } from 'element-plus';

export default {
  name: 'ContentManagement',
  components: { DraftList },
  setup() {
    const draftListRef = ref(null);
    const loadError = ref(false);
    const errorMessage = ref('');

    // 页面加载完成后检查列表是否正确加载
    onMounted(() => {
      setTimeout(() => {
        checkLoadStatus();
      }, 1000);
    });

    // 检查列表加载状态
    const checkLoadStatus = () => {
      if (!draftListRef.value) {
        loadError.value = true;
        errorMessage.value = '列表组件加载失败，请刷新页面重试。';
        return;
      }
      
      if (draftListRef.value.loadError) {
        loadError.value = true;
        errorMessage.value = draftListRef.value.errorMessage || '获取内容列表失败，可能是网络问题或服务器故障。';
      } else {
        loadError.value = false;
      }
    };

    // 重试加载数据
    const retryLoad = () => {
      loadError.value = false;
      if (draftListRef.value) {
        draftListRef.value.fetchDrafts().catch(error => {
          loadError.value = true;
          errorMessage.value = error.message || '重新加载失败';
          ElMessage.error('重新加载失败');
        });
      } else {
        ElMessage.error('组件未就绪，请刷新页面');
      }
    };

    return {
      draftListRef,
      loadError,
      errorMessage,
      retryLoad
    };
  }
};
</script>

<style scoped>
.content-management {
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

.actions {
  display: flex;
  gap: 10px;
}

.create-button {
  font-weight: 500;
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
</style>