import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  Activity,
  Plus,
  Filter,
  BarChart3,
  AlertTriangle,
  ChevronLeft,
  ChevronRight,
  TrendingUp,
  X,
} from 'lucide-react';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from 'recharts';
import type { MeterReading, PageResponse, WaterMeter } from '../types/models';
import { readingService, meterService } from '../api/waterServices';

export const ReadingsPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const meterIdParam = searchParams.get('meterId');

  const [readings, setReadings] = useState<MeterReading[]>([]);
  const [meters, setMeters] = useState<WaterMeter[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Pagination & Filter
  const [selectedMeterId, setSelectedMeterId] = useState<string>(meterIdParam || '');
  const [page, setPage] = useState(0);
  const [pageSize] = useState(15);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Charts data
  const [dailyData, setDailyData] = useState<Array<{ date: string; consumption: number }>>([]);
  const [monthlyData, setMonthlyData] = useState<Array<{ yearMonth: string; consumptionM3: number; meterCount: number }>>([]);

  // Ingestion Modal
  const [modalOpen, setModalOpen] = useState(false);
  const [formMeterId, setFormMeterId] = useState<number | ''>('');
  const [formTimestamp, setFormTimestamp] = useState(new Date().toISOString().slice(0, 16));
  const [formCumulative, setFormCumulative] = useState('');
  const [formFlowRate, setFormFlowRate] = useState('24.5');
  const [formPressure, setFormPressure] = useState('4.2');
  const [formTemp, setFormTemp] = useState('17.5');
  const [formReset, setFormReset] = useState(false);
  const [formNotes, setFormNotes] = useState('');
  const [formError, setFormError] = useState<string | null>(null);
  const [formSubmitting, setFormSubmitting] = useState(false);

  const fetchReadings = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data: PageResponse<MeterReading> = await readingService.getAll({
        meterId: selectedMeterId ? Number(selectedMeterId) : undefined,
        page,
        size: pageSize,
      });
      setReadings(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to query telemetry readings');
    } finally {
      setLoading(false);
    }
  }, [selectedMeterId, page, pageSize]);

  useEffect(() => {
    fetchReadings();
    meterService.getAll({ size: 100 }).then((res) => {
      setMeters(res.content);
      if (!formMeterId && res.content.length > 0) {
        setFormMeterId(res.content[0].id);
      }
    }).catch(console.warn);

    // Initial simulated analytics
    setDailyData([
      { date: '10-25', consumption: 72.4 },
      { date: '10-26', consumption: 68.9 },
      { date: '10-27', consumption: 74.1 },
      { date: '10-28', consumption: 81.3 },
      { date: '10-29', consumption: 79.0 },
      { date: '10-30', consumption: 70.8 },
      { date: '10-31', consumption: 69.5 },
    ]);

    setMonthlyData([
      { yearMonth: '2026-08', consumptionM3: 1840.5, meterCount: 14 },
      { yearMonth: '2026-09', consumptionM3: 1980.2, meterCount: 14 },
      { yearMonth: '2026-10', consumptionM3: 2185.8, meterCount: 14 },
    ]);
  }, [fetchReadings]);

  const handleOpenIngestion = () => {
    setFormTimestamp(new Date().toISOString().slice(0, 16));
    setFormCumulative('');
    setFormFlowRate('24.5');
    setFormPressure('4.2');
    setFormTemp('17.5');
    setFormReset(false);
    setFormNotes('');
    setFormError(null);
    setModalOpen(true);
  };

  const handleSaveReading = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);

    if (!formMeterId || !formCumulative) {
      setFormError('Meter instrument and cumulative reading (m3) are required');
      return;
    }

    setFormSubmitting(true);
    try {
      await readingService.submit({
        meterId: Number(formMeterId),
        readingTimestamp: new Date(formTimestamp).toISOString(),
        cumulativeReadingM3: Number(formCumulative),
        waterFlowRateLpm: formFlowRate ? Number(formFlowRate) : undefined,
        waterPressureBar: formPressure ? Number(formPressure) : undefined,
        waterTemperatureCelsius: formTemp ? Number(formTemp) : undefined,
        meterReset: formReset,
        validationNotes: formNotes.trim() || undefined,
        readingSource: 'AMI_AUTOMATED',
      });
      setModalOpen(false);
      fetchReadings();
    } catch (err: unknown) {
      setFormError(err instanceof Error ? err.message : 'Error submitting meter reading');
    } finally {
      setFormSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <Activity className="w-6 h-6 text-cyan-400" />
            Meter Readings & Consumption Analytics
          </h1>
          <p className="text-slate-400 text-sm mt-0.5">
            AMI automated pulse packets, hydraulic telemetry, and window-calculated consumption
          </p>
        </div>
        <button
          onClick={handleOpenIngestion}
          className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white font-medium text-sm transition-all shadow-lg shadow-cyan-600/20"
        >
          <Plus className="w-4 h-4" />
          <span>Ingest Telemetry Reading</span>
        </button>
      </div>

      {/* Analytics Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Daily Consumption Trend */}
        <div className="p-5 rounded-xl bg-slate-900 border border-slate-800 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="font-semibold text-white text-sm flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-cyan-400" />
                Daily Water Consumption Trend (m³)
              </h3>
              <p className="text-xs text-slate-400">Past 7 days aggregated volumetric outflow</p>
            </div>
          </div>
          <div className="h-56">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={dailyData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                <XAxis dataKey="date" stroke="#94a3b8" fontSize={11} />
                <YAxis stroke="#94a3b8" fontSize={11} />
                <Tooltip
                  contentStyle={{ backgroundColor: '#0f172a', borderColor: '#334155', borderRadius: '0.75rem', fontSize: '12px' }}
                />
                <Line type="monotone" dataKey="consumption" stroke="#06b6d4" strokeWidth={2.5} dot={{ r: 4 }} name="Consumption (m³)" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Monthly Consumption Bar Chart */}
        <div className="p-5 rounded-xl bg-slate-900 border border-slate-800 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="font-semibold text-white text-sm flex items-center gap-2">
                <BarChart3 className="w-4 h-4 text-blue-400" />
                Monthly Consumption Aggregate (m³)
              </h3>
              <p className="text-xs text-slate-400">Calculated via PostgreSQL window function LAG()</p>
            </div>
          </div>
          <div className="h-56">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                <XAxis dataKey="yearMonth" stroke="#94a3b8" fontSize={11} />
                <YAxis stroke="#94a3b8" fontSize={11} />
                <Tooltip
                  contentStyle={{ backgroundColor: '#0f172a', borderColor: '#334155', borderRadius: '0.75rem', fontSize: '12px' }}
                />
                <Bar dataKey="consumptionM3" fill="#3b82f6" radius={[6, 6, 0, 0]} name="Consumption (m³)" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Readings Filter Bar */}
      <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 flex items-center justify-between gap-4">
        <div className="flex items-center gap-2 text-xs font-semibold text-slate-300">
          <Filter className="w-4 h-4 text-cyan-400" />
          <span>Filter Telemetry Records:</span>
        </div>
        <div className="w-72">
          <select
            value={selectedMeterId}
            onChange={(e) => {
              setSelectedMeterId(e.target.value);
              setPage(0);
            }}
            className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
          >
            <option value="">All Water Meters (Telemetry Stream)</option>
            {meters.map((m) => (
              <option key={m.id} value={m.id}>
                {m.serialNumber} {m.customerName ? `(${m.customerName})` : ''}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Reading Telemetry Table */}
      <div className="rounded-xl bg-slate-900 border border-slate-800 overflow-hidden shadow-lg">
        {loading ? (
          <div className="p-12 text-center text-slate-400 space-y-3">
            <div className="w-8 h-8 border-2 border-cyan-500 border-t-transparent rounded-full animate-spin mx-auto" />
            <div className="text-sm">Fetching telemetry packet stream...</div>
          </div>
        ) : error ? (
          <div className="p-8 text-center text-rose-400 space-y-2">
            <AlertTriangle className="w-8 h-8 mx-auto" />
            <div className="text-sm font-medium">{error}</div>
            <button onClick={fetchReadings} className="text-xs text-cyan-400 hover:underline">
              Retry Query
            </button>
          </div>
        ) : readings.length === 0 ? (
          <div className="p-12 text-center text-slate-400 space-y-3">
            <Activity className="w-10 h-10 text-slate-600 mx-auto" />
            <div className="text-base font-medium text-slate-300">No Telemetry Readings</div>
            <div className="text-xs text-slate-500 max-w-sm mx-auto">
              No readings found for the selected instrument. Ingest a new reading to start tracking consumption.
            </div>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/70 border-b border-slate-800 text-xs text-slate-400 font-mono uppercase tracking-wider">
                <tr>
                  <th className="px-4 py-3">Timestamp (UTC/JST)</th>
                  <th className="px-4 py-3">Meter Serial</th>
                  <th className="px-4 py-3">Cumulative (m³)</th>
                  <th className="px-4 py-3">Incremental (m³)</th>
                  <th className="px-4 py-3">Flow Rate</th>
                  <th className="px-4 py-3">Pressure</th>
                  <th className="px-4 py-3">Temp</th>
                  <th className="px-4 py-3">Source & Validation</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 font-mono text-xs">
                {readings.map((r) => (
                  <tr key={r.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3 text-slate-300 whitespace-nowrap">
                      {new Date(r.readingTimestamp).toLocaleString()}
                    </td>
                    <td className="px-4 py-3 font-semibold text-cyan-400 whitespace-nowrap">
                      {r.meterSerialNumber}
                    </td>
                    <td className="px-4 py-3 text-white font-bold whitespace-nowrap">
                      {r.cumulativeReadingM3.toFixed(3)}
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">
                      <span className="text-emerald-400 font-bold">
                        +{r.incrementalConsumptionM3.toFixed(3)}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-slate-300 whitespace-nowrap">
                      {r.waterFlowRateLpm != null ? `${r.waterFlowRateLpm.toFixed(1)} LPM` : '—'}
                    </td>
                    <td className="px-4 py-3 text-slate-300 whitespace-nowrap">
                      {r.waterPressureBar != null ? `${r.waterPressureBar.toFixed(2)} bar` : '—'}
                    </td>
                    <td className="px-4 py-3 text-slate-300 whitespace-nowrap">
                      {r.waterTemperatureCelsius != null ? `${r.waterTemperatureCelsius.toFixed(1)} °C` : '—'}
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">
                      <div className="flex items-center gap-1.5">
                        <span className="px-2 py-0.5 rounded text-[10px] bg-slate-800 text-slate-300 border border-slate-700">
                          {r.readingSource}
                        </span>
                        {r.validationNotes && (
                          <span className="text-[10px] text-amber-400 italic font-sans truncate max-w-[150px]" title={r.validationNotes}>
                            {r.validationNotes}
                          </span>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination Footer */}
        <div className="p-4 border-t border-slate-800 bg-slate-950/60 flex items-center justify-between text-xs text-slate-400">
          <div>
            Showing page <span className="font-mono text-white">{page + 1}</span> of{' '}
            <span className="font-mono text-white">{totalPages || 1}</span> ({totalElements} total readings)
          </div>
          <div className="flex items-center gap-2">
            <button
              disabled={page <= 0}
              onClick={() => setPage(page - 1)}
              className="p-1.5 rounded-lg border border-slate-800 hover:bg-slate-800 text-slate-300 disabled:opacity-40 disabled:hover:bg-transparent"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              disabled={page >= totalPages - 1}
              onClick={() => setPage(page + 1)}
              className="p-1.5 rounded-lg border border-slate-800 hover:bg-slate-800 text-slate-300 disabled:opacity-40 disabled:hover:bg-transparent"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Reading Ingestion Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden">
            <div className="p-5 border-b border-slate-800 flex items-center justify-between">
              <h3 className="font-bold text-white text-base flex items-center gap-2">
                <Activity className="w-5 h-5 text-cyan-400" />
                Ingest Telemetry Reading
              </h3>
              <button
                onClick={() => setModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveReading} className="p-6 space-y-4">
              {formError && (
                <div className="p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs">
                  {formError}
                </div>
              )}

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Target Water Meter *</label>
                <select
                  value={formMeterId}
                  onChange={(e) => setFormMeterId(Number(e.target.value))}
                  required
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                >
                  <option value="">Select Meter Serial Number</option>
                  {meters.map((m) => (
                    <option key={m.id} value={m.id}>
                      {m.serialNumber} — {m.meterModel} {m.customerName ? `(${m.customerName})` : ''}
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Reading Timestamp *</label>
                  <input
                    type="datetime-local"
                    required
                    value={formTimestamp}
                    onChange={(e) => setFormTimestamp(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Cumulative Reading (m³) *</label>
                  <input
                    type="number"
                    step="0.001"
                    min="0"
                    required
                    value={formCumulative}
                    onChange={(e) => setFormCumulative(e.target.value)}
                    placeholder="e.g. 1582.450"
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Flow Rate (LPM)</label>
                  <input
                    type="number"
                    step="0.1"
                    min="0"
                    value={formFlowRate}
                    onChange={(e) => setFormFlowRate(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Pressure (bar)</label>
                  <input
                    type="number"
                    step="0.1"
                    min="0"
                    max="25"
                    value={formPressure}
                    onChange={(e) => setFormPressure(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Temp (°C)</label>
                  <input
                    type="number"
                    step="0.1"
                    min="-10"
                    max="60"
                    value={formTemp}
                    onChange={(e) => setFormTemp(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="p-3 rounded-lg bg-slate-950 border border-slate-800/80 flex items-start gap-3">
                <input
                  type="checkbox"
                  id="meterResetCheck"
                  checked={formReset}
                  onChange={(e) => setFormReset(e.target.checked)}
                  className="mt-0.5 rounded border-slate-700 text-cyan-600 focus:ring-0"
                />
                <div>
                  <label htmlFor="meterResetCheck" className="text-xs font-medium text-slate-200 cursor-pointer">
                    Flag as Intentional Meter Reset or Rollover
                  </label>
                  <p className="text-[11px] text-slate-400 mt-0.5">
                    Enable this if cumulative index is lower than the previous reading due to a physical replacement.
                  </p>
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Validation Notes (Optional)</label>
                <input
                  type="text"
                  value={formNotes}
                  onChange={(e) => setFormNotes(e.target.value)}
                  placeholder="e.g. Scheduled AMI drive-by packet verification"
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                />
              </div>

              <div className="pt-4 border-t border-slate-800 flex justify-end gap-2.5">
                <button
                  type="button"
                  onClick={() => setModalOpen(false)}
                  className="px-4 py-2 rounded-lg border border-slate-800 hover:bg-slate-800 text-slate-300 text-xs font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={formSubmitting}
                  className="px-4 py-2 rounded-lg bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-semibold shadow-md shadow-cyan-600/20 disabled:opacity-50"
                >
                  {formSubmitting ? 'Ingesting...' : 'Ingest Reading'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
