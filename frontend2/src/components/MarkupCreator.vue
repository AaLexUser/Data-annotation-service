<template>
  <div class="markup-creator">
    <div class="header">
      <h2>Создание разметки</h2>
      <button class="close-btn" @click="$emit('close')">&times;</button>
    </div>

    <div class="batch-selector" v-if="!selectedBatch">
      <h3>Выберите батч для разметки</h3>
      <div class="batch-list">
        <div 
          v-for="batch in batches" 
          :key="batch.id"
          class="batch-item"
          @click="selectBatch(batch)"
        >
          <div class="batch-info">
            <span class="batch-name">{{ batch.name }}</span>
            <span class="batch-format">{{ batch.format }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="markup-editor" v-else>
      <div class="selected-batch">
        <h3>Батч: {{ selectedBatch.name }}</h3>
        <button class="change-batch-btn" @click="selectedBatch = null">Изменить батч</button>
      </div>

      <div class="markup-types">
        <div class="type-selector">
          <button 
            :class="['type-btn', { active: currentType === 'radio' }]"
            @click="currentType = 'radio'"
          >
            Radio Buttons
          </button>
          <button 
            :class="['type-btn', { active: currentType === 'checkbox' }]"
            @click="currentType = 'checkbox'"
          >
            Checkboxes
          </button>
        </div>

        <div class="input-group">
          <button 
            class="add-btn"
            @click="addMarkupItem"
            :disabled="!newValue.trim()"
          >
            Добавить
          </button>
          <input 
            v-model="newValue"
            type="text"
            placeholder="Введите значение"
            @keyup.enter="addMarkupItem"
          />
        </div>
      </div>

      <div class="markup-preview">
        <h3>Предпросмотр разметки</h3>
        <div class="markup-items">
          <div 
            v-for="(item, index) in markupItems" 
            :key="index"
            class="markup-item"
          >
            <span class="item-type">{{ item.type }}</span>
            <span class="item-value">{{ item.value }}</span>
            <button class="remove-btn" @click="removeItem(index)">&times;</button>
          </div>
        </div>
      </div>

      <div class="actions">
        <button 
          class="save-btn"
          @click="saveMarkup"
          :disabled="markupItems.length === 0"
        >
          Сохранить разметку
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const emit = defineEmits(['close']);

const batches = ref([]);
const selectedBatch = ref(null);
const currentType = ref('radio');
const newValue = ref('');
const markupItems = ref([]);

const fetchBatches = async () => {
  try {
    const response = await axios.get('/api/v1/batch/all', { withCredentials: true });
    batches.value = response.data;
  } catch (error) {
    console.error('Error fetching batches:', error);
  }
};

const selectBatch = (batch) => {
  selectedBatch.value = batch;
};

const addMarkupItem = () => {
  if (!newValue.value.trim()) return;

  markupItems.value.push({
    type: currentType.value,
    value: newValue.value.trim()
  });

  newValue.value = '';
};

const removeItem = (index) => {
  markupItems.value.splice(index, 1);
};

const saveMarkup = async () => {
  if (markupItems.value.length === 0 || !selectedBatch.value) return;

  const payload = {
    data: markupItems.value,
    batchId: selectedBatch.value.id
  };

  try {
    await axios.post('/api/v1/markup/load', payload, {
      withCredentials: true
    });
    emit('close');
  } catch (error) {
    console.error('Error saving markup:', error);
  }
};

onMounted(() => {
  fetchBatches();
});
</script>

<style scoped>
.markup-creator {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eef2f7;
}

.header h2 {
  color: #2c3e50;
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #94a3b8;
  transition: color 0.2s ease;
  padding: 4px;
  line-height: 1;
}

.close-btn:hover {
  color: #64748b;
}

.batch-selector {
  margin-bottom: 24px;
}

.batch-selector h3 {
  color: #2c3e50;
  font-size: 1.1rem;
  margin-bottom: 16px;
}

.batch-list {
  display: grid;
  gap: 12px;
  margin-top: 12px;
}

.batch-item {
  padding: 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  background-color: #f8fafc;
}

.batch-item:hover {
  background-color: #f1f5f9;
  border-color: #94a3b8;
  transform: translateY(-1px);
}

.batch-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.batch-name {
  font-weight: 500;
  color: #334155;
}

.batch-format {
  color: #64748b;
  font-size: 0.9em;
  background-color: #f1f5f9;
  padding: 4px 8px;
  border-radius: 4px;
}

.markup-editor {
  margin-top: 24px;
}

.selected-batch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 12px 16px;
  background-color: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.selected-batch h3 {
  color: #334155;
  font-size: 1.1rem;
  margin: 0;
}

.change-batch-btn {
  padding: 6px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background-color: white;
  color: #64748b;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.change-batch-btn:hover {
  background-color: #f1f5f9;
  border-color: #94a3b8;
  color: #334155;
}

.markup-types {
  margin-bottom: 24px;
}

.type-selector {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.type-btn {
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background-color: white;
  color: #64748b;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.type-btn.active {
  background-color: #eef2ff;
  color: #4f46e5;
  border-color: #4f46e5;
}

.type-btn:hover:not(.active) {
  background-color: #f8fafc;
  border-color: #94a3b8;
  color: #334155;
}

.input-group {
  display: flex;
  width: 100%;       /* Make the container span full width */
  gap: 8px;          /* Slightly smaller gap if you want them closer */
}

.input-group input {
  flex: 1 1 auto;    /* Let the input expand to fill available space */
  min-width: 0;      /* Prevent input from overflowing */
  padding: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.95rem;
  color: #334155;
  transition: all 0.2s ease;
  /* Optional for debugging: */
  width: 100% !important;
}


.input-group input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.add-btn {
  width: 5rem;
  flex-shrink: 0;    /* Don’t let the button shrink below its contents */
  padding: 8px 12px;
  /* Remove or reduce min-width if you had one:
     min-width: 80px; */
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.add-btn:hover:not(:disabled) {
  background-color: #4338ca;
}

.add-btn:disabled {
  background-color: #e2e8f0;
  cursor: not-allowed;
}

.markup-preview {
  margin-top: 24px;
  padding: 20px;
  background-color: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.markup-preview h3 {
  color: #334155;
  font-size: 1.1rem;
  margin-bottom: 16px;
}

.markup-items {
  display: grid;
  gap: 12px;
}

.markup-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background-color: white;
}

.item-type {
  background-color: #eef2ff;
  color: #4f46e5;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.85rem;
  margin-right: 12px;
  font-weight: 500;
}

.item-value {
  flex: 1;
  color: #334155;
  font-size: 0.95rem;
}

.remove-btn {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  font-size: 18px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
  line-height: 1;
}

.remove-btn:hover {
  color: #ef4444;
  background-color: #fee2e2;
}

.actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #eef2f7;
}

.save-btn {
  padding: 10px 20px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  font-weight: 500;
}

.save-btn:disabled {
  background-color: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
}

.save-btn:hover:not(:disabled) {
  background-color: #4338ca;
  transform: translateY(-1px);
}
</style> 