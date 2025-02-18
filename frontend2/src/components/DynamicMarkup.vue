<template>
  <div class="dynamic-markup">
    <h3>Маркап #{{ markup.id }}</h3>

    <!-- Перебираем объект markup.elements -->
    <div
        v-for="(type, label) in markup.elements"
        :key="label"
        class="form-field"
    >
      <!-- Если тип checkbox -->
      <div v-if="type === 'checkbox'">
        <input
            type="checkbox"
            :id="label"
            :value="label"
            v-model="checkboxSelections"
        />
        <label :for="label">{{ label }}</label>
      </div>

      <!-- Если тип radio -->
      <div v-else-if="type === 'radio'">
        <input
            type="radio"
            :id="label"
            :value="label"
            v-model="radioSelection"
            name="globalRadioGroup"
        />
        <label :for="label">{{ label }}</label>
      </div>

      <!-- Иначе -->
      <div v-else>
        <p>Неизвестный тип: {{ type }}</p>
      </div>
    </div>

    <!-- Кнопка для "Сабмита" -->
    <button @click="submitMarkup" class="submit-btn">
      Отправить
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

/**
 * Принимаем объект markup и данные о задаче/ассессоре.
 * Пример structures:
 * props.markup:
 *   {
 *     "id": 1,
 *     "batchId": { ... },
 *     "elements": {
 *       "Кошка": "checkbox",
 *       "Согласен": "radio",
 *       ...
 *     }
 *   }
 * props.taskId: число (ID задачи)
 * props.assessorId: число (ID ассессора)
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
  }
});

// Для чекбоксов – массив выбранных значений
const checkboxSelections = ref([]);

// Для radio – выбранное значение (строка)
const radioSelection = ref(null);

/**
 * При клике "Отправить" формируем объект в стиле UserMarkupDTO:
 * {
 *   taskId: ...,
 *   assessorId: ...,
 *   selections: {
 *     "Кошка": "checked",
 *     "Согласен": "selected",
 *     ...
 *   }
 * }
 */
async function submitMarkup() {
  // Формируем selections как объект
  let selections = {};

  // 1. Для всех checkbox, которые user отметил, ставим "checked",
  //    а для неотмеченных – либо "", либо не записываем.
  //    Чтобы узнать ВСЕ checkbox, пробегаемся по markup.elements
  //    и смотрим, где type === 'checkbox'.
  Object.entries(props.markup.elements).forEach(([label, type]) => {
    if (type === 'checkbox') {
      if (checkboxSelections.value.includes(label)) {
        // Отмечено
        selections[label] = "checked";
      } else {
        // Не отмечено — можно поставить "" или не добавлять.
        selections[label] = "";
      }
    }
  });

  // 2. Для radio у нас один выбранный label (radioSelection.value).
  //    Для всех radio-элементов в markup.elements, если label === выбранное,
  //    пишем "selected", иначе — "".
  Object.entries(props.markup.elements).forEach(([label, type]) => {
    if (type === 'radio') {
      if (radioSelection.value === label) {
        selections[label] = "selected";
      } else {
        selections[label] = "";
      }
    }
  });

  // 3. Собираем payload для UserMarkupDTO
  const payload = {
    taskId: props.taskId,
    assessorId: props.assessorId,
    selections: selections
  };
  console.log(payload.taskId)
  console.log(payload.assessorId)
  console.log('Сабмитим payload:', payload);

  try {
    // 4. Отправляем POST-запрос
    const response = await axios.post('/api/v1/markup/submitUserMarkup', payload, {
      withCredentials: true
    });
    alert('Разметка успешно сохранена: ' + response.data);
  } catch (error) {
    if (error.response && error.response.status === 409) {
      alert('Вы уже аннотировали эту задачу!');
    } else {
      alert('Ошибка при сохранении разметки');
    }
  }
}
</script>

<style scoped>
.dynamic-markup {
  border: 1px solid #ccc;
  padding: 16px;
  border-radius: 8px;
  max-width: 500px;
}

.form-field {
  margin-bottom: 8px;
}

.submit-btn {
  margin-top: 12px;
  background-color: #28a745;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
}
</style>
