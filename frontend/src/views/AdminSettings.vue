<!-- src/views/AdminSettings.vue -->
<template>
  <div class="admin-settings">
    <div class="page-header">
      <h2 class="page-title">管理员设置</h2>
      <div class="tab-actions">
        <el-select v-model="activeTab" class="mobile-tab-selector">
          <el-option label="用户管理" value="users" />
          <el-option label="审批流程管理" value="flows" />
          <el-option label="节点管理" value="nodes" />
        </el-select>
      </div>
    </div>

    <el-card class="admin-card" shadow="hover">
      <el-tabs v-model="activeTab" class="admin-tabs">
        <!-- 用户管理 -->
        <el-tab-pane label="用户管理" name="users">
          <div class="tab-header">
            <el-button type="primary" @click="dialogVisible = true" class="add-button">
              <i class="el-icon-plus"></i> 添加用户
            </el-button>
            <el-input
              v-model="searchQuery"
              placeholder="搜索用户"
              class="search-input"
              clearable
            >
              <template #prefix>
                <el-icon><search /></el-icon>
              </template>
            </el-input>
          </div>
          
          <el-table 
            :data="filteredUsers" 
            v-loading="loading" 
            style="width: 100%"
            class="custom-table"
          >
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="username" label="用户名" width="150" />
            <el-table-column prop="name" label="姓名" width="150" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="role" label="角色" width="120">
              <template #default="scope">
                <el-tag :type="getRoleType(scope.row.role)" effect="light" class="role-tag">
                  {{ getRoleText(scope.row.role) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <div class="action-buttons">
                  <el-button
                    size="small"
                    type="primary"
                    @click="handleEditUser(scope.row)"
                    class="action-button"
                  >
                    编辑
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="handleDeleteUser(scope.row)"
                    class="action-button"
                  >
                    删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 审批流程管理 -->
        <el-tab-pane label="审批流程管理" name="flows">
          <div class="tab-header">
            <el-button
                type="primary"
                @click="showFlowForm = true"
                class="add-button"
            >
              <i class="el-icon-plus"></i> 创建流程
            </el-button>
          </div>
          <el-table 
            :data="flows" 
            style="width: 100%" 
            v-loading="flowLoading"
            class="custom-table"
          >
            <el-table-column prop="title" label="流程标题" />
            <el-table-column prop="name" label="流程名称" />
            <el-table-column prop="level" label="层级" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button 
                    size="small" 
                    type="primary" 
                    @click="editFlow(row)"
                    class="action-button"
                  >
                    编辑
                  </el-button>
                  <el-button 
                    size="small" 
                    type="danger" 
                    @click="deleteFlow(row)"
                    class="action-button"
                  >
                    删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 节点管理 -->
        <el-tab-pane label="节点管理" name="nodes">
          <div class="tab-header flow-selector">
            <el-form :inline="true">
              <el-form-item label="选择流程">
                <el-select
                    v-model="selectedFlowId"
                    placeholder="请选择流程"
                    @change="fetchNodes"
                >
                  <el-option
                      v-for="flow in flows"
                      :key="flow.id"
                      :label="flow.title"
                      :value="flow.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button
                    type="primary"
                    @click="showNodeForm = true"
                    :disabled="!selectedFlowId"
                    class="add-button"
                >
                  <i class="el-icon-plus"></i> 添加节点
                </el-button>
              </el-form-item>
            </el-form>
          </div>
          <el-table 
            :data="nodes" 
            style="width: 100%" 
            v-loading="nodeLoading"
            class="custom-table"
          >
            <el-table-column prop="nodeName" label="节点名称" />
            <el-table-column prop="approver" label="审批人 ID" />
            <el-table-column prop="approverRole" label="审批人角色" />
            <el-table-column prop="orderNum" label="序号" />
            <el-table-column prop="isCountersign" label="会签">
              <template #default="{ row }">
                <el-tag :type="row.isCountersign ? 'success' : 'info'" effect="light" class="tag">
                  {{ row.isCountersign ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button 
                    size="small" 
                    type="primary" 
                    @click="editNode(row)"
                    class="action-button"
                  >
                    编辑
                  </el-button>
                  <el-button 
                    size="small" 
                    type="danger" 
                    @click="deleteNode(row)"
                    class="action-button"
                  >
                    删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 加载错误时的提示 -->
    <el-card v-if="loadError" class="error-card">
      <template #header>
        <div class="error-header">
          <i class="el-icon-warning"></i> 
          <span>数据加载失败</span>
        </div>
      </template>
      <div class="error-content">
        <p>{{ errorMessage }}</p>
        <el-button type="primary" @click="retryLoad">重新加载</el-button>
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="审批员" value="APPROVER" />
            <el-option label="编辑" value="EDITOR" />
            <el-option label="普通用户" value="USER" />
            <el-option label="发布员" value="PUBLISHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="userForm.password" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUserForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审批流程表单对话框 -->
    <el-dialog
        :title="isEditFlow ? '编辑流程' : '创建流程'"
        v-model="showFlowForm"
        width="30%"
    >
      <el-form :model="flowForm" :rules="flowRules" ref="flowFormRef">
        <el-form-item label="流程标题" prop="title">
          <el-input v-model="flowForm.title" />
        </el-form-item>
        <el-form-item label="流程名称" prop="name">
          <el-input v-model="flowForm.name" />
        </el-form-item>
        <el-form-item label="层级" prop="level">
          <el-input-number v-model="flowForm.level" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFlowForm = false">取消</el-button>
        <el-button type="primary" @click="saveFlow">保存</el-button>
      </template>
    </el-dialog>

    <!-- 节点表单对话框 -->
    <el-dialog
        :title="isEditNode ? '编辑节点' : '添加节点'"
        v-model="showNodeForm"
        width="40%"
    >
      <el-form :model="nodeForm" :rules="nodeRules" ref="nodeFormRef">
        <el-form-item label="节点名称" prop="nodeName">
          <el-input v-model="nodeForm.nodeName" />
        </el-form-item>
        <el-form-item label="审批人" prop="approver">
          <el-select v-model="nodeForm.approver" placeholder="选择审批人">
            <el-option
                v-for="user in approvers"
                :key="user.id"
                :label="user.name"
                :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审批人角色" prop="approverRole">
          <el-select v-model="nodeForm.approverRole" placeholder="选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="编辑" value="EDITOR" />
            <el-option label="审批人" value="APPROVER" />
            <el-option label="普通用户" value="USER" />
            <el-option label="发布员" value="PUBLISHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="序号" prop="orderNum">
          <el-input-number v-model="nodeForm.orderNum" :min="1" />
        </el-form-item>
        <el-form-item label="会签">
          <el-switch v-model="nodeForm.isCountersign" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="nodeForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="允许退回">
          <el-switch v-model="nodeForm.allowReject" />
        </el-form-item>
        <el-form-item label="退回策略" prop="rejectStrategy">
          <el-select v-model="nodeForm.rejectStrategy">
            <el-option label="退回上一节点" value="TO_PREVIOUS" />
            <el-option label="退回发起人" value="TO_START" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNodeForm = false">取消</el-button>
        <el-button type="primary" @click="saveNode">保存</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="400px"
    >
      <p>确定要删除用户 "{{ userToDelete?.name }}" 吗？此操作不可恢复。</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useStore } from 'vuex';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

export default {
  name: 'AdminSettings',
  components: {
    Search
  },
  setup() {
    const store = useStore();

    // 用户管理
    const users = ref([]);
    const userLoading = ref(false);
    const showUserForm = ref(false);
    const isEditUser = ref(false);
    const userForm = reactive({
      id: null,
      username: '',
      name: '',
      email: '',
      password: '',
      role: '',
    });
    const userFormRef = ref(null); // 修正为 ref
    const userRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度为3-20字符', trigger: 'blur' },
      ],
      name: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { min: 2, max: 50, message: '姓名长度为2-50字符', trigger: 'blur' },
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效邮箱', trigger: 'blur' },
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码至少6字符', trigger: 'blur' },
      ],
      role: [{ required: true, message: '请选择角色', trigger: 'change' }],
    };

    // 审批流程管理
    const flows = ref([]);
    const flowLoading = ref(false);
    const showFlowForm = ref(false);
    const isEditFlow = ref(false);
    const flowForm = reactive({
      id: null,
      title: '',
      name: '',
      level: 1,
    });
    const flowFormRef = ref(null); // 修正为 ref
    const flowRules = {
      title: [{ required: true, message: '请输入流程标题', trigger: 'blur' }],
      name: [{ required: true, message: '请输入流程名称', trigger: 'blur' }],
      level: [{ required: true, message: '请输入层级', trigger: 'change' }],
    };

    // 节点管理
    const nodes = ref([]);
    const nodeLoading = ref(false);
    const showNodeForm = ref(false);
    const isEditNode = ref(false);
    const selectedFlowId = ref(null);
    const approvers = ref([]);
    const nodeForm = reactive({
      id: null,
      nodeName: '',
      approver: null,
      approverRole: '',
      orderNum: 1,
      isCountersign: false,
      description: '',
      flowId: null,
      allowReject: true,
      rejectStrategy: 'TO_PREVIOUS',
    });
    const nodeFormRef = ref(null); // 修正为 ref
    const nodeRules = {
      nodeName: [
        { required: true, message: '请输入节点名称', trigger: 'blur' },
      ],
      approver: [
        { required: true, message: '请选择审批人', trigger: 'change' },
      ],
      orderNum: [
        { required: true, message: '请输入序号', trigger: 'change' },
        { type: 'number', min: 1, message: '序号最小为1', trigger: 'change' },
      ],
      rejectStrategy: [
        { required: true, message: '请选择退回策略', trigger: 'change' },
      ],
    };

    const activeTab = ref('users');
    const loading = ref(false);
    const dialogVisible = ref(false);
    const deleteDialogVisible = ref(false);
    const isEdit = ref(false);
    const searchQuery = ref('');
    const userToDelete = ref(null);
    
    // 根据搜索筛选用户
    const filteredUsers = computed(() => {
      const query = searchQuery.value.toLowerCase().trim();
      if (!query) return users.value;
      
      return users.value.filter(user => 
        user.username.toLowerCase().includes(query) ||
        user.name.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query)
      );
    });
    
    // 获取角色文本
    const getRoleText = (role) => {
      const roleMap = {
        'ADMIN': '管理员',
        'APPROVER': '审批员',
        'EDITOR': '编辑',
        'USER': '普通用户',
        'PUBLISHER': '发布员'
      };
      return roleMap[role] || role;
    };
    
    // 获取角色标签类型
    const getRoleType = (role) => {
      const typeMap = {
        'ADMIN': 'danger',
        'APPROVER': 'warning',
        'EDITOR': 'success',
        'USER': 'info',
        'PUBLISHER': 'primary'
      };
      return typeMap[role] || 'info';
    };

    const fetchUsers = async () => {
      loading.value = true;
      try {
        // 获取当前登录用户ID
        const currentUser = store.state.auth.user;
        const userId = currentUser?.userId;
        
        // 确保有用户ID
        if (!userId) {
          ElMessage.error('未登录或缺少用户信息');
          return;
        }
        
        // 添加必要的请求头
        const headers = {
          'X-User-ID': userId
        };
        
        const response = await store.state.$http.get('/users/list', { headers });
        users.value = response.data;
        approvers.value = response.data.filter((user) =>
            ['ADMIN', 'APPROVER'].includes(user.role)
        );
      } catch (error) {
        console.error('获取用户列表失败', error);
        ElMessage.error('获取用户列表失败');
      } finally {
        loading.value = false;
      }
    };

    const editUser = (row) => {
      isEditUser.value = true;
      showUserForm.value = true;
      Object.assign(userForm, {
        id: row.id,
        username: row.username,
        name: row.name,
        email: row.email,
        password: '',
        role: row.role,
      });
    };

    const deleteUser = async (row) => {
      try {
        await ElMessageBox.confirm('确定删除此用户？', '提示', { type: 'warning' });
        
        // 获取当前登录用户ID
        const currentUser = store.state.auth.user;
        const userId = currentUser?.userId;
        
        // 确保有用户ID
        if (!userId) {
          ElMessage.error('未登录或缺少用户信息');
          return;
        }
        
        // 添加必要的请求头
        const headers = {
          'X-User-ID': userId
        };
        
        await store.state.$http.delete(`/users/${row.id}`, { headers });
        ElMessage.success('删除成功');
        fetchUsers();
      } catch (error) {
        ElMessage.error(error.message || '删除失败');
      }
    };

    const saveUser = async () => {
      try {
        await userFormRef.value.validate();
        
        // 获取当前登录用户ID
        const currentUser = store.state.auth.user;
        const userId = currentUser?.userId;
        
        // 确保有用户ID
        if (!userId) {
          ElMessage.error('未登录或缺少用户信息');
          return;
        }
        
        // 添加必要的请求头
        const headers = {
          'X-User-ID': userId
        };
        
        await store.state.$http[isEditUser.value ? 'put' : 'post'](
            `/users${isEditUser.value ? `/${userForm.id}` : ''}`,
            userForm,
            { headers }
        );
        ElMessage.success('保存成功');
        showUserForm.value = false;
        fetchUsers();
      } catch (error) {
        ElMessage.error(error.message || '保存失败');
      }
    };

    const editFlow = (row) => {
      isEditFlow.value = true;
      showFlowForm.value = true;
      Object.assign(flowForm, {
        id: row.id,
        title: row.title,
        name: row.name,
        level: row.level,
      });
    };

    const deleteFlow = async (row) => {
      try {
        await ElMessageBox.confirm('确定删除此流程？', '提示', { type: 'warning' });
        await store.state.$http.delete(`/approval-flow/${row.id}`);
        ElMessage.success('删除成功');
        fetchFlows();
      } catch (error) {
        ElMessage.error(error.message || '删除失败');
      }
    };

    const saveFlow = async () => {
      try {
        await flowFormRef.value.validate();
        await store.state.$http[isEditFlow.value ? 'put' : 'post'](
            `/approval-flow${isEditFlow.value ? `/${flowForm.id}` : ''}`,
            flowForm
        );
        ElMessage.success('保存成功');
        showFlowForm.value = false;
        fetchFlows();
      } catch (error) {
        ElMessage.error(error.message || '保存失败');
      }
    };

    const editNode = (row) => {
      isEditNode.value = true;
      showNodeForm.value = true;
      Object.assign(nodeForm, {
        id: row.id,
        nodeName: row.nodeName,
        approver: row.approver,
        approverRole: row.approverRole,
        orderNum: row.orderNum,
        isCountersign: row.isCountersign,
        description: row.description,
        flowId: row.flowId,
        allowReject: row.allowReject,
        rejectStrategy: row.rejectStrategy,
      });
    };

    const deleteNode = async (row) => {
      try {
        await ElMessageBox.confirm('确定删除此节点？', '提示', { type: 'warning' });
        await store.state.$http.delete(
            `/approval-flow/${row.flowId}/nodes/${row.id}`
        );
        ElMessage.success('删除成功');
        fetchNodes();
      } catch (error) {
        ElMessage.error(error.message || '删除失败');
      }
    };

    const saveNode = async () => {
      try {
        await nodeFormRef.value.validate();
        nodeForm.flowId = selectedFlowId.value;
        await store.state.$http[isEditNode.value ? 'put' : 'post'](
            `/approval-flow/${selectedFlowId.value}/nodes${
                isEditNode.value ? `/${nodeForm.id}` : ''
            }`,
            nodeForm
        );
        ElMessage.success('保存成功');
        showNodeForm.value = false;
        fetchNodes();
      } catch (error) {
        ElMessage.error(error.message || '保存失败');
      }
    };

    const handleEditUser = (user) => {
      isEdit.value = true;
      userForm.value = { ...user };
      // 编辑不需要密码字段
      delete userForm.value.password;
      dialogVisible.value = true;
    };

    const handleDeleteUser = (user) => {
      userToDelete.value = user;
      deleteDialogVisible.value = true;
    };

    const confirmDelete = async () => {
      try {
        // 获取当前登录用户ID
        const currentUser = store.state.auth.user;
        const userId = currentUser?.userId;
        
        // 确保有用户ID
        if (!userId) {
          ElMessage.error('未登录或缺少用户信息');
          return;
        }
        
        // 添加必要的请求头
        const headers = {
          'X-User-ID': userId
        };
        
        await store.state.$http.delete(`/users/${userToDelete.value.id}`, { headers });
        ElMessage.success('删除成功');
        fetchUsers();
        deleteDialogVisible.value = false;
      } catch (error) {
        ElMessage.error('删除失败');
      }
    };

    const submitUserForm = async () => {
      await userFormRef.value.validate(async (valid) => {
        if (!valid) return;
        
        try {
          // 获取当前登录用户ID
          const currentUser = store.state.auth.user;
          const userId = currentUser?.userId;
          
          // 确保有用户ID
          if (!userId) {
            ElMessage.error('未登录或缺少用户信息');
            return;
          }
          
          // 添加必要的请求头
          const headers = {
            'X-User-ID': userId
          };
          
          if (isEdit.value) {
            // 更新用户
            await store.state.$http.put(`/users/${userForm.value.id}`, userForm.value, { headers });
            ElMessage.success('更新成功');
          } else {
            // 创建用户
            await store.state.$http.post('/users', userForm.value, { headers });
            ElMessage.success('创建成功');
          }
          dialogVisible.value = false;
          resetForm();
          fetchUsers();
        } catch (error) {
          ElMessage.error(error.response?.data?.message || '操作失败');
        }
      });
    };

    const resetForm = () => {
      userForm.value = {
        id: null,
        username: '',
        name: '',
        email: '',
        role: '',
        password: ''
      };
      isEdit.value = false;
    };

    const fetchFlows = async () => {
      flowLoading.value = true;
      try {
        const response = await store.state.$http.get('/approval-flow'); // 修正路径
        flows.value = response.data;
      } catch (error) {
        ElMessage.error('获取审批流程失败');
      } finally {
        flowLoading.value = false;
      }
    };

    const fetchNodes = async () => {
      if (!selectedFlowId.value) {
        nodes.value = [];
        return;
      }
      nodeLoading.value = true;
      try {
        const response = await store.state.$http.get(
            `/approval-flow/${selectedFlowId.value}/nodes`
        );
        nodes.value = response.data;
      } catch (error) {
        ElMessage.error('获取节点列表失败');
        nodes.value = [];
      } finally {
        nodeLoading.value = false;
      }
    };

    const loadError = ref(false);
    const errorMessage = ref('');

    // 检查加载状态
    const checkLoadStatus = () => {
      if (activeTab.value === 'users' && userLoading) {
        loadError.value = true;
        errorMessage.value = '获取用户列表失败，请稍后重试。';
      } else if (activeTab.value === 'flows' && flowLoading) {
        loadError.value = true;
        errorMessage.value = '获取流程列表失败，请稍后重试。';
      } else if (activeTab.value === 'nodes' && nodeLoading) {
        loadError.value = true;
        errorMessage.value = '获取节点列表失败，请稍后重试。';
      } else {
        loadError.value = false;
      }
    };

    // 重试加载数据
    const retryLoad = () => {
      loadError.value = false;
      if (activeTab.value === 'users') {
        fetchUsers();
      } else if (activeTab.value === 'flows') {
        fetchFlows();
      } else if (activeTab.value === 'nodes' && selectedFlowId.value) {
        fetchNodes();
      } else {
        ElMessage.error('请先选择一个流程');
      }
    };

    // 监听标签页变化
    watch(activeTab, () => {
      checkLoadStatus();
    });

    onMounted(() => {
      fetchUsers();
      fetchFlows();
    });

    return {
      activeTab,
      users,
      userLoading,
      showUserForm,
      isEditUser,
      userForm,
      userFormRef,
      userRules,
      flows,
      flowLoading,
      showFlowForm,
      isEditFlow,
      flowForm,
      flowFormRef,
      flowRules,
      nodes,
      nodeLoading,
      showNodeForm,
      isEditNode,
      selectedFlowId,
      approvers,
      nodeForm,
      nodeFormRef,
      nodeRules,
      editUser,
      deleteUser,
      saveUser,
      editFlow,
      deleteFlow,
      saveFlow,
      fetchNodes,
      editNode,
      deleteNode,
      saveNode,
      dialogVisible,
      deleteDialogVisible,
      searchQuery,
      filteredUsers,
      getRoleText,
      getRoleType,
      handleEditUser,
      handleDeleteUser,
      confirmDelete,
      submitUserForm,
      resetForm,
      loadError,
      errorMessage,
      retryLoad
    };
  },
};
</script>

