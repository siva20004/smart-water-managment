import React, { useEffect, useState } from 'react';
import { qualityService, analyticsService } from '../api/waterServices';
import type { WaterQualityRecord, WaterQualityStats, Location } from '../types/models';
import { 
  Droplet, 
  CheckCircle, 
  AlertTriangle, 
  FlaskConical, 
  Plus, 
  RefreshCw, 
  Info,
  TrendingUp
} from 'lucide-react';
import { 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer, 
  Legend 
} from 'recharts';

export const WaterQualityPage: React.FC = () => {
  const [samples, setSamples] = useState<WaterQualityRecord[]>([]);
  const [stats, setStats] = useState<WaterQualityStats | null>(null);
  const [locations, setLocations] = useState<Location[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal State
  const [showAddModal, setShowAddModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    locationId: '',
    sampleTimestamp: new Date().toISOString().slice(0, 16),
    phLevel: 7.2,
    turbidityNtu: 0.8,
    residualChlorineMgL: 0.6,
    conductivityUsCm: 185.0,
    waterTemperatureCelsius: 16.5,
    sampledBy: 'T. Sato (Field Lab Chemist)'
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [samplesData, statsData, locData] = await Promise.all([
        qualityService.getAllSamples({ page: 0, size: 50 }),
        qualityService.getStats(),
        analyticsService.getLocations()
      ]);
      setSamples(samplesData.content);
      setStats(statsData);
      setLocations(locData);
      if (locData.length > 0 && !formData.locationId) {
        setFormData(prev => ({ ...prev, locationId: locData[0].id.toString() }));
      }
    } catch (err: unknown) {
      console.error('Failed to fetch water quality surveillance records:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreateSample = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.locationId) return;
    setSubmitting(true);
    try {
      await qualityService.recordSample({
        locationId: Number(formData.locationId),
        sampleTimestamp: new Date(formData.sampleTimestamp).toISOString(),
        phLevel: Number(formData.phLevel),
        turbidityNtu: Number(formData.turbidityNtu),
        residualChlorineMgL: Number(formData.residualChlorineMgL),
        conductivityUsCm: Number(formData.conductivityUsCm),
        waterTemperatureCelsius: Number(formData.waterTemperatureCelsius),
        sampledBy: formData.sampledBy
      });
      setShowAddModal(false);
      await fetchData();
    } catch (err: unknown) {
      alert('Failed to ingest water sample record.');
    } finally {
      setSubmitting(false);
    }
  };

  // Prepare chart trend data
  const chartData = [...samples]
    .sort((a, b) => new Date(a.sampleTimestamp).getTime() - new Date(b.sampleTimestamp).getTime())
    .map(s => ({
      time: new Date(s.sampleTimestamp).toLocaleDateString([], { month: 'short', day: 'numeric' }),
      pH: s.phLevel,
      turbidity: s.turbidityNtu,
      chlorine: s.residualChlorineMgL,
      temperature: s.waterTemperatureCelsius
    }));

  return (
    <div className="space-y-6">
      {/* Educational Notice Banner */}
      <div className="bg-cyan-50 border-l-4 border-cyan-500 p-4 rounded-r-lg">
        <div className="flex items-start gap-3">
          <Info className="w-5 h-5 text-cyan-600 mt-0.5 flex-shrink-0" />
          <div>
            <h4 className="text-sm font-semibold text-cyan-900">Module 9: Water Quality Surveillance & Potability Standards</h4>
            <p className="text-xs text-cyan-800 mt-0.5">
              Simulated chemical & physical parameters based on educational utility benchmarks (MHLW / WHO guidelines): 
              pH standard: 6.5 - 8.5 | Turbidity &le; 2.0 NTU | Free Chlorine: 0.10 - 1.00 mg/L. All records strictly simulated.
            </p>
          </div>
        </div>
      </div>

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Water Quality Surveillance</h1>
          <p className="text-sm text-gray-500">Physical-chemical parameter tracking & potability verification</p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => setShowAddModal(true)}
            className="inline-flex items-center gap-2 px-4 py-2 bg-cyan-600 hover:bg-cyan-700 text-white text-sm font-medium rounded-lg shadow-sm transition"
          >
            <Plus className="w-4 h-4" />
            Record Lab Sample
          </button>
          <button
            onClick={fetchData}
            className="p-2 border border-gray-300 text-gray-600 hover:bg-gray-50 rounded-lg shadow-sm transition"
            title="Refresh records"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-gray-500 uppercase">Total Samples</span>
            <FlaskConical className="w-4 h-4 text-cyan-500" />
          </div>
          <p className="text-2xl font-bold text-gray-900">{stats?.totalSamples || 0}</p>
          <p className="text-xs text-gray-500 mt-1">Network testing points</p>
        </div>

        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-gray-500 uppercase">Compliance Rate</span>
            <CheckCircle className="w-4 h-4 text-emerald-500" />
          </div>
          <p className="text-2xl font-bold text-emerald-600">
            {stats ? `${stats.complianceRatePercent.toFixed(1)}%` : '100%'}
          </p>
          <p className="text-xs text-emerald-600 mt-1 font-medium">Potable status achieved</p>
        </div>

        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-gray-500 uppercase">Avg pH Value</span>
            <Droplet className="w-4 h-4 text-blue-500" />
          </div>
          <p className="text-2xl font-bold text-gray-900">
            {stats?.avgPh ? stats.avgPh.toFixed(2) : '7.20'}
          </p>
          <p className="text-xs text-gray-500 mt-1">Optimal: 6.5 - 8.5 pH</p>
        </div>

        <div className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-semibold text-gray-500 uppercase">Avg Turbidity</span>
            <AlertTriangle className="w-4 h-4 text-amber-500" />
          </div>
          <p className="text-2xl font-bold text-gray-900">
            {stats?.avgTurbidityNtu ? `${stats.avgTurbidityNtu.toFixed(2)} NTU` : '0.75 NTU'}
          </p>
          <p className="text-xs text-gray-500 mt-1">Threshold: &le; 2.0 NTU</p>
        </div>
      </div>

      {/* Parameter Trend Chart */}
      <div className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm">
        <h3 className="text-base font-semibold text-gray-900 mb-4 flex items-center gap-2">
          <TrendingUp className="w-4 h-4 text-cyan-600" />
          Multi-Parameter Water Quality Trends (Timeline)
        </h3>
        <div className="h-72 w-full">
          {chartData.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
                <XAxis dataKey="time" stroke="#9ca3af" fontSize={11} />
                <YAxis stroke="#9ca3af" fontSize={11} domain={['auto', 'auto']} />
                <Tooltip />
                <Legend wrapperStyle={{ fontSize: '12px' }} />
                <Line type="monotone" dataKey="pH" stroke="#3b82f6" strokeWidth={2} dot={{ r: 3 }} name="pH" />
                <Line type="monotone" dataKey="turbidity" stroke="#f59e0b" strokeWidth={2} dot={{ r: 3 }} name="Turbidity (NTU)" />
                <Line type="monotone" dataKey="chlorine" stroke="#10b981" strokeWidth={2} dot={{ r: 3 }} name="Free Chlorine (mg/L)" />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <div className="h-full flex items-center justify-center text-gray-400 text-sm">
              No trend data available. Record samples below to visualize water chemistry.
            </div>
          )}
        </div>
      </div>

      {/* Water Samples Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
          <h3 className="text-base font-semibold text-gray-900">Lab Sampling Log</h3>
          <span className="text-xs text-gray-500">{samples.length} logged samples</span>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-100 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                <th className="px-6 py-3">Sample Code</th>
                <th className="px-6 py-3">Timestamp</th>
                <th className="px-6 py-3">Location Node</th>
                <th className="px-6 py-3">pH Level</th>
                <th className="px-6 py-3">Turbidity</th>
                <th className="px-6 py-3">Chlorine</th>
                <th className="px-6 py-3">Temp</th>
                <th className="px-6 py-3">Sampled By</th>
                <th className="px-6 py-3">Potability Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 text-sm">
              {loading ? (
                <tr>
                  <td colSpan={9} className="px-6 py-12 text-center text-gray-500">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-cyan-500 mb-2" />
                    Loading laboratory telemetry records...
                  </td>
                </tr>
              ) : samples.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-6 py-12 text-center text-gray-500">
                    No water sample records found in database.
                  </td>
                </tr>
              ) : (
                samples.map((s) => (
                  <tr key={s.id} className="hover:bg-gray-50/80 transition">
                    <td className="px-6 py-4 font-mono font-medium text-xs text-gray-700">{s.sampleCode || `#${s.id}`}</td>
                    <td className="px-6 py-4 text-xs text-gray-500">
                      {new Date(s.sampleTimestamp).toLocaleString()}
                    </td>
                    <td className="px-6 py-4 text-xs font-medium text-gray-900">
                      {s.locationCode || `Loc #${s.locationId}`}
                    </td>
                    <td className="px-6 py-4 text-xs font-semibold text-gray-900">
                      {s.phLevel.toFixed(2)}
                    </td>
                    <td className="px-6 py-4 text-xs text-gray-700">
                      {s.turbidityNtu.toFixed(2)} NTU
                    </td>
                    <td className="px-6 py-4 text-xs text-gray-700">
                      {s.residualChlorineMgL.toFixed(2)} mg/L
                    </td>
                    <td className="px-6 py-4 text-xs text-gray-500">
                      {s.waterTemperatureCelsius ? `${s.waterTemperatureCelsius.toFixed(1)} °C` : '-'}
                    </td>
                    <td className="px-6 py-4 text-xs text-gray-500">
                      {s.sampledBy}
                    </td>
                    <td className="px-6 py-4">
                      {s.potabilityStatus === 'POTABLE' ? (
                        <span className="inline-flex items-center gap-1 text-xs font-semibold px-2 py-0.5 rounded bg-emerald-50 text-emerald-700 border border-emerald-200">
                          <CheckCircle className="w-3 h-3" /> Potable Safe
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 text-xs font-semibold px-2 py-0.5 rounded bg-amber-50 text-amber-700 border border-amber-200">
                          <AlertTriangle className="w-3 h-3" /> {s.potabilityStatus}
                        </span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Record Sample Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6 max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-bold text-gray-900 mb-1">Record Lab Water Quality Sample</h3>
            <p className="text-xs text-gray-500 mb-4">
              Enter physical-chemical assay parameters. The backend automatically evaluates potability thresholds.
            </p>

            <form onSubmit={handleCreateSample} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">Location / Node</label>
                <select
                  required
                  value={formData.locationId}
                  onChange={(e) => setFormData({ ...formData, locationId: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-cyan-500 focus:outline-none"
                >
                  {locations.map(l => (
                    <option key={l.id} value={l.id}>{l.locationCode} - {l.city} ({l.installationType})</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">Sampling Timestamp</label>
                <input
                  type="datetime-local"
                  required
                  value={formData.sampleTimestamp}
                  onChange={(e) => setFormData({ ...formData, sampleTimestamp: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-cyan-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 mb-1">pH (6.5 - 8.5)</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.phLevel}
                    onChange={(e) => setFormData({ ...formData, phLevel: parseFloat(e.target.value) })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Turbidity (&le; 2.0 NTU)</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.turbidityNtu}
                    onChange={(e) => setFormData({ ...formData, turbidityNtu: parseFloat(e.target.value) })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Free Cl (mg/L)</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.residualChlorineMgL}
                    onChange={(e) => setFormData({ ...formData, residualChlorineMgL: parseFloat(e.target.value) })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-cyan-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Conductivity (&micro;S/cm)</label>
                  <input
                    type="number"
                    step="0.1"
                    value={formData.conductivityUsCm}
                    onChange={(e) => setFormData({ ...formData, conductivityUsCm: parseFloat(e.target.value) })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Temperature (&deg;C)</label>
                  <input
                    type="number"
                    step="0.1"
                    value={formData.waterTemperatureCelsius}
                    onChange={(e) => setFormData({ ...formData, waterTemperatureCelsius: parseFloat(e.target.value) })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-cyan-500 focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">Sampled By</label>
                <input
                  type="text"
                  value={formData.sampledBy}
                  onChange={(e) => setFormData({ ...formData, sampledBy: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-cyan-500 focus:outline-none"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-4 py-2 text-sm text-white bg-cyan-600 hover:bg-cyan-700 rounded-lg font-medium shadow-sm transition disabled:opacity-50"
                >
                  {submitting ? 'Recording...' : 'Save Lab Sample'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
