import { defineStore } from 'pinia';
import axios from 'axios';

axios.defaults.baseURL = 'http://localhost:8000';
axios.defaults.withCredentials = true;
export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null,
        isAuthenticated: false,
    }),

    actions: {
        async login(credentials) {
            try {
                const response = await axios.post('/api/v1/user/login', credentials, { withCredentials: true });
                this.user = response.data;
                this.isAuthenticated = true;
            } catch (error) {
                console.error('Ошибка входа:', error);
                throw error;
            }
        },

        async register(credentials) {
            try {
                const response = await axios.post('/api/v1/user/create', credentials, { withCredentials: true });
                this.user = response.data;
                this.isAuthenticated = true;
            } catch (error) {
                console.error('Ошибка регистрации:', error);
                throw error;
            }
        },

        logout() {
            document.cookie = 'access_token=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 UTC;';
            this.user = null;
            this.isAuthenticated = false;
        },
    },
});
