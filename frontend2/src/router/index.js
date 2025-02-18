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
  
  // Allow access to login page
  if (to.path === '/login') {
    if (authStore.isAuthenticated) {
      return next(authStore.user?.role === 'ADMIN' ? '/admin' : '/assessor');
    }
    return next();
  }

  // Check if route requires authentication
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!authStore.isAuthenticated) {
      return next('/login');
    }

    // Check role-based access
    const userRole = authStore.user?.role;
    if (to.meta.roles && !to.meta.roles.includes(userRole)) {
      return next(userRole === 'ADMIN' ? '/admin' : '/assessor');
    }
  }

  next();
});

export default router;