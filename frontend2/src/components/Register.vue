<template>
  <div class="register-container">
    <div class="register-card">
      <h1 class="title">Регистрация</h1>
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="username">Логин</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            required
            placeholder="Введите логин"
          />
        </div>

        <div class="form-group">
          <label for="password">Пароль</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            required
            placeholder="Введите пароль"
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword">Подтверждение пароля</label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            required
            placeholder="Подтвердите пароль"
          />
        </div>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <button type="submit" class="register-btn" :disabled="isLoading">
          {{ isLoading ? 'Регистрация...' : 'Зарегистрироваться' }}
        </button>

        <div class="login-link">
          Уже есть аккаунт? <router-link to="/login">Войти</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const form = ref({
  username: '',
  password: '',
  confirmPassword: ''
});

const error = ref('');
const isLoading = ref(false);

const handleRegister = async () => {
  error.value = '';
  
  if (form.value.password !== form.value.confirmPassword) {
    error.value = 'Пароли не совпадают';
    return;
  }

  isLoading.value = true;
  try {
    const response = await authStore.register({
      username: form.value.username,
      password: form.value.password
    });
    
    router.push(response.role === 'ADMIN' ? '/admin' : '/assessor');
  } catch (err) {
    if (err.response?.status === 409) {
      error.value = 'Пользователь с таким логином уже существует';
    } else {
      error.value = err.response?.data?.message || 'Ошибка при регистрации';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  min-width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
  padding: 1rem;
  margin: 0;
  box-sizing: border-box;
}

.register-card {
  background-color: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  width: 100%;
  max-width: 480px;
  margin: 0 auto;
  box-sizing: border-box;
}

.title {
  color: #2c3e50;
  font-size: 1.75rem;
  font-weight: 600;
  text-align: center;
  margin-bottom: 2rem;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  color: #4b5563;
  font-size: 0.875rem;
  font-weight: 500;
}

.form-group input {
  padding: 0.625rem;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.error-message {
  color: #dc2626;
  font-size: 0.875rem;
  text-align: center;
  padding: 0.5rem;
  background-color: #fee2e2;
  border-radius: 6px;
}

.register-btn {
  background-color: #4f46e5;
  color: white;
  padding: 0.75rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.register-btn:hover:not(:disabled) {
  background-color: #4338ca;
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  font-size: 0.875rem;
  color: #4b5563;
}

.login-link a {
  color: #4f46e5;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}

@media (max-width: 640px) {
  .register-card {
    padding: 1.5rem;
  }

  .title {
    font-size: 1.5rem;
  }
}
</style>
