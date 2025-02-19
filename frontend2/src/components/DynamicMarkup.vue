DynamicMarkup.vue
/**
 * DynamicMarkup.vue
 * Компонент для динамической разметки заданий.
 * Поддерживает два типа элементов: checkbox и radio.
 * Обрабатывает образовательные задачи с проверкой правильности ответов.
 */
<template>
  <div class="dynamic-markup">
    <h3>Маркап #{{ markup.id }}</h3>

    <!-- Форма для отправки разметки -->
    <form @submit.prevent="submitMarkup" class="markup-form">
      <!-- Группируем элементы по типу -->
      <div class="markup-sections">
        <!-- Секция с радио кнопками -->
        <div v-if="hasRadioButtons" class="section-container">
          <h4 class="section-title">Выберите один вариант</h4>
          <div class="button-group radio-group">
            <button
              v-for="(type, label) in radioElements"
              :key="label"
              type="button"
              class="choice-button radio-button"
              :class="{ 'selected': radioSelection === label }"
              @click="radioSelection = label"
              :disabled="isSubmitting"
            >
              <span class="button-content">
                <span class="radio-indicator" :class="{ 'selected': radioSelection === label }"></span>
                <span class="button-label">{{ label }}</span>
              </span>
            </button>
          </div>
        </div>

        <!-- Секция с чекбоксами -->
        <div v-if="hasCheckboxes" class="section-container">
          <h4 class="section-title">Выберите один или несколько вариантов</h4>
          <div class="button-group checkbox-group">
            <button
              v-for="(type, label) in checkboxElements"
              :key="label"
              type="button"
              class="choice-button checkbox-button"
              :class="{ 'selected': checkboxSelections.includes(label) }"
              @click="toggleCheckbox(label)"
              :disabled="isSubmitting"
            >
              <span class="button-content">
                <span class="checkbox-indicator" :class="{ 'selected': checkboxSelections.includes(label) }">
                  <svg v-if="checkboxSelections.includes(label)" viewBox="0 0 24 24" class="check-icon">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
                  </svg>
                </span>
                <span class="button-label">{{ label }}</span>
              </span>
            </button>
          </div>
        </div>
      </div>

      <!-- Кнопка отправки -->
      <button 
        type="submit" 
        class="submit-btn" 
        :disabled="isSubmitting || !hasSelection"
      >
        <span v-if="isSubmitting" class="loader"></span>
        {{ isSubmitting ? 'Отправка...' : 'Отправить' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, watch, inject } from 'vue';
import axios from 'axios';

// Get the global toast instance
const toast = inject('toast');

/**
 * Props компонента:
 * @prop {Object} markup - Объект разметки с полями id, batchId и elements
 * @prop {Number} taskId - ID текущей задачи
 * @prop {Number} assessorId - ID ассессора
 * @prop {Boolean} isEducational - Флаг образовательной задачи
 */
const props = defineProps({
  markup: {
    type: Object,
    required: true
  },
  taskId: {
    type: Number,
    required: true
  },
  assessorId: {
    type: Number,
    required: true
  },
  isEducational: {
    type: Boolean,
    required: false,
    default: false
  }
});

// Emit события submitted при успешной отправке
const emit = defineEmits(['submitted']);

// Состояние компонента
const checkboxSelections = ref([]); // Выбранные чекбоксы
const radioSelection = ref(null);    // Выбранная радио-кнопка
const isSubmitting = ref(false);     // Флаг отправки формы

// Сброс выбора при смене задачи
watch(() => props.taskId, () => {
  checkboxSelections.value = [];
  radioSelection.value = null;
});

// Проверка наличия выбранных элементов
const hasSelection = computed(() => {
  return checkboxSelections.value.length > 0 || radioSelection.value !== null;
});

// Computed properties для группировки элементов
const radioElements = computed(() => {
  return Object.fromEntries(
    Object.entries(props.markup.elements)
      .filter(([_, type]) => type === 'radio')
  );
});

const checkboxElements = computed(() => {
  return Object.fromEntries(
    Object.entries(props.markup.elements)
      .filter(([_, type]) => type === 'checkbox')
  );
});

const hasRadioButtons = computed(() => Object.values(radioElements.value).length > 0);
const hasCheckboxes = computed(() => Object.values(checkboxElements.value).length > 0);

// Метод для переключения чекбоксов
function toggleCheckbox(label) {
  const index = checkboxSelections.value.indexOf(label);
  if (index === -1) {
    checkboxSelections.value.push(label);
  } else {
    checkboxSelections.value.splice(index, 1);
  }
}

/**
 * Отправка формы
 * Формирует объект с выбранными значениями и отправляет на сервер
 * Для образовательных задач также проверяет правильность ответа
 */
async function submitMarkup() {
  if (isSubmitting.value) return;
  
  isSubmitting.value = true;
  let selections = {};

  try {
    // Формируем объект selections
    Object.entries(props.markup.elements).forEach(([label, type]) => {
      if (type === 'checkbox') {
        selections[label] = checkboxSelections.value.includes(label) ? "checked" : "";
      } else if (type === 'radio') {
        selections[label] = radioSelection.value === label ? "selected" : "";
      }
    });

    const payload = {
      taskId: props.taskId,
      assessorId: props.assessorId,
      selections: selections
    };

    // Отправляем разметку
    await axios.post('/api/v1/markup/submitUserMarkup', payload, {
      withCredentials: true
    });

    // Проверяем ответы для образовательных задач
    if (props.isEducational) {
      try {
        const response = await axios.get('/api/v1/educational/check-answers', {
          params: {
            taskId: props.taskId,
            assessorId: props.assessorId
          },
          withCredentials: true
        });
        showEducationalFeedback(response.data);
      } catch (eduError) {
        console.error('Error checking educational answers:', eduError);
        showError('Ошибка при проверке ответов');
      }
    }

    emit('submitted', payload);
    
    // Сброс формы после успешной отправки
    checkboxSelections.value = [];
    radioSelection.value = null;

  } catch (error) {
    console.error('Error submitting markup:', error);
    showError('Ошибка при отправке ответа');
  } finally {
    isSubmitting.value = false;
  }
}

function showEducationalFeedback(result) {
  const isCorrect = result.isCorrect;
  const correctAnswer = result.correctAnswer;

  if (isCorrect) {
    toast.success('✅ Отлично! Правильный ответ!');
  } else {
    const correctAnswers = Object.entries(correctAnswer)
      .filter(([_, value]) => value === 'selected' || value === 'checked')
      .map(([label]) => label)
      .join(', ');
    toast.error(`❌ Не совсем верно. Правильный ответ: ${correctAnswers}`);
  }
}

function showError(message) {
  toast.error(message);
}
</script>

<style scoped>
.dynamic-markup {
  padding: 1.5rem;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.markup-form {
  flex: 1;
  overflow-y: auto;
  padding-right: 0.5rem;
}

.dynamic-markup h3 {
  color: #2d3748;
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.section-container {
  margin-bottom: 2rem;
}

.section-title {
  color: #4a5568;
  font-size: 1.1rem;
  margin-bottom: 1rem;
  font-weight: 500;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.choice-button {
  width: 100%;
  padding: 0.75rem 1rem;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.choice-button:hover:not(:disabled) {
  border-color: #4f46e5;
  background: #f8fafc;
}

.choice-button.selected {
  border-color: #4f46e5;
  background: #eef2ff;
}

.choice-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.button-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.radio-indicator {
  width: 20px;
  height: 20px;
  border: 2px solid #cbd5e0;
  border-radius: 50%;
  position: relative;
  flex-shrink: 0;
}

.radio-indicator.selected {
  border-color: #4f46e5;
}

.radio-indicator.selected::after {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  background: #4f46e5;
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.checkbox-indicator {
  width: 20px;
  height: 20px;
  border: 2px solid #cbd5e0;
  border-radius: 6px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.checkbox-indicator.selected {
  border-color: #4f46e5;
  background: #4f46e5;
}

.check-icon {
  width: 16px;
  height: 16px;
  fill: white;
}

.button-label {
  font-weight: 500;
  color: #2d3748;
}

.submit-btn {
  position: sticky;
  bottom: 0;
  margin-top: 1.5rem;
  width: 100%;
  padding: 0.75rem;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 1;
}

.submit-btn:hover:not(:disabled) {
  background-color: #4338ca;
  transform: translateY(-1px);
}

.submit-btn:disabled {
  background-color: #cbd5e0;
  cursor: not-allowed;
  transform: none;
}

.submit-btn .loader {
  display: inline-block;
  width: 1.5rem;
  height: 1.5rem;
  border: 2px solid #ffffff;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 1s linear infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Remove all educational toast related styles */
.educational-toast,
.educational-toast.show,
.educational-toast.hide,
.toast-content,
.toast-header,
.toast-body,
.educational-toast.correct,
.educational-toast.incorrect,
.correct .toast-header,
.incorrect .toast-header,
.correct-answer,
.answer-item,
.answer-label,
.educational-feedback-modal,
.feedback-content,
.close-feedback {
  /* These styles are no longer needed as we're using the global toast */
}
</style>
