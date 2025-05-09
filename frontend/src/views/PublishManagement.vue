<template>
  <el-container class="publish-management">
    <el-main>
      <div class="page-header">
        <h2>发布管理</h2>
        <el-input
          v-model="searchQuery"
          placeholder="搜索已批准内容"
          style="width: 300px"
          clearable
        >
          <template #suffix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <el-table
        :data="filteredContent"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="createdBy" label="创建者" width="120" />
        <el-table-column prop="approvedAt" label="批准时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="viewContent(scope.row.id)"
            >
              查看
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="publishContent(scope.row.id)"
            >
              发布
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-if="approvedContent.length > 0"
        layout="prev, pager, next"
        :total="approvedContent.length"
        :page-size="pageSize"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: center;"
      />
      
      <el-empty 
        v-if="approvedContent.length === 0 && !loading" 
        description="暂无待发布内容" 
      />
    </el-main>
    
    <el-dialog
      v-model="dialogVisible"
      title="内容预览"
      width="70%"
    >
      <div class="content-preview">
        <h3>{{ currentContent.title }}</h3>
        <div class="content-meta">
          <span>分类: {{ currentContent.category }}</span>
          <span>创建者: {{ currentContent.createdBy }}</span>
          <span>批准时间: {{ currentContent.approvedAt }}</span>
        </div>
        <div class="content-body" v-html="currentContent.content"></div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="success" @click="publishContent(currentContent.id)">
            发布
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

export default {
  name: 'PublishManagement',
  components: { Search },
  setup() {
    const store = useStore();
    const loading = ref(false);
    const approvedContent = ref([]);
    const searchQuery = ref('');
    const currentPage = ref(1);
    const pageSize = ref(10);
    const dialogVisible = ref(false);
    const currentContent = ref({});

    const filteredContent = computed(() => {
      const query = searchQuery.value.toLowerCase().trim();
      
      if (!query) {
        return approvedContent.value.slice(
          (currentPage.value - 1) * pageSize.value, 
          currentPage.value * pageSize.value
        );
      }
      
      return approvedContent.value
        .filter(item => 
          item.title.toLowerCase().includes(query) || 
          item.category.toLowerCase().includes(query)
        )
        .slice(
          (currentPage.value - 1) * pageSize.value, 
          currentPage.value * pageSize.value
        );
    });

    const fetchApprovedContent = async () => {
      loading.value = true;
      try {
        const response = await store.state.$http.get('/api/publish/pending');
        approvedContent.value = response.data || [];
      } catch (error) {
        ElMessage.error('获取待发布内容失败');
      } finally {
        loading.value = false;
      }
    };

    const viewContent = async (id) => {
      loading.value = true;
      try {
        const response = await store.state.$http.get(`/api/content/${id}`);
        currentContent.value = response.data;
        dialogVisible.value = true;
      } catch (error) {
        ElMessage.error('获取内容详情失败');
      } finally {
        loading.value = false;
      }
    };

    const publishContent = async (id) => {
      try {
        await ElMessageBox.confirm(
          '确定要发布此内容吗？发布后将无法撤回。',
          '确认发布',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        );
        
        loading.value = true;
        await store.state.$http.post(`/api/publish/${id}`);
        ElMessage.success('发布成功');
        fetchApprovedContent();
        dialogVisible.value = false;
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('发布失败');
        }
      } finally {
        loading.value = false;
      }
    };

    const handlePageChange = (page) => {
      currentPage.value = page;
    };

    onMounted(fetchApprovedContent);

    return {
      loading,
      approvedContent,
      filteredContent,
      searchQuery,
      pageSize,
      dialogVisible,
      currentContent,
      viewContent,
      publishContent,
      handlePageChange,
    };
  },
};
</script>

<style scoped>
.publish-management {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.content-preview {
  padding: 10px;
}
.content-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #666;
  font-size: 14px;
}
.content-body {
  line-height: 1.6;
}
</style> 