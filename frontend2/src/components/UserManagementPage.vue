<template>
  <AppLayout>
    <div class="user-management">
      <div class="header">
        <h1>Управление пользователями</h1>
        <button class="add-user-btn" @click="openCreateUserModal">
          Создать пользователя
        </button>
      </div>

      <div class="filter-section">
        <input 
          v-model="searchQuery"
          type="text"
          placeholder="Поиск по логину или роли"
          class="filter-input"
        />
      </div>

      <table class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Логин</th>
            <th>Роль</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.login }}</td>
            <td>
              <span :class="['role-badge', user.role.toLowerCase()]">
                {{ user.role }}
              </span>
            </td>
            <td class="actions">
              <button 
                class="edit-btn"
                @click="openEditUserModal(user)"
                title="Редактировать"
              >
                ✏️
              </button>
              <button 
                class="delete-btn"
                @click="confirmDelete(user)"
                title="Удалить"
                :disabled="user.role === 'ADMIN'"
              >
                🗑️
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Create/Edit User Modal -->
      <div v-if="isUserModalOpen" class="modal-overlay">
        <div class="modal-content">
          <div class="modal-header">
            <h2>{{ editingUser ? 'Редактировать пользователя' : 'Создать пользователя' }}</h2>
            <button class="close-btn" @click="closeUserModal">&times;</button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleSubmit">
              <div class="form-group">
                <label for="login">Логин</label>
                <input 
                  id="login"
                  v-model="userForm.login"
                  type="text"
                  required
                  :disabled="editingUser"
                />
              </div>
              
              <div class="form-group">
                <label for="password">Пароль</label>
                <input 
                  id="password"
                  v-model="userForm.password"
                  type="password"
                  :required="!editingUser"
                  :placeholder="editingUser ? 'Оставьте пустым, чтобы не менять' : ''"
                />
              </div>

              <div class="form-group">
                <label for="role">Роль</label>
                <select 
                  id="role"
                  v-model="userForm.role"
                  required
                >
                  <option value="ASSESSOR">Асессор</option>
                  <option value="ADMIN">Администратор</option>
                </select>
              </div>

              <div class="form-actions">
                <button 
                  type="submit" 
                  class="submit-btn"
                  :disabled="isSubmitting"
                >
                  {{ isSubmitting ? 'Сохранение...' : 'Сохранить' }}
                </button>
                <button 
                  type="button"
                  class="cancel-btn"
                  @click="closeUserModal"
                >
                  Отмена
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Delete Confirmation Modal -->
      <div v-if="showDeleteConfirm" class="modal-overlay">
        <div class="modal-content">
          <div class="modal-header">
            <h2>Подтверждение удаления</h2>
            <button class="close-btn" @click="showDeleteConfirm = false">&times;</button>
          </div>
          <div class="modal-body">
            <p>Вы уверены, что хотите удалить пользователя "{{ userToDelete?.login }}"?</p>
            <div class="form-actions">
              <button 
                class="delete-confirm-btn"
                @click="deleteUser"
                :disabled="isDeleting"
              >
                {{ isDeleting ? 'Удаление...' : 'Удалить' }}
              </button>
              <button 
                class="cancel-btn"
                @click="showDeleteConfirm = false"
              >
                Отмена
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import AppLayout from './AppLayout.vue';

// State
const users = ref([]);
const searchQuery = ref('');
const isUserModalOpen = ref(false);
const showDeleteConfirm = ref(false);
const isSubmitting = ref(false);
const isDeleting = ref(false);
const editingUser = ref(null);
const userToDelete = ref(null);
const userForm = ref({
  login: '',
  password: '',
  role: 'ASSESSOR'
});

// Computed
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value;
  const query = searchQuery.value.toLowerCase();
  return users.value.filter(user => 
    user.login.toLowerCase().includes(query) ||
    user.role.toLowerCase().includes(query)
  );
});

// Methods
const fetchUsers = async () => {
  try {
    const response = await axios.get('/api/v1/user/all', { withCredentials: true });
    users.value = response.data;
  } catch (error) {
    console.error('Error fetching users:', error);
  }
};

const openCreateUserModal = () => {
  editingUser.value = null;
  userForm.value = {
    login: '',
    password: '',
    role: 'ASSESSOR'
  };
  isUserModalOpen.value = true;
};

