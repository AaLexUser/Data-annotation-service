<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>Educational Batch Statistics</h2>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="modal-body">
        <div v-if="loading" class="loading">Loading statistics...</div>
        <div v-else>
          <div class="overall-stats">
            <div class="stat-card">
              <div class="stat-circle">
                <svg viewBox="0 0 36 36" class="circular-chart">
                  <path d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                    fill="none"
                    stroke="#e5e7eb"
                    stroke-width="3.8"
                  />
                  <path d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                    fill="none"
                    :stroke="getProgressColor(100 - averageErrorRate)"
                    stroke-width="3.8"
                    :stroke-dasharray="`${100 - averageErrorRate}, 100`"
                    class="progress"
                  />
                  <g class="percentage-group" transform="rotate(90, 18, 18)">
                    <text x="18" y="16" class="percentage-value">{{ formatPercent(100 - averageErrorRate) }}</text>
                    <text x="18" y="24" class="percentage-symbol">%</text>
                  </g>
                </svg>
                <div class="stat-info">
                  <span class="stat-label">Success Rate</span>
                  <span class="stat-sublabel">Avg. Correct Answers</span>
                </div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-circle">
                <svg viewBox="0 0 36 36" class="circular-chart">
                  <path d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                    fill="none"
                    stroke="#e5e7eb"
                    stroke-width="3.8"
                  />
                  <path d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                    fill="none"
                    :stroke="getProgressColor(averageCompletionRate)"
                    stroke-width="3.8"
                    :stroke-dasharray="`${averageCompletionRate}, 100`"
                    class="progress"
                  />
                  <g class="percentage-group" transform="rotate(90, 18, 18)">
                    <text x="18" y="16" class="percentage-value">{{ formatPercent(averageCompletionRate) }}</text>
                    <text x="18" y="24" class="percentage-symbol">%</text>
                  </g>
                </svg>
                <div class="stat-info">
                  <span class="stat-label">Completion Rate</span>
                  <span class="stat-sublabel">Avg. Tasks Done</span>
                </div>
              </div>
            </div>

            <div class="stat-card assessors">
              <div class="stat-number">
                <span class="number">{{ stats.length }}</span>
                <i class="fas fa-users icon"></i>
              </div>
              <div class="stat-info">
                <span class="stat-label">Total Assessors</span>
                <span class="stat-sublabel">Active Participants</span>
              </div>
            </div>

            <div class="stat-card tasks">
              <div class="stat-number">
                <span class="number">{{ totalTasks }}</span>
                <i class="fas fa-tasks icon"></i>
              </div>
              <div class="stat-info">
                <span class="stat-label">Total Tasks</span>
                <span class="stat-sublabel">Completed Tasks</span>
              </div>
            </div>
          </div>

          <div style="height: 400px; width: 100%;">
            <AgGridVue
              class="ag-theme-alpine"
              style="height: 100%; width: 100%;"
              :columnDefs="columnDefs"
              :rowData="rowData"
              :defaultColDef="defaultColDef"
              :pinnedBottomRowData="pinnedBottomRowData"
              :pagination="true"
              :paginationAutoPageSize="true"
              :enableRangeSelection="true"
              :animateRows="true"
              @grid-ready="onGridReady"
            />
          </div>

          <div v-if="stats.length === 0" class="no-data">
            No statistics available for this batch
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { AgGridVue } from 'ag-grid-vue3';
import { ModuleRegistry, ClientSideRowModelModule } from 'ag-grid-community';
import axios from 'axios';

// Register required modules
ModuleRegistry.register(ClientSideRowModelModule);

