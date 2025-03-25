import { createRouter, createWebHistory } from 'vue-router';
import UserLogin from '../views/UserLogin.vue';
import UserRegister from '../views/UserRegister.vue';
import AdminDashboard from '../views/AdminDashboard.vue';
import ContentEdit from '../views/ContentEdit.vue';
import ApprovalManagement from '../views/ApprovalManagement.vue';
import FlowConfig from '../views/FlowConfig.vue';
import HotNews from '../views/HotNews.vue';

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', component: UserLogin },
  { path: '/register', component: UserRegister },
  { path: '/dashboard', component: AdminDashboard },
  { path: '/content/edit', component: ContentEdit },
  { path: '/approval', component: ApprovalManagement },
  { path: '/flow-config', component: FlowConfig },
  { path: '/hot-news', component: HotNews }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由导航守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login');
  } else {
    next();
  }
});

export default router;
