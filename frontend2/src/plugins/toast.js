import { createApp } from 'vue';
import Toast from '../components/Toast.vue';

let toastInstance = null;

const toast = {
  install(app) {
    // Create a new div for mounting the toast
    const toastContainer = document.createElement('div');
    document.body.appendChild(toastContainer);

    // Create a new Vue instance for Toast
    const toastApp = createApp(Toast);
    
    // Mount the toast app and store the instance
    toastInstance = toastApp.mount(toastContainer);

    // Add toast method to the global app config
    const toastMethods = {
      show(message, type = 'info', duration = 3000) {
        if (toastInstance) {
          toastInstance.addToast(message, type, duration);
        }
      },
      success(message, duration = 3000) {
        if (toastInstance) {
          toastInstance.addToast(message, 'success', duration);
        }
      },
      error(message, duration = 3000) {
        if (toastInstance) {
          toastInstance.addToast(message, 'error', duration);
        }
      },
      info(message, duration = 3000) {
        if (toastInstance) {
          toastInstance.addToast(message, 'info', duration);
        }
      }
    };

    // Add to global properties
    app.config.globalProperties.$toast = toastMethods;

    // Add to provide/inject system
    app.provide('toast', toastMethods);
  }
};

export default toast; 