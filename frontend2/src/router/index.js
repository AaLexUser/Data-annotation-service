import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import AdminPage from '@/components/AdminPage.vue';
import AssessorPage from '@/components/AssessorPage.vue';
import { useAuthStore } from '@/stores/auth';

const routes = [
  {
    path: '/',
    redirect: to => {
      const authStore = useAuthStore();
      if (!authStore.isAuthenticated) return '/login';
      return authStore.user?.role === 'ADMIN' ? '/admin' : '/assessor';
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/components/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/components/Register.vue')
  },
  {
    path: '/assessor',
    name: 'Assessor',
    component: () => import('@/components/AssessorPage.vue'),
    meta: {
      requiresAuth: true,
      roles: ['ADMIN', 'ASSESSOR']
    }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/components/AdminPage.vue'),
    meta: {
      requiresAuth: true,
      roles: ['ADMIN']
    }
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
  } else if (to.meta.role && authStore.role !== to.meta.role) {
      next('/login'); // Перенаправляем, если нет прав доступа
  } else {
      next();
  }
});

export default router;