<template>
  <div class="approval-container">
    <el-tabs v-model="activeTab" class="approval-tabs">
      <el-tab-pane label="待我审批" name="pending">
        <el-table :data="pendingList" style="width: 100%">
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="author" label="作者" width="120" />
          <el-table-column prop="category" label="分类" width="100">
            <template #default="{ row }">
              <el-tag>{{ row.category }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleView(row)">
                查看
              </el-button>
              <el-button type="success" size="small" @click="handleApprove(row)">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="handleReject(row)">
                驳回
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="我已审批" name="processed">
        <el-table :data="processedList" style="width: 100%">
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="author" label="作者" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'approved' ? 'success' : 'danger'">
                {{ row.status === 'approved' ? '已通过' : '已驳回' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="approvalTime" label="审批时间" width="180" />
          <el-table-column prop="comment" label="审批意见" min-width="200" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleView(row)">
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'approve' ? '通过审批' : '驳回稿件'"
      width="500px"
    >
      <el-form ref="approvalForm" :model="approvalForm" :rules="approvalRules">
        <el-form-item label="审批意见" prop="comment">
          <el-input
            v-model="approvalForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApproval" :loading="submitting">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('pending')
const dialogVisible = ref(false)
const dialogType = ref('')
const submitting = ref(false)

const pendingList = ref([])
const processedList = ref([])

const currentContent = ref(null)
const approvalForm = reactive({
  comment: ''
})

const approvalRules = {
  comment: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

// 获取待审批列表
const fetchPendingList = async () => {
  try {
    // TODO: 调用获取待审批列表API
    pendingList.value = [
      {
        id: 1,
        title: '关于科技创新的深度报道',
        author: '张三',
        category: '科技',
        submitTime: '2024-01-20 10:30:00'
      },
      {
        id: 2,
        title: '经济政策解读',
        author: '李四',
        category: '经济',
        submitTime: '2024-01-20 09:15:00'
      }
    ]
  } catch (error) {
    ElMessage.error('获取待审批列表失败')
  }
}

// 获取已审批列表
const fetchProcessedList = async () => {
  try {
    // TODO: 调用获取已审批列表API
    processedList.value = [
      {
        id: 3,
        title: '文化产业发展趋势',
        author: '王五',
        status: 'approved',
        approvalTime: '2024-01-19 15:30:00',
        comment: '内容详实，观点准确'
      },
      {
        id: 4,
        title: '教育改革新动向',
        author: '赵六',
        status: 'rejected',
        approvalTime: '2024-01-19 14:20:00',
        comment: '需要补充更多数据支撑'
      }
    ]
  } catch (error) {
    ElMessage.error('获取已审批列表失败')
  }
}

// 查看稿件
const handleView = (content) => {
  // TODO: 实现稿件预览功能
  console.log('查看稿件:', content)
}

// 通过审批
const handleApprove = (content) => {
  dialogType.value = 'approve'
  currentContent.value = content
  dialogVisible.value = true
}

// 驳回稿件
const handleReject = (content) => {
  dialogType.value = 'reject'
  currentContent.value = content
  dialogVisible.value = true
}

// 提交审批
const submitApproval = async () => {
  if (!approvalForm.comment) {
    ElMessage.warning('请输入审批意见')
    return
  }

  try {
    submitting.value = true
    // TODO: 调用审批API
    const action = dialogType.value === 'approve' ? '通过' : '驳回'
    ElMessage.success(`${action}审批成功`)
    dialogVisible.value = false
    fetchPendingList()
    fetchProcessedList()
  } catch (error) {
    ElMessage.error(error.message || '审批操作失败')
  } finally {
    submitting.value = false
    approvalForm.comment = ''
  }
}

onMounted(() => {
  fetchPendingList()
  fetchProcessedList()
})
</script>

<style scoped>
.approval-container {
  padding: 20px;
}

.approval-tabs {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>