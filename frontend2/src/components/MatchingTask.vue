<!-- MatchingTask.vue -->
<template>
  <div class="matching-container">
    <h2>Сопоставление значений</h2>

    <!-- Таблица пар -->
    <table class="matching-table">
      <thead>
      <tr>
        <th>Ключ (1)</th>
        <th>Значение (1)</th>
        <th>Ключ (2)</th>
        <th>Значение (2)</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="pair in matchedPairs" :key="pair.keyBase">
        <td>{{ pair.key1 }}</td>
        <td>{{ pair.value1 }}</td>
        <td>{{ pair.key2 }}</td>
        <td>{{ pair.value2 }}</td>
      </tr>
      </tbody>
    </table>

    <!-- Блок кнопок (маркап) -->
    <div class="markup-buttons">
      <button
          v-for="(value, label) in markupOptions"
          :key="label"
          :class="{ selected: selectedMarkup === label }"
          @click="selectMarkup(label)"
      >
        {{ label }}
      </button>
    </div>

    <!-- Кнопка "Сабмит" -->
    <button
        :disabled="!selectedMarkup"
        class="submit-btn"
        @click="submitMarkup"
    >
      Отправить разметку
    </button>

    <!-- Кнопка "Закрыть" или "Следующее" (по желанию) -->
    <button class="close-btn" @click="$emit('close')">Закрыть</button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';

/**
 * Получаем из родителя одну конкретную задачу (task),
 * содержащую как минимум: { id, rowFromBatch: {...} }
 */
const props = defineProps({
  task: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['close']);

/** Пример опций маркапа */
const markupOptions = {
  'Полное совпадение': 'full_match',
  'Частичное совпадение': 'partial_match',
  'Не совпадает': 'no_match'
};

const selectedMarkup = ref(null);

function selectMarkup(label) {
  selectedMarkup.value = label;
}

/**
 * submitMarkup отправляет на сервер разметку (markup)
 * вместе с ID задачи. После успешного ответа можно либо:
 * - Закрыть компонент (emit('close'))
 * - Или загрузить следующую задачу
 */
async function submitMarkup() {
  if (!selectedMarkup.value) return;

  const payload = {
    taskId: props.task.id,
    markup: markupOptions[selectedMarkup.value]
  };

  try {
    await axios.post('/api/v1/submit', payload, { withCredentials: true });
    alert('Разметка отправлена!');
    // Можно автоматически закрывать, либо эмитить событие
    emit('close');
  } catch (error) {
    console.error('Ошибка при отправке разметки', error);
  }
}

/**
 * matchedPairs: вычисляемое свойство.
 * Парсит rowFromBatch и ищет пары, отличающиеся цифрой на конце.
 */
const matchedPairs = computed(() => {
  const rfb = props.task.rowFromBatch || {};
  const entries = Object.entries(rfb); // [ [key, value], ... ]

  const pairsMap = {};

  entries.forEach(([key, value]) => {
    const regex = /^(.*?)([12])$/;
    const match = key.match(regex);
    if (match) {
      const base = match[1];   // например, "title"
      const suffix = match[2]; // "1" или "2"
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
});
</script>

<style scoped>
.matching-container {
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 8px;
  max-width: 800px;
}

.matching-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 16px;
}

.matching-table th, .matching-table td {
  border: 1px solid #ccc;
  padding: 8px;
}

.markup-buttons {
  margin-bottom: 16px;
}

.markup-buttons button {
  margin-right: 8px;
  padding: 6px 12px;
}

.markup-buttons button.selected {
  background-color: #007bff;
  color: white;
}

.submit-btn {
  background-color: #28a745;
  color: white;
  padding: 8px 16px;
  margin-right: 10px;
  border: none;
  border-radius: 4px;
}

.submit-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.close-btn {
  background: none;
  border: 1px solid #ccc;
  padding: 8px 16px;
  border-radius: 4px;
}
</style>
