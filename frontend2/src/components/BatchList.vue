<template>
  <div>
    <h2>Выберите батч для разметки</h2>
    <ul class="batch-list">
      <li
          v-for="batch in batches"
          :key="batch.id"
          @click="onBatchClick(batch)"
          class="batch-item"
      >
        {{ batch.name }}
      </li>
    </ul>
  </div>
</template>

<script setup>
/**
 * Пропсы, которые принимает компонент:
 * - batches: массив объектов батчей
 */
defineProps({
  batches: {
    type: Array,
    default: () => []
  }
});

/**
 * Эмитим событие "batchSelected", когда пользователь кликает по батчу.
 */
const emit = defineEmits(['batchSelected']);

function onBatchClick(batch) {
  emit('batchSelected', batch);
}
</script>

<style scoped>
.batch-list {
  list-style: none;
  padding: 0;
}

.batch-item {
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.batch-item:hover {
  background-color: #f0f0f0;
}
</style>
