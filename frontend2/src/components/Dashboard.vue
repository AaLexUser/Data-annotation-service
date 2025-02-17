<template>
  <div class="container">
    <h2>Личный кабинет</h2>
    <p>Вы вошли в систему!</p>
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
