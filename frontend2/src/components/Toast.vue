<template>
  <Teleport to="body">
    <div class="toast-container">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="toast"
          :class="toast.type"
        >
          <div class="toast-content">
            <span class="toast-icon">
              <i v-if="toast.type === 'success'" class="fas fa-check-circle"></i>
              <i v-else-if="toast.type === 'error'" class="fas fa-exclamation-circle"></i>
              <i v-else class="fas fa-info-circle"></i>
            </span>
            <span class="toast-message">{{ toast.message }}</span>
          </div>
          <div class="toast-progress" :style="{ animationDuration: `${toast.duration}ms` }"></div>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue';

const toasts = ref([]);
let toastId = 0;

const addToast = (message, type = 'info', duration = 3000) => {
  const id = toastId++;
  const toast = {
    id,
    message,
    type,
    duration
  };
  
  toasts.value.push(toast);
  
  setTimeout(() => {
    removeToast(id);
  }, duration);
};

const removeToast = (id) => {
  const index = toasts.value.findIndex(t => t.id === id);
  if (index > -1) {
    toasts.value.splice(index, 1);
  }
};

// Make sure to expose the addToast method
defineExpose({
  addToast
});
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.toast {
  min-width: 300px;
  padding: 16px;
  border-radius: 8px;
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
  cursor: pointer;
  animation: slideIn 0.3s ease;
}

.toast-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.toast-icon {
  font-size: 1.25rem;
}

.toast-message {
  color: #1a1a1a;
  font-size: 0.875rem;
}

.toast.success {
  background: #ecfdf5;
  border-left: 4px solid #059669;
}

.toast.error {
  background: #fef2f2;
  border-left: 4px solid #dc2626;
}

.toast.info {
  background: #eff6ff;
  border-left: 4px solid #3b82f6;
}

.toast.success .toast-icon {
  color: #059669;
}

.toast.error .toast-icon {
  color: #dc2626;
}

.toast.info .toast-icon {
  color: #3b82f6;
}

.toast-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: rgba(0, 0, 0, 0.1);
}

.toast-progress::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
  background: currentColor;
  animation: progress linear forwards;
}

.success .toast-progress::after {
  background: #059669;
}

.error .toast-progress::after {
  background: #dc2626;
}

.info .toast-progress::after {
  background: #3b82f6;
}

@keyframes progress {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  transform: translateX(100%);
  opacity: 0;
}

.toast-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style> 