const openEditUserModal = (user) => {
  editingUser.value = user;
  userForm.value = {
    login: user.login,
    password: '',
    role: user.role
  };
  isUserModalOpen.value = true;
};

const closeUserModal = () => {
  isUserModalOpen.value = false;
  editingUser.value = null;
  userForm.value = {
    login: '',
    password: '',
    role: 'ASSESSOR'
  };
};

const handleSubmit = async () => {
  isSubmitting.value = true;
  try {
    if (editingUser.value) {
      await axios.put(`/api/v1/user/${editingUser.value.id}`, userForm.value, {
        withCredentials: true
      });
    } else {
      await axios.post('/api/v1/user/create', userForm.value, {
        withCredentials: true
      });
    }
    await fetchUsers();
    closeUserModal();
  } catch (error) {
    console.error('Error saving user:', error);
  } finally {
    isSubmitting.value = false;
  }
};

const confirmDelete = (user) => {
  userToDelete.value = user;
  showDeleteConfirm.value = true;
};

const deleteUser = async () => {
  if (!userToDelete.value) return;
  
  isDeleting.value = true;
  try {
    await axios.delete(`/api/v1/user/${userToDelete.value.id}`, {
      withCredentials: true
    });
    await fetchUsers();
    showDeleteConfirm.value = false;
  } catch (error) {
    console.error('Error deleting user:', error);
  } finally {
    isDeleting.value = false;
    userToDelete.value = null;
  }
};

// Lifecycle
onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.user-management {
  padding: 2rem;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eef2f7;
}

.header h1 {
  color: #2c3e50;
  font-size: 1.75rem;
  font-weight: 600;
  margin: 0;
}

.add-user-btn {
  padding: 8px 16px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.add-user-btn:hover {
  background-color: #4338ca;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.95rem;
  color: #334155;
  transition: all 0.2s ease;
  background-color: #f8fafc;
}

.filter-input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
  background-color: white;
}

.user-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.user-table th,
.user-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

.user-table th {
  background-color: #f8fafc;
  font-weight: 600;
  font-size: 0.85rem;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.user-table tr:hover {
  background-color: #f8fafc;
}

.user-table tr:last-child td {
  border-bottom: none;
}

.role-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 500;
}

.role-badge.admin {
  background-color: #fee2e2;
  color: #ef4444;
}

.role-badge.assessor {
  background-color: #e0e7ff;
  color: #4f46e5;
}

.actions {
  display: flex;
  gap: 8px;
}

.edit-btn,
.delete-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  border-radius: 6px;
  transition: all 0.2s ease;
  font-size: 1rem;
}

.edit-btn:hover {
  background-color: #e0e7ff;
  color: #4f46e5;
}

.delete-btn:hover:not(:disabled) {
  background-color: #fee2e2;
  color: #ef4444;
}

.delete-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(15, 23, 42, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #eef2f7;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  color: #2c3e50;
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0;
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #64748b;
  font-size: 0.9rem;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.95rem;
  color: #334155;
  transition: all 0.2s ease;
  background-color: white;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.form-group input:disabled {
  background-color: #f8fafc;
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.submit-btn,
.delete-confirm-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.submit-btn {
  background-color: #4f46e5;
  color: white;
}

.submit-btn:hover:not(:disabled) {
  background-color: #4338ca;
}

.delete-confirm-btn {
  background-color: #ef4444;
  color: white;
}

.delete-confirm-btn:hover:not(:disabled) {
  background-color: #dc2626;
}

.cancel-btn {
  padding: 10px 20px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  color: #64748b;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background-color: #f1f5f9;
  border-color: #94a3b8;
  color: #334155;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #94a3b8;
  padding: 4px;
  line-height: 1;
  transition: color 0.2s ease;
}

.close-btn:hover {
  color: #64748b;
}

@media (max-width: 768px) {
  .user-management {
    padding: 16px;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .add-user-btn {
    width: 100%;
  }

  .user-table {
    display: block;
    overflow-x: auto;
  }

  .user-table th,
  .user-table td {
    padding: 10px 12px;
    font-size: 0.9rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .submit-btn,
  .cancel-btn,
  .delete-confirm-btn {
    width: 100%;
  }
}
</style> 