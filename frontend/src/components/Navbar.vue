<template>
  <el-menu
    :router="true"
    mode="horizontal"
    class="navbar"
  >
    <el-menu-item index="/">
      <el-icon><HomeFilled /></el-icon>
      控制台
    </el-menu-item>
    <el-menu-item index="/content/edit">
      <el-icon><EditPen /></el-icon>
      新建稿件
    </el-menu-item>
    <el-menu-item index="/approval">
      <el-icon><Document /></el-icon>
      审批管理
    </el-menu-item>
    <el-menu-item index="/flow-config">
      <el-icon><Setting /></el-icon>
      流程配置
    </el-menu-item>
    <el-menu-item index="/hot-news">
      <el-icon><TrendCharts /></el-icon>
      热点新闻
    </el-menu-item>

    <div class="flex-grow" />

    <el-dropdown class="user-dropdown" @command="handleCommand">
      <span class="user-info">
        <el-avatar :size="32" :src="userAvatar" />
        {{ userName }}
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile">
            <el-icon><User /></el-icon>
            个人信息
          </el-dropdown-item>
          <el-dropdown-item command="logout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import {
  HomeFilled,
  EditPen,
  Document,
  Setting,
  TrendCharts,
  User,
  SwitchButton
} from '@element-plus/icons-vue'

const store = useStore()
const router = useRouter()

const userName = computed(() => store.state.user?.name || '未登录')
const userAvatar = computed(() => store.state.user?.avatar || '')

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await store.dispatch('logout')
      router.push('/login')
    } catch (error) {
      ElMessage.error('退出登录失败')
    }
  } else if (command === 'profile') {
    // 跳转到个人信息页面
    router.push('/profile')
  }
}
</script>

<style scoped>
.navbar {
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
}

.flex-grow {
  flex-grow: 1;
}

.user-dropdown {
  margin-left: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-info .el-avatar {
  margin-right: 8px;
}
</style>