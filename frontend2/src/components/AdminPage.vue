<template>
  <div class="admin-container">
    <div class="header">
      <h1>Таблица со списком батчей</h1>
      <button class="add-batch-btn" @click="openUploadModal">Добавить батч</button>
    </div>

    <div class="filter-section">
      <input 
        v-model="filterType"
        type="text"
        placeholder="Возможность фильтрации по типам разметки"
        class="filter-input"
      />
    </div>

    <table class="batch-table">
      <thead>
        <tr>
          <th>Id Батча</th>
          <th>Title</th>
          <th>Тип разметки</th>
          <th>Количество заданий в батче</th>
          <th>Процент выполнения</th>
          <th>Дата и время загрузки</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="batch in filteredBatches" :key="batch.id">
          <td>{{ batch.id }}</td>
          <td>{{ batch.name }}</td>
          <td>{{ batch.format }}</td>
          <td>{{ batch.taskCount }}</td>
          <td>{{ batch.completionPercentage }}%</td>
          <td>{{ formatDate(batch.uploadedAt) }}</td>
        </tr>
      </tbody>
    </table>

    <BatchUploadModal 
      :is-open="isUploadModalOpen"
      @close="closeUploadModal"
      @batch-uploaded="handleBatchUploaded"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import axios from 'axios';
import BatchUploadModal from './BatchUploadModal.vue';

// Types
const BatchType = {
  id: Number,
  name: String,
  format: String,
  taskCount: Number,
  completionPercentage: Number,
  uploadedAt: String
};

// Composables
function useBatchList() {
  const batches = ref([]);
  const filterType = ref('');

  const filteredBatches = computed(() => {
    if (!filterType.value) return batches.value;
    return batches.value.filter(batch => 
      batch.format.toLowerCase().includes(filterType.value.toLowerCase())
    );
  });

  const fetchBatches = async () => {
    try {
      const response = await axios.get('/api/v1/batch/all', { withCredentials: true });
      batches.value = response.data;
    } catch (error) {
      console.error('Error fetching batches:', error);
    }
  };

  return {
    batches,
    filterType,
    filteredBatches,
    fetchBatches
  };
}

function useAuth() {
  const authStore = useAuthStore();
  const router = useRouter();

  const logout = () => {
    authStore.logout();
    router.push('/login');
  };

  return {
    logout
  };
}

function useUploadModal() {
  const isUploadModalOpen = ref(false);

  const openUploadModal = () => {
    isUploadModalOpen.value = true;
  };

  const closeUploadModal = () => {
    isUploadModalOpen.value = false;
  };

  return {
    isUploadModalOpen,
    openUploadModal,
    closeUploadModal
  };
}

function useFormatting() {
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('ru-RU', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  return {
    formatDate
  };
}

// Component setup
const { batches, filterType, filteredBatches, fetchBatches } = useBatchList();
const { logout } = useAuth();
const { isUploadModalOpen, openUploadModal, closeUploadModal } = useUploadModal();
const { formatDate } = useFormatting();

const handleBatchUploaded = () => {
  fetchBatches();
};

// Lifecycle hooks
onMounted(() => {
  fetchBatches();
});
</script>

<style scoped>
.admin-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.add-batch-btn {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s ease;
}

.add-batch-btn:hover {
  background-color: #45a049;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s ease;
}

.filter-input:focus {
  border-color: #4CAF50;
  outline: none;
}

.batch-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  overflow: hidden;
}

.batch-table th,
.batch-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.batch-table th {
  background-color: #f5f5f5;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.9em;
  color: #666;
}

.batch-table tr:hover {
  background-color: #f9f9f9;
}

.batch-table tr:last-child td {
  border-bottom: none;
}

@media (max-width: 768px) {
  .batch-table {
    display: block;
    overflow-x: auto;
  }
}
</style>
