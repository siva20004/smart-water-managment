export interface DatabaseHealthInfo {
  status: 'UP' | 'DOWN' | 'DEGRADED' | 'NOT_CONFIGURED';
  databaseProductName: string;
  responseTimeMs: number;
  details: string;
}

export interface SystemMetrics {
  totalMemoryMb: number;
  freeMemoryMb: number;
  maxMemoryMb: number;
  availableProcessors: number;
  jvmUptimeMs: number;
}

export interface HealthResponse {
  status: 'UP' | 'DOWN' | 'DEGRADED';
  service: string;
  version: string;
  environment: string;
  timestamp: string;
  uptimeSeconds: number;
  database: DatabaseHealthInfo;
  system: SystemMetrics;
}

export interface ApiError {
  message: string;
  statusCode?: number;
  details?: string;
  timestamp: string;
}
