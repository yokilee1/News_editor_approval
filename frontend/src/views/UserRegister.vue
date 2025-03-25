<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h2>新闻编辑审批系统</h2>
          <p>注册新账号</p>
        </div>
      </template>
      
      <el-form ref="formRef" :model="registerForm" :rules="registerRules" label-width="0" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名" />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" show-password />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="电子邮箱" />
        </el-form-item>
        
        <el-form-item prop="role">
          <el-select v-model="registerForm.role" placeholder="选择角色" style="width: 100%">
            <el-option label="编辑" value="EDITOR" />
            <el-option label="审批员" value="APPROVER" />
            <el-option label="普通用户" value="USER" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="register-button">
            注册
          </el-button>
        </el-form-item>
        
        <div class="login-link">
          已有账号？<router-link to="/login">点击登录</router-link>
        </div>
      </el-form>
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
  password: '',
  confirmPassword: '',
  email: '',
  role: ''
});

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else {
    if (registerForm.confirmPassword !== '') {
      // 如果确认密码已填写，则验证两次输入是否一致
      if (registerForm.confirmPassword !== value) {
        callback(new Error('两次输入密码不一致!'));
      }
    }
    callback();
  }
};

const validateConfirmPass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPass, trigger: 'blur' }
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
    
    // 表单验证
    await formRef.value.validate();
    
    console.log('发送注册请求:', {
      username: registerForm.username,
      password: registerForm.password,
      email: registerForm.email,
      role: registerForm.role
    });
    
    const result = await store.dispatch('register', {
      username: registerForm.username,
      password: registerForm.password,
      email: registerForm.email,
      role: registerForm.role
    });
    
    console.log('注册响应:', result);
    ElMessage.success('注册成功，请登录');
    router.push('/login');
  } catch (error) {
    console.error('注册失败详情:', error);
    if (error.response) {
      console.error('错误响应:', error.response);
      ElMessage.error(error.response.data?.message || '注册失败，请稍后再试');
    } else if (error.request) {
      console.error('未收到响应:', error.request);
      ElMessage.error('服务器未响应，请检查网络连接');
    } else {
      console.error('请求配置错误:', error.message);
      ElMessage.error('请求错误: ' + error.message);
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.register-card {
  width: 400px;
}

.register-header {
  text-align: center;
}

.register-header h2 {
  margin-bottom: 10px;
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
</style> 