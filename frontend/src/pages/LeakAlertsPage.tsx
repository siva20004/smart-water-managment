import React, { useEffect, useState } from 'react';
import { leakService } from '../api/waterServices';
import type { LeakAlert } from '../types/models';
import { 
  AlertTriangle, 
  CheckCircle, 
  Clock, 
  Search, 
  Play, 
  ShieldAlert, 
  RefreshCw,
  Info
} from 'lucide-react';

export const LeakAlertsPage: React.FC = () => {
  const [alerts, setAlerts] = useState<LeakAlert[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [severityFilter, setSeverityFilter] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState('');
  const [scanning, setScanning] = useState(false);
  const [scanResult, setScanResult] = useState<string | null>(null);

  // Resolution modal state
  const [selectedAlert, setSelectedAlert] = useState<LeakAlert | null>(null);
  const [actionType, setActionType] = useState<'INVESTIGATING' | 'RESOLVED' | 'FALSE_POSITIVE' | null>(null);
  const [actionNotes, setActionNotes] = useState('');
  const [updating, setUpdating] = useState(false);

  const fetchAlerts = async () => {
    setLoading(true);
    try {
      const data = await leakService.getAllAlerts({
        status: statusFilter || undefined, 
        severity: severityFilter || undefined, 
        page: 0, 
        size: 100
      });
      setAlerts(data.content);
    } catch (err: unknown) {
      console.error('Failed to load leak alerts:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAlerts();
  }, [statusFilter, severityFilter]);

  const handleRunScan = async () => {
    setScanning(true);
    setScanResult(null);
    try {
      const res = await leakService.triggerScan();
      setScanResult(`Detection engine completed: ${res.length} potential anomalies detected across telemetry nodes.`);
      await fetchAlerts();
    } catch (err: unknown) {
      alert('Failed to trigger anomaly scan.');
    } finally {
      setScanning(false);
    }
  };

  const handleOpenActionModal = (alert: LeakAlert, type: 'INVESTIGATING' | 'RESOLVED' | 'FALSE_POSITIVE') => {
    setSelectedAlert(alert);
    setActionType(type);
    setActionNotes('');
  };

  const handleSubmitStatusUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAlert || !actionType) return;
    setUpdating(true);
    try {
      await leakService.updateStatus(selectedAlert.id, actionType, actionNotes);
      setSelectedAlert(null);
      setActionType(null);
      await fetchAlerts();
    } catch (err: unknown) {
      alert('Status update failed.');
    } finally {
      setUpdating(false);
    }
  };

  const filteredAlerts = alerts.filter(a => {
    const term = searchTerm.toLowerCase();
    const meterMatch = a.meterSerial?.toLowerCase().includes(term);
    const descMatch = a.alertType?.toLowerCase().includes(term);
    const idMatch = a.id.toString().includes(term);
    return meterMatch || descMatch || idMatch;
  });

  const getSeverityBadge = (severity: string) => {
    switch (severity) {
      case 'CRITICAL':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-red-100 text-red-800 border border-red-200">Critical Surge</span>;
      case 'HIGH':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-orange-100 text-orange-800 border border-orange-200">High Risk</span>;
      case 'MEDIUM':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-100 text-amber-800 border border-amber-200">Medium</span>;
      default:
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-100 text-blue-800 border border-blue-200">Low</span>;
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'NEW':
      case 'OPEN':
        return <span className="inline-flex items-center gap-1 text-xs font-medium text-red-600 bg-red-50 px-2 py-0.5 rounded"><AlertTriangle className="w-3 h-3"/> Active</span>;
      case 'INVESTIGATING':
        return <span className="inline-flex items-center gap-1 text-xs font-medium text-amber-600 bg-amber-50 px-2 py-0.5 rounded"><Clock className="w-3 h-3"/> Under Review</span>;
      case 'RESOLVED':
        return <span className="inline-flex items-center gap-1 text-xs font-medium text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded"><CheckCircle className="w-3 h-3"/> Resolved</span>;
      case 'FALSE_POSITIVE':
        return <span className="inline-flex items-center gap-1 text-xs font-medium text-gray-500 bg-gray-100 px-2 py-0.5 rounded">False Positive</span>;
      default:
        return <span>{status}</span>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Simulation Banner */}
      <div className="bg-amber-50 border-l-4 border-amber-500 p-4 rounded-r-lg">
        <div className="flex items-start gap-3">
          <Info className="w-5 h-5 text-amber-600 mt-0.5 flex-shrink-0" />
          <div>
            <h4 className="text-sm font-semibold text-amber-900">Module 8: Heuristic Leak Detection & Acoustic Anomaly Simulation</h4>
            <p className="text-xs text-amber-800 mt-0.5">
              Simulated telemetry rules detect abnormal continuous nocturnal flow (&gt; 0.05 m³/h), high-magnitude burst surges (&gt; 250% baseline), and network pressure drops. Educational demonstrator only.
            </p>
          </div>
        </div>
      </div>

      {/* Header & Controls */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Leak Detection & Alerts</h1>
          <p className="text-sm text-gray-500">Autonomous pressure & telemetry anomaly engine</p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={handleRunScan}
            disabled={scanning}
            className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium rounded-lg shadow-sm transition disabled:opacity-50"
          >
            <Play className={`w-4 h-4 ${scanning ? 'animate-spin' : ''}`} />
            {scanning ? 'Analyzing Telemetry...' : 'Run Anomaly Scan'}
          </button>
          <button
            onClick={fetchAlerts}
            className="p-2 border border-gray-300 text-gray-600 hover:bg-gray-50 rounded-lg shadow-sm transition"
            title="Refresh alerts"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {scanResult && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs rounded-lg flex items-center justify-between">
          <span>{scanResult}</span>
          <button onClick={() => setScanResult(null)} className="text-emerald-600 hover:text-emerald-900 font-bold ml-2">×</button>
        </div>
      )}

      {/* Filters & Search */}
      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3 flex-1 min-w-[280px]">
          <div className="relative flex-1">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Search serial number, reason..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-9 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>

        <div className="flex items-center gap-3">
          <select
            value={severityFilter}
            onChange={(e) => setSeverityFilter(e.target.value)}
            className="border border-gray-200 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">All Severities</option>
            <option value="CRITICAL">Critical</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="border border-gray-200 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">All Statuses</option>
            <option value="OPEN">Open</option>
            <option value="INVESTIGATING">Under Investigation</option>
            <option value="RESOLVED">Resolved</option>
            <option value="FALSE_POSITIVE">False Positive</option>
          </select>
        </div>
      </div>

      {/* Alerts Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-100 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                <th className="px-6 py-3">Alert Code</th>
                <th className="px-6 py-3">Timestamp</th>
                <th className="px-6 py-3">Severity</th>
                <th className="px-6 py-3">Target Meter</th>
                <th className="px-6 py-3">Anomaly Type</th>
                <th className="px-6 py-3">Loss Estimate</th>
                <th className="px-6 py-3">Status</th>
                <th className="px-6 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 text-sm">
              {loading ? (
                <tr>
                  <td colSpan={8} className="px-6 py-12 text-center text-gray-500">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-indigo-500 mb-2" />
                    Scanning database for telemetry alert records...
                  </td>
                </tr>
              ) : filteredAlerts.length === 0 ? (
                <tr>
                  <td colSpan={8} className="px-6 py-12 text-center text-gray-500">
                    <ShieldAlert className="w-8 h-8 mx-auto text-emerald-400 mb-2" />
                    No active leak alerts found for current criteria.
                  </td>
                </tr>
              ) : (
                filteredAlerts.map((alert) => (
                  <tr key={alert.id} className="hover:bg-gray-50/80 transition">
                    <td className="px-6 py-4 font-mono font-bold text-gray-900">{alert.alertCode || `#${alert.id}`}</td>
                    <td className="px-6 py-4 text-xs text-gray-500">
                      {new Date(alert.detectedAt).toLocaleString()}
                    </td>
                    <td className="px-6 py-4">
                      {getSeverityBadge(alert.severity)}
                    </td>
                    <td className="px-6 py-4">
                      <span className="font-mono text-xs font-medium bg-gray-100 px-2 py-1 rounded">
                        {alert.meterSerial || `Meter #${alert.meterId || 'N/A'}`}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <p className="text-gray-900 font-medium text-xs">{alert.alertType}</p>
                    </td>
                    <td className="px-6 py-4 text-xs font-semibold text-red-600">
                      {alert.estimatedLossRateM3H ? `${alert.estimatedLossRateM3H.toFixed(2)} m³/h` : 'Pending Assessment'}
                    </td>
                    <td className="px-6 py-4">
                      {getStatusBadge(alert.status)}
                    </td>
                    <td className="px-6 py-4 text-right space-x-1">
                      {(alert.status === 'NEW' || alert.status === 'ACKNOWLEDGED') && (
                        <button
                          onClick={() => handleOpenActionModal(alert, 'INVESTIGATING')}
                          className="px-2 py-1 text-xs font-medium bg-amber-50 text-amber-700 hover:bg-amber-100 rounded border border-amber-200 transition"
                        >
                          Investigate
                        </button>
                      )}
                      {(alert.status === 'NEW' || alert.status === 'ACKNOWLEDGED' || alert.status === 'INVESTIGATING') && (
                        <>
                          <button
                            onClick={() => handleOpenActionModal(alert, 'RESOLVED')}
                            className="px-2 py-1 text-xs font-medium bg-emerald-50 text-emerald-700 hover:bg-emerald-100 rounded border border-emerald-200 transition"
                          >
                            Resolve
                          </button>
                          <button
                            onClick={() => handleOpenActionModal(alert, 'FALSE_POSITIVE')}
                            className="px-2 py-1 text-xs font-medium bg-gray-50 text-gray-600 hover:bg-gray-100 rounded border border-gray-200 transition"
                          >
                            Dismiss
                          </button>
                        </>
                      )}
                      {alert.status === 'RESOLVED' && (
                        <span className="text-xs text-emerald-600 font-medium">Closed</span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Resolution Modal */}
      {selectedAlert && actionType && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6 animate-in fade-in zoom-in duration-150">
            <h3 className="text-lg font-bold text-gray-900 mb-1">
              {actionType === 'INVESTIGATING' && 'Dispatch Inspection / Investigation'}
              {actionType === 'RESOLVED' && 'Mark Anomaly as Resolved'}
              {actionType === 'FALSE_POSITIVE' && 'Mark as False Positive Anomaly'}
            </h3>
            <p className="text-xs text-gray-500 mb-4">
              Updating Alert #{selectedAlert.id} (Meter: {selectedAlert.meterSerial || 'N/A'})
            </p>

            <form onSubmit={handleSubmitStatusUpdate} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">Resolution / Operator Notes</label>
                <textarea
                  rows={3}
                  required
                  placeholder="Describe field findings, acoustic sensor check, or mitigation steps..."
                  value={actionNotes}
                  onChange={(e) => setActionNotes(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => { setSelectedAlert(null); setActionType(null); }}
                  className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={updating}
                  className="px-4 py-2 text-sm text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg font-medium shadow-sm transition disabled:opacity-50"
                >
                  {updating ? 'Updating...' : 'Confirm Action'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
