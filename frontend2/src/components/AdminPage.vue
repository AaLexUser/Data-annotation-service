<template>
  <div class="admin-container">
    <div class="header">
      <h1>Таблица со списком батчей</h1>
      <div class="header-buttons">
        <button class="add-markup-btn" @click="openMarkupModal">Создать разметку</button>
        <button class="add-batch-btn" @click="openUploadModal">Добавить батч</button>
      </div>
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
          <th>Действия</th>
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
          <td>
            <button 
              class="view-markup-btn"
              @click="viewMarkup(batch.id)"
              title="Просмотреть разметку"
            >
              <span class="icon">👁</span>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <BatchUploadModal 
      :is-open="isUploadModalOpen"
      @close="closeUploadModal"
      @batch-uploaded="handleBatchUploaded"
    />

    <MarkupCreator
      v-if="isMarkupModalOpen"
      @close="closeMarkupModal"
    />

    <div v-if="isViewMarkupModalOpen" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h2>Разметка для батча</h2>
          <button class="close-btn" @click="closeViewMarkupModal">&times;</button>
        </div>
        <div class="modal-body">
          <div v-if="currentMarkup">
            <div class="markup-group">
              <h3>Radio Buttons</h3>
              <ul class="markup-list">
                <li v-for="(type, value) in radioItems" :key="value">
                  {{ value }}
                </li>
              </ul>
            </div>
            <div class="markup-group">
              <h3>Checkboxes</h3>
              <ul class="markup-list">
                <li v-for="(type, value) in checkboxItems" :key="value">
                  {{ value }}
                </li>
              </ul>
            </div>
          </div>
          <div v-else class="no-markup">
            Разметка не найдена
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import axios from 'axios';
import BatchUploadModal from './BatchUploadModal.vue';
import MarkupCreator from './MarkupCreator.vue';

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

function useMarkupModal() {
  const isMarkupModalOpen = ref(false);
  const isViewMarkupModalOpen = ref(false);
  const currentMarkup = ref(null);
  
  const openMarkupModal = () => {
    isMarkupModalOpen.value = true;
  };

  const closeMarkupModal = () => {
    isMarkupModalOpen.value = false;
  };

  const openViewMarkupModal = () => {
    isViewMarkupModalOpen.value = true;
  };

  const closeViewMarkupModal = () => {
    isViewMarkupModalOpen.value = false;
    currentMarkup.value = null;
  };

  const viewMarkup = async (batchId) => {
    try {
      const response = await axios.get(`/api/v1/markup/byBatchId?id=${batchId}`, {
        withCredentials: true
      });
      currentMarkup.value = response.data;
      openViewMarkupModal();
    } catch (error) {
      console.error('Error fetching markup:', error);
    }
  };

  const radioItems = computed(() => {
    if (!currentMarkup.value?.elements) return {};
    return Object.entries(currentMarkup.value.elements)
      .filter(([_, type]) => type === 'radio')
      .reduce((acc, [value, type]) => {
        acc[value] = type;
        return acc;
      }, {});
  });

  const checkboxItems = computed(() => {
    if (!currentMarkup.value?.elements) return {};
    return Object.entries(currentMarkup.value.elements)
      .filter(([_, type]) => type === 'checkbox')
      .reduce((acc, [value, type]) => {
        acc[value] = type;
        return acc;
      }, {});
  });

  return {
    isMarkupModalOpen,
    isViewMarkupModalOpen,
    currentMarkup,
    radioItems,
    checkboxItems,
    openMarkupModal,
    closeMarkupModal,
    viewMarkup,
    closeViewMarkupModal
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
const { 
  isMarkupModalOpen, 
  isViewMarkupModalOpen,
  currentMarkup,
  radioItems,
  checkboxItems,
  openMarkupModal, 
  closeMarkupModal,
  viewMarkup,
  closeViewMarkupModal
} = useMarkupModal();
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
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
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

.header-buttons {
  display: flex;
  gap: 12px;
}

.add-batch-btn,
.add-markup-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
  font-weight: 500;
}

.add-batch-btn {
  background-color: #4f46e5;
  color: white;
}

.add-markup-btn {
  background-color: #eef2ff;
  color: #4f46e5;
}

.add-batch-btn:hover {
  background-color: #4338ca;
}

.add-markup-btn:hover {
  background-color: #e0e7ff;
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

.filter-input::placeholder {
  color: #94a3b8;
}

.batch-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.batch-table th,
.batch-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

.batch-table th {
  background-color: #f8fafc;
  font-weight: 600;
  font-size: 0.85rem;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.batch-table tr:hover {
  background-color: #f8fafc;
}

.batch-table tr:last-child td {
  border-bottom: none;
}

.batch-table td {
  color: #334155;
  font-size: 0.95rem;
}

.view-markup-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  border-radius: 6px;
  transition: all 0.2s ease;
  color: #64748b;
}

.view-markup-btn:hover {
  background-color: #f1f5f9;
  color: #4f46e5;
}

.icon {
  font-size: 16px;
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
  max-width: 600px;
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

.markup-group {
  margin-bottom: 24px;
}

.markup-group h3 {
  color: #64748b;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 12px;
}

.markup-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 8px;
}

.markup-list li {
  padding: 10px 12px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  color: #334155;
  font-size: 0.95rem;
}

.no-markup {
  text-align: center;
  color: #64748b;
  padding: 32px;
  background-color: #f8fafc;
  border-radius: 8px;
  font-size: 0.95rem;
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
  .admin-container {
    padding: 16px;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-buttons {
    width: 100%;
  }

  .add-batch-btn,
  .add-markup-btn {
    flex: 1;
    text-align: center;
  }

  .batch-table {
    display: block;
    overflow-x: auto;
  }

  .batch-table th,
  .batch-table td {
    padding: 10px 12px;
    font-size: 0.9rem;
  }
}
</style>
