<!-- src/components/FileList.vue -->
<template>
  <div class="file-list">
    <el-table :data="files" style="width: 100%" v-loading="loading">
      <el-table-column prop="originalName" label="文件名" min-width="200" />
      <el-table-column prop="fileType" label="类型" width="150" />
      <el-table-column prop="size" label="大小" width="120">
        <template #default="{ row }">
          {{ formatSize(row.size) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="text" @click="handleDownload(row)">
            下载
          </el-button>
          <el-button type="text" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'FileList',
  props: {
    contentId: {
      type: [String, Number],
      required: true,
    },
  },
  setup(props) {
    const store = useStore();
    const files = ref([]);
    const loading = ref(false);

    const formatSize = (bytes) => {
      if (bytes < 1024) return `${bytes} B`;
      if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(2)} KB`;
      return `${(bytes / 1024 / 1024).toFixed(2)} MB`;
    };

    const fetchFiles = async () => {
      loading.value = true;
      try {
        const response = await store.state.$http.get(
            `/api/content/${props.contentId}/attachments`
        );
        files.value = response.data;
      } catch (error) {
        ElMessage.error('获取文件列表失败');
      } finally {
        loading.value = false;
      }
    };

    const handleDownload = (row) => {
      window.open(row.fileDownloadUri, '_blank');
    };

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定删除此文件？', '提示', { type: 'warning' });
        await store.state.$http.delete(
            `/api/content/${props.contentId}/attachments/${row.fileName}`
        );
        ElMessage.success('删除成功');
        fetchFiles();
      } catch (error) {
        ElMessage.error(error.message || '删除失败');
      }
    };

    onMounted(fetchFiles);

    return {
      files,
      loading,
      formatSize,
      handleDownload,
      handleDelete,
    };
  },
};
</script>

<style scoped>
.file-list {
  margin-top: 20px;
}
</style>