<style scoped>
.admin-settings {
  padding: 0;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.tab-actions {
  display: flex;
  gap: 10px;
}

.mobile-tab-selector {
  display: none;
}

.admin-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.admin-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12) !important;
}

.admin-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.admin-tabs :deep(.el-tabs__nav) {
  border-radius: 4px;
}

.admin-tabs :deep(.el-tabs__item) {
  font-size: 14px;
  font-weight: 500;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-input {
  width: 300px;
}

.add-button {
  font-weight: 500;
}

.flow-selector {
  flex-direction: column;
  align-items: flex-start;
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

.role-tag, .tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.action-button {
  transition: all 0.2s ease;
}

.action-button:hover {
  transform: translateY(-2px);
}

.error-card {
  margin-top: 40px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.error-header {
  display: flex;
  align-items: center;
  color: #f56c6c;
  gap: 8px;
  font-weight: 500;
}

.error-content {
  text-align: center;
  padding: 20px 0;
}

.error-content p {
  margin-bottom: 20px;
  color: #606266;
}

@media (max-width: 768px) {
  .admin-tabs {
    display: none;
  }
  
  .mobile-tab-selector {
    display: block;
    width: 100%;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .tab-actions {
    width: 100%;
  }
  
  .tab-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-input {
    width: 100%;
  }
  
  .action-buttons {
    flex-wrap: wrap;
  }
}
</style>