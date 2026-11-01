import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Gauge,
  Search,
  Filter,
  Plus,
  Edit2,
  Activity,
  Battery,
  BatteryCharging,
  BatteryWarning,
  CheckCircle,
  XCircle,
  AlertTriangle,
  Radio,
  ChevronLeft,
  ChevronRight,
  UserCheck,
  X,
} from 'lucide-react';
import type { WaterMeter, PageResponse, Location, Customer } from '../types/models';
import { meterService, customerService, analyticsService } from '../api/waterServices';

export const MetersPage: React.FC = () => {
  const navigate = useNavigate();
  const [meters, setMeters] = useState<WaterMeter[]>([]);
  const [locations, setLocations] = useState<Location[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filters & Pagination
  const [search, setSearch] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Modals
  const [modalOpen, setModalOpen] = useState(false);
  const [assignModalOpen, setAssignModalOpen] = useState(false);
  const [selectedMeterForAssign, setSelectedMeterForAssign] = useState<WaterMeter | null>(null);
  const [targetCustomerId, setTargetCustomerId] = useState<number | ''>('');
  const [editingMeter, setEditingMeter] = useState<WaterMeter | null>(null);

  // Form State
  const [formSerial, setFormSerial] = useState('');
  const [formModel, setFormModel] = useState('Aichi Tokei SU20');
  const [formType, setFormType] = useState('ULTRASONIC');
  const [formDiameter, setFormDiameter] = useState(20);
  const [formLocationId, setFormLocationId] = useState<number | ''>('');
  const [formCustomerId, setFormCustomerId] = useState<number | ''>('');
  const [formDate, setFormDate] = useState(new Date().toISOString().split('T')[0]);
  const [formStatus, setFormStatus] = useState('ACTIVE');
  const [formBattery, setFormBattery] = useState(98.5);
  const [formError, setFormError] = useState<string | null>(null);
  const [formSubmitting, setFormSubmitting] = useState(false);

  const fetchMeters = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data: PageResponse<WaterMeter> = await meterService.getAll({
        search: search.trim() || undefined,
        status: selectedStatus || undefined,
        page,
        size: pageSize,
      });
      setMeters(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to query water meters');
    } finally {
      setLoading(false);
    }
  }, [search, selectedStatus, page, pageSize]);

  useEffect(() => {
    fetchMeters();
    // Load lookup locations and customers for forms
    analyticsService.getLocations().then(setLocations).catch(console.warn);
    customerService.getAll({ size: 100 }).then((res) => setCustomers(res.content)).catch(console.warn);
  }, [fetchMeters]);

  const openCreateModal = () => {
    setEditingMeter(null);
    setFormSerial(`MTR-TK-${Math.floor(100000 + Math.random() * 900000)}`);
    setFormModel('Aichi Tokei SU20');
    setFormType('ULTRASONIC');
    setFormDiameter(20);
    setFormLocationId(locations[0]?.id || '');
    setFormCustomerId('');
    setFormDate(new Date().toISOString().split('T')[0]);
    setFormStatus('ACTIVE');
    setFormBattery(99.0);
    setFormError(null);
    setModalOpen(true);
  };

  const openEditModal = (m: WaterMeter) => {
    setEditingMeter(m);
    setFormSerial(m.serialNumber);
    setFormModel(m.meterModel);
    setFormType(m.meterType);
    setFormDiameter(m.diameterMm);
    setFormLocationId(m.locationId);
    setFormCustomerId(m.customerId || '');
    setFormDate(m.installationDate);
    setFormStatus(m.status);
    setFormBattery(m.batteryLevelPercent || 100);
    setFormError(null);
    setModalOpen(true);
  };

  const handleSaveMeter = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);

    if (!formSerial.trim() || !formLocationId) {
      setFormError('Serial number and deployment location are required');
      return;
    }

    setFormSubmitting(true);
    try {
      const payload: Partial<WaterMeter> = {
        serialNumber: formSerial.trim(),
        locationId: Number(formLocationId),
        customerId: formCustomerId ? Number(formCustomerId) : undefined,
        meterModel: formModel.trim(),
        meterType: formType,
        diameterMm: Number(formDiameter),
        installationDate: formDate,
        status: formStatus as WaterMeter['status'],
        batteryLevelPercent: Number(formBattery),
      };

      if (editingMeter) {
        await meterService.update(editingMeter.id, payload);
      } else {
        await meterService.create(payload);
      }

      setModalOpen(false);
      fetchMeters();
    } catch (err: unknown) {
      setFormError(err instanceof Error ? err.message : 'Error saving water meter');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleStatusChange = async (meterId: number, status: string) => {
    try {
      await meterService.setStatus(meterId, status);
      fetchMeters();
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Failed to change status');
    }
  };

  const openAssignModal = (m: WaterMeter) => {
    setSelectedMeterForAssign(m);
    setTargetCustomerId(m.customerId || '');
    setAssignModalOpen(true);
  };

  const handleAssignSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedMeterForAssign) return;
    try {
      await meterService.assign(selectedMeterForAssign.id, targetCustomerId ? Number(targetCustomerId) : null);
      setAssignModalOpen(false);
      fetchMeters();
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Could not reassign meter');
    }
  };

  const getBatteryIcon = (pct?: number) => {
    if (!pct) return <Battery className="w-3.5 h-3.5 text-slate-500" />;
    if (pct > 70) return <BatteryCharging className="w-3.5 h-3.5 text-emerald-400" />;
    if (pct > 25) return <Battery className="w-3.5 h-3.5 text-amber-400" />;
    return <BatteryWarning className="w-3.5 h-3.5 text-rose-400 animate-pulse" />;
  };

  const getStatusBadge = (status: string) => {
    switch (status.toUpperCase()) {
      case 'ACTIVE':
        return (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono bg-emerald-500/10 border border-emerald-500/30 text-emerald-400">
            <CheckCircle className="w-3 h-3" /> ACTIVE
          </span>
        );
      case 'FAULTY':
        return (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono bg-rose-500/10 border border-rose-500/30 text-rose-400">
            <AlertTriangle className="w-3 h-3" /> FAULTY
          </span>
        );
      case 'MAINTENANCE':
        return (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono bg-amber-500/10 border border-amber-500/30 text-amber-400">
            <Radio className="w-3 h-3" /> MAINTENANCE
          </span>
        );
      case 'REMOVED':
      case 'RETIRED':
        return (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono bg-slate-700/50 border border-slate-600 text-slate-400">
            <XCircle className="w-3 h-3" /> {status}
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono bg-slate-800 text-slate-300">
            {status}
          </span>
        );
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <Gauge className="w-6 h-6 text-cyan-400" />
            Smart Water Meter Fleet
          </h1>
          <p className="text-slate-400 text-sm mt-0.5">
            Ultrasonic & electromagnetic AMI meters, battery states, and telemetry link status
          </p>
        </div>
        <button
          onClick={openCreateModal}
          className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white font-medium text-sm transition-all shadow-lg shadow-cyan-600/20"
        >
          <Plus className="w-4 h-4" />
          <span>Register Meter</span>
        </button>
      </div>

      {/* Filter Bar */}
      <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 flex flex-col sm:flex-row items-center gap-3">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search by serial number (e.g. MTR-TK-889101) or customer name..."
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(0);
            }}
            className="w-full pl-9 pr-4 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500"
          />
        </div>

        <div className="relative w-full sm:w-56">
          <Filter className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <select
            value={selectedStatus}
            onChange={(e) => {
              setSelectedStatus(e.target.value);
              setPage(0);
            }}
            className="w-full pl-8 pr-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-medium focus:outline-none focus:border-cyan-500"
          >
            <option value="">All Statuses</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="INACTIVE">INACTIVE</option>
            <option value="FAULTY">FAULTY</option>
            <option value="MAINTENANCE">MAINTENANCE</option>
            <option value="REMOVED">REMOVED</option>
            <option value="RETIRED">RETIRED</option>
          </select>
        </div>
      </div>

      {/* Meter Fleet Table */}
      <div className="rounded-xl bg-slate-900 border border-slate-800 overflow-hidden shadow-lg">
        {loading ? (
          <div className="p-12 text-center text-slate-400 space-y-3">
            <div className="w-8 h-8 border-2 border-cyan-500 border-t-transparent rounded-full animate-spin mx-auto" />
            <div className="text-sm">Querying smart meter fleet telemetry...</div>
          </div>
        ) : error ? (
          <div className="p-8 text-center text-rose-400 space-y-2">
            <AlertTriangle className="w-8 h-8 mx-auto" />
            <div className="text-sm font-medium">{error}</div>
            <button onClick={fetchMeters} className="text-xs text-cyan-400 hover:underline">
              Retry Query
            </button>
          </div>
        ) : meters.length === 0 ? (
          <div className="p-12 text-center text-slate-400 space-y-3">
            <Gauge className="w-10 h-10 text-slate-600 mx-auto" />
            <div className="text-base font-medium text-slate-300">No Water Meters Found</div>
            <div className="text-xs text-slate-500 max-w-sm mx-auto">
              No meters matching the selected filters exist in the utility database.
            </div>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/70 border-b border-slate-800 text-xs text-slate-400 font-mono uppercase tracking-wider">
                <tr>
                  <th className="px-4 py-3">Serial Number</th>
                  <th className="px-4 py-3">Assigned Customer</th>
                  <th className="px-4 py-3">Location</th>
                  <th className="px-4 py-3">Hardware & Type</th>
                  <th className="px-4 py-3">Battery</th>
                  <th className="px-4 py-3">Installed</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {meters.map((m) => (
                  <tr key={m.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3 font-mono font-medium text-cyan-400 text-xs whitespace-nowrap">
                      {m.serialNumber}
                    </td>
                    <td className="px-4 py-3 text-xs whitespace-nowrap">
                      {m.customerName ? (
                        <div className="text-white font-medium">{m.customerName}</div>
                      ) : (
                        <span className="text-amber-400/80 font-mono text-[11px] italic">Unassigned Asset</span>
                      )}
                    </td>
                    <td className="px-4 py-3 font-mono text-xs text-slate-300 whitespace-nowrap">
                      {m.locationCode || 'LOC-001'}
                    </td>
                    <td className="px-4 py-3 text-xs whitespace-nowrap">
                      <div className="text-slate-200 font-medium">{m.meterModel}</div>
                      <div className="text-[11px] text-slate-400 font-mono">
                        {m.meterType} &bull; DN{m.diameterMm}mm
                      </div>
                    </td>
                    <td className="px-4 py-3 font-mono text-xs whitespace-nowrap">
                      <div className="flex items-center gap-1.5">
                        {getBatteryIcon(m.batteryLevelPercent)}
                        <span className="text-slate-300">{m.batteryLevelPercent?.toFixed(1) || '100.0'}%</span>
                      </div>
                    </td>
                    <td className="px-4 py-3 font-mono text-xs text-slate-400 whitespace-nowrap">
                      {m.installationDate}
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">{getStatusBadge(m.status)}</td>
                    <td className="px-4 py-3 text-right whitespace-nowrap">
                      <div className="inline-flex items-center gap-1.5">
                        <button
                          onClick={() => navigate(`/readings?meterId=${m.id}`)}
                          title="View Telemetry Ingestion History"
                          className="p-1.5 text-slate-400 hover:text-cyan-400 hover:bg-slate-800 rounded-md transition-colors"
                        >
                          <Activity className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => openAssignModal(m)}
                          title="Assign Customer"
                          className="p-1.5 text-slate-400 hover:text-blue-400 hover:bg-slate-800 rounded-md transition-colors"
                        >
                          <UserCheck className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => openEditModal(m)}
                          title="Edit Instrument"
                          className="p-1.5 text-slate-400 hover:text-amber-400 hover:bg-slate-800 rounded-md transition-colors"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <select
                          value={m.status}
                          onChange={(e) => handleStatusChange(m.id, e.target.value)}
                          className="px-2 py-1 bg-slate-950 border border-slate-800 text-[11px] font-mono rounded text-slate-300 focus:outline-none focus:border-cyan-500"
                        >
                          <option value="ACTIVE">ACTIVE</option>
                          <option value="INACTIVE">INACTIVE</option>
                          <option value="FAULTY">FAULTY</option>
                          <option value="MAINTENANCE">MAINTENANCE</option>
                          <option value="REMOVED">REMOVED</option>
                        </select>
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
            <span className="font-mono text-white">{totalPages || 1}</span> ({totalElements} total meters)
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

      {/* Register / Edit Meter Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden">
            <div className="p-5 border-b border-slate-800 flex items-center justify-between">
              <h3 className="font-bold text-white text-base flex items-center gap-2">
                <Gauge className="w-5 h-5 text-cyan-400" />
                {editingMeter ? 'Edit Water Meter' : 'Register New Water Meter'}
              </h3>
              <button
                onClick={() => setModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveMeter} className="p-6 space-y-4">
              {formError && (
                <div className="p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs">
                  {formError}
                </div>
              )}

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Serial Number *</label>
                  <input
                    type="text"
                    required
                    value={formSerial}
                    onChange={(e) => setFormSerial(e.target.value)}
                    placeholder="e.g. MTR-TK-889101"
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Meter Model *</label>
                  <input
                    type="text"
                    required
                    value={formModel}
                    onChange={(e) => setFormModel(e.target.value)}
                    placeholder="e.g. Aichi Tokei SU20"
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Technology Type</label>
                  <select
                    value={formType}
                    onChange={(e) => setFormType(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                  >
                    <option value="ULTRASONIC">Ultrasonic AMI</option>
                    <option value="ELECTROMAGNETIC">Electromagnetic MagFlow</option>
                    <option value="MECHANICAL_PISTON">Mechanical Rotary Piston</option>
                    <option value="OPTICAL_DIGITAL">Optical Digital Pulse</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Nominal Diameter (mm) *</label>
                  <input
                    type="number"
                    min="1"
                    max="1000"
                    required
                    value={formDiameter}
                    onChange={(e) => setFormDiameter(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Deployment Location *</label>
                <select
                  value={formLocationId}
                  onChange={(e) => setFormLocationId(Number(e.target.value))}
                  required
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                >
                  <option value="">Select Physical Facility / Street Location</option>
                  {locations.map((loc) => (
                    <option key={loc.id} value={loc.id}>
                      {loc.locationCode} — {loc.addressLine1}, {loc.city}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Assigned Customer (Optional)</label>
                <select
                  value={formCustomerId}
                  onChange={(e) => setFormCustomerId(e.target.value ? Number(e.target.value) : '')}
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                >
                  <option value="">No Customer Assigned (Asset in Inventory)</option>
                  {customers.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.customerNumber} — {c.fullName} ({c.customerType})
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Installed Date</label>
                  <input
                    type="date"
                    required
                    value={formDate}
                    onChange={(e) => setFormDate(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Status</label>
                  <select
                    value={formStatus}
                    onChange={(e) => setFormStatus(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                  >
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                    <option value="FAULTY">FAULTY</option>
                    <option value="MAINTENANCE">MAINTENANCE</option>
                    <option value="REMOVED">REMOVED</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Battery (%)</label>
                  <input
                    type="number"
                    step="0.1"
                    min="0"
                    max="100"
                    value={formBattery}
                    onChange={(e) => setFormBattery(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
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
                  {formSubmitting ? 'Saving...' : editingMeter ? 'Update Meter' : 'Register Meter'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Reassign Customer Modal */}
      {assignModalOpen && selectedMeterForAssign && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md shadow-2xl p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <h3 className="font-bold text-white text-base flex items-center gap-2">
                <UserCheck className="w-5 h-5 text-cyan-400" />
                Assign Meter to Customer
              </h3>
              <button
                onClick={() => setAssignModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAssignSave} className="space-y-4">
              <div className="text-xs text-slate-300">
                Assigning Instrument Serial:{' '}
                <span className="font-mono text-cyan-400 font-bold">{selectedMeterForAssign.serialNumber}</span>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1.5">Select Subscriber Account</label>
                <select
                  value={targetCustomerId}
                  onChange={(e) => setTargetCustomerId(e.target.value ? Number(e.target.value) : '')}
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                >
                  <option value="">Unassign / Return to Inventory</option>
                  {customers.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.customerNumber} — {c.fullName} ({c.status})
                    </option>
                  ))}
                </select>
              </div>

              <div className="pt-3 border-t border-slate-800 flex justify-end gap-2.5">
                <button
                  type="button"
                  onClick={() => setAssignModalOpen(false)}
                  className="px-4 py-2 rounded-lg border border-slate-800 hover:bg-slate-800 text-slate-300 text-xs font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-lg bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-semibold shadow-md shadow-cyan-600/20"
                >
                  Confirm Assignment
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
