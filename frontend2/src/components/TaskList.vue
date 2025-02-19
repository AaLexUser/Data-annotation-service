<template>
  <div class="task-list-page">
    <div class="main-content" :class="{ 'sidebar-open': isSidebarOpen }">
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
                  <img 
                    :src="pair.value1" 
                    :alt="pair.keyBase" 
                    class="photo-img" 
                    @click="openImagePreview(pair.value1)"
                  />
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
                  <img 
                    :src="pair.value1" 
                    alt="photo1" 
                    class="photo-img" 
                    @click="openImagePreview(pair.value1)"
                  />
                </div>
                <div class="pair-col">
                  <img 
                    :src="pair.value2" 
                    alt="photo2" 
                    class="photo-img" 
                    @click="openImagePreview(pair.value2)"
                  />
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
        </div>

        <div v-else class="task-empty">
          <p>Все задания в этом батче аннотированы или индекс вне диапазона.</p>
        </div>
      </div>
    </div>

    <button 
      class="sidebar-toggle" 
      :class="{ 'open': isSidebarOpen }"
      @click="toggleSidebar"
    >
      {{ isSidebarOpen ? '→' : '←' }}
    </button>

    <div v-if="batchMarkup && currentTask" 
         class="markup-container"
         :class="{ 'open': isSidebarOpen }">
      <DynamicMarkup
        :markup="batchMarkup"
        :assessor-id="assessorId"
        :task-id="currentTask.id"
        :is-educational="isEducational"
        @submitted="handleMarkupSubmitted"
      />
      <div class="nav-buttons">
        <button @click="prevTask" :disabled="currentIndex === 0">
          Предыдущая
        </button>
        <button @click="nextTask" :disabled="currentIndex === tasks.length - 1">
          Следующая
        </button>
      </div>
    </div>

    <!-- Image Preview Modal -->
    <div v-if="previewImage" class="image-preview-modal" @click="closeImagePreview">
      <div class="modal-content" @click.stop>
        <button class="close-button" @click="closeImagePreview">&times;</button>
        <img :src="previewImage" alt="Preview" class="preview-image" />
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, computed } from 'vue';
import DynamicMarkup from '@/components/DynamicMarkup.vue';

const { tasks, batchName, assessorId, batchMarkup, isEducational } = defineProps({
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
  },
  isEducational: {
    type: Boolean,
    default: false
  }
});
const emit = defineEmits(['back', 'submitted']);

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
  console.log('Markup submitted:', payload);
  emit('submitted', payload);
  // After submission, automatically move to next task if available
  if (currentIndex.value < tasks.length - 1) {
    nextTask();
  }
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

const isSidebarOpen = ref(true);

function toggleSidebar() {
  isSidebarOpen.value = !isSidebarOpen.value;
}

// Add new refs for image preview
const previewImage = ref(null);

// Add new methods for image preview
function openImagePreview(imageUrl) {
  previewImage.value = imageUrl;
  document.body.style.overflow = 'hidden'; // Prevent scrolling when modal is open
}

function closeImagePreview() {
  previewImage.value = null;
  document.body.style.overflow = ''; // Restore scrolling
}
</script>

<style scoped>
/* Контейнер всей страницы TaskList */
.task-list-page {
  display: flex;
  width: 100%;
  height: calc(100vh - 180px);
  gap: 1.5rem;
  padding: 1rem;
  position: relative;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding-right: 20px;
  transition: padding-right 0.3s ease;
}

.main-content.sidebar-open {
  padding-right: 520px;
}

.back-button {
  background: none;
  border: none;
  color: #4f46e5;
  cursor: pointer;
  font-size: 1rem;
  padding: 0.5rem 1rem;
  display: inline-flex;
  align-items: center;
  transition: color 0.2s ease;
}

.back-button:hover {
  color: #4338ca;
}

/* Область, где показываем задание */
.task-container {
  width: 100%;
  max-width: 1000px;
  flex: 1;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1.5rem;
  margin: 0 auto;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.task-content {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.task-empty {
  text-align: center;
  padding: 2rem;
  color: #64748b;
}

.pair-row {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.pair-col {
  flex: 1;
  min-width: 300px;
  padding: 1rem;
  background-color: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.photo-img {
  width: 100%;
  max-width: 300px;
  height: 300px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  cursor: zoom-in;
  transition: transform 0.2s ease;
}

.photo-img:hover {
  transform: scale(1.02);
}

/* Кнопки навигации */
.nav-buttons {
  padding: 1rem;
  background: white;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-top: auto;
}

.nav-buttons button {
  padding: 0.75rem 1.5rem;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 120px;
}

.nav-buttons button:hover:not(:disabled) {
  background-color: #4338ca;
  transform: translateY(-1px);
}

.nav-buttons button:disabled {
  background-color: #e2e8f0;
  cursor: not-allowed;
}

/* Контейнер для DynamicMarkup */
.markup-container {
  position: fixed;
  top: 100px;
  right: -500px; /* Начальное положение за пределами экрана */
  width: 500px;
  height: calc(100vh - 120px);
  overflow-y: auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: right 0.3s ease;
  display: flex;
  flex-direction: column;
}

.markup-container.open {
  right: 20px;
}

.sidebar-toggle {
  position: fixed;
  top: 120px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 20px;
  background: #4f46e5;
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  transition: all 0.3s ease;
}

.sidebar-toggle:hover {
  background: #4338ca;
  transform: scale(1.05);
}

.sidebar-toggle.open {
  right: 520px;
}

/* Адаптивный дизайн */
@media (max-width: 768px) {
  .task-container {
    padding: 1rem;
  }

  .pair-col {
    min-width: 100%;
  }

  .photo-img {
    max-width: 100%;
    height: 200px;
  }

  .nav-buttons button {
    padding: 0.5rem 1rem;
    min-width: 100px;
  }
}

/* Image Preview Modal Styles */
.image-preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1100;
  padding: 2rem;
  animation: fadeIn 0.2s ease-out;
}

.modal-content {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
  animation: zoomIn 0.2s ease-out;
}

.preview-image {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.close-button {
  position: absolute;
  top: -2rem;
  right: -2rem;
  width: 2.5rem;
  height: 2.5rem;
  background-color: white;
  border: none;
  border-radius: 50%;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a1a1a;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.close-button:hover {
  background-color: #f3f4f6;
  transform: scale(1.1);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes zoomIn {
  from {
    transform: scale(0.95);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
