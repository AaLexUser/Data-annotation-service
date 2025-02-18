<template>
  <div class="honeypot-manager">
    <div class="header">
      <h2>Управление обучающими заданиями</h2>
      <button 
        class="btn-primary"
        @click="showCreateModal = true"
      >
        Создать обучающее задание
      </button>
    </div>

    <!-- Create Honeypot Task Modal -->
    <div v-if="showCreateModal" class="modal">
      <div class="modal-content">
        <h3>Создать обучающее задание</h3>
        <div class="form-group">
          <label>ID задания:</label>
          <input v-model="newTask.taskId" type="number" class="form-control" />
        </div>
        
        <div class="answers-section">
          <h4>Правильные ответы:</h4>
          <div v-for="(answer, index) in answerFields" :key="index" class="answer-field">
            <input 
              v-model="answer.field" 
              placeholder="Поле" 
              class="form-control"
            />
            <input 
              v-model="answer.value" 
              placeholder="Значение" 
              class="form-control"
            />
            <button @click="removeAnswerField(index)" class="btn-danger">Удалить</button>
          </div>
          <button @click="addAnswerField" class="btn-secondary">Добавить ответ</button>
        </div>

        <div class="modal-actions">
          <button @click="createHoneypotTask" class="btn-primary">Создать</button>
          <button @click="showCreateModal = false" class="btn-secondary">Отмена</button>
        </div>
      </div>
    </div>

    <!-- Results Section -->
    <div v-if="honeypotTasks.length > 0" class="results-section">
      <h3>Обучающие задания в пакете</h3>
      <div class="tasks-list">
        <div v-for="task in honeypotTasks" :key="task.id" class="task-item">
          <div class="task-header">
            <span>Задание #{{ task.taskId }}</span>
            <span class="created-at">{{ formatDate(task.createdAt) }}</span>
          </div>
          <div class="correct-answers">
            <h4>Правильные ответы:</h4>
            <div v-for="(value, field) in task.correctAnswers" :key="field" class="answer-item">
              <span class="field">{{ field }}:</span>
              <span class="value">{{ value }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Assessor Results -->
    <div v-if="assessorResults" class="assessor-results">
      <h3>Результаты асессоров</h3>
      <div class="results-grid">
        <div v-for="(result, assessorId) in assessorResults" :key="assessorId" class="assessor-result">
          <h4>Асессор #{{ assessorId }}</h4>
          <div class="stats">
            <div class="stat">
              <span>Всего заданий:</span>
              <span>{{ result.totalTasks }}</span>
            </div>
            <div class="stat">
              <span>Правильно выполнено:</span>
              <span>{{ result.correctTasks }}</span>
            </div>
            <div class="stat">
              <span>Точность:</span>
              <span>{{ (result.accuracy * 100).toFixed(1) }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { honeypotService } from '@/services/honeypotService';

const props = defineProps({
  batchId: {
    type: Number,
    required: true
  }
});

const showCreateModal = ref(false);
const honeypotTasks = ref([]);
const assessorResults = ref(null);
const answerFields = ref([{ field: '', value: '' }]);

const newTask = ref({
  taskId: null,
  correctAnswers: {}
});

const addAnswerField = () => {
  answerFields.value.push({ field: '', value: '' });
};

const removeAnswerField = (index) => {
  answerFields.value.splice(index, 1);
};

const createHoneypotTask = async () => {
  try {
    const taskData = {
      batchId: props.batchId,
      taskId: newTask.value.taskId,
      correctAnswers: {}
    };

    // Convert answer fields to correct format
    answerFields.value.forEach(answer => {
      if (answer.field && answer.value) {
        taskData.correctAnswers[answer.field] = answer.value;
      }
    });

    await honeypotService.createHoneypotTask(taskData);
    await loadHoneypotTasks();
    showCreateModal.value = false;
    
    // Reset form
    newTask.value = { taskId: null, correctAnswers: {} };
    answerFields.value = [{ field: '', value: '' }];
  } catch (error) {
    console.error('Error creating honeypot task:', error);
  }
};

const loadHoneypotTasks = async () => {
  try {
    honeypotTasks.value = await honeypotService.getHoneypotTasksByBatch(props.batchId);
  } catch (error) {
    console.error('Error loading honeypot tasks:', error);
  }
};

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString();
};

onMounted(async () => {
  await loadHoneypotTasks();
});
</script>

<style scoped>
.honeypot-manager {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
}

.form-group {
  margin-bottom: 15px;
}

.form-control {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 10px;
}

.answers-section {
  margin: 20px 0;
}

.answer-field {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-primary {
  background-color: #4f46e5;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-secondary {
  background-color: #6b7280;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-danger {
  background-color: #ef4444;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.tasks-list {
  display: grid;
  gap: 20px;
  margin-top: 20px;
}

.task-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.created-at {
  color: #6b7280;
  font-size: 0.9em;
}

.correct-answers {
  margin-top: 10px;
}

.answer-item {
  display: flex;
  gap: 10px;
  margin: 5px 0;
}

.field {
  font-weight: 500;
}

.assessor-results {
  margin-top: 30px;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.assessor-result {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
}

.stats {
  margin-top: 10px;
}

.stat {
  display: flex;
  justify-content: space-between;
  margin: 5px 0;
}
</style> 