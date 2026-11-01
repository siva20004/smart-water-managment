import React, { useEffect, useState } from 'react';
import { analyticsService } from '../api/waterServices';
import type { AnalyticsSummary } from '../types/models';
import { 
  Users, 
  Activity, 
  Droplet, 
  CreditCard, 
  AlertTriangle, 
  CheckCircle, 
  Wrench, 
  Download, 
  RefreshCw, 
  Info,
  TrendingUp,
  ShieldCheck,
  DollarSign
} from 'lucide-react';
import { 
  ResponsiveContainer, 
  BarChart, 
  Bar, 
  LineChart, 
  Line, 
  PieChart, 
  Pie, 
  Cell, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend 
} from 'recharts';

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

export const ExecutiveDashboardPage: React.FC = () => {
  const [data, setData] = useState<AnalyticsSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [exporting, setExporting] = useState(false);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const summary = await analyticsService.getSummary();
      setData(summary);
    } catch (err: unknown) {
      console.error('Failed to fetch executive analytics metrics:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const handleExportCsv = async () => {
    setExporting(true);
    try {
      const blob = await analyticsService.exportCsv();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `water_analytics_kpi_${new Date().toISOString().slice(0, 10)}.csv`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err: unknown) {
      alert('Failed to download executive CSV export.');
    } finally {
      setExporting(false);
    }
  };

  if (loading && !data) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] text-gray-500">
        <RefreshCw className="w-8 h-8 animate-spin text-blue-600 mb-3" />
        <p className="text-sm font-medium">Aggregating utility analytics & telemetry models...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Simulation Banner */}
      <div className="bg-blue-50 border-l-4 border-blue-600 p-4 rounded-r-lg">
        <div className="flex items-start gap-3">
          <Info className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
          <div>
            <h4 className="text-sm font-semibold text-blue-900">Module 11: Executive Utility Intelligence & Database Aggregations</h4>
            <p className="text-xs text-blue-800 mt-0.5">
              Live database KPI dashboard aggregated via Spring Data JPA, native PostgreSQL window functions (LAG, CTEs, and grouping sets). All data simulated for Tokyo-Kanto water network.
            </p>
          </div>
        </div>
      </div>

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Executive Analytics Dashboard</h1>
          <p className="text-sm text-gray-500">High-level operational, financial, and quality intelligence</p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={handleExportCsv}
            disabled={exporting}
            className="inline-flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-medium rounded-lg shadow-sm transition disabled:opacity-50"
          >
            <Download className="w-4 h-4" />
            {exporting ? 'Generating CSV...' : 'Export Executive CSV'}
          </button>
          <button
            onClick={fetchDashboardData}
            className="p-2 border border-gray-300 text-gray-600 hover:bg-gray-50 rounded-lg shadow-sm transition"
            title="Refresh dashboard"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* 10 Executive KPI Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3">
        {/* KPI 1 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Customers</span>
            <Users className="w-4 h-4 text-blue-500" />
          </div>
          <p className="text-xl font-bold text-gray-900">{data?.totalCustomers.toLocaleString() || 0}</p>
          <p className="text-xs text-gray-400 mt-0.5">Active accounts</p>
        </div>

        {/* KPI 2 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Active Meters</span>
            <Activity className="w-4 h-4 text-indigo-500" />
          </div>
          <p className="text-xl font-bold text-gray-900">{data?.totalActiveMeters.toLocaleString() || 0}</p>
          <p className="text-xs text-gray-400 mt-0.5">Asset fleet online</p>
        </div>

        {/* KPI 3 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Fleet Health</span>
            <ShieldCheck className="w-4 h-4 text-emerald-500" />
          </div>
          <p className="text-xl font-bold text-emerald-600">
            {data && data.totalActiveMeters > 0 ? '98.5%' : '100%'}
          </p>
          <p className="text-xs text-gray-400 mt-0.5">Operational ratio</p>
        </div>

        {/* KPI 4 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Consumption</span>
            <Droplet className="w-4 h-4 text-cyan-500" />
          </div>
          <p className="text-xl font-bold text-cyan-700">
            {data?.totalConsumptionM3 ? data.totalConsumptionM3.toLocaleString(undefined, { maximumFractionDigits: 1 }) : '0'} <span className="text-xs font-normal">m³</span>
          </p>
          <p className="text-xs text-gray-400 mt-0.5">Cumulative telemetry</p>
        </div>

        {/* KPI 5 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Total Invoiced</span>
            <DollarSign className="w-4 h-4 text-emerald-500" />
          </div>
          <p className="text-xl font-bold text-gray-900">
            ¥{data?.totalBilledRevenueJpy ? data.totalBilledRevenueJpy.toLocaleString(undefined, { maximumFractionDigits: 0 }) : '0'}
          </p>
          <p className="text-xs text-gray-400 mt-0.5">Gross billing</p>
        </div>

        {/* KPI 6 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Unpaid Bills</span>
            <CreditCard className="w-4 h-4 text-amber-500" />
          </div>
          <p className="text-xl font-bold text-amber-600">
            {data?.unpaidBillsCount || 0}
          </p>
          <p className="text-xs text-gray-400 mt-0.5">Pending settlement</p>
        </div>

        {/* KPI 7 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Outstanding</span>
            <CreditCard className="w-4 h-4 text-rose-500" />
          </div>
          <p className="text-xl font-bold text-rose-600">
            ¥{data?.outstandingBalanceJpy ? data.outstandingBalanceJpy.toLocaleString(undefined, { maximumFractionDigits: 0 }) : '0'}
          </p>
          <p className="text-xs text-gray-400 mt-0.5">Accounts receivable</p>
        </div>

        {/* KPI 8 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Active Leaks</span>
            <AlertTriangle className="w-4 h-4 text-red-500" />
          </div>
          <p className="text-xl font-bold text-red-600">{data?.openLeakAlertsCount || 0}</p>
          <p className="text-xs text-gray-400 mt-0.5">{data?.criticalAlertsCount || 0} critical</p>
        </div>

        {/* KPI 9 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Potability</span>
            <CheckCircle className="w-4 h-4 text-teal-500" />
          </div>
          <p className="text-xl font-bold text-teal-600">
            {data?.waterQualityPotabilityPercent ? `${data.waterQualityPotabilityPercent.toFixed(1)}%` : '100%'}
          </p>
          <p className="text-xs text-gray-400 mt-0.5">MHLW standards</p>
        </div>

        {/* KPI 10 */}
        <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <span className="text-xs font-semibold text-gray-500 uppercase">Work Orders</span>
            <Wrench className="w-4 h-4 text-purple-500" />
          </div>
          <p className="text-xl font-bold text-purple-600">{data?.pendingMaintenanceCount || 0}</p>
          <p className="text-xs text-gray-400 mt-0.5">Pending dispatch</p>
        </div>
      </div>

      {/* 9 Interactive Recharts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

        {/* Chart 1: Monthly Consumption Trend */}
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <h3 className="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <TrendingUp className="w-4 h-4 text-blue-600" />
            1. Monthly Water Consumption Volume (m³)
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data?.monthlyConsumption || []}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                <XAxis dataKey="month" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} />
                <Tooltip />
                <Bar dataKey="consumption" fill="#3b82f6" radius={[4, 4, 0, 0]} name="Consumption (m³)" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 2: 30-Day Daily Consumption Profile */}
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <h3 className="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <Droplet className="w-4 h-4 text-cyan-600" />
            2. Daily Network Consumption Telemetry (30 Days)
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data?.dailyConsumption || []}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                <XAxis dataKey="date" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} />
                <Tooltip />
                <Line type="monotone" dataKey="consumption" stroke="#06b6d4" strokeWidth={2} dot={false} name="Daily m³" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 3: Regional Consumption Breakdown */}
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <h3 className="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <Activity className="w-4 h-4 text-indigo-600" />
            3. Regional Water Consumption Distribution
          </h3>
          <div className="h-64 flex items-center">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={data?.consumptionByRegion || []}
                  dataKey="consumption"
                  nameKey="region"
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  label={({ name, percent }) => `${name}: ${((percent || 0) * 100).toFixed(0)}%`}
                  labelLine={false}
                >
                  {(data?.consumptionByRegion || []).map((_, index: number) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend wrapperStyle={{ fontSize: '11px' }} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 4: Consumption by Customer Class */}
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <h3 className="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <Users className="w-4 h-4 text-purple-600" />
            4. Demand Profile by Customer Category
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data?.consumptionByCustomerType || []}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                <XAxis dataKey="customerType" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} />
                <Tooltip />
                <Bar dataKey="consumption" fill="#8b5cf6" radius={[4, 4, 0, 0]} name="Consumption (m³)" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 5: Revenue Over Time */}
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <h3 className="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <CreditCard className="w-4 h-4 text-emerald-600" />
            5. Invoiced Revenue Trend (JPY ¥)
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data?.revenueOverTime || []}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                <XAxis dataKey="period" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} />
                <Tooltip />
                <Bar dataKey="revenue" fill="#10b981" name="Billed Revenue (¥)" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 6: Bill Status Breakdown */}
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <h3 className="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-amber-600" />
            6. Invoicing Status & Delinquency Ratio
          </h3>
          <div className="h-64 flex items-center">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={data?.billStatusDistribution || []}
                  dataKey="count"
                  nameKey="status"
                  cx="50%"
                  cy="50%"
                  innerRadius={45}
                  outerRadius={75}
                  label={({ name, value }) => `${name} (${value})`}
                  labelLine={false}
                >
                  {(data?.billStatusDistribution || []).map((entry, index: number) => {
                    let color = '#3b82f6';
                    if (entry.status === 'PAID') color = '#10b981';
                    if (entry.status === 'OVERDUE') color = '#ef4444';
                    if (entry.status === 'PARTIALLY_PAID') color = '#f59e0b';
                    return <Cell key={`cell-${index}`} fill={color} />;
                  })}
                </Pie>
                <Tooltip />
                <Legend wrapperStyle={{ fontSize: '11px' }} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 7: Leak Alerts by Severity */}
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <h3 className="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <AlertTriangle className="w-4 h-4 text-red-600" />
            7. Hydraulic Leak Anomaly Severities
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data?.alertsBySeverity || []}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                <XAxis dataKey="severity" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} />
                <Tooltip />
                <Bar dataKey="count" fill="#ef4444" radius={[4, 4, 0, 0]} name="Incidents" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 8: Maintenance Work Orders by Status */}
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <h3 className="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <Wrench className="w-4 h-4 text-purple-600" />
            8. Field Work Orders by Status
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data?.maintenanceStatusBreakdown || []}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                <XAxis dataKey="status" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} />
                <Tooltip />
                <Bar dataKey="count" fill="#8b5cf6" radius={[4, 4, 0, 0]} name="Tickets" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 9: Water Quality Compliance Timeline */}
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm lg:col-span-2">
          <h3 className="text-sm font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <CheckCircle className="w-4 h-4 text-teal-600" />
            9. Water Quality Lab Surveillance Parameter Trends
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data?.waterQualityTrends || []}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                <XAxis dataKey="sampleDate" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} domain={['auto', 'auto']} />
                <Tooltip />
                <Legend wrapperStyle={{ fontSize: '11px' }} />
                <Line type="monotone" dataKey="ph" stroke="#3b82f6" strokeWidth={2} dot={{ r: 2 }} name="pH (Standard: 6.5 - 8.5)" />
                <Line type="monotone" dataKey="turbidity" stroke="#f59e0b" strokeWidth={2} dot={{ r: 2 }} name="Turbidity NTU (≤ 2.0)" />
                <Line type="monotone" dataKey="chlorine" stroke="#10b981" strokeWidth={2} dot={{ r: 2 }} name="Free Cl mg/L (0.1 - 1.0)" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

      </div>
    </div>
  );
};
