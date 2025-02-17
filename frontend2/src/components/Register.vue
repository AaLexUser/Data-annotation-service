<template>
  <div class="container">
    <h2>Регистрация</h2>
    <form @submit.prevent="register">
      <input v-model="username" type="text" placeholder="Имя пользователя" required />
      <input v-model="password" type="password" placeholder="Пароль" required />
      <button type="submit">Зарегистрироваться</button>
    </form>
    <p>Уже есть аккаунт? <router-link to="/login">Войти</router-link></p>
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

const register = async () => {
  try {
    await authStore.register({ username: username.value, password: password.value });
    router.push('/dashboard');
  } catch (error) {
    alert('Ошибка регистрации');
  }
};
</script>
