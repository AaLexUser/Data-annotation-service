<template>
  <div class="assessor-container">
    <!-- Если батч не выбран, показываем список батчей -->
    <div v-if="!selectedBatch">
      <h2>Выберите батч для разметки</h2>
      <ul class="batch-list">
        <li
            v-for="batch in batches"
            :key="batch.id"
            @click="selectBatch(batch)"
            class="batch-item"
        >
          {{ batch.name }}
          <span v-if="tasksForBatch(batch.id)">
            ({{ tasksForBatch(batch.id).length }} заданий)
          </span>
        </li>
      </ul>
    </div>

    <!-- Если выбран батч -->
    <div v-else>
      <button class="back-button" @click="clearBatch">← Назад к выбору батча</button>
      <h2>Батч: {{ selectedBatch.name }}</h2>

      <!-- Если в батче осталось незавершённых заданий, показываем первое из них -->
      <div v-if="currentTask">
        <h3>Задание №{{ currentTask.id }}</h3>
        <!-- Выводим данные из rowFromBatch, можно адаптировать под нужный формат -->
        <div class="task-data">
          <div v-for="(value, key) in currentTask.rowFromBatch" :key="key">
            <strong>{{ key }}:</strong> {{ value }}
          </div>
        </div>
        <form @submit.prevent="submitAnnotation">
          <textarea
              v-model="annotation"
              placeholder="Введите разметку (например, JSON-массив или текст)"
              rows="5"
          ></textarea>
          <button type="submit">Отправить разметку</button>
        </form>
      </div>

      <!-- Если все задания в батче аннотированы -->
      <div v-else>
        <p>Все задания в этом батче аннотированы.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

// Списки батчей и задач, приходящих с сервера
const batches = ref([]);
const tasks = ref([]);

// Выбранный батч
const selectedBatch = ref(null);

// Поле для ввода аннотации
const annotation = ref('');

/**
 * Получаем все батчи.
 * Предполагается, что эндпоинт возвращает объекты Batch с полями:
 * id, name, format, uploadedAt, isActive.
 */
const fetchBatches = async () => {
  try {
    const response = await axios.get('/api/v1/batch/all', { withCredentials: true });
    batches.value = response.data;
  } catch (error) {
    console.error('Ошибка при получении батчей', error);
  }
};

/**
 * Получаем задачи для данного ассессора.
 * Предполагается, что каждая задача имеет структуру TaskDTO:
 * { id, batchId, rowFromBatch }
 * При необходимости можно расширить DTO для добавления признака отправленности.
 */
const fetchTasks = async () => {
  try {
    const response = await axios.get(`/api/v1/tasks?assessorId=${authStore.userId}`, { withCredentials: true });
    tasks.value = response.data;
  } catch (error) {
    console.error('Ошибка при получении задач', error);
  }
};

onMounted(() => {
  fetchBatches();
  fetchTasks();
});

/** Функция выбора батча. При выборе сбрасываем поле аннотации. */
const selectBatch = (batch) => {
  selectedBatch.value = batch;
  annotation.value = '';
};

/** Возврат к выбору батча. */
const clearBatch = () => {
  selectedBatch.value = null;
};

/**
 * Функция, возвращающая массив задач для указанного batchId.
 * Если задач для этого batch нет, возвращается пустой массив.
 */
const tasksForBatch = (batchId) => {
  return tasks.value.filter(task => task.batchId === batchId);
};

/**
 * Вычисляемое свойство, которое возвращает первое незавершённое задание выбранного батча.
 * Здесь предполагается, что если задача уже аннотирована, то в ней появится поле finalMarkup или иной признак.
 * Если такого признака нет, можно завести локальное поле, например, submitted, и обновлять его после отправки.
 */
const currentTask = computed(() => {
  if (!selectedBatch.value) return null;
  // Фильтруем задачи выбранного батча.
  const batchTasks = tasksForBatch(selectedBatch.value.id);
  // Предположим, что если задача аннотирована, то finalMarkup уже заполнен (или добавьте свой признак).
  return batchTasks.find(task => !task.finalMarkup);
});

/**
 * Отправка аннотации для текущего задания.
 * Если введённый текст можно распарсить как JSON-массив, используем его,
 * иначе оборачиваем текст в массив с одним объектом.
 */
const submitAnnotation = async () => {
  if (!currentTask.value) return;

  let selections;
  try {
    selections = JSON.parse(annotation.value);
    if (!Array.isArray(selections)) {
      throw new Error('Разметка должна быть массивом');
    }
  } catch (err) {
    selections = [{ type: 'text', value: annotation.value }];
  }

  const payload = {
    taskId: currentTask.value.id,
    assessorId: authStore.userId,
    selections: selections
  };

  try {
    const response = await axios.post('/api/v1/submit', payload, { withCredentials: true });
    alert(response.data); // Например, "Разметка сохранена"
    // Обновляем задачу локально: здесь устанавливаем finalMarkup,
    // чтобы эта задача не выбиралась снова.
    currentTask.value.finalMarkup = 'submitted';
    // Очищаем поле ввода аннотации.
    annotation.value = '';
  } catch (error) {
    console.error('Ошибка отправки аннотации', error);
    alert('Ошибка отправки аннотации');
  }
};
</script>

<style scoped>
.assessor-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
}

/* Стили для списка батчей */
.batch-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.batch-item {
  padding: 10px;
  margin-bottom: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.batch-item:hover {
  background-color: #f0f0f0;
}

.back-button {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  font-size: 16px;
  margin-bottom: 10px;
}

.back-button:hover {
  text-decoration: underline;
}

/* Стили для формы аннотации */
textarea {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button[type="submit"] {
  padding: 10px 15px;
  background-color: #007bff;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
}

button[type="submit"]:hover {
  background-color: #0056b3;
}

.task-data {
  margin-bottom: 15px;
}
</style>
