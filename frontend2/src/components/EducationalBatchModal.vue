<!-- EducationalBatchModal.vue -->
<template>
  <div class="modal-overlay" v-if="isOpen">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Set Educational Batch</h2>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="modal-body">
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <div class="correct-answers-section">
          <h3>Correct Answers <span class="required">*</span></h3>
          <div v-for="task in tasks" :key="task.id" class="task-answer">
            <div class="task-header">
              <h4>Task #{{ task.id }}</h4>
              <button 
                class="preview-btn"
                @click="togglePreview(task.id)"
              >
                {{ previewTasks[task.id] ? 'Hide Task' : 'Show Task' }}
              </button>
            </div>

            <div v-if="previewTasks[task.id]" class="preview-section">
              <h5>Task Content:</h5>
              <div class="task-content">
                <div v-if="getTaskData(task).isSingleColumn">
                  <!-- Single column display -->
                  <div class="single-col" v-for="pair in getTaskData(task).pairs" :key="pair.keyBase">
                    <template v-if="pair.keyBase.toLowerCase().includes('photo')">
                      <div class="single-photo">
                        <img :src="pair.value1" :alt="pair.keyBase" class="photo-img" />
                      </div>
                    </template>
                    <template v-else>
                      <div class="single-text">
                        <strong>{{ pair.key1 }}:</strong> {{ pair.value1 }}
                      </div>
                    </template>
                  </div>
                </div>
                <div v-else>
                  <!-- Paired elements display -->
                  <div class="pair-row" v-for="pair in getTaskData(task).pairs" :key="pair.keyBase">
                    <template v-if="pair.keyBase.toLowerCase().includes('photo')">
                      <div class="pair-col">
                        <img :src="pair.value1" alt="photo1" class="photo-img" />
                      </div>
                      <div class="pair-col">
                        <img :src="pair.value2" alt="photo2" class="photo-img" />
                      </div>
                    </template>
                    <template v-else>
                      <div class="pair-col">
                        <strong>{{ pair.key1 }}:</strong> {{ pair.value1 }}
                      </div>
                      <div class="pair-col">
                        <strong>{{ pair.key2 }}:</strong> {{ pair.value2 }}
                      </div>
                    </template>
                  </div>
                </div>
              </div>
            </div>

            <div class="markup-fields">
              <template v-for="(type, label) in markup.elements" :key="label">
                <div class="field-group">
                  <label>{{ label }}</label>
                  <select 
                    v-model="correctAnswers[task.id][label]"
                    :class="{ 'error': isSubmitted && !correctAnswers[task.id][label] && hasError(task.id) }"
                  >
                    <option value="">Not selected</option>
                    <option v-if="type === 'checkbox'" value="checked">Checked</option>
                    <option v-if="type === 'radio'" value="selected">Selected</option>
                  </select>
                  <span v-if="isSubmitted && !correctAnswers[task.id][label] && hasError(task.id)" class="error-text">
                    At least one option must be selected
                  </span>
                </div>
              </template>
            </div>
          </div>
        </div>

        <div class="actions">
          <button 
            class="submit-btn" 
            @click="handleSubmit"
            :disabled="isLoading"
          >
            <span v-if="isLoading" class="loader"></span>
            <span v-else>Save Educational Batch</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  batchId: {
    type: Number,
    required: true
  },
  markup: {
    type: Object,
    required: true
  },
  tasks: {
    type: Array,
    required: true
  }
});

const emit = defineEmits(['close', 'batch-set']);

const correctAnswers = ref({});
const error = ref('');
const isLoading = ref(false);
const isSubmitted = ref(false);
const previewTasks = ref({});

onMounted(() => {
  // Initialize correctAnswers structure for each task
  props.tasks.forEach(task => {
    correctAnswers.value[task.id] = {};
    Object.keys(props.markup.elements).forEach(label => {
      correctAnswers.value[task.id][label] = '';
    });
  });
});

function hasError(taskId) {
  const taskAnswers = correctAnswers.value[taskId];
  return !Object.values(taskAnswers).some(value => value === 'checked' || value === 'selected');
}

function validateForm() {
  isSubmitted.value = true;

  // Check if all tasks have at least one selection
  for (const task of props.tasks) {
    if (hasError(task.id)) {
      error.value = `Task #${task.id} requires at least one selection`;
      return false;
    }
  }

  return true;
}

