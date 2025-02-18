<template>
  <AppLayout>
    <div class="batch-detail-container">
      <div class="header">
        <h1>Страница пакета</h1>
        <div class="header-buttons">
          <button class="export-btn">Экспорт</button>
        </div>
      </div>

      <div class="batch-info">
        <h2>Название пакета</h2>
        <input v-model="packageName" @blur="savePackageName" placeholder="Введите название пакета" />
        <div>
          <label>
            <input type="checkbox" v-model="packageStatus" @change="togglePackageStatus" />
            Включить/Выключить пакет
          </label>
        </div>
        <p>
          {{ packageDetails }}
        </p>
      </div>

      <div class="task-table-section">
        <h3>Таблица со списком заданий в данном пакете</h3>
        <p>необходимые фильтры по статусу, финальной оценке и т.д.</p>
        <br>
        <table class="task-table">
          <thead>
            <tr>
              <th>Id задания</th>
              <th>query/title</th>
              <th>Статус (готов/частично/ожидает)</th>
              <th>финальная оценка</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="task in tasks" :key="task.id" @click="goToTask(task.id)">
              <td>{{ task.id }}</td>
              <td>{{ task.title }}</td>
              <td>{{ task.status }}</td>
              <td>{{ task.finalScore }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref } from 'vue';
import AppLayout from './AppLayout.vue';

const tasks = ref([
  // Example data
  { id: 1, title: 'Task 1', status: 'готов', finalScore: 'A' },
  { id: 2, title: 'Task 2', status: 'частично', finalScore: 'B' },
  // Add more tasks as needed
]);

function goToTask(taskId) {
  // Logic to navigate to the task detail page
  console.log('Navigating to task:', taskId);
}
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


.export-btn:hover {
  background-color: #4338ca;
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
</style> 