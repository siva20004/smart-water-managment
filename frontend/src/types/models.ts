// TypeScript Model Definitions for Smart Water Management System

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  errorCode?: string;
  validationErrors?: Record<string, string>;
}

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'OPERATOR' | 'TECHNICIAN' | 'ANALYST' | string;
  department?: string;
  status: string;
  lastLoginAt?: string;
}

export interface Region {
  id: number;
  code: string;
  name: string;
  prefecture: string;
  description: string;
  status: string;
  locationCount: number;
}

export interface Location {
  id: number;
  regionId: number;
  regionCode: string;
  locationCode: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  postalCode: string;
  latitude: number;
  longitude: number;
  elevationMeters?: number;
  installationType: string;
}

export interface Customer {
  id: number;
  customerNumber: string;
  customerType: string;
  fullName: string;
  email?: string;
  phoneNumber?: string;
  taxId?: string;
  billingAddress: string;
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'TERMINATED';
  meterCount: number;
  createdAt: string;
}

export interface WaterMeter {
  id: number;
  serialNumber: string;
  customerId?: number;
  customerName?: string;
  locationId: number;
  locationCode?: string;
  meterModel: string;
  meterType: string;
  diameterMm: number;
  installationDate: string;
  status: 'ACTIVE' | 'INACTIVE' | 'FAULTY' | 'MAINTENANCE' | 'RETIRED' | 'REMOVED';
  lastInspectionDate?: string;
  batteryLevelPercent?: number;
  lastCommunicationAt?: string;
}

export interface MeterReading {
  id: number;
  meterId: number;
  meterSerialNumber: string;
  readingTimestamp: string;
  cumulativeReadingM3: number;
  incrementalConsumptionM3: number;
  waterFlowRateLpm?: number;
  waterPressureBar?: number;
  waterTemperatureCelsius?: number;
  readingSource: string;
  isValidated: boolean;
  validationNotes?: string;
}

export interface WaterBill {
  id: number;
  billNumber: string;
  customerId: number;
  customerName: string;
  meterId?: number;
  meterSerial?: string;
  billingPeriodStart: string;
  billingPeriodEnd: string;
  baseFee: number;
  volumetricRate: number;
  consumptionM3: number;
  consumptionCharge: number;
  taxAmount: number;
  totalAmount: number;
  dueDate: string;
  status: 'ISSUED' | 'PARTIALLY_PAID' | 'PAID' | 'UNPAID' | 'OVERDUE' | 'CANCELLED';
  paidAmount: number;
  outstandingBalance: number;
}

export interface Payment {
  id: number;
  paymentReference: string;
  billId: number;
  billNumber: string;
  paymentDate: string;
  amount: number;
  paymentMethod: string;
  transactionStatus: string;
  notes?: string;
}

export interface LeakAlert {
  id: number;
  alertCode: string;
  meterId?: number;
  meterSerial?: string;
  locationId: number;
  locationCode?: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  alertType: string;
  detectedAt: string;
  estimatedLossRateM3H?: number;
  status: 'NEW' | 'ACKNOWLEDGED' | 'INVESTIGATING' | 'WORK_ORDER_DISPATCHED' | 'RESOLVED' | 'FALSE_POSITIVE';
  resolutionNotes?: string;
  resolvedAt?: string;
}

export interface WaterQualityRecord {
  id: number;
  sampleCode: string;
  locationId: number;
  locationCode?: string;
  sampleTimestamp: string;
  phLevel: number;
  turbidityNtu: number;
  residualChlorineMgL: number;
  dissolvedOxygenMgL?: number;
  conductivityUsCm?: number;
  waterTemperatureCelsius?: number;
  potabilityStatus: 'POTABLE' | 'POTABLE_MONITORING_REQUIRED' | 'NON_POTABLE';
  sampledBy: string;
}

export interface WaterQualityStats {
  totalSamples: number;
  potableCount: number;
  monitoringRequiredCount: number;
  nonPotableCount: number;
  complianceRatePercent: number;
  avgPh: number;
  avgTurbidityNtu: number;
  avgChlorineMgL: number;
  simulatedDisclaimer: string;
}

export interface MaintenanceRequest {
  id: number;
  requestNumber: string;
  meterId?: number;
  meterSerial?: string;
  alertId?: number;
  alertCode?: string;
  locationId: number;
  locationCode?: string;
  assignedTechnicianId?: number;
  assignedTechnicianName?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'EMERGENCY';
  category: string;
  title: string;
  description: string;
  status: 'PENDING' | 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  scheduledStart?: string;
  actualCompletion?: string;
  resolutionSummary?: string;
  partsCost: number;
  laborHours: number;
}

export interface AnalyticsSummary {
  totalCustomers: number;
  totalActiveMeters: number;
  totalConsumptionM3: number;
  totalBilledRevenueJpy: number;
  outstandingBalanceJpy: number;
  unpaidBillsCount: number;
  openLeakAlertsCount: number;
  criticalAlertsCount: number;
  pendingMaintenanceCount: number;
  totalQualitySamples: number;
  waterQualityPotabilityPercent: number;
  monthlyConsumption: Array<{ month: string; consumption: number }>;
  dailyConsumption: Array<{ date: string; consumption: number }>;
  consumptionByRegion: Array<{ region: string; consumption: number }>;
  consumptionByCustomerType: Array<{ customerType: string; consumption: number }>;
  revenueOverTime: Array<{ period: string; revenue: number }>;
  billStatusDistribution: Array<{ status: string; count: number; amount: number }>;
  alertsBySeverity: Array<{ severity: string; count: number }>;
  maintenanceStatusBreakdown: Array<{ status: string; count: number }>;
  waterQualityTrends: Array<{ sampleDate: string; ph: number; turbidity: number; chlorine: number }>;
}
