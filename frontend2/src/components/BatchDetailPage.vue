<template>
  <AppLayout>
    <div class="batch-detail-container">
      <div class="header">
        <h1>Страница определенного пакета</h1>
        <div class="header-buttons">
          <button class="export-btn">Экспорт</button>
        </div>
      </div>

      <div class="batch-info">
        <h2>Название пакета</h2>
        <div class="package-name">
          <input v-model="packageName" @blur="savePackageName" placeholder="Введите название пакета"/>
          <button class="save-btn" @click="savePackageName">Сохранить</button>
        </div>
        <div class="package-status">
          <div class="package-status">
            <label class="switch">
              <input type="checkbox" v-model="packageStatus" @change="togglePackageStatus"/>
              <span class="slider"></span>
            </label>
            <span>{{ packageStatus ? 'Активен' : 'Неактивен' }}</span>
          </div>
        </div>

        <table class="batch-table">
          <thead>
          <tr>
            <th>Id Батча</th>
            <th>Title</th>
            <th>Тип разметки</th>
            <th>Количество заданий в батче</th>
            <th>Процент выполнения</th>
            <th>Дата и время загрузки</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>{{ batch.id }}</td>
            <td>{{ batch.name }}</td>
            <td>{{ batch.format }}</td>
            <td>{{ batch.taskCount }}</td>
            <td>{{ batch.completionPercentage }}%</td>
            <td>{{ formatDate(batch.uploadedAt) }}</td>
          </tr>
          </tbody>
        </table>
      </div>


      <div class="task-table-section">
        <h3>Список заданий в данном пакете</h3>
        <div class="filters">
          <input v-model="statusFilter" placeholder="Фильтр по статусу"/>
          <input v-model="scoreFilter" placeholder="Фильтр по финальной оценке"/>
        </div>
        <div class="task-table-wrapper">
          <table class="task-table">
            <thead>
            <tr>
              <th>Id задания</th>
              <th>Статус (готов/частично/ожидает)</th>
              <th>финальная оценка</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="task in filteredTasks" :key="task.id" @click="goToTask(task.id)">
              <td>{{ task.id }}</td>
              <td>{{ task.status }}</td>
              <td v-html="formatMarkup(task.finalMark)"></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import AppLayout from './AppLayout.vue';
import axios from 'axios';
import {useRoute} from "vue-router";

const route = useRoute();
import {useBatchStore} from '@/stores/batchStore';


const batchStore = useBatchStore();
const batch = ref(batchStore.selectedBatch);

const packageName = ref('');
const packageStatus = ref(false);
const packageDetails = ref('');
const tasks = ref([]);
const statusFilter = ref('');
const scoreFilter = ref('');

const fetchBatchTasks = async () => {
  if (!batch.value || !batch.value.id) {
    console.error(" Ошибка: batch.id отсутствует, не могу загрузить задачи!");
    return;
  }

  try {
    console.log(`Запрашиваем задачи для batchId=${batch.value.id}`);
    const response = await axios.get(`/api/v1/batch/${batch.value.id}/tasks`, { withCredentials: true });
    tasks.value = response.data; // Загружаем все задачи
    console.log(`Загружено ${tasks.value.length} задач`);
  } catch (error) {
    console.error("Ошибка при загрузке задач:", error);
    tasks.value = [];
  }
};

const savePackageName = async () => {
  try {
    await axios.post('/api/v1/batch/update-name', {
      batchId: batch.value.id,
      name: packageName.value
    }, { withCredentials: true });
  } catch (error) {
    console.error('Error saving package name:', error);
  }
};
console.log(batch.value.id);

