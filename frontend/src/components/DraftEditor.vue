<!-- src/components/DraftEditor.vue -->
<template>
  <div class="draft-editor">
    <el-row :gutter="20">
      <!-- 左侧AI辅助写作栏 -->
      <el-col :span="5" class="side-panel">
        <el-card shadow="hover" class="ai-card">
          <template #header>
            <span>AI 辅助写作</span>
          </template>
          <el-input
              v-model="aiPrompt"
              placeholder="输入提示以生成内容"
              type="textarea"
              :rows="3"
          />
          <el-button
              type="primary"
              :loading="aiLoading"
              @click="handleAIGenerate"
              style="margin-top: 10px"
              class="ai-generate-btn"
          >
            生成
          </el-button>
        </el-card>
      </el-col>
      
      <!-- 中间编辑区域 -->
      <el-col :span="12">
        <el-form
            ref="draftForm"
            :model="form"
            :rules="rules"
            label-width="100px"
            v-loading="loading"
        >
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入标题" />
          </el-form-item>
          <el-form-item label="分类" prop="category">
            <el-select v-model="form.category" placeholder="请选择分类">
              <el-option label="新闻" value="news" />
              <el-option label="分析" value="analysis" />
              <el-option label="评论" value="comment" />
            </el-select>
          </el-form-item>
          <el-form-item label="摘要" prop="summary">
            <el-input
                v-model="form.summary"
                type="textarea"
                :rows="2"
                placeholder="请输入摘要"
            />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <div class="editor-container">
              <el-input
                type="textarea"
                v-model="form.content"
                :rows="15"
                placeholder="请输入正文内容"
                ref="contentEditor"
              ></el-input>
            </div>
          </el-form-item>
          <el-form-item label="标签">
            <el-select v-model="form.tags" multiple placeholder="选择标签">
              <el-option v-for="tag in tags" :key="tag" :label="tag" :value="tag" />
            </el-select>
          </el-form-item>
          <el-form-item label="版本说明">
            <el-input
                v-model="form.versionComment"
                placeholder="请输入版本说明（选填）"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSave">保存草稿</el-button>
            <el-button @click="handleSubmit" :disabled="isNew">提交审批</el-button>
            <el-button @click="$router.go(-1)">返回</el-button>
          </el-form-item>
        </el-form>
        
        <el-card shadow="hover" style="margin-top: 20px" v-if="!isNew">
          <template #header>
            <div class="version-header">
              <span>版本历史</span>
              <el-button link @click="fetchVersionHistory">刷新</el-button>
            </div>
          </template>
          <div v-loading="versionLoading">
            <el-table :data="versionHistory" stripe style="width: 100%">
              <el-table-column prop="versionId" label="版本ID" width="100" />
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column prop="creator" label="创建者" width="120" />
              <el-table-column prop="comment" label="版本说明" />
              <el-table-column label="操作" width="180">
                <template #default="scope">
                  <el-button 
                    size="small" 
                    type="primary" 
                    plain
                    @click="viewVersion(scope.row)"
                  >
                    查看
                  </el-button>
                  <el-button 
                    size="small" 
                    type="success" 
                    plain
                    @click="restoreVersion(scope.row.versionId)"
                  >
                    恢复
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧热点新闻栏 -->
      <el-col :span="7" class="side-panel">
        <hotspot-news 
          :on-select-prompt="updateAIPrompt"
          :on-select-tags="updateTags"
          class="hotspot-card"
        />
      </el-col>
    </el-row>

    <el-dialog
      v-model="versionDialogVisible"
      title="版本内容"
      width="70%"
    >
      <div v-html="versionContent"></div>
    </el-dialog>
    
    <!-- 新增确认对话框 -->
    <el-dialog
      v-model="aiConfirmVisible"
      title="AI生成内容预览"
      width="70%"
    >
      <div>
        <p>AI已成功生成内容，请确认是否使用：</p>
        <p class="ai-preview">{{ aiPreviewContent }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectAIContent">取消</el-button>
          <el-button type="primary" @click="applyAIContent">
            确认使用
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import {ElMessage, ElMessageBox} from 'element-plus';
import DOMPurify from 'dompurify';
import HotspotNews from './HotspotNews.vue';

export default {
  name: 'DraftEditor',
  components: { 
    HotspotNews
  },
  setup() {
    const store = useStore();
    const router = useRouter();
    const route = useRoute();
    const draftForm = ref(null);
    const contentEditor = ref(null);
    const loading = ref(false);
    const aiLoading = ref(false);
    const versionLoading = ref(false);
    const versionHistory = ref([]);
    const versionDialogVisible = ref(false);
    const versionContent = ref('');
    const aiConfirmVisible = ref(false);
    const aiPreviewContent = ref('');
    const pendingContent = ref('');

    const form = reactive({
      title: '',
      category: '',
      summary: '',
      content: '',
      tags: [],
      isDraft: true, // 默认草稿
      versionComment: '',
    });

    const rules = {
      title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
      category: [{ required: true, message: '请选择分类', trigger: 'change' }],
      content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
    };

    const tags = ref(['新闻', '热点', '分析', '评论']);
    const aiPrompt = ref('');

    const isNew = computed(() => !route.params.id);

    const fetchDraft = async () => {
      if (isNew.value) return;
      
      loading.value = true;
      try {
        const response = await store.state.$http.get(`/content/${route.params.id}`);
        const draft = response.data;
        form.title = draft.title;
        form.category = draft.category;
        form.summary = draft.summary;
        form.content = draft.content;
        form.tags = draft.tags || [];
        form.isDraft = draft.status === 'DRAFT';
      } catch (error) {
        ElMessage.error('获取草稿失败');
      } finally {
        loading.value = false;
      }
    };

    const handleSave = async () => {
      try {
        await draftForm.value.validate();
        loading.value = true;
        
        const endpoint = isNew.value 
          ? '/content/create'
          : `/content/${route.params.id}`;
        
        const method = isNew.value ? 'post' : 'put';
        
        // 处理内容安全
        const cleanContent = DOMPurify.sanitize(form.content);
        const payload = {
          ...form,
          content: cleanContent
        };
        
        const response = await store.state.$http[method](endpoint, payload);
        
        ElMessage.success('保存成功');
        
        if (isNew.value) {
          // 如果是新建，保存后导航到编辑页
          router.replace(`/content`);
        }
      } catch (error) {
        ElMessage.error(error.message || '保存失败');
      } finally {
        loading.value = false;
      }
    };

    const handleSubmit = async () => {
      try {
        await draftForm.value.validate();
        loading.value = true;
        form.isDraft = false; // 提交审批时设置为非草稿

        await ElMessageBox.confirm('确定提交此草稿进行审批？', '提示', { type: 'warning' });
        
        // 处理内容安全
        const cleanContent = DOMPurify.sanitize(form.content);
        const payload = {
          ...form,
          content: cleanContent
        };
        
        // 先保存草稿内容
        await store.state.$http.put(`/content/${route.params.id}`, payload);
        
        // 然后提交审批
        await store.state.$http.post(`/approval/submit/${route.params.id}`);
        
        ElMessage.success('提交审批成功');
        router.push('/content');
      } catch (error) {
        console.error('提交失败:', error);
        if (error.code === 'ECONNABORTED') {
          ElMessage.error('请求超时，请稍后重试');
        } else {
          ElMessage.error(error.message || '提交失败');
        }
      } finally {
        loading.value = false;
      }
    };

    const handleAIGenerate = async () => {
      if (!aiPrompt.value.trim()) {
        ElMessage.warning('请输入提示');
        return;
      }
      
      // 获取当前登录用户ID
      const currentUser = store.state.auth.user;
      const userId = currentUser?.userId;
      
      // 确保有用户ID
      if (!userId) {
        ElMessage.error('未登录或缺少用户信息');
        return;
      }
      
      aiLoading.value = true;
      try {
        // 添加必要的请求头
        const headers = {
          'X-User-ID': userId
        };
        
        console.log('开始AI生成，用户ID:', userId);
        
        const aiRequest = {
          prompt: aiPrompt.value,
          maxTokens: 2000,
          temperature: 0.7,
          category: form.category,
          tags: form.tags,
        };
        
        console.log('AI请求参数:', aiRequest);
        
        const response = await store.state.$http.post('/ai/generate', aiRequest, { headers, timeout: 30000 });
        console.log('AI响应:', response.data);
        
        if (response.data && response.data.success) {
          const content = response.data.content;
          console.log('AI生成的内容长度:', content.length, '字符');
          console.log('AI生成的内容前100字符:', content.substring(0, 100));
          
          // 处理内容安全
          const generatedText = DOMPurify.sanitize(content);
          console.log('净化后的内容长度:', generatedText.length, '字符');
          
          // 确保内容不为空
          if (!generatedText.trim()) {
            throw new Error('生成的内容为空');
          }
          
          // 存储生成的内容，等待用户确认
          pendingContent.value = generatedText;
          
          // 设置预览内容
          const previewLength = 100;
          aiPreviewContent.value = generatedText.length > previewLength 
              ? generatedText.substring(0, previewLength) + '...' 
              : generatedText;
              
          // 显示确认对话框
          aiConfirmVisible.value = true;
          
          ElMessage.success('内容生成成功，请确认是否使用');
        } else {
          throw new Error(response.data?.message || 'AI生成返回错误结果');
        }
      } catch (error) {
        console.error('AI生成失败:', error);
        if (error.code === 'ECONNABORTED') {
          ElMessage.error('AI生成请求超时，请稍后重试或缩短提示内容');
        } else {
          ElMessage.error(error.response?.data?.message || error.message || 'AI 生成失败');
        }
      } finally {
        aiLoading.value = false;
      }
    };
    
    // 确认使用AI生成的内容
    const applyAIContent = () => {
      if (!pendingContent.value) {
        ElMessage.warning('没有待处理的内容');
        return;
      }
      
      // 更新表单内容，显示在编辑器中
      if (form.content) {
        console.log('追加内容到现有文本');
        form.content = `${form.content}\n\n${pendingContent.value}`;
      } else {
        console.log('设置为新内容');
        form.content = pendingContent.value;
      }
      
      // 使用ref直接更新内容编辑器
      if (contentEditor.value) {
        console.log('使用ref更新内容编辑器');
        contentEditor.value.modelValue = form.content;
      }
      
      // 关闭对话框
      aiConfirmVisible.value = false;
      
      // 根据内容提取标签
      extractTagsFromContent(pendingContent.value);
      
      // 清空待处理内容
      pendingContent.value = '';
      
      ElMessage.success('内容已应用');
    };
    
    // 拒绝使用AI生成的内容
    const rejectAIContent = () => {
      pendingContent.value = '';
      aiConfirmVisible.value = false;
      ElMessage.info('已取消使用AI生成内容');
    };
    
    // 从内容中提取可能的标签
    const extractTagsFromContent = (content) => {
      const lowerContent = content.toLowerCase();
      const possibleTags = [];
      
      if (lowerContent.includes('音乐') || lowerContent.includes('旋律') || lowerContent.includes('歌曲')) {
        possibleTags.push('音乐');
      }
      if (lowerContent.includes('艺术') || lowerContent.includes('文艺')) {
        possibleTags.push('艺术');
      }
      if (lowerContent.includes('文化') || lowerContent.includes('历史')) {
        possibleTags.push('文化');
      }
      if (lowerContent.includes('科技') || lowerContent.includes('技术')) {
        possibleTags.push('科技');
      }
      
      // 添加提取的标签
      if (possibleTags.length > 0) {
        console.log('从内容中提取的标签:', possibleTags);
        updateTags(possibleTags);
      }
    };
    
    const updateAIPrompt = (prompt) => {
      aiPrompt.value = prompt;
    };
    
    const updateTags = (newTags) => {
      // 合并并去重
      const uniqueTags = [...new Set([...form.tags, ...newTags])];
      form.tags = uniqueTags;
    };
    
    const fetchVersionHistory = async () => {
      if (isNew.value) return;
      
      versionLoading.value = true;
      try {
        const response = await store.state.$http.get(`/content/${route.params.id}/history`);
        versionHistory.value = response.data || [];
      } catch (error) {
        ElMessage.error('获取版本历史失败');
      } finally {
        versionLoading.value = false;
      }
    };
    
    const viewVersion = (version) => {
      versionContent.value = version.content;
      versionDialogVisible.value = true;
    };
    
    const restoreVersion = async (versionId) => {
      try {
        loading.value = true;
        await store.state.$http.post(`/content/drafts/${route.params.id}/restore/${versionId}`);
        ElMessage.success('版本恢复成功');
        // 重新获取草稿内容
        await fetchDraft();
        // 刷新版本历史
        await fetchVersionHistory();
      } catch (error) {
        ElMessage.error('版本恢复失败');
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      fetchDraft();
      if (!isNew.value) {
        fetchVersionHistory();
      }
    });

    return {
      form,
      rules,
      draftForm,
      contentEditor,
      loading,
      tags,
      aiPrompt,
      aiLoading,
      isNew,
      versionHistory,
      versionLoading,
      versionDialogVisible,
      versionContent,
      aiConfirmVisible,
      aiPreviewContent,
      handleSave,
      handleSubmit,
      handleAIGenerate,
      updateAIPrompt,
      updateTags,
      fetchVersionHistory,
      viewVersion,
      restoreVersion,
      applyAIContent,
      rejectAIContent
    };
  },
};
</script>

<style scoped>
.draft-editor {
  max-width: 2000px;
  margin: 0 auto;
  padding: 20px;
}
.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.editor-container {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
.ai-preview {
  background-color: #f5f7fa;
  border-left: 4px solid #409eff;
  padding: 10px;
  margin: 15px 0;
  max-height: 300px;
  overflow-y: auto;
  white-space: pre-wrap;
  font-family: Arial, sans-serif;
}
.side-panel {
  position: sticky;
  top: 20px;
  align-self: flex-start;
  height: 100%;
}
.ai-card {
  margin-bottom: 20px;
}
.ai-generate-btn {
  width: 100%;
}
.hotspot-card {
  max-height: calc(100vh - 40px);
  overflow-y: auto;
}
</style>
