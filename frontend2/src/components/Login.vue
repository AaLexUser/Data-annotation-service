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
    // Перенаправление в зависимости от роли
    if (authStore.role === 'ADMIN') {
      router.push('/admin');
    } else if (authStore.role === 'ASSESSOR') {
      router.push('/assessor');
    } else {
      router.push('/login'); // fallback
    }
  } catch (error) {
    alert('Ошибка регистрации');
  }
};
</script>