const props = defineProps({
  batchId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['close']);

const stats = ref([]);
const loading = ref(true);
const gridApi = ref(null);

// AG Grid column definitions
const columnDefs = ref([
  { 
    field: 'login', 
    headerName: 'Assessor', 
    sortable: true, 
    filter: true,
    filterParams: {
      buttons: ['reset', 'apply'],
      closeOnApply: true
    },
    pinned: 'left'
  },
  { 
    field: 'totalTasks', 
    headerName: 'Total Tasks', 
    sortable: true, 
    filter: 'agNumberColumnFilter',
    filterParams: {
      buttons: ['apply', 'reset'],
      closeOnApply: true
    },
    valueFormatter: params => params.value.toLocaleString()
  },
  { 
    field: 'correctAnswers', 
    headerName: 'Correct Answers', 
    sortable: true, 
    filter: 'agNumberColumnFilter',
    filterParams: {
      buttons: ['apply', 'reset'],
      closeOnApply: true
    },
    valueFormatter: params => params.value.toLocaleString()
  },
  { 
    field: 'errorRate', 
    headerName: 'Error Rate', 
    sortable: true, 
    filter: 'agNumberColumnFilter',
    filterParams: {
      buttons: ['apply', 'reset'],
      closeOnApply: true
    },
    valueFormatter: params => `${formatPercent(params.value)}%`,
    cellClass: params => {
      if (params.value <= 10) return 'success-rate';
      if (params.value <= 30) return 'warning-rate';
      return 'error-rate';
    }
  },
  { 
    field: 'completionRate', 
    headerName: 'Completion Rate', 
    sortable: true, 
    filter: 'agNumberColumnFilter',
    filterParams: {
      buttons: ['apply', 'reset'],
      closeOnApply: true
    },
    valueFormatter: params => `${formatPercent(params.value)}%`,
    cellClass: params => {
      if (params.value >= 90) return 'success-rate';
      if (params.value >= 50) return 'warning-rate';
      return 'error-rate';
    }
  }
]);

// Default column definitions
const defaultColDef = {
  flex: 1,
  resizable: true,
  minWidth: 120,
  sortable: true,
  filter: true,
  floatingFilter: true
};

const formatPercent = (value) => {
  return value?.toFixed(1) ?? '0.0';
};

// Computed row data from stats
const rowData = computed(() => stats.value);

// Computed pinned bottom row (summary)
const pinnedBottomRowData = computed(() => {
  if (stats.value.length === 0) return [];
  
  return [{
    login: 'Summary',
    totalTasks: totalTasks.value,
    correctAnswers: totalCorrectAnswers.value,
    errorRate: averageErrorRate.value,
    completionRate: averageCompletionRate.value
  }];
});

const onGridReady = (params) => {
  gridApi.value = params.api;
  params.api.sizeColumnsToFit();
};

const fetchStats = async () => {
  try {
    const response = await axios.get(`/api/v1/stats/educational/assessors`, {
      params: { batchId: props.batchId },
      withCredentials: true
    });
    stats.value = response.data;
  } catch (error) {
    console.error('Error fetching educational stats:', error);
  } finally {
    loading.value = false;
  }
};

// Computed summary statistics
const totalTasks = computed(() => {
  return stats.value.reduce((sum, stat) => sum + stat.totalTasks, 0);
});

const totalCorrectAnswers = computed(() => {
  return stats.value.reduce((sum, stat) => sum + stat.correctAnswers, 0);
});

const averageErrorRate = computed(() => {
  if (stats.value.length === 0) return 0;
  return stats.value.reduce((sum, stat) => sum + stat.errorRate, 0) / stats.value.length;
});

const averageCompletionRate = computed(() => {
  if (stats.value.length === 0) return 0;
  return stats.value.reduce((sum, stat) => sum + stat.completionRate, 0) / stats.value.length;
});

const getProgressColor = (value) => {
  if (value >= 90) return '#22c55e'; // Bright green
  if (value >= 70) return '#3b82f6'; // Bright blue
  if (value >= 50) return '#f59e0b'; // Bright orange
  return '#ef4444'; // Bright red
};

onMounted(() => {
  fetchStats();
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 1200px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #eef2f7;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  color: #2c3e50;
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0;
}

.modal-body {
  padding: 24px;
}

.overall-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
  padding: 24px;
  background-color: #ffffff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 24px;
  background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  transition: all 0.3s ease;
  border: 1px solid rgba(226, 232, 240, 0.8);
  min-width: 250px;
  position: relative;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px -4px rgba(0, 0, 0, 0.1), 0 4px 8px -4px rgba(0, 0, 0, 0.06);
  border-color: rgba(226, 232, 240, 1);
}

.stat-circle {
  position: relative;
  width: 180px;
  height: 180px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0;
  flex-shrink: 0;
  z-index: 2;
}

.stat-info {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: -20px;
  z-index: 1;
  padding-top: 24px;
}

.circular-chart {
  transform: rotate(-90deg);
  width: 180px !important;
  height: 180px !important;
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.1));
  position: relative;
  z-index: 2;
}

.percentage-value {
  font-family: 'Inter', sans-serif;
  font-size: 12px;
  font-weight: 700;
  fill: #1e293b;
  text-anchor: middle;
  dominant-baseline: middle;
}

.percentage-symbol {
  font-family: 'Inter', sans-serif;
  font-size: 8px;
  font-weight: 600;
  fill: #64748b;
  text-anchor: middle;
  dominant-baseline: middle;
}

.percentage-group {
  filter: drop-shadow(0 1px 1px rgba(0, 0, 0, 0.1));
}

.stat-label {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.025em;
  margin-top: 4px;
}

.stat-sublabel {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
  margin-top: -2px;
}

.stat-number {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.number {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1e293b;
  font-family: 'Inter', sans-serif;
  line-height: 1;
  text-align: center;
}

.icon {
  font-size: 1.5rem;
  color: #64748b;
  background-color: #f1f5f9;
  padding: 12px;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.assessors .icon {
  color: #6366f1;
  background-color: #eef2ff;
}

.tasks .icon {
  color: #14b8a6;
  background-color: #f0fdfa;
}

@keyframes progress {
  0% {
    stroke-dasharray: 0 100;
  }
}

.progress {
  animation: progress 1s ease-out forwards;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #64748b;
}

.no-data {
  text-align: center;
  padding: 2rem;
  color: #64748b;
  background-color: #f8fafc;
  border-radius: 8px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #94a3b8;
  padding: 4px;
  line-height: 1;
  transition: color 0.2s ease;
}

.close-btn:hover {
  color: #64748b;
}

:deep(.success-rate) {
  background-color: #dcfce7 !important;
  color: #166534;
}

:deep(.warning-rate) {
  background-color: #fef9c3 !important;
  color: #854d0e;
}

:deep(.error-rate) {
  background-color: #fee2e2 !important;
  color: #991b1b;
}

:deep(.ag-row-pinned) {
  background-color: #f8fafc !important;
  font-weight: 600;
}

:deep(.ag-header-cell-filtered) {
  background-color: #eff6ff !important;
  color: #1e40af;
}

.stat-card.assessors, .stat-card.tasks {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding-top: 32px;
  padding-bottom: 32px;
}

.stat-card.assessors .stat-info, .stat-card.tasks .stat-info {
  margin-top: 0;
  padding-top: 12px;
}
</style> 