// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store';

// 路由定义
const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
    },
    {
        path: '/',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { requiresAuth: true },
    },
    {
        path: '/content',
        name: 'ContentManagement',
        component: () => import('@/views/ContentManagement.vue'),
        meta: { requiresAuth: true, roles: ['editor'] },
    },
    {
        path: '/content/create',
        name: 'CreateDraft',
        component: () => import('@/views/DraftEdit.vue'),
        meta: { requiresAuth: true, roles: ['editor'] },
    },
    {
        path: '/content/edit/:id',
        name: 'EditDraft',
        component: () => import('@/views/DraftEdit.vue'),
        meta: { requiresAuth: true, roles: ['editor'] },
        props: true,
    },
    {
        path: '/approvals',
        name: 'ApprovalManagement',
        component: () => import('@/views/ApprovalManagement.vue'),
        meta: { requiresAuth: true, roles: ['approver'] },
    },
    {
        path: '/approvals/:id',
        name: 'ApprovalDetail',
        component: () => import('@/views/ApprovalDetail.vue'),
        meta: { requiresAuth: true, roles: ['approver'] },
        props: true,
    },
    {
        path: '/publish',
        name: 'PublishManagement',
        component: () => import('@/views/PublishManagement.vue'),
        meta: { requiresAuth: true, roles: ['publisher'] },
    },
    {
        path: '/admin',
        name: 'AdminSettings',
        component: () => import('@/views/AdminSettings.vue'),
        meta: { requiresAuth: true, roles: ['admin'] },
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/', // 404 重定向到仪表板
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 导航守卫
router.beforeEach((to, from, next) => {
    const isAuthenticated = store.getters.isAuthenticated;
    const userRole = store.state.auth.user?.role || '';

    // 检查是否需要认证
    if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login');
        return;
    }

    // 检查角色权限
    if (to.meta.roles && !to.meta.roles.includes(userRole)) {
        next('/'); // 无权限时重定向到仪表板
        return;
    }

    // 登录用户访问登录页面时，重定向到仪表板
    if (to.path === '/login' && isAuthenticated) {
        next('/');
        return;
    }

    next();
});

export default router;