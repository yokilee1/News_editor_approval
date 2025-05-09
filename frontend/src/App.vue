<!-- src/App.vue -->
<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <el-menu
          :default-active="activeRoute"
          mode="horizontal"
          @select="handleMenuSelect"
          class="app-menu"
          background-color="#ffffff"
          text-color="#303133"
          active-text-color="#409EFF"
      >
        <div class="logo-container">
          <span class="logo-text">新闻管理系统</span>
        </div>
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/content" v-if="isAuthenticated && userRole === 'editor'" >内容管理</el-menu-item>
        <el-menu-item index="/approvals" v-if="isAuthenticated && userRole === 'approver'">审批管理</el-menu-item>
        <el-menu-item index="/admin" v-if="isAuthenticated && userRole === 'admin'">管理员设置</el-menu-item>
        <el-sub-menu index="auth" class="user-menu">
          <template #title>
            <div class="user-info">
              <el-avatar size="small" class="user-avatar" :style="{backgroundColor: '#409EFF'}">{{ userInitial }}</el-avatar>
              <span>{{ isAuthenticated ? userName || '用户' : '未登录' }}</span>
            </div>
          </template>
          <el-menu-item v-if="!isAuthenticated" index="/login">登录</el-menu-item>
          <el-menu-item v-if="isAuthenticated" @click="handleLogout">登出</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-header>
    <el-main class="app-main">
      <router-view />
    </el-main>
    <el-footer class="app-footer">
      <p>© 2023 新闻管理系统 - 专业的内容审批与发布平台</p>
    </el-footer>
  </el-container>
</template>

<script>
import { computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';

export default {
  name: 'App',
  setup() {
    const store = useStore();
    const router = useRouter();
    const route = useRoute();

    const isAuthenticated = computed(() => store.getters.isAuthenticated);
    const userRole = computed(() => store.getters.userRole);
    const userName = computed(() => store.getters.userName);
    const activeRoute = computed(() => route.path);
    const userInitial = computed(() => {
      if (!userName.value) return '?';
      return userName.value.charAt(0).toUpperCase();
    });

    const handleMenuSelect = (key) => {
      if (key.startsWith('/')) {
        router.push(key);
      }
    };

    const handleLogout = async () => {
      try {
        await store.dispatch('logout'); // 直接调用 logout 动作
        ElMessage.success('登出成功');
        router.push('/login');
      } catch (error) {
        ElMessage.error('登出失败');
      }
    };

    // 验证 token
    onMounted(async () => {
      if (store.state.auth.token) {
        const isValid = await store.dispatch('validateToken');
        if (!isValid) {
          router.push('/login');
        }
      } else if (route.path !== '/login') {
        router.push('/login');
      }
    });

    return {
      isAuthenticated,
      userRole,
      userName,
      userInitial,
      activeRoute,
      handleMenuSelect,
      handleLogout,
    };
  },
};
</script>

<style>
:root {
  --primary-color: #409EFF;
  --success-color: #67C23A;
  --warning-color: #E6A23C;
  --danger-color: #F56C6C;
  --info-color: #909399;
  --text-color: #303133;
  --text-color-secondary: #606266;
  --border-color: #DCDFE6;
  --background-color: #F5F7FA;
  --card-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

body {
  margin: 0;
  padding: 0;
  font-family: "PingFang SC", "Helvetica Neue", Helvetica, "Hiragino Sans GB", "Microsoft YaHei", Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  color: var(--text-color);
  background-color: var(--background-color);
}

.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
  background-color: #ffffff;
}

.logo-container {
  display: inline-flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  color: var(--primary-color);
  font-size: 18px;
  font-weight: bold;
}

.logo-text {
  margin-left: 10px;
}

.app-menu {
  border-bottom: none !important;
}

.app-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: var(--background-color);
}

.app-footer {
  text-align: center;
  background-color: #ffffff;
  color: var(--info-color);
  font-size: 12px;
  padding: 15px 0;
  border-top: 1px solid #eee;
}

.user-menu {
  float: right;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  background-color: var(--primary-color);
  color: #ffffff;
}

/* 美化Element UI组件 */
.el-card {
  border-radius: 8px;
  border: none;
  margin-bottom: 20px;
  box-shadow: var(--card-shadow) !important;
  transition: box-shadow 0.3s, transform 0.3s;
}

.el-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15) !important;
  transform: translateY(-2px);
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-button {
  border-radius: 4px;
  transition: all 0.3s;
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.5);
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>