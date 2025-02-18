import axios from 'axios';

export const honeypotService = {
    createHoneypotTask: async (taskData) => {
        const response = await axios.post('/api/v1/honeypot/task', taskData);
        return response.data;
    },

    getHoneypotTasksByBatch: async (batchId) => {
        const response = await axios.get(`/api/v1/honeypot/batch/${batchId}`);
        return response.data;
    },

    checkAssessorAnswers: async (batchId, assessorId) => {
        const response = await axios.get(`/api/v1/honeypot/check/${batchId}/${assessorId}`);
        return response.data;
    }
}; 