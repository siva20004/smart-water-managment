import apiClient from './apiClient';
import type { HealthResponse } from '../types/health';

/**
 * Health API service
 * Module 1: Diagnostic endpoint to verify system foundation connectivity.
 */
export const healthService = {
  async checkHealth(): Promise<HealthResponse> {
    const response = await apiClient.get<HealthResponse>('/health');
    return response.data;
  },
};
