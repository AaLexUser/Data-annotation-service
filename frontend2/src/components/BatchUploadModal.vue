<template>
  <div v-if="isOpen" class="modal-overlay">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Загрузить новый батч</h2>
        <button class="close-btn" @click="close">&times;</button>
      </div>

      <div class="modal-body">
        <div class="form-group">
          <label for="name">Введите имя для разметки</label>
          <input
              type="text"
              id="name"
              class="text-input"
              v-model="batchName"
          />
          <label for="file">Выберите файл (CSV или JSON)</label>
          <input
              type="file"
              id="file"
              ref="fileInput"
              @change="handleFileChange"
              accept=".csv,.json"
              class="file-input"
          />
        </div>

        <div class="form-group">
          <label for="overlaps">Количество перекрытий</label>
          <input
              type="number"
              id="overlaps"
              v-model="overlaps"
              min="1"
              class="number-input"
          />
        </div>

        <div class="error-message" v-if="error">
          {{ error }}
        </div>

        <div class="modal-footer">
          <button
              class="upload-btn"
              @click="uploadBatch"
              :disabled="!selectedFile || uploading"
          >
            {{ uploading ? 'Загрузка...' : 'Загрузить' }}
          </button>
          <button class="cancel-btn" @click="close">Отмена</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import axios from 'axios';

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  }
});

const emit = defineEmits(['close', 'batchUploaded']);

const fileInput = ref(null);
const selectedFile = ref(null);
const overlaps = ref(1);
const error = ref('');
const uploading = ref(false);
const batchName = ref('');
const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    if (file.type === 'text/csv' || file.type === 'application/json') {
      selectedFile.value = file;
      error.value = '';
    } else {
      error.value = 'Пожалуйста, выберите файл CSV или JSON';
      event.target.value = '';
    }
  }
};

const uploadBatch = async () => {
  if (!selectedFile.value) {
    error.value = 'Пожалуйста, выберите файл';
    return;
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);
  formData.append('overlaps', overlaps.value);
  formData.append('batchName',batchName.value);
  uploading.value = true;
  error.value = '';

  try {
    const response = await axios.post('/api/v1/batch/load', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      withCredentials: true
    });

    emit('batchUploaded');
    close();
  } catch (err) {
    error.value = 'Ошибка при загрузке файла: ' + (err.response?.data || err.message);
  } finally {
    uploading.value = false;
  }
};

const close = () => {
  selectedFile.value = null;
  overlaps.value = 1;
  error.value = '';
  if (fileInput.value) {
    fileInput.value.value = '';
  }
  emit('close');
};
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
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.file-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.number-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.upload-btn {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s ease;
}

.upload-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.upload-btn:hover:not(:disabled) {
  background-color: #45a049;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s ease;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.error-message {
  color: #dc3545;
  margin-top: 10px;
  font-size: 14px;
}
</style> 