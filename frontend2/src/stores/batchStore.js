import { defineStore } from 'pinia';

export const useBatchStore = defineStore('batchStore', {
    state: () => ({
        selectedBatch: null
    }),
    actions: {
        setBatch(batch) {
            this.selectedBatch = batch;
        }
    }
});