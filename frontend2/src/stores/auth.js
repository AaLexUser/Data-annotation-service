import { defineStore } from 'pinia';
import axios from 'axios';

axios.defaults.withCredentials = true;

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: {
            name: null,
            role: null,
            id: null
        },
        isAuthenticated: false,
    }),

    getters: {
        isAdmin: (state) => state.user.role === 'ADMIN',
        isAssessor: (state) => state.user.role === 'ASSESSOR',
    },

    actions: {
        setUser(userData) {
            if (!userData) {
                this.logout();
                return;
            }
            this.user = {
                name: userData.name,
                role: userData.role,
                id: userData.id
            };
            this.isAuthenticated = true;
        },

        async login(credentials) {
            try {
                const response = await axios.post('/api/v1/user/login', credentials);
                if (response.data && response.data.username) {
                    this.setUser({
                        name: response.data.username,
                        role: response.data.role,
                        id: response.data.userId
                    });
                    return response.data;
                }
                throw new Error('Invalid response data');
            } catch (error) {
                console.error('Ошибка входа:', error);
                this.logout();
                throw error;
            }
        },

        async register(credentials) {
            try {
                const response = await axios.post('/api/v1/user/create', credentials);
                if (response.data && response.data.username) {
                    this.setUser({
                        name: response.data.username,
                        role: response.data.role,
                        id: response.data.userId
                    });
                    return response.data;
                }
                throw new Error('Invalid response data');
            } catch (error) {
                console.error('Ошибка регистрации:', error);
                this.logout();
                throw error;
            }
        },

        logout() {
            document.cookie = 'access_token=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 UTC;';
            this.user = {
                name: null,
                role: null,
                id: null
            };
            this.isAuthenticated = false;
        },

        initialize() {
            // Check if there's a valid session
            const token = document.cookie.split('; ').find(row => row.startsWith('access_token='));
            if (!token) {
                this.logout();
            }
        }
    },
    persist: {
        enabled: true,
        strategies: [
            {
                key: 'auth',
                storage: localStorage,
                paths: ['user', 'isAuthenticated']
            },
        ],
    },
});