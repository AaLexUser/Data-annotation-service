<template>
  <div>
    <button class="back-button" @click="$emit('back')">← Назад к выбору батча</button>
    <h2>Батч: {{ batchName }}</h2>

    <div v-if="currentTask">
      <h3>Задача №{{ currentTask.id }}</h3>

      <!-- Пары ключей -->
      <div
          class="pair-row"
          v-for="pair in getPairs(currentTask.rowFromBatch)"
          :key="pair.keyBase"
      >
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

      <!-- Навигация -->
      <div class="nav-buttons">
        <button @click="prevTask" :disabled="currentIndex === 0" >
          Предыдущая
        </button>
        <button @click="nextTask" :disabled="currentIndex === tasks.length - 1">
          Следующая
        </button>
      </div>
    </div>
    <div v-else>
      <p>Все задания в этом батче аннотированы или индекс вне диапазона.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const { tasks, batchName } = defineProps({
  tasks: {
    type: Array,
    default: () => []
  },
  batchName: {
    type: String,
    default: ''
  }
});

const currentIndex = ref(0);

// currentTask — берём элемент массива по currentIndex
const currentTask = computed(() => {
  return tasks[currentIndex.value] || null;
});

// Кнопка "предыдущая"
function prevTask() {
  if (currentIndex.value > 0) {
    currentIndex.value--;
  }
}

// Кнопка "следующая"
function nextTask() {
  if (currentIndex.value < tasks.length - 1) {
    currentIndex.value++;
  }
}

/** Формируем пары по суффиксу 1/2 */
function getPairs(row) {
  if (!row) return [];
  const entries = Object.entries(row);
  const pairsMap = {};
  const regex = /^(.*?)([12])$/;

  entries.forEach(([key, value]) => {
    const match = key.match(regex);
    if (match) {
      const base = match[1];
      const suffix = match[2];
      if (!pairsMap[base]) {
        pairsMap[base] = { keyBase: base, key1: '', value1: '', key2: '', value2: '' };
      }
      if (suffix === '1') {
        pairsMap[base].key1 = key;
        pairsMap[base].value1 = value;
      } else {
        pairsMap[base].key2 = key;
        pairsMap[base].value2 = value;
      }
    }
  });

  return Object.values(pairsMap);
}
</script>

<style scoped>
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

.nav-buttons {
  margin-top: 12px;
}
.nav-buttons button {
  margin-right: 8px;
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
.nav-buttons {
  width: 150px;
}
</style>
