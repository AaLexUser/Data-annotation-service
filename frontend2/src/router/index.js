import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import Dashboard from '@/components/Dashboard.vue';
import { useAuthStore } from '@/stores/auth';

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    {
        path: '/dashboard',
        component: Dashboard,
        meta: { requiresAuth: true },
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next('/login');
    } else {
        next();
    }
});

export default router;
