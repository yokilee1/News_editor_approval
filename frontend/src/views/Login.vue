<!-- src/views/Login.vue -->
<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2 class="login-title">欢迎登录</h2>
        <p class="login-subtitle">新闻内容管理与审批系统</p>
      </div>
      <login-form />
    </div>
    <div class="login-background">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>
    <div class="login-footer">
      <p>© 2023 新闻管理系统</p>
    </div>
  </div>
</template>

<script>
import LoginForm from '@/components/LoginForm.vue';

export default {
  name: 'Login',
  components: { LoginForm },
};
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
  position: relative;
  overflow: hidden;
}

.login-card {
  width: 420px;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  z-index: 1;
  position: relative;
  transition: all 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-title {
  color: #409EFF;
  font-size: 28px;
  margin-bottom: 8px;
  font-weight: 500;
}

.login-subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.login-footer {
  position: absolute;
  bottom: 20px;
  color: #909399;
  font-size: 12px;
  z-index: 1;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(64, 158, 255, 0.1);
}

.shape-1 {
  width: 400px;
  height: 400px;
  top: -150px;
  right: -150px;
  animation: float 15s infinite ease-in-out;
}

.shape-2 {
  width: 300px;
  height: 300px;
  bottom: -100px;
  left: -100px;
  background: rgba(64, 158, 255, 0.15);
  animation: float 18s infinite ease-in-out reverse;
}

.shape-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  background: rgba(64, 158, 255, 0.05);
  animation: float 12s infinite ease-in-out;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  25% {
    transform: translate(10px, 10px);
  }
  50% {
    transform: translate(20px, 5px);
  }
  75% {
    transform: translate(5px, 15px);
  }
}

@media (max-width: 576px) {
  .login-card {
    width: 90%;
    padding: 20px;
  }
}
</style>