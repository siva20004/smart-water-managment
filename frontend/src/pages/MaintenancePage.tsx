import React, { useEffect, useState } from 'react';
import { maintenanceService, meterService, analyticsService } from '../api/waterServices';
import type { MaintenanceRequest, WaterMeter, Location } from '../types/models';
import { 
  Plus, 
  RefreshCw, 
  Info,
  User
} from 'lucide-react';

export const MaintenancePage: React.FC = () => {
  const [requests, setRequests] = useState<MaintenanceRequest[]>([]);
  const [meters, setMeters] = useState<WaterMeter[]>([]);
  const [locations, setLocations] = useState<Location[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');

  // Create Modal
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [creating, setCreating] = useState(false);
  const [createForm, setCreateForm] = useState({
    locationId: '',
    meterId: '',
    title: '',
    description: '',
    priority: 'MEDIUM',
    category: 'METER_REPLACEMENT',
    assignedTechnician: 'Kenji Tanaka'
  });

  // Update Status Modal
  const [selectedReq, setSelectedReq] = useState<MaintenanceRequest | null>(null);
  const [targetStatus, setTargetStatus] = useState<string>('');
  const [resolutionSummary, setResolutionSummary] = useState('');
  const [partsCost, setPartsCost] = useState('0');
  const [laborHours, setLaborHours] = useState('1.5');
  const [updating, setUpdating] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [reqData, meterData, locData] = await Promise.all([
        maintenanceService.getAllRequests({ 
          status: statusFilter || undefined, 
          priority: priorityFilter || undefined, 
          page: 0, 
          size: 100 
        }),
        meterService.getAll({ page: 0, size: 50 }),
        analyticsService.getLocations()
      ]);
      setRequests(reqData.content);
      setMeters(meterData.content);
      setLocations(locData);
      if (locData.length > 0 && !createForm.locationId) {
        setCreateForm(prev => ({ ...prev, locationId: locData[0].id.toString() }));
      }
    } catch (err: unknown) {
      console.error('Failed to fetch maintenance work orders:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [statusFilter, priorityFilter]);

  const handleCreateOrder = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!createForm.locationId) return;
    setCreating(true);
    try {
      await maintenanceService.createRequest({
        locationId: Number(createForm.locationId),
        meterId: createForm.meterId ? Number(createForm.meterId) : undefined,
        title: createForm.title,
        description: createForm.description,
        priority: createForm.priority,
        category: createForm.category
      });
      setShowCreateModal(false);
      setCreateForm({
        locationId: locations[0]?.id.toString() || '',
        meterId: '',
        title: '',
        description: '',
        priority: 'MEDIUM',
        category: 'METER_REPLACEMENT',
        assignedTechnician: 'Kenji Tanaka'
      });
      await fetchData();
    } catch (err: unknown) {
      alert('Failed to create work order.');
    } finally {
      setCreating(false);
    }
  };

  const handleOpenStatusModal = (req: MaintenanceRequest, nextStatus: string) => {
    setSelectedReq(req);
    setTargetStatus(nextStatus);
    setResolutionSummary('');
    setPartsCost('0');
    setLaborHours('1.5');
  };

  const handleUpdateStatus = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedReq || !targetStatus) return;
    setUpdating(true);
    try {
      await maintenanceService.updateStatus(selectedReq.id, {
        status: targetStatus,
        resolutionSummary: resolutionSummary || undefined,
        partsCost: partsCost ? Number(partsCost) : undefined,
        laborHours: laborHours ? Number(laborHours) : undefined
      });
      setSelectedReq(null);
      await fetchData();
    } catch (err: unknown) {
      alert('Failed to update work order lifecycle.');
    } finally {
      setUpdating(false);
    }
  };

  const getPriorityBadge = (p: string) => {
    switch (p) {
      case 'CRITICAL':
      case 'EMERGENCY':
        return <span className="px-2 py-0.5 text-xs font-semibold rounded bg-red-100 text-red-800">Critical</span>;
      case 'HIGH':
        return <span className="px-2 py-0.5 text-xs font-semibold rounded bg-orange-100 text-orange-800">High</span>;
      case 'MEDIUM':
        return <span className="px-2 py-0.5 text-xs font-semibold rounded bg-amber-100 text-amber-800">Medium</span>;
      default:
        return <span className="px-2 py-0.5 text-xs font-semibold rounded bg-blue-100 text-blue-800">Low</span>;
    }
  };

  const getStatusBadge = (s: string) => {
    switch (s) {
      case 'PENDING':
      case 'OPEN':
        return <span className="px-2 py-0.5 text-xs font-medium rounded bg-gray-100 text-gray-700">Pending</span>;
      case 'ASSIGNED':
        return <span className="px-2 py-0.5 text-xs font-medium rounded bg-blue-100 text-blue-800">Assigned</span>;
      case 'IN_PROGRESS':
        return <span className="px-2 py-0.5 text-xs font-medium rounded bg-purple-100 text-purple-800 animate-pulse">In Progress</span>;
      case 'COMPLETED':
        return <span className="px-2 py-0.5 text-xs font-medium rounded bg-emerald-100 text-emerald-800">Completed</span>;
      case 'CANCELLED':
        return <span className="px-2 py-0.5 text-xs font-medium rounded bg-red-100 text-red-700">Cancelled</span>;
      default:
        return <span>{s}</span>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Simulation Info */}
      <div className="bg-purple-50 border-l-4 border-purple-500 p-4 rounded-r-lg">
        <div className="flex items-start gap-3">
          <Info className="w-5 h-5 text-purple-600 mt-0.5 flex-shrink-0" />
          <div>
            <h4 className="text-sm font-semibold text-purple-900">Module 10: Utility Asset Maintenance & Field Work Orders</h4>
            <p className="text-xs text-purple-800 mt-0.5">
              Simulates Tokyo-Kanto field technician dispatching, hardware replacement workflows, labor tracking and automated alert resolution upon completion.
            </p>
          </div>
        </div>
      </div>

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Maintenance & Work Orders</h1>
          <p className="text-sm text-gray-500">Field engineering dispatch and asset lifecycle management</p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => setShowCreateModal(true)}
            className="inline-flex items-center gap-2 px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white text-sm font-medium rounded-lg shadow-sm transition"
          >
            <Plus className="w-4 h-4" />
            Dispatch Work Order
          </button>
          <button
            onClick={fetchData}
            className="p-2 border border-gray-300 text-gray-600 hover:bg-gray-50 rounded-lg shadow-sm transition"
            title="Refresh orders"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="border border-gray-200 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-purple-500"
          >
            <option value="">All Statuses</option>
            <option value="OPEN">Pending / Open</option>
            <option value="ASSIGNED">Assigned</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>

          <select
            value={priorityFilter}
            onChange={(e) => setPriorityFilter(e.target.value)}
            className="border border-gray-200 rounded-lg px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-purple-500"
          >
            <option value="">All Priorities</option>
            <option value="EMERGENCY">Emergency / Critical</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>
        </div>
        <span className="text-xs text-gray-500 font-medium">{requests.length} work orders</span>
      </div>

      {/* Orders Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-100 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                <th className="px-6 py-3">Order Number</th>
                <th className="px-6 py-3">Title & Category</th>
                <th className="px-6 py-3">Target Meter</th>
                <th className="px-6 py-3">Priority</th>
                <th className="px-6 py-3">Assigned Tech</th>
                <th className="px-6 py-3">Status</th>
                <th className="px-6 py-3">Cost / Labor</th>
                <th className="px-6 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 text-sm">
              {loading ? (
                <tr>
                  <td colSpan={8} className="px-6 py-12 text-center text-gray-500">
                    <RefreshCw className="w-6 h-6 animate-spin mx-auto text-purple-500 mb-2" />
                    Fetching maintenance lifecycle tickets...
                  </td>
                </tr>
              ) : requests.length === 0 ? (
                <tr>
                  <td colSpan={8} className="px-6 py-12 text-center text-gray-500">
                    No maintenance orders match current criteria.
                  </td>
                </tr>
              ) : (
                requests.map((r) => (
                  <tr key={r.id} className="hover:bg-gray-50/80 transition">
                    <td className="px-6 py-4 font-mono font-bold text-gray-900">{r.requestNumber || `#${r.id}`}</td>
                    <td className="px-6 py-4">
                      <p className="font-semibold text-gray-900 text-xs">{r.title}</p>
                      <p className="text-xs text-gray-400 capitalize">{r.category?.toLowerCase().replace('_', ' ')}</p>
                    </td>
                    <td className="px-6 py-4">
                      {r.meterSerial ? (
                        <span className="font-mono text-xs bg-gray-100 px-2 py-0.5 rounded">{r.meterSerial}</span>
                      ) : (
                        <span className="text-xs text-gray-400">Network Segment</span>
                      )}
                    </td>
                    <td className="px-6 py-4">
                      {getPriorityBadge(r.priority)}
                    </td>
                    <td className="px-6 py-4 text-xs font-medium text-gray-700">
                      <div className="flex items-center gap-1.5">
                        <User className="w-3.5 h-3.5 text-gray-400" />
                        {r.assignedTechnicianName || 'Kenji Tanaka'}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      {getStatusBadge(r.status)}
                    </td>
                    <td className="px-6 py-4 text-xs text-gray-600">
                      {r.laborHours ? `${r.laborHours}h` : '-'} / {r.partsCost ? `¥${r.partsCost.toLocaleString()}` : '¥0'}
                    </td>
                    <td className="px-6 py-4 text-right space-x-1">
                      {(r.status === 'PENDING' || r.status === 'OPEN' || r.status === 'ASSIGNED') && (
                        <button
                          onClick={() => handleOpenStatusModal(r, 'IN_PROGRESS')}
                          className="px-2.5 py-1 text-xs font-medium bg-blue-50 text-blue-700 hover:bg-blue-100 rounded border border-blue-200 transition"
                        >
                          Start Work
                        </button>
                      )}
                      {r.status === 'IN_PROGRESS' && (
                        <button
                          onClick={() => handleOpenStatusModal(r, 'COMPLETED')}
                          className="px-2.5 py-1 text-xs font-medium bg-emerald-50 text-emerald-700 hover:bg-emerald-100 rounded border border-emerald-200 transition"
                        >
                          Complete Order
                        </button>
                      )}
                      {(r.status === 'PENDING' || r.status === 'OPEN' || r.status === 'IN_PROGRESS') && (
                        <button
                          onClick={() => handleOpenStatusModal(r, 'CANCELLED')}
                          className="px-2.5 py-1 text-xs font-medium bg-gray-50 text-gray-600 hover:bg-gray-100 rounded border border-gray-200 transition"
                        >
                          Cancel
                        </button>
                      )}
                      {r.status === 'COMPLETED' && (
                        <span className="text-xs text-emerald-600 font-medium">Archived</span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create Order Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-lg w-full p-6 animate-in fade-in zoom-in duration-150">
            <h3 className="text-lg font-bold text-gray-900 mb-1">Create Maintenance Work Order</h3>
            <p className="text-xs text-gray-500 mb-4">
              Schedule field technician inspection, meter calibration, or pipe maintenance.
            </p>

            <form onSubmit={handleCreateOrder} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">Order Title</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Ultrasonic meter acoustic seal inspection"
                  value={createForm.title}
                  onChange={(e) => setCreateForm({ ...createForm, title: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">Location Node</label>
                <select
                  required
                  value={createForm.locationId}
                  onChange={(e) => setCreateForm({ ...createForm, locationId: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  {locations.map(l => (
                    <option key={l.id} value={l.id}>{l.locationCode} - {l.city} ({l.installationType})</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Category</label>
                  <select
                    value={createForm.category}
                    onChange={(e) => setCreateForm({ ...createForm, category: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-purple-500 focus:outline-none"
                  >
                    <option value="METER_REPLACEMENT">Meter Replacement</option>
                    <option value="LEAK_REPAIR">Leak Repair</option>
                    <option value="ROUTINE_INSPECTION">Routine Inspection</option>
                    <option value="SENSOR_CALIBRATION">Sensor Calibration</option>
                    <option value="EMERGENCY_DISPATCH">Emergency Dispatch</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Priority</label>
                  <select
                    value={createForm.priority}
                    onChange={(e) => setCreateForm({ ...createForm, priority: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-purple-500 focus:outline-none"
                  >
                    <option value="EMERGENCY">Emergency</option>
                    <option value="HIGH">High</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="LOW">Low</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Target Meter (Optional)</label>
                  <select
                    value={createForm.meterId}
                    onChange={(e) => setCreateForm({ ...createForm, meterId: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white focus:ring-2 focus:ring-purple-500 focus:outline-none"
                  >
                    <option value="">General Network (No Meter)</option>
                    {meters.map(m => (
                      <option key={m.id} value={m.id}>{m.serialNumber} ({m.customerName || 'Unassigned'})</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Assigned Technician</label>
                  <input
                    type="text"
                    required
                    value={createForm.assignedTechnician}
                    onChange={(e) => setCreateForm({ ...createForm, assignedTechnician: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">Scope & Description</label>
                <textarea
                  rows={2}
                  value={createForm.description}
                  onChange={(e) => setCreateForm({ ...createForm, description: e.target.value })}
                  placeholder="Field instructions, access codes, pipe segment info..."
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={creating}
                  className="px-4 py-2 text-sm text-white bg-purple-600 hover:bg-purple-700 rounded-lg font-medium shadow-sm transition disabled:opacity-50"
                >
                  {creating ? 'Dispatching...' : 'Dispatch Ticket'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Update Status Modal */}
      {selectedReq && targetStatus && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6 animate-in fade-in zoom-in duration-150">
            <h3 className="text-lg font-bold text-gray-900 mb-1">
              Transition Work Order to {targetStatus.replace('_', ' ')}
            </h3>
            <p className="text-xs text-gray-500 mb-4">
              Order #{selectedReq.id}: {selectedReq.title}
            </p>

            <form onSubmit={handleUpdateStatus} className="space-y-4">
              {targetStatus === 'COMPLETED' && (
                <>
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="block text-xs font-semibold text-gray-700 mb-1">Labor Hours</label>
                      <input
                        type="number"
                        step="0.5"
                        required
                        value={laborHours}
                        onChange={(e) => setLaborHours(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:outline-none"
                      />
                    </div>
                    <div>
                      <label className="block text-xs font-semibold text-gray-700 mb-1">Parts Cost (JPY ¥)</label>
                      <input
                        type="number"
                        step="100"
                        required
                        value={partsCost}
                        onChange={(e) => setPartsCost(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:outline-none"
                      />
                    </div>
                  </div>
                </>
              )}

              <div>
                <label className="block text-xs font-semibold text-gray-700 mb-1">Resolution Summary / Notes</label>
                <textarea
                  rows={3}
                  required={targetStatus === 'COMPLETED'}
                  value={resolutionSummary}
                  onChange={(e) => setResolutionSummary(e.target.value)}
                  placeholder="Technician observations, repaired parts, hydraulic test result..."
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setSelectedReq(null)}
                  className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={updating}
                  className="px-4 py-2 text-sm text-white bg-purple-600 hover:bg-purple-700 rounded-lg font-medium shadow-sm transition disabled:opacity-50"
                >
                  {updating ? 'Updating...' : 'Confirm Transition'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
