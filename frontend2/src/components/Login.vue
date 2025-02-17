<template>
  <div class="container">
    <h2>Вход</h2>
    <form @submit.prevent="login">
      <input v-model="username" type="text" placeholder="Имя пользователя" required />
      <input v-model="password" type="password" placeholder="Пароль" required />
      <button type="submit">Войти</button>
    </form>
    <p>Нет аккаунта? <router-link to="/register">Зарегистрируйтесь</router-link></p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const authStore = useAuthStore();
const router = useRouter();

const login = async () => {
  try {
    await authStore.login({ username: username.value, password: password.value });
    router.push('/dashboard');
  } catch (error) {
    alert('Ошибка входа');
  }
};
</script>
