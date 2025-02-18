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
      <!-- Динамически создаем элементы формы на основе markup.elements -->
      <div
          v-for="(type, label) in markup.elements"
          :key="label"
          class="form-field"
      >
        <!-- Чекбоксы -->
        <div v-if="type === 'checkbox'" class="checkbox-field">
          <input
              type="checkbox"
              :id="label"
              :value="label"
              v-model="checkboxSelections"
              :disabled="isSubmitting"
          />
          <label :for="label">{{ label }}</label>
        </div>

        <!-- Радио кнопки -->
        <div v-else-if="type === 'radio'" class="radio-field">
          <input
              type="radio"
              :id="label"
              :value="label"
              v-model="radioSelection"
              name="globalRadioGroup"
              :disabled="isSubmitting"
          />
          <label :for="label">{{ label }}</label>
        </div>

        <!-- Обработка неизвестных типов -->
        <div v-else>
          <p class="error-text">Неизвестный тип: {{ type }}</p>
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

.form-field {
  margin-bottom: 1rem;
  padding: 0.75rem;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.form-field:hover {
  background-color: #f7fafc;
}

.form-field label {
  display: block;
  margin-left: 0.5rem;
  color: #4a5568;
  font-weight: 500;
  cursor: pointer;
}

input[type="checkbox"],
input[type="radio"] {
  width: 1.25rem;
  height: 1.25rem;
  margin-right: 0.5rem;
  cursor: pointer;
  border: 2px solid #cbd5e0;
  border-radius: 4px;
  transition: all 0.2s ease;
}

input[type="checkbox"]:checked,
input[type="radio"]:checked {
  background-color: #4f46e5;
  border-color: #4f46e5;
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
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 1;
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
