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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import BatchUploadModal from './BatchUploadModal.vue';
import BatchAssignmentModal from './BatchAssignmentModal.vue';

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

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString();
};

const openAssignModal = (batch) => {
  selectedBatchId.value = batch.id;
  showAssignModal.value = true;
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
</style>
