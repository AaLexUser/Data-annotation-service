<template>
  <div class="task-list-page">
    <button class="back-button" @click="$emit('back')">
      ← Назад к выбору батча
    </button>
    <h2>Батч: {{ batchName }}</h2>

    <div class="task-container">
      <div v-if="currentTask" class="task-content">
        <h3>Задача №{{ currentTask.id }}</h3>

        <!-- Определяем, какой тип данных отображать -->
        <div v-if="taskData.isSingleColumn">
          <!-- Одиночные элементы (если нет xxx1 и xxx2) -->
          <div class="single-col" v-for="pair in taskData.pairs" :key="pair.keyBase">
            <template v-if="pair.keyBase.toLowerCase().includes('photo')">
              <div class="single-photo">
                <img :src="pair.value1" :alt="pair.keyBase" class="photo-img" />
              </div>
            </template>
            <template v-else>
              <div class="single-text">
                <strong>{{ pair.key1 }}:</strong> {{ pair.value1 }}
              </div>
            </template>
          </div>
        </div>

        <div v-else>
          <!-- Парные элементы -->
          <div class="pair-row" v-for="pair in taskData.pairs" :key="pair.keyBase">
            <template v-if="pair.keyBase.toLowerCase().includes('photo')">
              <div class="pair-col">
                <img :src="pair.value1" alt="photo1" class="photo-img" />
              </div>
              <div class="pair-col">
                <img :src="pair.value2" alt="photo2" class="photo-img" />
              </div>
            </template>
            <template v-else>
              <div class="pair-col">
                <strong>{{ pair.key1 }}:</strong> {{ pair.value1 }}
              </div>
              <div class="pair-col">
                <strong>{{ pair.key2 }}:</strong> {{ pair.value2 }}
              </div>
            </template>
          </div>
        </div>

        <div class="nav-buttons">
          <button @click="prevTask" :disabled="currentIndex === 0">
            Предыдущая
          </button>
          <button @click="nextTask" :disabled="currentIndex === tasks.length - 1">
            Следующая
          </button>
        </div>
      </div>

      <div v-else class="task-empty">
        <p>Все задания в этом батче аннотированы или индекс вне диапазона.</p>
      </div>
    </div>

    <DynamicMarkup
        v-if="batchMarkup"
        class="markup-container"
        :markup="batchMarkup"
        :assessor-id = "assessorId"
        :task-id = "currentTask.id"
        @submitted="handleMarkupSubmitted"
    />
  </div>
</template>


<script setup>
import { ref, computed } from 'vue';
import DynamicMarkup from '@/components/DynamicMarkup.vue';

const { tasks, batchName, assessorId, batchMarkup } = defineProps({
  tasks: {
    type: Array,
    default: () => []
  },
  batchName: {
    type: String,
    default: ''
  },
  batchMarkup: {
    type: Object,
    default: null
  },
  assessorId:{
    type: Number,
    default: null
  }
});
const emit = defineEmits(['back']);

const currentIndex = ref(0);

const currentTask = computed(() => {
  return tasks[currentIndex.value] || null;
});

function prevTask() {
  if (currentIndex.value > 0) {
    currentIndex.value--;
  }
}

function nextTask() {
  if (currentIndex.value < tasks.length - 1) {
    currentIndex.value++;
  }
}

function handleMarkupSubmitted(payload) {
  console.log('Сабмит динамического маркапа:', payload);
  // здесь ваша логика, если нужно
}
const taskData = computed(() => {
  return currentTask.value ? getPairs(currentTask.value.rowFromBatch) : { isSingleColumn: false, pairs: [] };
});
/** Собираем пары (xxx1, xxx2) */
function getPairs(row) {
  if (!row) return [];

  const entries = Object.entries(row);
  const pairsMap = {};
  const regex = /^(.*?)([12])$/;
  let isSingleColumn = true; // Флаг, который определяет, что это одиночные элементы

  entries.forEach(([key, value]) => {
    const match = key.match(regex);
    if (match) {
      isSingleColumn = false; // Нашли парный элемент, переключаем в режим сравнения
      const base = match[1];
      const suffix = match[2];

      if (!pairsMap[base]) {
        pairsMap[base] = {
          keyBase: base,
          key1: '',
          value1: '',
          key2: '',
          value2: ''
        };
      }
      if (suffix === '1') {
        pairsMap[base].key1 = key;
        pairsMap[base].value1 = value;
      } else {
        pairsMap[base].key2 = key;
        pairsMap[base].value2 = value;
      }
    } else {
      // Одиночные элементы
      pairsMap[key] = {
        keyBase: key,
        key1: key,
        value1: value,
        key2: null,
        value2: null
      };
    }
  });

  return {
    isSingleColumn,
    pairs: Object.values(pairsMap)
  };
}
</script>

<style scoped>
/* Контейнер всей страницы TaskList */
.task-list-page {
  display: flex;
  flex-direction: column;
  height: 100vh;

  /*width: 100%;

  /* Можно добавить min-height, если нужно */
  /* min-height: 100vh; */
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

/* Область, где показываем задание */
.task-container {
  width: 1000px;
  min-height: 400px;
  max-height: 80vh;
  overflow-y: auto;

  /* Остальное */
  background-color: #fdfdfd;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.task-content {
  width: 100%;
}

.task-empty {
  text-align: center;
  width: 100%;
}

.pair-row {
  display: flex;
  margin-bottom: 8px;
}
.pair-col {
  flex: 1;
  padding-right: 10px;
}
.photo-img {
  max-width: 200px;
  height: 200px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

/* Кнопки "Предыдущая" / "Следующая" */
.nav-buttons {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}
.nav-buttons button {
  padding: 6px 12px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
}
.nav-buttons button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Контейнер для DynamicMarkup */
.markup-container {

  margin: 0 auto;
  width: 300px;
  /* Можно зафиксировать ширину, если надо */
}
</style>
