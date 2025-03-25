<template>
  <div class="content-edit-container">
    <el-form
      ref="contentForm"
      :model="contentForm"
      :rules="contentRules"
      label-width="100px"
      @submit.prevent="handleSubmit"
    >
      <el-card class="content-card">
        <template #header>
          <div class="card-header">
            <span>稿件信息</span>
            <div class="header-actions">
              <el-button @click="handleSaveDraft">保存草稿</el-button>
              <el-button type="primary" native-type="submit">提交审批</el-button>
            </div>
          </div>
        </template>

        <el-form-item label="标题" prop="title">
          <el-input v-model="contentForm.title" placeholder="请输入稿件标题" />
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select v-model="contentForm.category" placeholder="请选择稿件分类">
            <el-option label="时政" value="politics" />
            <el-option label="经济" value="economy" />
            <el-option label="科技" value="technology" />
            <el-option label="文化" value="culture" />
            <el-option label="社会" value="society" />
          </el-select>
        </el-form-item>

        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="contentForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入标签"
          >
            <el-option
              v-for="tag in tagOptions"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="摘要" prop="summary">
          <el-input
            v-model="contentForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入稿件摘要"
          />
        </el-form-item>

        <el-form-item label="正文" prop="content">
          <RichTextEditor
            v-model="contentForm.content"
            @change="handleEditorChange"
          />
        </el-form-item>
      </el-card>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import RichTextEditor from '@/components/RichTextEditor.vue'

const router = useRouter()

const contentForm = reactive({
  title: '',
  category: '',
  tags: [],
  summary: '',
  content: ''
})

const contentRules = {
  title: [{ required: true, message: '请输入稿件标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择稿件分类', trigger: 'change' }],
  summary: [{ required: true, message: '请输入稿件摘要', trigger: 'blur' }],
  content: [{ required: true, message: '请输入稿件正文', trigger: 'blur' }]
}

const tagOptions = ref([
  '热点',
  '独家',
  '专题',
  '深度',
  '评论',
  '调查'
])

// 保存草稿
const handleSaveDraft = async () => {
  try {
    // TODO: 调用保存草稿API
    ElMessage.success('草稿保存成功')
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}

// 提交审批
const handleSubmit = async () => {
  try {
    // TODO: 调用提交审批API
    ElMessage.success('稿件提交成功')
    router.push('/approval')
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  }
}

// 编辑器内容变化
const handleEditorChange = (html) => {
  contentForm.content = html
}
</script>

<style scoped>
.content-edit-container {
  padding: 20px;
}

.content-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}
</style>