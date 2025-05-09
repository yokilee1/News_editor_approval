<!-- src/components/ApprovalDetail.vue -->
<template>
  <div class="approval-detail">
    <el-card v-loading="loading">
      <template #header>
        <span>审批详情 - {{ content?.title || '加载中...' }}</span>
      </template>
      <el-form label-width="100px">
        <el-form-item label="标题">
          <span>{{ content?.title }}</span>
        </el-form-item>
        <el-form-item label="分类">
          <span>{{ content?.category }}</span>
        </el-form-item>
        <el-form-item label="摘要">
          <span>{{ content?.summary }}</span>
        </el-form-item>
        <el-form-item label="提交人">
          <span>{{ content?.submitter }}</span>
        </el-form-item>
        <el-form-item label="内容">
          <div v-html="sanitizedContent" class="content-preview"></div>
        </el-form-item>
        <el-form-item label="提交时间">
          <span>{{ formatDate(content?.submittedAt) }}</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="statusTagType">{{ content?.status }}</el-tag>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input
              v-model="comment"
              type="textarea"
              :rows="3"
              placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <el-button
          type="primary"
          @click="handleApprove"
          :disabled="content?.status !== 'SUBMITTED'"
      >
        通过
      </el-button>
      <el-button
          type="danger"
          @click="handleReject"
          :disabled="content?.status !== 'SUBMITTED'"
      >
        拒绝
      </el-button>
      <el-button @click="$router.go(-1)">返回</el-button>
    </el-card>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import DOMPurify from 'dompurify';

export default {
  name: 'ApprovalDetail',
  setup() {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();

    const content = ref(null);
    const loading = ref(false);
    const comment = ref('');

    const sanitizedContent = computed(() =>
        content.value ? DOMPurify.sanitize(content.value.content) : ''
    );

    const statusTagType = computed(() => {
      switch (content.value?.status) {
        case 'SUBMITTED':
          return 'warning';
        case 'APPROVED':
          return 'success';
        case 'REJECTED':
          return 'danger';
        default:
          return '';
      }
    });

    const formatDate = (date) => {
      if (!date) return '--';
      return new Date(date).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
      });
    };

    const fetchContent = async () => {
      loading.value = true;
      try {
        const response = await store.state.$http.get(
            `/api/content/${route.params.id}`
        );
        content.value = response.data;
      } catch (error) {
        ElMessage.error('加载内容失败');
        router.push('/approvals');
      } finally {
        loading.value = false;
      }
    };

    const handleApprove = async () => {
      try {
        await ElMessageBox.confirm('确定通过此审批？', '提示', { type: 'warning' });
        await store.state.$http.post('/api/approval/process', {
          contentId: Number(route.params.id),
          approved: true,
          comment: comment.value,
        });
        ElMessage.success('审批通过');
        router.push('/approvals');
      } catch (error) {
        ElMessage.error(error.message || '操作失败');
      }
    };

    const handleReject = async () => {
      try {
        await ElMessageBox.confirm('确定拒绝此审批？', '提示', { type: 'warning' });
        await store.state.$http.post('/api/approval/process', {
          contentId: Number(route.params.id),
          approved: false,
          comment: comment.value,
        });
        ElMessage.success('审批拒绝');
        router.push('/approvals');
      } catch (error) {
        ElMessage.error(error.message || '操作失败');
      }
    };

    onMounted(fetchContent);

    return {
      content,
      loading,
      comment,
      sanitizedContent,
      statusTagType,
      formatDate,
      handleApprove,
      handleReject,
    };
  },
};
</script>

<style scoped>
.approval-detail {
  max-width: 800px;
  margin: 20px auto;
}
.content-preview {
  border: 1px solid #e8e8e8;
  padding: 10px;
  min-height: 100px;
}
</style>