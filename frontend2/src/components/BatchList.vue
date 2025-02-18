<template>
  <div class="batch-list">
    <div class="batch-header">
      <h2>Available Batches</h2>
      <div v-if="isAdmin" class="batch-actions">
        <button @click="showUploadModal = true" class="btn-upload">
          Upload New Batch
        </button>
      </div>
    </div>

    <div class="batches">
      <div v-for="batch in batches" :key="batch.id" class="batch-item">
        <div class="batch-info" @click="$emit('batchSelected', batch)">
          <h3>{{ batch.name }}</h3>
          <p>Uploaded: {{ formatDate(batch.uploadedAt) }}</p>
          <!-- Индикатор статуса -->
          <div class="batch-status">
            <span class="status-dot" :class="getStatusClass(batch.id)"></span>
            <span>{{ batchStatuses[batch.id] || 'Загрузка...' }}</span>
          </div>
        </div>
          <div v-if="batch.isEducational && educationalStats[batch.id]" class="educational-stats">
            <p class="stats-item">
              <span class="stats-label">Success Rate:</span>
              <span class="stats-value">{{ formatPercent(educationalStats[batch.id].successRate) }}%</span>
            </p>
            <p class="stats-item">
              <span class="stats-label">Total Attempts:</span>
              <span class="stats-value">{{ educationalStats[batch.id].totalAttempts }}</span>
            </p>
            <p class="stats-item">
              <span class="stats-label">Correct Answers:</span>
              <span class="stats-value">{{ educationalStats[batch.id].correctAttempts }}</span>
            </p>
            <p class="stats-item">
              <span class="stats-label">Unique Assessors:</span>
              <span class="stats-value">{{ educationalStats[batch.id].uniqueAssessors }}</span>
            </p>
          </div>
        </div>
        <div v-if="isAdmin" class="batch-admin-actions">
          <button @click.stop="openAssignModal(batch)" class="btn-assign">
            Assign Assessors
          </button>
        </div>
      </div>
    </div>

    <!-- Upload Modal -->
    <BatchUploadModal 
      v-if="showUploadModal" 
      @close="showUploadModal = false"
      @uploaded="handleBatchUploaded"
    />

    <!-- Assignment Modal -->
    <BatchAssignmentModal
      v-if="showAssignModal"
      :batchId="selectedBatchId"
      @close="showAssignModal = false"
      @assigned="handleBatchAssigned"
    />
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useAuthStore } from '@/stores/auth';
import BatchUploadModal from './BatchUploadModal.vue';
import BatchAssignmentModal from './BatchAssignmentModal.vue';
import axios from 'axios';

/**
 * Пропсы, которые принимает компонент:
 * - batches: массив объектов батчей
 */
const props = defineProps({
  batches: {
    type: Array,
    required: true
  }
});

/**
 * Эмитим событие "batchSelected", когда пользователь кликает по батчу.
 */
const emit = defineEmits(['batchSelected']);

const authStore = useAuthStore();
const isAdmin = computed(() => authStore.role === 'ADMIN');

const showUploadModal = ref(false);
const showAssignModal = ref(false);
const selectedBatchId = ref(null);
const batchStatuses = ref({});

const educationalStats = ref({});

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString();
};
watch(() => props.batches, (newBatches) => {
  console.log("📦 Обновленные батчи:", newBatches);
  newBatches.forEach(batch => fetchBatchStatus(batch.id));
}, { immediate: true });

onMounted(() => {
  props.batches.forEach(batch => {
    fetchBatchStatus(batch.id);
  });
});
const formatPercent = (value) => {
  return value.toFixed(1);
};

const openAssignModal = (batch) => {
  selectedBatchId.value = batch.id;
  showAssignModal.value = true;
};
const fetchBatchStatus = async (batchId) => {
  try {
    const response = await axios.get(`/api/v1/batch/status?batchId=${batchId}`, { withCredentials: true });
    batchStatuses.value[batchId] = response.data; // Например, "active" или "inactive"
  } catch (error) {
    console.error(`Ошибка при получении статуса для батча ${batchId}:`, error);
    batchStatuses.value[batchId] = 'unknown'; // Если ошибка
  }
};

const handleBatchUploaded = () => {
  showUploadModal.value = false;
  // Emit event to refresh batch list
  emit('batchUploaded');
};

const handleBatchAssigned = () => {
  // You might want to refresh the batch list or update UI
  emit('batchAssigned');
};
const getStatusClass = (batchId) => {
  const status = batchStatuses.value[batchId];
  if (status === 'active') return 'status-active';
  if (status === 'inactive') return 'status-inactive';
  return 'status-unknown'; // Если статус неизвестен
};

const fetchEducationalStats = async (batch) => {
  if (!batch.isEducational) return;
  
  try {
    const response = await axios.get(`/api/v1/stats/educational`, {
      params: { batchId: batch.id },
      withCredentials: true
    });
    educationalStats.value[batch.id] = response.data;
  } catch (error) {
    console.error(`Error fetching educational stats for batch ${batch.id}:`, error);
  }
};

// Watch for changes in batches prop
watch(() => props.batches, async (newBatches) => {
  for (const batch of newBatches) {
    if (batch.isEducational) {
      await fetchEducationalStats(batch);
    }
  }
}, { immediate: true });
</script>

<style scoped>
.batch-list {
  padding: 1rem;
}

.batch-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.batch-actions {
  display: flex;
  gap: 1rem;
}

.batches {
  display: grid;
  gap: 1rem;
}

.batch-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.batch-item:hover {
  background-color: #e9ecef;
}

.batch-info {
  flex: 1;
}

.batch-info h3 {
  margin: 0;
  color: #2c3e50;
}

.batch-info p {
  margin: 0.5rem 0 0;
  color: #6c757d;
  font-size: 0.9rem;
}

.batch-admin-actions {
  display: flex;
  gap: 0.5rem;
}

button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.btn-upload {
  background-color: #4CAF50;
  color: white;
}

.btn-upload:hover {
  background-color: #45a049;
}

.btn-assign {
  background-color: #2196F3;
  color: white;
}

.btn-assign:hover {
  background-color: #1e88e5;
}

.batch-status {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin-top: 5px;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 5px;
}

/* Статусы */
.status-active {
  background-color: green;
}

.status-inactive {
  background-color: gray;
}

.status-unknown {
  background-color: orange;
}

.educational-stats {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background-color: #f0fdf4;
  border-radius: 4px;
  border: 1px solid #dcfce7;
}

.stats-item {
  display: flex;
  justify-content: space-between;
  margin: 0.25rem 0;
  font-size: 0.875rem;
  color: #166534;
}

.stats-label {
  font-weight: 500;
}

.stats-value {
  font-family: monospace;
}
</style>
