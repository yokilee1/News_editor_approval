<!-- src/components/FileUpload.vue -->
<template>
  <div class="file-upload">
    <el-upload
        :action="uploadUrl"
        :headers="headers"
        :multiple="multiple"
        :on-success="handleSuccess"
        :on-error="handleError"
        :before-upload="beforeUpload"
        :file-list="fileList"
        drag
    >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">
        将文件拖到此处，或<em>点击上传</em>
      </div>
    </el-upload>
  </div>
</template>

<script>
import { computed, ref } from 'vue';
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus';

export default {
  name: 'FileUpload',
  props: {
    contentId: {
      type: [String, Number],
      default: null,
    },
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  setup(props, { emit }) {
    const store = useStore();
    const fileList = ref([]);

    const uploadUrl = computed(() =>
        props.contentId
            ? `/api/content/${props.contentId}/attachments`
            : '/api/files/upload'
    );

    const headers = computed(() => ({
      Authorization: `Bearer ${store.state.auth.token}`,
    }));

    const beforeUpload = (file) => {
      const isLt10M = file.size / 1024 / 1024 < 10;
      if (!isLt10M) {
        ElMessage.error('文件大小不能超过 10MB');
      }
      return isLt10M;
    };

    const handleSuccess = (response) => {
      ElMessage.success('文件上传成功');
      fileList.value.push({
        name: response.originalName,
        url: response.fileDownloadUri,
      });
      emit('upload-success', response);
    };

    const handleError = (error) => {
      ElMessage.error('文件上传失败');
      console.error(error);
    };

    return {
      uploadUrl,
      headers,
      fileList,
      beforeUpload,
      handleSuccess,
      handleError,
    };
  },
};
</script>

<style scoped>
.file-upload {
  margin: 20px 0;
}
</style>