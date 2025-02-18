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
import router from "@/router/index.js";
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

// const fetchPackageDetails = async () => {
//   try {
//     const response = await axios.get('/api/v1/batch/details');
//     packageName.value = response.data.name;
//     packageStatus.value = response.data.status;
//     packageDetails.value = response.data.details;
//     tasks.value = response.data.tasks;
//   } catch (error) {
//     console.error('Error fetching package details:', error);
//   }
// };
const fetchBatchTasks = async () => {
  if (!batch.value || !batch.value.id) {
    console.error(" Ошибка: batch.id отсутствует, не могу загрузить задачи!");
    return;
  }

  try {
    console.log(`Запрашиваем задачи для batchId=${batch.value.id}`);
    const response = await axios.get(`/api/v1/batch/${batch.value.id}/tasks`);
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
    });
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
    await axios.post(`/api/v1/batch/${batch.value.id}/toggle-status`, {status: newStatus});
    console.log(`Статус батча обновлён: ${newStatus}`);
  } catch (error) {
    console.error('Ошибка при изменении статуса батча:', error);
    packageStatus.value = !packageStatus.value; // Откатить изменение в случае ошибки
  }
};
const fetchBatchStatus = async () => {
  try {
    const response = await axios.get(`/api/v1/batch/${batch.value.id}/status`);
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
.batch-detail-container {
  padding: 2rem;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eef2f7;
}

.header h1 {
  color: #2c3e50;
  font-size: 1.75rem;
  font-weight: 600;
  margin: 0;
}

.header-buttons {
  display: flex;
  gap: 12px;
}

.export-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
  font-weight: 500;
}

.task-table-wrapper {
  max-height: 400px; /* Ограничение высоты (можно изменить) */
  overflow-y: auto; /* Добавляем вертикальную прокрутку */
  border: 1px solid #e2e8f0; /* Граница для красоты */
}

/* Фиксируем заголовок таблицы */
.task-table thead {
  position: sticky;
  top: 0;
  background: #f8fafc;
  z-index: 10;
}

.export-btn:hover {
  background-color: #4338ca;
}


.package-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.package-name input {
  width: auto;
  max-width: 300px;
}

.package-status {
  max-width: 300px;
}

.task-table-section {
  margin-top: 24px;
}

.task-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.task-table th,
.task-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

.task-table th {
  background-color: #f8fafc;
  font-weight: 600;
  font-size: 0.85rem;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.task-table tr:hover {
  background-color: #f8fafc;
}

.task-table tr:last-child td {
  border-bottom: none;
}

.task-table td {
  color: #334155;
  font-size: 0.95rem;
}

.filters {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

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
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #4CAF50;
}

input:checked + .slider:before {
  transform: translateX(20px);
}

.batch-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.batch-table th,
.batch-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

.batch-table th {
  background-color: #f8fafc;
  font-weight: 600;
  font-size: 0.85rem;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.batch-table tr:hover {
  background-color: #f8fafc;
}

.batch-table tr:last-child td {
  border-bottom: none;
}

.batch-table td {
  color: #334155;
  font-size: 0.95rem;
}

.label-container {
  display: flex;
  align-items: center; /* Выровнять элементы по центру */
  gap: 6px; /* Уменьшили отступ */
  margin-bottom: 3px; /* Минимальный отступ */
}

/* ✅ Кастомные чекбоксы */
.custom-checkbox {
  width: 18px;
  height: 18px;
  border: 2px solid #007bff; /* Синий контур */
  border-radius: 4px;
  position: relative;
  display: inline-block;
  cursor: not-allowed;
  vertical-align: middle;
}

.custom-checkbox:checked {
  background-color: #007bff;
  border-color: #007bff;
}

.custom-checkbox:checked::after {
  content: '✔';
  color: white;
  font-size: 14px;
  font-weight: bold;
  position: absolute;
  left: 3px;
  top: 0px;
}

.custom-radio {
  width: 18px;
  height: 18px;
  border: 2px solid #28a745; /* Зеленый контур */
  border-radius: 50%;
  position: relative;
  display: inline-block;
  cursor: not-allowed;
  vertical-align: middle;
}

.custom-radio:checked {
  background-color: #28a745;
  border-color: #28a745;
}

.custom-radio:checked::after {
  content: '';
  width: 10px;
  height: 10px;
  background-color: white;
  border-radius: 50%;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.checkbox-text {
  color: #007bff;
  font-weight: bold;
}

.radio-text {
  color: #28a745;
  font-weight: bold;
}

/* Серый текст для "нет разметки" */
.text-gray {
  color: #6c757d;
}

.save-btn {
  padding: 8px 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  margin-left: 10px;
}

.save-btn:hover {
  background-color: #0056b3;
}
</style>