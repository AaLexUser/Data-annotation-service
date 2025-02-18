/**
 * AssessorPage.vue
 * Главная страница асессора.
 * Отображает список батчей и задач, отслеживает прогресс выполнения.
 */
<template>
  <AppLayout>
    <div class="assessor-container">
      <!-- Заголовок и информация о прогрессе -->
      <div class="header">
        <h1>Страница асессора</h1>
        <!-- Информация о выбранном батче и прогрессе -->
        <div v-if="selectedBatch" class="batch-info">
          <span class="batch-name">{{ selectedBatch.name }}</span>
          <div class="progress-info">
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: `${completionPercentage}%` }"
                :class="{ 'completed': completionPercentage === 100 }"
              ></div>
            </div>
            <span class="progress-text">{{ completionPercentage }}% выполнено</span>
          </div>
        </div>
      </div>

        <!-- Список батчей (если батч не выбран) -->
        <BatchList
          v-if="!selectedBatch"
          :batches="batches"
          @batchSelected="handleBatchSelected"
          class="batch-list"
        />
      
         <!-- Список задач (если батч выбран) -->
        <div v-else class="task-section">
          <TaskList
            :tasks="tasks"
            :batchName="selectedBatch.name"
            :assessorId="authStore.user.id"
            :batchMarkup="batchMarkup"
            :isEducational="selectedBatch.isEducational"
            @back="handleBack"
            @submitted="handleTaskSubmitted"
          />
          
          <!-- Сообщение о завершении всех задач -->
          <div v-if="completionPercentage === 100" class="completion-message">
            <div class="completion-content">
              <h3>🎉 Поздравляем!</h3>
              <p>Вы завершили все задания в этом батче.</p>
              <button @click="handleBack" class="back-to-batches">
                Вернуться к списку батчей
              </button>
            </div>
          </div>
        </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import BatchList from '@/components/BatchList.vue';
import TaskList from '@/components/TaskList.vue';
import AppLayout from './AppLayout.vue';
import { useAuthStore } from '@/stores/auth';

// Инициализация хранилища аутентификации
const authStore = useAuthStore();

// Состояние компонента
const batches = ref([]); // Список всех батчей
const tasks = ref([]); // Список задач текущего батча
const selectedBatch = ref(null); // Выбранный батч
const batchMarkup = ref(null); // Разметка для текущего батча
const completedTasks = ref(new Set()); // Множество выполненных задач

/**
 * Вычисляет процент выполнения текущего батча
 * @returns {number} Процент выполнения (0-100)
 */
const completionPercentage = computed(() => {
  if (!tasks.value.length) return 0;
  return Math.round((completedTasks.value.size / tasks.value.length) * 100);
});

/**
 * Загружает список батчей для текущего асессора
 */
async function fetchBatches() {
  try {
    const response = await axios.get(
      `/api/v1/batch/for-assessor?userId=${authStore.user.id}`, 
      { withCredentials: true }
    );
    batches.value = response.data;
  } catch (error) {
    console.error('Ошибка при получении батчей', error);
    showError('Не удалось загрузить список батчей');
  }
}

/**
 * Загружает список задач для выбранного батча
 * @param {number} batchId - ID батча
 */
async function fetchTasks(batchId) {
  try {
    console.log('Fetching tasks for batch:', batchId);
    const response = await axios.get(
      `/api/v1/assessor/tasks?batchId=${batchId}`,
      { withCredentials: true }
    );
    console.log('Tasks response:', response.data);
    tasks.value = response.data;
    
    // Инициализация списка выполненных задач
    completedTasks.value = new Set(
      tasks.value
        .filter(task => task.isFinished)
        .map(task => task.id)
    );
  } catch (error) {
    console.error('Ошибка при получении задач', error);
    showError('Не удалось загрузить задания');
  }
}

/**
 * Обработчик выбора батча
 * Загружает задачи и разметку для выбранного батча
 * @param {Object} batch - Выбранный батч
 */
async function handleBatchSelected(batch) {
  try {
    console.log('Selected batch:', batch);
    selectedBatch.value = batch;
    await fetchTasks(batch.id);
    batchMarkup.value = await fetchMarkupForBatch(batch.id);
    console.log('Loaded tasks:', tasks.value);
    console.log('Loaded markup:', batchMarkup.value);
  } catch (error) {
    console.error('Ошибка при выборе батча:', error);
    showError('Не удалось загрузить батч');
    handleBack();
  }
}

/**
 * Загружает разметку для батча
 * @param {number} batchId - ID батча
 * @returns {Object|null} Объект разметки или null в случае ошибки
 */
async function fetchMarkupForBatch(batchId) {
  try {
    console.log('Fetching markup for batch:', batchId);
    const response = await axios.get(
      `/api/v1/markup/byBatchId?id=${batchId}`,
      { withCredentials: true }
    );
    console.log('Markup response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Ошибка получения маркапа для батча:', error);
    showError('Не удалось загрузить разметку');
    return null;
  }
}

/**
 * Обработчик возврата к списку батчей
 * Сбрасывает состояние и обновляет список батчей
 */
function handleBack() {
  selectedBatch.value = null;
  tasks.value = [];
  completedTasks.value = new Set();
  fetchBatches(); // Обновляем список батчей
}

/**
 * Обработчик отправки задачи
 * Добавляет задачу в список выполненных
 * @param {Object} payload - Данные отправленной задачи
 */
function handleTaskSubmitted(payload) {
  completedTasks.value.add(payload.taskId);
  
  // Если все задачи выполнены, показываем сообщение
  if (completionPercentage.value === 100) {
    showCompletionMessage();
  }
}

/**
 * Показывает сообщение об ошибке
 * @param {string} message - Текст ошибки
 */
function showError(message) {
  // You could implement a toast notification here
  alert(message);
}

/**
 * Показывает сообщение о завершении батча
 * Реализовано через template когда completionPercentage === 100
 */
function showCompletionMessage() {
  // The completion message is shown via template when completionPercentage is 100
}

// Загрузка батчей при монтировании компонента
onMounted(() => {
  fetchBatches();
});
</script>

<style scoped>
.assessor-container {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  padding: 24px;
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

h1 {
  color: #2c3e50;
  font-size: 1.75rem;
  font-weight: 600;
  margin: 0;
}

.batch-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.batch-name {
  font-weight: 500;
  color: #4a5568;
}

.progress-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar {
  width: 200px;
  height: 8px;
  background-color: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #4f46e5;
  transition: width 0.3s ease;
}

.progress-fill.completed {
  background-color: #059669;
}

.progress-text {
  font-size: 0.875rem;
  color: #64748b;
  min-width: 4rem;
}

.completion-message {
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
  backdrop-filter: blur(4px);
}

.completion-content {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
  animation: slideIn 0.3s ease;
}

.completion-content h3 {
  color: #059669;
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.completion-content p {
  color: #4a5568;
  margin-bottom: 1.5rem;
}

.back-to-batches {
  background-color: #4f46e5;
  color: white;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-to-batches:hover {
  background-color: #4338ca;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@keyframes slideIn {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.task-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
  position: relative;
  overflow: hidden;
}

.batch-list {
  flex: 1;
  width: 100%;
  overflow-y: auto;
}
</style>
