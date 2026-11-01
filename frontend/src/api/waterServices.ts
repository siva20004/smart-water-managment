import apiClient from './apiClient';
import type {
  ApiResponse,
  PageResponse,
  Customer,
  WaterMeter,
  MeterReading,
  WaterBill,
  Payment,
  LeakAlert,
  WaterQualityRecord,
  WaterQualityStats,
  MaintenanceRequest,
  AnalyticsSummary,
  User,
  Location,
} from '../types/models';

// Authentication & Identity
export const authService = {
  login: async (username: string, password: string): Promise<{ token: string; user: User }> => {
    const res = await apiClient.post<ApiResponse<{ token: string; user: User }>>('/auth/login', { username, password });
    if (res.data.data.token) {
      localStorage.setItem('auth_token', res.data.data.token);
      localStorage.setItem('auth_user', JSON.stringify(res.data.data.user));
    }
    return res.data.data;
  },
  getCurrentUser: async (): Promise<User> => {
    const res = await apiClient.get<ApiResponse<User>>('/auth/me');
    return res.data.data;
  },
  getDemoAccounts: async (): Promise<Array<{ username: string; role: string; description: string }>> => {
    const res = await apiClient.get<ApiResponse<Array<{ username: string; role: string; description: string }>>>('/auth/demo-accounts');
    return res.data.data;
  },
  logout: () => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
  },
};

