<!-- src/components/DraftList.vue -->
<template>
  <div class="draft-list-container">
    <!-- 搜索和筛选区域 -->
    <div class="filter-bar">
      <div class="search-input">
        <el-input
          v-model="searchQuery"
          placeholder="搜索标题或分类"
          :prefix-icon="Search"
          clearable
          @clear="handleSearch"
          @input="handleSearch"
          class="custom-input"
        />
      </div>
      
      <!-- 状态筛选按钮 -->
      <div class="status-filters">
        <span class="filter-label">状态筛选:</span>
        <el-radio-group v-model="statusFilter" @change="handleStatusFilterChange" size="small">
          <el-radio-button label="ALL">全部</el-radio-button>
          <el-radio-button label="DRAFT">草稿</el-radio-button>
          <el-radio-button label="PENDING">待审批</el-radio-button>
          <el-radio-button label="APPROVED">已批准</el-radio-button>
          <el-radio-button label="REJECTED">已拒绝</el-radio-button>
          <el-radio-button label="PUBLISHED">已发布</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <el-card class="draft-table-card" shadow="hover">
      <el-table
          :data="displayedDrafts"
          style="width: 100%"
          v-loading="loading"
          @row-click="handleRowClick"
          class="custom-table"
          :row-class-name="tableRowClassName"
      >
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="category" label="分类" width="150" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            <div class="time-cell">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDate(row.createdAt) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                  size="small"
                  type="primary"
                  :icon="Edit"
                  circle
                  @click.stop="handleEdit(row)"
                  :disabled="row.status !== 'DRAFT'"
                  class="action-button"
              />
              <el-button
                  size="small"
                  type="danger"
                  :icon="Delete"
                  circle
                  @click.stop="handleDelete(row)"
                  :disabled="row.status !== 'DRAFT'"
                  class="action-button"
              />
              <el-button
                  size="small"
                  type="success"
                  :icon="Check"
                  circle
                  @click.stop="handleSubmit(row)"
                  :disabled="row.status !== 'DRAFT'"
                  class="action-button"
              />
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          v-if="totalItems > 0"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalItems"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="pagination"
      />
    </el-card>
    
    <!-- 无数据时的提示 -->
    <el-empty v-if="!loading && displayedDrafts.length === 0" description="没有找到符合条件的内容" class="empty-state">
      <el-button type="primary" @click="resetFilters">重置筛选</el-button>
    </el-empty>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Edit, Delete, Check, Calendar } from '@element-plus/icons-vue';

