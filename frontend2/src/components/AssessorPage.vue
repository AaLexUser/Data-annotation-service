<template>
  <AppLayout>
    <div class="assessor-container">
      <h1>Страница асессора</h1>
      <!-- Если батч не выбран, показываем BatchList -->
      <BatchList
          v-if="!selectedBatch"
          :batches="batches"
          @batchSelected="handleBatchSelected"
      />

      <!-- Если батч выбран, показываем TaskList -->
      <TaskList
          v-else
          :tasks="tasks"
          :batchName="selectedBatch.name"
          :assessorId="authStore.user.id"
          :batchMarkup="batchMarkup"

          @back="handleBack"
      />
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// Компоненты
import BatchList from '@/components/BatchList.vue';
import TaskList from '@/components/TaskList.vue';
import AppLayout from './AppLayout.vue';

import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
console.log(authStore.user.id)

// Списки батчей и заданий
const batches = ref([]);
const tasks = ref([]);
const selectedBatch = ref(null);
const batchMarkup = ref(null);

/** Получаем все батчи */
async function fetchBatches() {
  try {

    const response = await axios.get(`/api/v1/batch/for-assessor?userId=${authStore.user.id}`, { withCredentials: true });
    batches.value = response.data;
  } catch (error) {
    console.error('Ошибка при получении батчей', error);
  }
}

/** Получаем задачи для выбранного батча */
async function fetchTasks(batchId) {
  try {
    const response = await axios.get(
        `/api/v1/assessor/tasks?batchId=${batchId}`,
        { withCredentials: true }
    );
    tasks.value = response.data;
  } catch (error) {
    console.error('Ошибка при получении задач', error);
  }
}

/** Обработчик события batchSelected */
async function handleBatchSelected(batch) {
  selectedBatch.value = batch;
  await fetchTasks(batch.id);
  batchMarkup.value = await fetchMarkupForBatch(batch.id);
}
async function fetchMarkupForBatch(batchId) {
  try {
    // Предполагается, что ваш контроллер обрабатывает запрос
    // GET /api/v1/markup/byBatchId?id={batchId}
    const response = await axios.get(
        `/api/v1/markup/byBatchId?id=${batchId}`,
        { withCredentials: true }
    );
    return response.data;
  } catch (error) {
    console.error('Ошибка получения маркапа для батча:', error);
    return null;
  }
}
/** Обработчик "вернуться назад к списку батчей" */
function handleBack() {
  selectedBatch.value = null;
  tasks.value = [];
}

/** При монтировании загружаем батчи */
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
  margin-bottom: 24px;
}

h1 {
  color: #2c3e50;
  font-size: 1.75rem;
  font-weight: 600;
  margin: 0 0 24px 0;
}
</style>