async function handleSubmit() {
  error.value = '';
  
  if (!validateForm()) {
    return;
  }

  isLoading.value = true;
  
  try {
    const payload = {
      batchId: props.batchId,
      correctAnswers: Object.entries(correctAnswers.value).reduce((acc, [taskId, answers]) => {
        // Convert empty strings to null or remove them
        const cleanAnswers = Object.fromEntries(
          Object.entries(answers).filter(([_, value]) => value !== '')
        );
        acc[taskId] = JSON.stringify(cleanAnswers);
        return acc;
      }, {})
    };

    console.log('Submitting payload:', payload);

    await axios.post('/api/v1/educational/set-batch', payload, {
      withCredentials: true
    });

    emit('batch-set');
    emit('close');
  } catch (err) {
    console.error('Error setting educational batch:', err);
    error.value = err.response?.data || 'Failed to set educational batch. Please try again.';
  } finally {
    isLoading.value = false;
  }
}

function togglePreview(taskId) {
  previewTasks.value[taskId] = !previewTasks.value[taskId];
}

function getTaskData(task) {
  if (!task?.rowFromBatch) return { isSingleColumn: false, pairs: [] };
  
  const entries = Object.entries(task.rowFromBatch);
  const pairsMap = {};
  const regex = /^(.*?)([12])$/;
  let isSingleColumn = true;

  entries.forEach(([key, value]) => {
    const match = key.match(regex);
    if (match) {
      isSingleColumn = false;
      const base = match[1];
      const suffix = match[2];

      if (!pairsMap[base]) {
        pairsMap[base] = {
          keyBase: base,
          key1: '',
          value1: '',
          key2: '',
          value2: ''
        };
      }
      if (suffix === '1') {
        pairsMap[base].key1 = key;
        pairsMap[base].value1 = value;
      } else {
        pairsMap[base].key2 = key;
        pairsMap[base].value2 = value;
      }
    } else {
      pairsMap[key] = {
        keyBase: key,
        key1: key,
        value1: value,
        key2: null,
        value2: null
      };
    }
  });

  return {
    isSingleColumn,
    pairs: Object.values(pairsMap)
  };
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  resize: vertical;
}

.task-answer {
  margin-bottom: 2rem;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.preview-btn {
  background-color: #f1f5f9;
  color: #475569;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s ease;
}

.preview-btn:hover {
  background-color: #e2e8f0;
  color: #1e293b;
}

.preview-section {
  margin: 1rem 0;
  padding: 1rem;
  background-color: #f8fafc;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
}

.preview-section h5 {
  margin: 0 0 0.5rem 0;
  color: #64748b;
  font-size: 0.875rem;
}

.task-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
}

.pair-row {
  display: flex;
  margin-bottom: 1rem;
  gap: 1rem;
}

.pair-col {
  flex: 1;
}

.single-col {
  margin-bottom: 1rem;
}

.photo-img {
  max-width: 200px;
  height: 200px;
  object-fit: cover;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
}

.single-text,
.pair-col {
  padding: 0.5rem;
  background-color: #f8fafc;
  border-radius: 4px;
}

.single-text strong,
.pair-col strong {
  color: #64748b;
  margin-right: 0.5rem;
}

.field-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 0.5rem;
}

.field-group label {
  margin-bottom: 0.5rem;
  font-size: 1.1rem;
  font-weight: 500;
  color: #000;
}

select {
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background-color: white;
  font-size: 1rem;
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1em;
  padding-right: 2.5rem;
}

select:hover {
  border-color: #cbd5e1;
}

select:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

select.error {
  border-color: #dc2626;
  border-width: 1px;
}

select.error:focus {
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.error-text {
  color: #dc2626;
  font-size: 0.875rem;
  margin-top: 0.375rem;
}

.markup-fields {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.actions {
  margin-top: 2rem;
  text-align: right;
}

.submit-btn {
  background-color: #4f46e5;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  border: none;
  cursor: pointer;
}

.submit-btn:hover {
  background-color: #4338ca;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #64748b;
}

.close-btn:hover {
  color: #475569;
}

.error-message {
  background-color: #fef2f2;
  color: #dc2626;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
  border: 1px solid #fee2e2;
}

.required {
  color: #dc2626;
}

.loader {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid #ffffff;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.submit-btn:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
}
</style> 