export default {
  name: 'DraftList',
  props: { limit: { type: [Number, String], default: 0 } },
  setup(props) {
    const store = useStore();
    const router = useRouter();

    const allDrafts = ref([]); // 存储所有获取的草稿
    const drafts = ref([]); // 当前页面显示的草稿
    const loading = ref(false);
    const totalItems = ref(0);
    const currentPage = ref(1);
    const pageSize = ref(10);
    const searchQuery = ref('');
    const statusFilter = ref('ALL'); // 默认显示所有状态
    const loadError = ref(false);
    const errorMessage = ref('');

    const formatDate = (date) => {
      if (!date) return '未知';
      return new Date(date).toLocaleString();
    };
    
    const getStatusText = (status) => {
      const statusMap = {
        'DRAFT': '草稿',
        'PENDING': '待审批',
        'APPROVED': '已批准',
        'REJECTED': '已拒绝',
        'PUBLISHED': '已发布'
      };
      return statusMap[status] || status;
    };
    
    const getStatusType = (status) => {
      const typeMap = {
        'DRAFT': 'info',
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'PUBLISHED': 'primary'
      };
      return typeMap[status] || 'info';
    };

    // 根据搜索条件和分页信息展示数据
    const displayedDrafts = computed(() => {
      // 如果设置了limit属性，则仅显示指定数量的条目
      if (props.limit) {
        return drafts.value.slice(0, parseInt(props.limit) || 5);
      }
      
      return drafts.value;
    });

    // 根据状态筛选和搜索条件筛选数据
    const filterAndPaginateDrafts = () => {
      // 首先按状态筛选
      let filteredData = allDrafts.value;
      
      // 如果选择了特定状态（非ALL），则筛选该状态的内容
      if (statusFilter.value !== 'ALL') {
        filteredData = filteredData.filter(item => item.status === statusFilter.value);
      }
      
      // 然后按搜索关键词筛选
      if (searchQuery.value) {
        filteredData = filteredData.filter(item => 
          item.title?.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
          item.category?.toLowerCase().includes(searchQuery.value.toLowerCase())
        );
      }
      
      totalItems.value = filteredData.length;
      
      // 应用分页
      const startIndex = (currentPage.value - 1) * pageSize.value;
      const endIndex = startIndex + pageSize.value;
      drafts.value = filteredData.slice(startIndex, endIndex);
    };

    // 监听搜索查询、状态筛选和分页变化，更新展示数据
    watch([searchQuery, statusFilter, currentPage, pageSize], () => {
      if (props.limit) return; // 如果设置了limit属性，不应用客户端分页
      filterAndPaginateDrafts();
    });

    const fetchDrafts = async () => {
      console.log('Fetching drafts...'); 
      loading.value = true;
      loadError.value = false; // 重置错误状态
      errorMessage.value = '';
      
      try {
        // 使用Spring Boot后端分页接口参数格式
        const params = {
          page: currentPage.value - 1, // Spring Boot页码从0开始
          size: pageSize.value,
          sort: 'createdAt,desc' // 默认按创建时间降序排序
        };

        // 如果有搜索条件，添加到请求参数
        if (searchQuery.value) {
          params.search = searchQuery.value;
        }
        
        // 如果选择了特定状态（非ALL），添加状态筛选参数
        if (statusFilter.value !== 'ALL') {
          params.status = statusFilter.value;
        }

        const response = await store.state.$http.get('/content/list', { params });
        console.log('Drafts response:', response.data);
        
        // 处理后端返回的分页数据
        if (response.data && response.data.content) {
          // Spring Boot分页格式
          allDrafts.value = response.data.content;
          drafts.value = response.data.content;
          totalItems.value = response.data.totalElements || 0;
        } else if (Array.isArray(response.data)) {
          // 普通数组格式，前端自己处理分页
          allDrafts.value = response.data;
          filterAndPaginateDrafts();
        } else {
          // 处理空数据或非预期格式
          console.warn('Unexpected response format:', response.data);
          allDrafts.value = [];
          drafts.value = [];
          totalItems.value = 0;
        }
      } catch (error) {
        console.error('Fetch drafts error:', error);
        const errMsg = error.response?.data?.message || error.message || '获取草稿列表失败';
        ElMessage.error(errMsg);
        errorMessage.value = errMsg;
        loadError.value = true;
        allDrafts.value = [];
        drafts.value = [];
        totalItems.value = 0;
      } finally {
        loading.value = false;
      }
    };

    const handleEdit = (row) => {
      router.push(`/content/edit/${row.id}`);
    };

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定删除此草稿？', '提示', { type: 'warning' });
        await store.state.$http.delete(`/content/${row.id}`);
        ElMessage.success('删除成功');
        fetchDrafts();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.message || '删除失败');
        }
      }
    };

    const handleSubmit = async (row) => {
      try {
        await ElMessageBox.confirm('确定提交此草稿进行审批？', '提示', { type: 'warning' });
        await store.state.$http.post(`/approval/submit/${row.id}`);
        ElMessage.success('提交成功');
        fetchDrafts();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.message || '提交失败');
        }
      }
    };

    const handleRowClick = (row) => {
      router.push(`/content/detail/${row.id}`);
    };

    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1; // 重置为第一页
      if (!props.limit) {
        fetchDrafts(); // 重新获取数据
      }
    };

    const handleCurrentChange = (page) => {
      currentPage.value = page;
      if (!props.limit) {
        fetchDrafts(); // 重新获取数据 
      }
    };

    const handleSearch = () => {
      currentPage.value = 1; // 搜索时重置为第一页
      if (props.limit) {
        filterAndPaginateDrafts(); // 客户端过滤
      } else {
        fetchDrafts(); // 服务器端搜索
      }
    };

    // 处理状态筛选变化
    const handleStatusFilterChange = () => {
      currentPage.value = 1; // 重置为第一页
      if (!props.limit) {
        fetchDrafts(); // 服务器端筛选
      }
    };
    
    // 重置所有筛选条件
    const resetFilters = () => {
      searchQuery.value = '';
      statusFilter.value = 'ALL';
      currentPage.value = 1;
      fetchDrafts();
    };

    const tableRowClassName = ({ row }) => {
      return row.status === 'DRAFT' ? 'editable-row' : '';
    };

    onMounted(() => {
      console.log('DraftList mounted, fetching drafts...');
      fetchDrafts();
    });

    return {
      drafts,
      displayedDrafts,
      loading,
      totalItems,
      currentPage,
      pageSize,
      searchQuery,
      statusFilter,
      formatDate,
      getStatusText,
      getStatusType,
      handleEdit,
      handleDelete,
      handleSubmit,
      handleRowClick,
      handleSizeChange,
      handleCurrentChange,
      handleSearch,
      handleStatusFilterChange,
      resetFilters,
      tableRowClassName,
      // 图标
      Search,
      Edit,
      Delete,
      Check,
      Calendar,
      // 向外暴露错误状态
      loadError,
      errorMessage,
      fetchDrafts
    };
  },
};
</script>

<style scoped>
.draft-list-container {
  padding: 20px;
}

.filter-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  padding-bottom: 15px;
}

.search-input {
  flex: 1;
  min-width: 250px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.status-filters {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.draft-table-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
  margin-bottom: 20px;
}

.custom-table {
  --el-table-border: none;
  --el-table-header-bg-color: #f8fafc;
  --el-table-row-hover-bg-color: #f0f7ff;
}

.custom-table :deep(th) {
  font-weight: 600;
  color: #303133;
}

.custom-table :deep(.editable-row) {
  cursor: pointer;
  transition: background-color 0.2s;
}

.status-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 500;
}

.time-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.action-button {
  transition: all 0.2s ease;
}

.action-button:hover:not(:disabled) {
  transform: translateY(-2px);
}

.action-button:disabled {
  opacity: 0.5;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
  display: flex;
}

.empty-state {
  margin-top: 40px;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-input, 
  .status-filters {
    width: 100%;
  }
  
  .action-buttons {
    flex-wrap: wrap;
  }
}
</style>