function useFormatting() {
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('ru-RU', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  return {
    formatDate
  };
}

const filteredTasks = computed(() => {
  if (!tasks.value.length) return []; // Если задач нет, возвращаем пустой массив

  return tasks.value.filter(task => {
    return (
        (!statusFilter.value || (task.status && task.status.includes(statusFilter.value))) &&
        (!scoreFilter.value || (task.finalScore && task.finalScore.includes(scoreFilter.value)))
    );
  });
});
const {formatDate} = useFormatting();

function goToTask(taskId) {

  console.log('Navigating to task:', taskId);
}

const togglePackageStatus = async () => {
  try {
    const newStatus = packageStatus.value ? "active" : "inactive";
    await axios.post(`/api/v1/batch/${batch.value.id}/toggle-status`, {status: newStatus}, { withCredentials: true });
    console.log(`Статус батча обновлён: ${newStatus}`);
  } catch (error) {
    console.error('Ошибка при изменении статуса батча:', error);
    packageStatus.value = !packageStatus.value; // Откатить изменение в случае ошибки
  }
};
const fetchBatchStatus = async () => {
  try {
    const response = await axios.get(`/api/v1/batch/${batch.value.id}/status`, { withCredentials: true });
    packageStatus.value = response.data.status === "active"; // Если "active", ставим true
  } catch (error) {
    console.error('Ошибка при получении статуса батча:', error);
  }
};

const formatMarkup = (markup) => {
  if (!markup || markup === "N/A") return "<span class='text-gray'>Нет разметки</span>";

  return markup
      .split(" | ") // Разделяем "радио 2: selected | чек 2: checked"
      .map(item => {
        const [label, value] = item.split(": ");

        if (value === "selected") {
          return `<span class="label-container">
                  <input type="radio" checked disabled class="custom-radio">
                  <span class="radio-text">${label}</span>
                </span>`;
        }
        if (value === "checked") {
          return `<span class="label-container">
                  <input type="checkbox" checked disabled class="custom-checkbox">
                  <span class="checkbox-text">${label}</span>
                </span>`;
        }

        return `<span class="text-gray">${label}</span>`;
      })
      .join(""); // Убираем лишние переносы строк
};


onMounted(() => {
  fetchBatchTasks();
  fetchBatchStatus();
});
</script>

<style scoped>
/* ============================= */
/* 1. ОБЩИЙ КОНТЕЙНЕР СТРАНИЦЫ   */
/* ============================= */
.batch-detail-container {
  display: flex;
  flex-direction: column;
  max-height: 100vh; /* Ограничиваем высоту всей страницы компонента */
  overflow-y: auto;  /* Разрешаем вертикальную прокрутку */
  padding: 1rem;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* ============================= */
/* 2. ШАПКА (ЗАГОЛОВОК)          */
/* ============================= */
.header {
  position: sticky; /* Фиксируем заголовок */
  top: 0;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 8px 0;
  border-bottom: 1px solid #eef2f7;
}

.header h1 {
  color: #2c3e50;
  font-size: 1.4rem;
  font-weight: 600;
  margin: 0;
}

.header-buttons {
  display: flex;
  gap: 8px;
}

/* Кнопка "Экспорт" в заголовке */
.export-btn {
  padding: 4px 8px;
  font-size: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.export-btn:hover {
  background-color: #0056b3;
}

/* ============================= */
/* 3. ИНФОРМАЦИЯ О БАТЧЕ         */
/* ============================= */
.batch-info {
  background: #f8fafc;
  padding: 8px;
  border-radius: 6px;
  margin-bottom: 12px;
}

.batch-info h2 {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
}

/* Поле ввода + кнопка "Сохранить" */
.package-name {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}

.package-name input {
  width: 180px;
  font-size: 12px;
  padding: 4px 6px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

/* Кнопка "Сохранить" */
.save-btn {
  padding: 4px 6px;
  font-size: 12px;
  height: 24px;
  width: 100px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.save-btn:hover {
  background-color: #0056b3;
}

/* ============================= */
/* 4. ПЕРЕКЛЮЧАТЕЛЬ СТАТУСА      */
/* ============================= */
.package-status {
  display: flex;
  align-items: center;
  gap: 8px;
  max-width: 300px;
  margin-bottom: 12px;
}

/* Стили для toggle-switch */
.switch {
  position: relative;
  display: inline-block;
  width: 44px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  cursor: pointer;
  background-color: #ccc;
  border-radius: 24px;
  transition: 0.4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  border-radius: 50%;
  transition: 0.4s;
}

input:checked + .slider {
  background-color: #4CAF50;
}

input:checked + .slider:before {
  transform: translateX(20px);
}

/* ============================= */
/* 5. ТАБЛИЦА (ИТОГИ БАТЧА)      */
/* ============================= */
.batch-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background-color: white;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 16px;
}

.batch-table th,
.batch-table td {
  padding: 8px 12px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
  font-size: 0.85rem;
}

.batch-table th {
  background-color: #f8fafc;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.batch-table tr:hover {
  background-color: #f1f5f9;
}

.batch-table tr:last-child td {
  border-bottom: none;
}

/* ============================= */
/* 6. СПИСОК ЗАДАНИЙ (SCROLL)    */
/* ============================= */
.task-table-section h3 {
  margin: 0 0 8px 0;
  font-size: 1rem;
}

.task-table-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filters {
  display: flex;
  gap: 8px;
}

.task-table-wrapper {
  max-height: 300px; /* Ограничение высоты списка задач */
  overflow-y: auto;  /* Прокрутка */
  border: 1px solid #e2e8f0;
  border-radius: 6px;
}

/* Таблица задач */
.task-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background-color: white;
  border-radius: 6px;
  overflow: hidden;
  border: none; /* Внешний border уже есть в wrapper */
}

/* Фиксируем thead при прокрутке */
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

/* ============================= */
/* 7. СТИЛИ ДЛЯ ЧЕКБОКСОВ/РАДИО  */
/* ============================= */
.label-container {
  display: flex;
  align-items: center;
  gap: 4px;
  vertical-align: middle;
}

.custom-checkbox,
.custom-radio {
  width: 14px;
  height: 14px;
  margin: 0; /* Убираем лишние отступы */
  cursor: not-allowed; /* Так как disabled */
}

/* Просто используем accent-color (если поддерживается) */
/* Можно оставить кастомные стили, если нужно */
.custom-checkbox {
  accent-color: #007bff;
}

.custom-radio {
  accent-color: #28a745;
}

.checkbox-text {
  color: #007bff;
  font-weight: bold;
  font-size: 0.85rem;
}

.radio-text {
  color: #28a745;
  font-weight: bold;
  font-size: 0.85rem;
}

.text-gray {
  color: #6c757d;
  font-size: 0.85rem;
}
</style>