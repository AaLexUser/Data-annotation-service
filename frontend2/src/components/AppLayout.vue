<template>
  <div class="app-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h1 class="sidebar-title">DeepPick</h1>
      </div>
      
      <nav class="nav-menu">
        <router-link 
          v-if="userRole === 'ADMIN'"
          to="/admin" 
          class="nav-item" 
          active-class="active"
        >
          <span class="nav-icon">📊</span>
          Администратор
        </router-link>
        <router-link 
          v-if="userRole === 'ASSESSOR' || userRole === 'ADMIN'"
          to="/assessor" 
          class="nav-item" 
          active-class="active"
        >
          <span class="nav-icon">✍️</span>
          Асессор
        </router-link>
      </nav>

      <div class="user-section">
        <div class="user-info">
          <div class="avatar">
            {{ userInitials }}
          </div>
          <div class="user-details">
            <div class="user-name">{{ userName }}</div>
            <div class="user-role">{{ userRole }}</div>
          </div>
        </div>
        <button class="logout-btn" @click="handleLogout">
          <span class="logout-icon">🚪</span>
          Выйти
        </button>
      </div>
    </aside>

    <div class="main-content">
      <main class="content">
        <slot></slot>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const userName = computed(() => authStore.user?.name || 'Пользователь');
const userRole = computed(() => authStore.user?.role || 'Гость');
const userInitials = computed(() => {
  const name = userName.value;
  return name
    .split(' ')
    .map(word => word[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);
});

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 280px;
  background-color: white;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
}

.sidebar-header {
  padding: 24px;
  border-bottom: 1px solid #e2e8f0;
}

.sidebar-title {
  color: #4f46e5;
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.5px;
}

.nav-menu {
  padding: 24px 16px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  color: #64748b;
  text-decoration: none;
  border-radius: 8px;
  margin-bottom: 8px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background-color: #f1f5f9;
  color: #334155;
}

.nav-item.active {
  background-color: #eef2ff;
  color: #4f46e5;
  font-weight: 500;
}

.nav-icon {
  margin-right: 12px;
  font-size: 1.2rem;
}

.user-section {
  padding: 16px;
  border-top: 1px solid #e2e8f0;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  background-color: #eef2ff;
  color: #4f46e5;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.9rem;
  margin-right: 12px;
}

.user-details {
  flex: 1;
}

.user-name {
  color: #334155;
  font-weight: 500;
  font-size: 0.95rem;
}

.user-role {
  color: #64748b;
  font-size: 0.85rem;
}

.logout-btn {
  width: 100%;
  padding: 12px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.logout-btn:hover {
  background-color: #fee2e2;
  border-color: #fecaca;
  color: #ef4444;
}

.logout-icon {
  margin-right: 8px;
}

.main-content {
  flex: 1;
  margin-left: 280px;
}

.main-header {
  height: 72px;
  background-color: white;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  padding: 0 32px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.company-name {
  color: #2c3e50;
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }

  .sidebar.open {
    transform: translateX(0);
  }

  .main-content {
    margin-left: 0;
  }
}
</style> 