<template>
  <el-container class="approval-detail">
    <el-main>
      <div class="page-header">
        <h2>审批详情</h2>
        <el-button link @click="$router.push('/approvals')">
          返回列表
        </el-button>
      </div>
      
      <el-card v-loading="loading">
        <template #header>
          <div class="card-header">
            <h3>{{ content.title }}</h3>
            <el-tag :type="statusTagType" effect="dark">{{ statusText }}</el-tag>
          </div>
        </template>
        
        <div class="content-meta">
          <div class="meta-item">
            <span class="meta-label">分类：</span>
            <span>{{ content.category }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">作者：</span>
            <span>{{ content.createdBy || '未知' }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">提交时间：</span>
            <span>{{ content.submittedAt || '未提交' }}</span>
          </div>
        </div>
        
        <div class="content-tags">
          <el-tag 
            v-for="tag in content.tags" 
            :key="tag" 
            size="small" 
            effect="plain" 
            style="margin-right: 5px"
          >
            {{ tag }}
          </el-tag>
        </div>
        
        <el-divider content-position="left">摘要</el-divider>
        <div class="content-summary">{{ content.summary }}</div>
        
        <el-divider content-position="left">内容</el-divider>
        <div class="content-body" v-html="content.content"></div>
        
        <el-divider v-if="content.status === 'PENDING'" />
        
        <div v-if="content.status === 'PENDING'" class="approval-actions">
          <el-form ref="approvalFormRef" :model="approvalForm" :rules="rules">
            <el-form-item label="审批意见" prop="comment">
              <el-input
                v-model="approvalForm.comment"
                type="textarea"
                :rows="4"
                placeholder="请输入审批意见"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="success" 
                @click="handleApprove" 
                :loading="actionLoading"
              >
                批准
              </el-button>
              <el-button 
                type="danger" 
                @click="handleReject" 
                :loading="actionLoading"
              >
                拒绝
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <div v-if="content.status !== 'PENDING'" class="approval-history">
          <el-timeline>
            <el-timeline-item
              v-for="(record, index) in approvalHistory"
              :key="index"
              :timestamp="record.createTime"
              :type="record.action === 'APPROVE' ? 'success' : 'danger'"
            >
              <h4>{{ record.action === 'APPROVE' ? '批准' : '拒绝' }}</h4>
              <p>{{ record.comment }}</p>
              <p class="history-operator">操作人: {{ record.operator }}</p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';

export default {
  name: 'ApprovalDetail',
  setup() {
    const store = useStore();
    const router = useRouter();
    const route = useRoute();
    const approvalFormRef = ref(null);
    
    const loading = ref(false);
    const actionLoading = ref(false);
    const content = ref({});
    const approvalHistory = ref([]);
    
    const approvalForm = reactive({
      comment: '',
    });
    
    const rules = {
      comment: [
        { required: true, message: '请输入审批意见', trigger: 'blur' },
        { min: 5, message: '审批意见至少5个字符', trigger: 'blur' }
      ]
    };

    const statusText = computed(() => {
      const statusMap = {
        'DRAFT': '草稿',
        'PENDING': '待审批',
        'APPROVED': '已批准',
        'REJECTED': '已拒绝',
        'PUBLISHED': '已发布'
      };
      return statusMap[content.value.status] || '未知';
    });
    
    const statusTagType = computed(() => {
      const typeMap = {
        'DRAFT': 'info',
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'PUBLISHED': 'primary'
      };
      return typeMap[content.value.status] || 'info';
    });

    const fetchContent = async () => {
      const contentId = route.params.id;
      if (!contentId) {
        ElMessage.error('未指定内容ID');
        router.push('/approvals');
        return;
      }
      
      loading.value = true;
      try {
        const response = await store.state.$http.get(`/content/${contentId}`);
        content.value = response.data;
        
        // 获取审批历史
        if (content.value.status !== 'PENDING') {
          const historyResponse = await store.state.$http.get(`/approval/history/${contentId}`);
          approvalHistory.value = historyResponse.data || [];
        }
      } catch (error) {
        console.error('获取审批内容失败', error);
        ElMessage.error('获取审批内容失败');
        router.push('/approvals');
      } finally {
        loading.value = false;
      }
    };
    
    const handleApprove = async () => {
      try {
        await approvalFormRef.value.validate();
        actionLoading.value = true;
        
        await store.state.$http.post('/approval/process', {
          contentId: route.params.id,
          approved: true,
          comment: approvalForm.comment
        });
        
        ElMessage.success('审批通过成功');
        router.push('/approvals');
      } catch (error) {
        if (error.message) {
          ElMessage.error(error.message);
        }
      } finally {
        actionLoading.value = false;
      }
    };
    
    const handleReject = async () => {
      try {
        await approvalFormRef.value.validate();
        actionLoading.value = true;
        
        await store.state.$http.post('/approval/process', {
          contentId: route.params.id,
          approved: false,
          comment: approvalForm.comment
        });
        
        ElMessage.success('已拒绝该审批');
        router.push('/approvals');
      } catch (error) {
        if (error.message) {
          ElMessage.error(error.message);
        }
      } finally {
        actionLoading.value = false;
      }
    };

    onMounted(fetchContent);

    return {
      loading,
      actionLoading,
      content,
      approvalHistory,
      approvalForm,
      approvalFormRef,
      rules,
      statusText,
      statusTagType,
      handleApprove,
      handleReject
    };
  }
};
</script>

<style scoped>
.approval-detail {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.content-meta {
  margin: 10px 0 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}
.meta-item {
  padding: 5px 0;
}
.meta-label {
  font-weight: bold;
  margin-right: 5px;
}
.content-tags {
  margin: 15px 0;
}
.content-summary {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}
.content-body {
  line-height: 1.8;
  margin-bottom: 20px;
}
.approval-actions {
  max-width: 600px;
  margin: 0 auto;
}
.history-operator {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style> 