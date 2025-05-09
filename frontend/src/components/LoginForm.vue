<!-- src/components/LoginForm.vue -->
<template>
  <div class="login-form-container">
    <el-form
        ref="loginForm"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleLogin"
    >
      <el-form-item prop="username">
        <template #label>
          <div class="form-label">用户名</div>
        </template>
        <el-input 
          v-model="form.username" 
          placeholder="请输入用户名"
          prefix-icon="el-icon-user"
          class="login-input"
        />
      </el-form-item>
      <el-form-item prop="password">
        <template #label>
          <div class="form-label">密码</div>
        </template>
        <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            prefix-icon="el-icon-lock"
            class="login-input"
            @keyup.enter="handleLogin"
        />
      </el-form-item>
      <div class="form-options">
        <el-checkbox>记住我</el-checkbox>
        <a href="#" class="forgot-password">忘记密码?</a>
      </div>
      <el-form-item>
        <el-button 
          type="primary" 
          class="login-button"
          :loading="loading"
          @click="handleLogin"
        >
          <span v-if="!loading">登录</span>
          <span v-else>登录中...</span>
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

export default {
  name: 'LoginForm',
  setup() {
    const store = useStore();
    const router = useRouter();
    const loginForm = ref(null);
    const loading = ref(false);

    const form = reactive({
      username: '',
      password: '',
    });

    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度为3-20字符', trigger: 'blur' },
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码至少6字符', trigger: 'blur' },
      ],
    };

    const handleLogin = async () => {
      try {
        await loginForm.value.validate();
        loading.value = true;
        await store.dispatch('login', form);
        ElMessage.success('登录成功');
        router.push('/');
      } catch (error) {
        ElMessage.error(error.message || '登录失败');
      } finally {
        loading.value = false;
      }
    };

    return {
      form,
      rules,
      loginForm,
      loading,
      handleLogin,
    };
  },
};
</script>

<style scoped>
.login-form-container {
  width: 100%;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
}

.login-input {
  margin-bottom: 5px;
}

.login-input :deep(.el-input__inner) {
  height: 44px;
  border-radius: 4px;
  border-color: #dcdfe6;
  transition: all 0.3s;
}

.login-input :deep(.el-input__inner:focus) {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.login-input :deep(.el-input__prefix) {
  color: #909399;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  font-size: 14px;
}

.forgot-password {
  color: #409EFF;
  text-decoration: none;
  transition: color 0.3s;
}

.forgot-password:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 4px;
  transition: all 0.3s;
}

.login-button:hover {
  background-color: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.el-form-item {
  margin-bottom: 22px;
}
</style>