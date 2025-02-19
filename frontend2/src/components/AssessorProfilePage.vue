<template>
  <AppLayout>
    <div class="assessor-profile">
      <h2>Профиль асессора</h2>
      <div class="statistics-widget">
        <h3>Статистика разметки</h3>
        <div class="date-filter">
          <button @click="setToday">сегодня</button>
          <button @click="setPeriod">задать период</button>
        </div>
        <div class="markup-statistics">
          <span>Количество: {{ totalMarkupCount }}</span>
        </div>
      </div>
      <div class="task-table">
        <h3>Размеченные задания</h3>
        <table>
          <thead>
            <tr>
              <th>Id задания</th>
              <th>query/title</th>
              <th>финальная оценка</th>
              <th>время завершения</th>
              <th>исправить</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="task in tasks" :key="task.id">
              <td>{{ task.id }}</td>
              <td>{{ task.title }}</td>
              <td>{{ task.finalScore }}</td>
              <td>{{ task.completionTime }}</td>
              <td><button @click="editTask(task.id)">Исправить</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </AppLayout>
</template>


<script setup>
import { ref, onMounted } from 'vue';
import AppLayout from './AppLayout.vue';
import axios from 'axios';

const markupStats = ref([]);
const tasks = ref([]);

const fetchMarkupStats = async () => {
  try {
    const response = await axios.get('/api/v1/markup/stats', { withCredentials: true });
    markupStats.value = response.data;
  } catch (error) {
    console.error('Ошибка при получении статистики разметки:', error);
  }
};

const fetchTasks = async () => {
  try {
    const response = await axios.get('/api/v1/tasks/completed', { withCredentials: true });
    tasks.value = response.data;
  } catch (error) {
    console.error('Ошибка при получении заданий:', error);
  }
};

const setToday = () => {
  // Logic to set the date filter to today
};

const setPeriod = () => {
  // Logic to set a custom date period
};

const navigateToTasks = (type) => {
  // Logic to navigate to the task list filtered by markup type
};

const editTask = (taskId) => {
  // Logic to edit a specific task
};

onMounted(() => {
  fetchMarkupStats();
  fetchTasks();
});
</script>


<style scoped>
.assessor-profile {
  /* Общий контейнер, как в примере */
  display: flex;
  flex-direction: column;
  max-height: 100vh;
  overflow-y: auto;
  padding: 1rem;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* Заголовок страницы */
.assessor-profile h2 {
  color: #2c3e50;
  font-size: 1.4rem;
  font-weight: 600;
  margin: 0 0 16px 0;
}

/* Блок со статистикой (аналог batch-info) */
.statistics-widget {
  background: #f8fafc;
  padding: 8px;
  border-radius: 6px;
  margin-bottom: 12px;
}

.statistics-widget h3 {
  margin: 0 0 8px 0;
  font-size: 1rem;
}

/* Фильтр по датам */
.date-filter {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.date-filter button {
  padding: 4px 8px;
  font-size: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.date-filter button:hover {
  background-color: #0056b3;
}

/* Отображение статистики разметки */
.markup-statistics {
  font-size: 0.9rem;
  color: #334155;
}

/* Секция, в которой будет таблица заданий (аналог task-table-section) */
.task-table {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* Заголовок таблицы */
.task-table h3 {
  margin: 0 0 8px 0;
  font-size: 1rem;
}

/* При желании можно обернуть таблицу в блок с прокруткой, как в примере
.task-table-wrapper {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
} */

/* Стили таблицы (аналог batch-table/task-table в примере) */
.task-table table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background-color: white;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  overflow: hidden;
}

/* Фиксируем шапку при прокрутке */
.task-table thead {
  position: sticky;
  top: 0;
  background: #f8fafc;
  z-index: 10;
}

.task-table th,
.task-table td {
  padding: 8px 10px;
  border-bottom: 1px solid #e2e8f0;
  font-size: 0.85rem;
  text-align: left;
}

.task-table th {
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.task-table tr:hover {
  background-color: #f1f5f9;
}

.task-table tr:last-child td {
  border-bottom: none;
}

.task-table td {
  color: #334155;
}

/* Кнопка "Исправить" в таблице заданий (аналог save-btn/export-btn) */
.task-table button {
  padding: 4px 6px;
  font-size: 12px;
  height: 24px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.task-table button:hover {
  background-color: #0056b3;
}
</style>