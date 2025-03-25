/* eslint-disable no-unused-vars */
<template>
  <div class="flow-config-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审批流程配置</span>
          <el-button type="primary" @click="handleAddFlow">新建流程</el-button>
        </div>
      </template>

      <el-table :data="flowList" style="width: 100%">
        <el-table-column prop="name" label="流程名称" />
        <el-table-column prop="description" label="流程描述" />
        <el-table-column prop="nodeCount" label="节点数量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEditFlow(row)">
              编辑
            </el-button>
            <el-button
              :type="row.status === 'active' ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'active' ? '停用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteFlow(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 流程配置对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑流程' : '新建流程'"
      width="800px"
    >
      <el-form
        ref="flowForm"
        :model="flowForm"
        :rules="flowRules"
        label-width="100px"
      >
        <el-form-item label="流程名称" prop="name">
          <el-input v-model="flowForm.name" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程描述" prop="description">
          <el-input
            v-model="flowForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入流程描述"
          />
        </el-form-item>

        <el-divider>审批节点配置</el-divider>

        <div
          v-for="(node, index) in flowForm.nodes"
          :key="index"
          class="node-item"
        >
          <div class="node-header">
            <span>节点 {{ index + 1 }}</span>
            <el-button
              type="danger"
              size="small"
              circle
              @click="removeNode(index)"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>

          <el-form-item
            :label="'节点名称'"
            :prop="`nodes.${index}.name`"
            :rules="{ required: true, message: '请输入节点名称', trigger: 'blur' }"
          >
            <el-input v-model="node.name" placeholder="请输入节点名称" />
          </el-form-item>

          <el-form-item
            :label="'审批人'"
            :prop="`nodes.${index}.approvers`"
            :rules="{ required: true, message: '请选择审批人', trigger: 'change' }"
          >
            <el-select
              v-model="node.approvers"
              multiple
              filterable
              placeholder="请选择审批人"
            >
              <el-option
                v-for="user in userList"
                :key="user.id"
                :label="user.name"
                :value="user.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="审批类型">
            <el-radio-group v-model="node.approvalType">
              <el-radio label="any">任意一人</el-radio>
              <el-radio label="all">所有人</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>

        <div class="add-node">
          <el-button type="dashed" @click="addNode">添加节点</el-button>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)

const flowList = ref([])
const userList = ref([])

const flowForm = reactive({
  name: '',
  description: '',
  nodes: []
})

const selectedFlow = ref(null)

const flowRules = {
  name: [{ required: true, message: '请输入流程名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入流程描述', trigger: 'blur' }]
}

// 获取流程列表
const fetchFlowList = async () => {
  try {
    // TODO: 调用获取流程列表API
    flowList.value = [
      {
        id: 1,
        name: '普通新闻审批流程',
        description: '适用于日常新闻稿件的审批流程',
        nodeCount: 2,
        status: 'active'
      },
      {
        id: 2,
        name: '重要新闻审批流程',
        description: '适用于重要新闻稿件的审批流程',
        nodeCount: 3,
        status: 'active'
      }
    ]
  } catch (error) {
    ElMessage.error('获取流程列表失败')
  }
}

// 获取用户列表
const fetchUserList = async () => {
  try {
    // TODO: 调用获取用户列表API
    userList.value = [
      { id: 1, name: '张主编' },
      { id: 2, name: '李编辑' },
      { id: 3, name: '王审核' }
    ]
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
}

// 新建流程
const handleAddFlow = () => {
  isEdit.value = false
  flowForm.name = ''
  flowForm.description = ''
  flowForm.nodes = []
  dialogVisible.value = true
}

// 编辑流程
const handleEditFlow = (row) => {
  isEdit.value = true
  selectedFlow.value = row
  Object.assign(flowForm, row)
  dialogVisible.value = true
}

// 切换流程状态
const handleToggleStatus = async (row) => {
  try {
    // TODO: 调用切换状态API
    const action = row.status === 'active' ? '停用' : '启用'
    ElMessage.success(`${action}成功`)
    await fetchFlowList()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 删除流程
const handleDeleteFlow = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该流程吗？', '提示', {
      type: 'warning'
    })
    // TODO: 调用删除流程API
    ElMessage.success('删除成功')
    await fetchFlowList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 添加节点
const addNode = () => {
  flowForm.nodes.push({
    name: '',
    approvers: [],
    approvalType: 'any'
  })
}

// 删除节点
const removeNode = (index) => {
  flowForm.nodes.splice(index, 1)
}

// 提交表单
const handleSubmit = async () => {
  try {
    submitting.value = true
    // TODO: 调用保存流程API
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    await fetchFlowList()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchFlowList()
  fetchUserList()
})
</script>

<style scoped>
.flow-config-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.node-item {
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
}

.node-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.add-node {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>