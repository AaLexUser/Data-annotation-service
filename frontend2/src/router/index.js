import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import AdminPage from '@/components/AdminPage.vue';
import AssessorPage from '@/components/AssessorPage.vue';
import { useAuthStore } from '@/stores/auth';

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    {
        path: '/assessor',
        component: AssessorPage,
        meta: {requiresAuth: true, role: 'ASSESSOR'}
    },
    {
        path: '/admin',
        component: AdminPage,
        meta: { requiresAuth: true, role: 'ADMIN' }
    }

];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next('/login');
    } else if (to.meta.role && authStore.role !== to.meta.role) {
        next('/login'); // Перенаправляем, если нет прав доступа
    } else {
        next();
    }
});

export default router;
