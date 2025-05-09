<!-- src/components/ApprovalList.vue -->
<template>
  <div class="approval-list">
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else>
      <el-empty v-if="filteredApprovals.length === 0" description="暂无审批任务" />
      
      <el-table v-else :data="filteredApprovals" stripe style="width: 100%">
        <el-table-column prop="recordId" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="contentId" label="内容ID" width="120" />
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status || 'PENDING')"
            >
              {{ getStatusText(scope.row.status || 'PENDING') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button
              link
              type="primary"
              @click="viewDetail(scope.row.contentId)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue';
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus';

export default {
  name: 'ApprovalList',
  props: {
    status: {
      type: String,
      default: 'pending'
    },
    limit: {
      type: [String, Number],
      default: null
    }
  },
  emits: ['view-detail'],
  setup(props, { emit }) {
    const store = useStore();
    const loading = ref(false);
    const approvals = ref([]);
    
    const filteredApprovals = computed(() => {
      console.log('原始审批数据:', approvals.value);
      let result = approvals.value;
      
      // API返回的数据可能没有status字段，因此我们不对其进行状态筛选
      // 对于pending-tasks接口，所有返回的数据都是待审批的
      // 对于history-tasks接口，所有返回的数据都是已处理的
      
      // 限制条数
      if (props.limit) {
        const limit = parseInt(props.limit);
        if (!isNaN(limit) && limit > 0) {
          result = result.slice(0, limit);
        }
      }
      
      console.log('过滤后审批数据:', result);
      return result;
    });
    
    const fetchApprovals = async () => {
      loading.value = true;
      try {
        let endpoint = '/approval/pending-tasks';
        if (props.status === 'processed') {
          endpoint = '/approval/history-tasks';
        }
        
        console.log('请求审批数据，接口:', endpoint);
        const response = await store.state.$http.get(endpoint);
        console.log('接口返回数据:', response);
        approvals.value = response.data || [];
        console.log('设置审批数据:', approvals.value);
      } catch (error) {
        console.error('获取审批任务失败', error);
        ElMessage.error('获取审批任务失败');
      } finally {
        loading.value = false;
      }
    };
    
    const getStatusText = (status) => {
      // 如果是通过pending-tasks接口获取的数据，默认为待审批
      if (!status && props.status === 'pending') {
        return '待审批';
      }
      // 如果是通过history-tasks接口获取的数据，默认为已处理
      if (!status && props.status === 'processed') {
        return '已处理';
      }
      
      const statusMap = {
        'DRAFT': '草稿',
        'PENDING': '待审批',
        'APPROVED': '已批准',
        'REJECTED': '已拒绝',
        'PUBLISHED': '已发布'
      };
      return statusMap[status] || status || '待审批';
    };
    
    const getStatusType = (status) => {
      // 如果是通过pending-tasks接口获取的数据，默认为待审批样式
      if (!status && props.status === 'pending') {
        return 'warning';
      }
      // 如果是通过history-tasks接口获取的数据，默认为普通信息样式
      if (!status && props.status === 'processed') {
        return 'info';
      }
      
      const typeMap = {
        'DRAFT': 'info',
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'PUBLISHED': 'primary'
      };
      return typeMap[status] || 'info';
    };
    
    const viewDetail = (id) => {
      emit('view-detail', id);
    };
    
    watch(() => props.status, () => {
      fetchApprovals();
    });
    
    onMounted(fetchApprovals);
    
    return {
      loading,
      approvals,
      filteredApprovals,
      getStatusText,
      getStatusType,
      viewDetail
    };
  }
};
</script>

<style scoped>
.approval-list {
  margin-top: 10px;
}
.loading-container {
  padding: 20px;
}
</style>