// Customer Service
export const customerService = {
  getAll: async (params?: { search?: string; type?: string; status?: string; page?: number; size?: number }): Promise<PageResponse<Customer>> => {
    const res = await apiClient.get<ApiResponse<PageResponse<Customer>>>('/customers', { params });
    return res.data.data;
  },
  getById: async (id: number): Promise<Customer> => {
    const res = await apiClient.get<ApiResponse<Customer>>(`/customers/${id}`);
    return res.data.data;
  },
  create: async (data: Partial<Customer>): Promise<Customer> => {
    const res = await apiClient.post<ApiResponse<Customer>>('/customers', data);
    return res.data.data;
  },
  update: async (id: number, data: Partial<Customer>): Promise<Customer> => {
    const res = await apiClient.put<ApiResponse<Customer>>(`/customers/${id}`, data);
    return res.data.data;
  },
  setStatus: async (id: number, status: string): Promise<Customer> => {
    const res = await apiClient.patch<ApiResponse<Customer>>(`/customers/${id}/status`, { status });
    return res.data.data;
  },
  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/customers/${id}`);
  },
};

// Water Meter Service
export const meterService = {
  getAll: async (params?: { search?: string; status?: string; customerId?: number; locationId?: number; page?: number; size?: number }): Promise<PageResponse<WaterMeter>> => {
    const res = await apiClient.get<ApiResponse<PageResponse<WaterMeter>>>('/meters', { params });
    return res.data.data;
  },
  getById: async (id: number): Promise<WaterMeter> => {
    const res = await apiClient.get<ApiResponse<WaterMeter>>(`/meters/${id}`);
    return res.data.data;
  },
  create: async (data: Partial<WaterMeter>): Promise<WaterMeter> => {
    const res = await apiClient.post<ApiResponse<WaterMeter>>('/meters', data);
    return res.data.data;
  },
  update: async (id: number, data: Partial<WaterMeter>): Promise<WaterMeter> => {
    const res = await apiClient.put<ApiResponse<WaterMeter>>(`/meters/${id}`, data);
    return res.data.data;
  },
  setStatus: async (id: number, status: string): Promise<WaterMeter> => {
    const res = await apiClient.patch<ApiResponse<WaterMeter>>(`/meters/${id}/status`, { status });
    return res.data.data;
  },
  assign: async (id: number, customerId: number | null): Promise<WaterMeter> => {
    const res = await apiClient.patch<ApiResponse<WaterMeter>>(`/meters/${id}/assign`, { customerId });
    return res.data.data;
  },
};

// Meter Reading Service
export const readingService = {
  getAll: async (params?: { meterId?: number; page?: number; size?: number }): Promise<PageResponse<MeterReading>> => {
    const res = await apiClient.get<ApiResponse<PageResponse<MeterReading>>>('/readings', { params });
    return res.data.data;
  },
  getByMeter: async (meterId: number, params?: { page?: number; size?: number }): Promise<PageResponse<MeterReading>> => {
    const res = await apiClient.get<ApiResponse<PageResponse<MeterReading>>>(`/readings/meter/${meterId}`, { params });
    return res.data.data;
  },
  submit: async (data: {
    meterId: number;
    readingTimestamp: string;
    cumulativeReadingM3: number;
    waterFlowRateLpm?: number;
    waterPressureBar?: number;
    waterTemperatureCelsius?: number;
    readingSource?: string;
    meterReset?: boolean;
    validationNotes?: string;
  }): Promise<MeterReading> => {
    const res = await apiClient.post<ApiResponse<MeterReading>>('/readings', data);
    return res.data.data;
  },
};

// Billing Service
export const billingService = {
  getAllBills: async (params?: { customerId?: number; status?: string; page?: number; size?: number }): Promise<PageResponse<WaterBill>> => {
    const res = await apiClient.get<ApiResponse<PageResponse<WaterBill>>>('/bills', { params });
    return res.data.data;
  },
  getBillById: async (id: number): Promise<WaterBill> => {
    const res = await apiClient.get<ApiResponse<WaterBill>>(`/bills/${id}`);
    return res.data.data;
  },
  generateBill: async (data: {
    customerId: number;
    meterId?: number;
    billingPeriodStart: string;
    billingPeriodEnd: string;
    simulatedBaseFee?: number;
    simulatedVolumetricRate?: number;
    taxRatePercent?: number;
    dueDate?: string;
  }): Promise<WaterBill> => {
    const res = await apiClient.post<ApiResponse<WaterBill>>('/bills/generate', data);
    return res.data.data;
  },
  recordPayment: async (data: {
    billId: number;
    amount: number;
    paymentMethod: string;
    notes?: string;
  }): Promise<Payment> => {
    const res = await apiClient.post<ApiResponse<Payment>>('/bills/payments', data);
    return res.data.data;
  },
  getAllPayments: async (params?: { billId?: number; page?: number; size?: number }): Promise<PageResponse<Payment>> => {
    const res = await apiClient.get<ApiResponse<PageResponse<Payment>>>('/payments', { params });
    return res.data.data;
  },
  getPaymentsForBill: async (billId: number): Promise<Payment[]> => {
    const res = await apiClient.get<ApiResponse<Payment[]>>(`/bills/${billId}/payments`);
    return res.data.data;
  },
};

// Leak Alert Service
export const leakService = {
  getAllAlerts: async (params?: { status?: string; severity?: string; locationId?: number; page?: number; size?: number }): Promise<PageResponse<LeakAlert>> => {
    const res = await apiClient.get<ApiResponse<PageResponse<LeakAlert>>>('/leak-alerts', { params });
    return res.data.data;
  },
  getById: async (id: number): Promise<LeakAlert> => {
    const res = await apiClient.get<ApiResponse<LeakAlert>>(`/leak-alerts/${id}`);
    return res.data.data;
  },
  triggerScan: async (): Promise<LeakAlert[]> => {
    const res = await apiClient.post<ApiResponse<LeakAlert[]>>('/leak-alerts/scan');
    return res.data.data;
  },
  updateStatus: async (id: number, status: string, resolutionNotes?: string): Promise<LeakAlert> => {
    const res = await apiClient.patch<ApiResponse<LeakAlert>>(`/leak-alerts/${id}/status`, { status, resolutionNotes });
    return res.data.data;
  },
};

// Water Quality Service
export const qualityService = {
  getAllSamples: async (params?: { locationId?: number; potabilityStatus?: string; page?: number; size?: number }): Promise<PageResponse<WaterQualityRecord>> => {
    const res = await apiClient.get<ApiResponse<PageResponse<WaterQualityRecord>>>('/water-quality', { params });
    return res.data.data;
  },
  getStats: async (): Promise<WaterQualityStats> => {
    const res = await apiClient.get<ApiResponse<WaterQualityStats>>('/water-quality/statistics');
    return res.data.data;
  },
  getTrends: async (limit = 15): Promise<WaterQualityRecord[]> => {
    const res = await apiClient.get<ApiResponse<WaterQualityRecord[]>>('/water-quality/trends', { params: { limit } });
    return res.data.data;
  },
  recordSample: async (data: {
    locationId: number;
    sampleTimestamp: string;
    phLevel: number;
    turbidityNtu: number;
    residualChlorineMgL: number;
    dissolvedOxygenMgL?: number;
    conductivityUsCm?: number;
    waterTemperatureCelsius?: number;
    sampledBy: string;
  }): Promise<WaterQualityRecord> => {
    const res = await apiClient.post<ApiResponse<WaterQualityRecord>>('/water-quality', data);
    return res.data.data;
  },
};

// Maintenance Service
export const maintenanceService = {
  getAllRequests: async (params?: { status?: string; priority?: string; technicianId?: number; page?: number; size?: number }): Promise<PageResponse<MaintenanceRequest>> => {
    const res = await apiClient.get<ApiResponse<PageResponse<MaintenanceRequest>>>('/maintenance', { params });
    return res.data.data;
  },
  getById: async (id: number): Promise<MaintenanceRequest> => {
    const res = await apiClient.get<ApiResponse<MaintenanceRequest>>(`/maintenance/${id}`);
    return res.data.data;
  },
  createRequest: async (data: {
    locationId: number;
    meterId?: number;
    alertId?: number;
    assignedTechnicianId?: number;
    priority: string;
    category: string;
    title: string;
    description: string;
    scheduledStart?: string;
  }): Promise<MaintenanceRequest> => {
    const res = await apiClient.post<ApiResponse<MaintenanceRequest>>('/maintenance', data);
    return res.data.data;
  },
  updateStatus: async (id: number, data: {
    status: string;
    assignedTechnicianId?: number;
    resolutionSummary?: string;
    partsCost?: number;
    laborHours?: number;
  }): Promise<MaintenanceRequest> => {
    const res = await apiClient.patch<ApiResponse<MaintenanceRequest>>(`/maintenance/${id}/status`, data);
    return res.data.data;
  },
};

// Executive Analytics Service
export const analyticsService = {
  getSummary: async (): Promise<AnalyticsSummary> => {
    const res = await apiClient.get<ApiResponse<AnalyticsSummary>>('/analytics');
    return res.data.data;
  },
  getLocations: async (): Promise<Location[]> => {
    const res = await apiClient.get<ApiResponse<PageResponse<Location>>>('/locations', { params: { size: 100 } });
    return res.data.data.content;
  },
  exportCsv: async (): Promise<Blob> => {
    const res = await apiClient.get('/analytics/export/csv', { responseType: 'blob' });
    return res.data;
  },
};

// Aliases for convenience
export const leakAlertApi = leakService;
export const waterQualityApi = qualityService;
export const maintenanceApi = maintenanceService;
export const analyticsApi = analyticsService;
export const meterApi = meterService;

