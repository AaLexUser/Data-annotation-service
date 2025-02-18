import { defineStore } from 'pinia';


import axios from 'axios';

axios.defaults.withCredentials = true;
export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null,
        role: null,
        userId: null,
        isAuthenticated: false,
    }),

    actions: {
        async login(credentials) {
            try {
                const response = await axios.post('/api/v1/user/login', credentials, { withCredentials: true });
                this.user = response.data;
                this.role = response.data.role;
                this.userId = response.data.userId;
                console.log(this.userId);
                this.isAuthenticated = true;
            } catch (error) {
                console.error('Ошибка входа:', error);
                throw error;
            }
        },

        async register(credentials) {
            try {
                const response = await axios.post('/api/v1/user/create', credentials, { withCredentials: true });
                this.user = response.data.username;
                this.role = response.data.role;
                this.userId = response.data.userId;
                this.isAuthenticated = true;
            } catch (error) {
                console.error('Ошибка регистрации:', error);
                throw error;
            }
        },

        logout() {
            document.cookie = 'access_token=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 UTC;';
            this.user = null;
            this.role = null;
            this.id = null
            this.isAuthenticated = false;
        },
    },
    persist: true,
});
