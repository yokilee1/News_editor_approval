<template>
  <div class="register-container" role="main">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h1>新闻编辑审批系统</h1>
          <p>注册新账号</p>
        </div>
      </template>
      
      <el-form 
        ref="formRef" 
        :model="registerForm" 
        :rules="registerRules" 
        label-position="top"
        @submit.prevent="handleRegister"
      >
        <el-form-item 
          prop="username" 
          label="用户名"
        >
          <el-input 
            v-model="registerForm.username" 
            placeholder="请输入用户名" 
            required
            aria-label="用户名输入框"
          />
        </el-form-item>
        
        <el-form-item 
          prop="name" 
          label="姓名"
        >
          <el-input 
            v-model="registerForm.name" 
            placeholder="请输入姓名" 
            required
            aria-label="姓名输入框"
          />
        </el-form-item>
        
        <el-form-item 
          prop="password" 
          label="密码"
        >
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码" 
            required
            show-password
            aria-label="密码输入框"
          />
        </el-form-item>
        
        <el-form-item 
          prop="confirmPassword" 
          label="确认密码"
        >
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            required
            show-password
            aria-label="确认密码输入框"
          />
        </el-form-item>
        
        <el-form-item 
          prop="email" 
          label="电子邮箱"
        >
          <el-input 
            v-model="registerForm.email" 
            placeholder="请输入电子邮箱" 
            required
            type="email"
            aria-label="电子邮箱输入框"
          />
        </el-form-item>
        
        <el-form-item 
          prop="role" 
          label="角色"
        >
          <el-select 
            v-model="registerForm.role" 
            placeholder="请选择角色" 
            style="width: 100%"
            required
            aria-label="角色选择"
          >
            <el-option 
              label="编辑" 
              value="EDITOR" 
            />
            <el-option 
              label="审批员" 
              value="APPROVER" 
            />
            <el-option 
              label="普通用户" 
              value="USER" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            native-type="submit" 
            :loading="loading" 
            class="register-button"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-link">
        已有账号？
        <router-link to="/login">
          点击登录
        </router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useStore } from 'vuex';

const router = useRouter();
const store = useStore();
const loading = ref(false);
const formRef = ref(null);

const registerForm = reactive({
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
  email: '',
  role: 'USER'
});

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的电子邮箱格式', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
};

const handleRegister = async () => {
  try {
    loading.value = true;
    await formRef.value.validate();
    
    await store.dispatch('register', {
      username: registerForm.username,
      name: registerForm.name,
      password: registerForm.password,
      email: registerForm.email,
      role: registerForm.role
    });
    
    ElMessage.success('注册成功，请登录');
    router.push('/login');
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data?.message || '注册失败，请稍后再试');
    } else if (error.request) {
      ElMessage.error('服务器未响应，请检查网络连接');
    } else {
      ElMessage.error('注册失败: ' + error.message);
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss">
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 400px;
}

.register-header {
  text-align: center;
  margin-bottom: 20px;
}

.register-header h1 {
  margin: 0 0 10px;
  font-size: 24px;
  color: #303133;
}

.register-header p {
  margin: 0;
  color: #909399;
}

.register-button {
  width: 100%;
}

.login-link {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}

/* 添加表单项间距 */
.el-form-item {
  margin-bottom: 20px;
}

/* 改善输入框样式 */
.el-input {
  width: 100%;
}

/* 添加响应式样式 */
@media (max-width: 480px) {
  .register-card {
    margin: 10px;
  }
}

/* 输入框样式覆盖 */
:deep(.el-input__inner) {
  &[type='number'] {
    // 使用标准属性
    appearance: textfield;
    
    &::-webkit-outer-spin-button,
    &::-webkit-inner-spin-button {
      appearance: none;
      margin: 0;
    }
  }
}

/* 分页输入框样式 */
:deep(.el-pagination .el-input__inner) {
  // 使用标准属性
  appearance: textfield;
}

/* 表单控件通用样式 */
:deep(.el-select),
:deep(.el-input),
:deep(.el-textarea) {
  .el-input__inner {
    // 使用标准属性
    appearance: none;
  }
}
</style>