<template>
  <AppLayout>
    <div class="assessor-profile">
      <h2>Профиль асессора</h2>

      <!-- Блок со статистикой -->
      <div class="statistics-widget">
        <h3>Статистика разметки</h3>

        <div class="date-filter">
          <button @click="setToday">сегодня</button>
          <button @click="setPeriod">задать период</button>
        </div>

        <div class="markup-statistics">
          <span>Количество: {{ totalMarkupCount }}</span>
        </div>

        <table class="markup-table">
          <thead>
          <tr>
            <th>Батч</th>
            <th>Количество разметок</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="(count, batchName) in markupsStatistic"
              :key="batchName"
          >
            <td>{{ batchName }}</td>
            <td>{{ count }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Таблица с задачами -->
      <div class="task-table">
        <h3>Размеченные задания</h3>
        <table>
          <thead>
          <tr>
            <th>Id задания</th>
            <th>Batch</th>
            <th>Финальная оценка</th>
            <!-- <th>Время завершения</th> -->
            <!-- <th>Исправить</th> -->
          </tr>
          </thead>
          <tbody>
          <!-- Перебираем stats -->
          <tr
              v-for="stat in stats"
              :key="stat.id"
          >
            <td>{{ stat.id }}</td>
            <td>{{ stat.batchName }}</td>
            <td>{{ formatFinalMark(stat.finalMark) }}</td>
            <!-- <td>{{ stat.finishTime }}</td> -->
            <!-- <td><button @click="editTask(stat.id)">Исправить</button></td> -->
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </AppLayout>
</template>



<script setup>
import {ref, onMounted} from 'vue';
import AppLayout from './AppLayout.vue';
import axios from 'axios';
import {useAuthStore} from '@/stores/auth';

const authStore = useAuthStore();
const markupsStatistic = ref({});
const stats = ref([]);


const fetchTasks = async () => {
  try {
    const response = await axios.get(`/api/v1/stats/assessor-stats?assessorId=${authStore.user.id}`, {withCredentials: true});
    markupsStatistic.value = response.data.markupsStatistic;
    stats.value = response.data.stats;
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
function formatFinalMark(finalMark) {
  if (!finalMark) return "N/A";

  // Удаляем фигурные скобки
  finalMark = finalMark.replace(/[{}]/g, "");

  // Разделяем по ", "
  const pairs = finalMark.split(", ");

  // Преобразуем каждую пару "ключ=значение"
  const formattedPairs = pairs.map(pair => {
    const [key, value = ""] = pair.split("=");

    // Если value === "selected" → вывести "selected",
    // иначе "(нет)" или оставляем value
    if (value.trim() === "selected") {
      return `${key.trim()}: selected`;
    } else if (!value.trim()) {
      return `${key.trim()}: (нет)`;
    }
    // Если есть другое значение — выводим как есть
    return `${key.trim()}: ${value.trim()}`;
  });

  // Объединяем с разделителем " | "
  return formattedPairs.join(" | ");
}

onMounted(() => {
  // fetchMarkupStats();
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

.markup-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background-color: white;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  overflow: hidden;
}

.markup-table th,
.markup-table td {
  padding: 8px 12px;
  border-bottom: 1px solid #e2e8f0;
  text-align: left;
}

.markup-table th {
  background-color: #f8fafc;
  font-weight: 600;
  font-size: 14px;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.markup-table tr:hover {
  background-color: #f1f5f9;
}

.markup-table tr:last-child td {
  border-bottom: none;
}

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

task-answer {
  margin-bottom: 2rem;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
}


.single-text strong,
.pair-col strong {
  color: #64748b;
  margin-right: 0.5rem;
}
</style>