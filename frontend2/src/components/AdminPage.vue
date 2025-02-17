<template>
  <div class="container">
    <h1>Админ-панель</h1>
    <p>Вы вошли как <strong>Администратор</strong></p>
    <button @click="logout">Выйти</button>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const logout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.container {
  text-align: center;
}
</style>
