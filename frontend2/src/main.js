import { createApp } from 'vue';
import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import App from './App.vue';
import router from './router';
import axios from 'axios';

// Configure axios defaults
axios.defaults.baseURL = import.meta.env.VITE_API_URL || '/';
axios.defaults.withCredentials = true;

// Create Pinia instance
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// Create and mount app
const app = createApp(App);
app.use(pinia);
app.use(router);
app.mount('#app');