<template>
  <div class="assessor-container">
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
        @back="handleBack"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// Компоненты
import BatchList from '@/components/BatchList.vue';
import TaskList from '@/components/TaskList.vue';

import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

// Списки батчей и заданий
const batches = ref([]);
const tasks = ref([]);
const selectedBatch = ref(null);

/** Получаем все батчи */
async function fetchBatches() {
  try {
    const response = await axios.get('/api/v1/batch/all', { withCredentials: true });
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
function handleBatchSelected(batch) {
  selectedBatch.value = batch;
  fetchTasks(batch.id);
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
  padding: 20px;
  font-family: Arial, sans-serif;
}
</style>
