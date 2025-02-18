<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <h2>Assign Batch to Assessors</h2>
      
      <div class="assessor-list">
        <div v-if="loading" class="loading">Loading assessors...</div>
        <div v-else>
          <div class="search-box">
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="Search assessors..."
              @input="filterAssessors"
            />
          </div>
          
          <div class="assessors">
            <div 
              v-for="assessor in filteredAssessors" 
              :key="assessor.id"
              class="assessor-item"
            >
              <label>
                <input 
                  type="checkbox" 
                  :value="assessor.id"
                  v-model="selectedAssessors"
                />
                {{ assessor.login }}
              </label>
            </div>
          </div>
        </div>
      </div>

      <div class="actions">
        <button 
          class="btn-assign" 
          @click="assignBatch" 
          :disabled="selectedAssessors.length === 0"
        >
          Assign to Selected Assessors
        </button>
        <button class="btn-cancel" @click="$emit('close')">Cancel</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const props = defineProps({
  batchId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['close', 'assigned']);

const assessors = ref([]);
const filteredAssessors = ref([]);
const selectedAssessors = ref([]);
const searchQuery = ref('');
const loading = ref(true);

const fetchAssessors = async () => {
  try {
    const response = await axios.get('/api/v1/admin/assessor/all', { withCredentials: true });
    assessors.value = response.data;
    filteredAssessors.value = response.data;
    loading.value = false;
  } catch (error) {
    console.error('Error fetching assessors:', error);
    loading.value = false;
  }
};

const filterAssessors = () => {
  if (!searchQuery.value) {
    filteredAssessors.value = assessors.value;
    return;
  }
  
  const query = searchQuery.value.toLowerCase();
  filteredAssessors.value = assessors.value.filter(assessor => 
    assessor.login.toLowerCase().includes(query)
  );
};

const assignBatch = async () => {
  try {
    await axios.post('/api/v1/admin/batch/assign', {
      batchId: props.batchId,
      assessorIds: selectedAssessors.value
    }, { withCredentials: true });
    emit('assigned');
    emit('close');
  } catch (error) {
    console.error('Error assigning batch:', error);
    // Here you might want to show an error message to the user
  }
};

onMounted(() => {
  fetchAssessors();
});
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
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.search-box {
  margin-bottom: 1rem;
}

.search-box input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.assessor-list {
  margin-bottom: 1.5rem;
}

.assessors {
  max-height: 300px;
  overflow-y: auto;
}

.assessor-item {
  padding: 0.5rem;
  border-bottom: 1px solid #eee;
}

.assessor-item:last-child {
  border-bottom: none;
}

.assessor-item label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

button {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  border: none;
}

.btn-assign {
  background-color: #4CAF50;
  color: white;
}

.btn-assign:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.btn-cancel {
  background-color: #f44336;
  color: white;
}

.loading {
  text-align: center;
  padding: 1rem;
  color: #666;
}
</style> 