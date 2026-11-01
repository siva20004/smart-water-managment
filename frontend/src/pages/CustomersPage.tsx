import React, { useState, useEffect, useCallback } from 'react';
import {
  Users,
  Search,
  Filter,
  Plus,
  Edit2,
  Trash2,
  Eye,
  CheckCircle,
  XCircle,
  AlertCircle,
  ChevronLeft,
  ChevronRight,
  ShieldCheck,
  Building,
  Home,
  Briefcase,
  X,
  Gauge,
} from 'lucide-react';
import type { Customer, PageResponse } from '../types/models';
import { customerService } from '../api/waterServices';

export const CustomersPage: React.FC = () => {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filters & Pagination
  const [search, setSearch] = useState('');
  const [selectedType, setSelectedType] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Modals & Drawers
  const [modalOpen, setModalOpen] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState<Customer | null>(null);
  const [detailCustomer, setDetailCustomer] = useState<Customer | null>(null);
  const [deleteConfirmId, setDeleteConfirmId] = useState<number | null>(null);

  // Form State
  const [formNumber, setFormNumber] = useState('');
  const [formType, setFormType] = useState('RESIDENTIAL');
  const [formName, setFormName] = useState('');
  const [formEmail, setFormEmail] = useState('');
  const [formPhone, setFormPhone] = useState('');
  const [formAddress, setFormAddress] = useState('');
  const [formStatus, setFormStatus] = useState('ACTIVE');
  const [formTaxId, setFormTaxId] = useState('');
  const [formError, setFormError] = useState<string | null>(null);
  const [formSubmitting, setFormSubmitting] = useState(false);

  const fetchCustomers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data: PageResponse<Customer> = await customerService.getAll({
        search: search.trim() || undefined,
        type: selectedType || undefined,
        status: selectedStatus || undefined,
        page,
        size: pageSize,
      });
      setCustomers(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to fetch customer accounts');
    } finally {
      setLoading(false);
    }
  }, [search, selectedType, selectedStatus, page, pageSize]);

  useEffect(() => {
    fetchCustomers();
  }, [fetchCustomers]);

  const openCreateModal = () => {
    setEditingCustomer(null);
    setFormNumber(`CUST-TK-${new Date().getFullYear()}-${Math.floor(1000 + Math.random() * 9000)}`);
    setFormType('RESIDENTIAL');
    setFormName('');
    setFormEmail('');
    setFormPhone('');
    setFormAddress('');
    setFormStatus('ACTIVE');
    setFormTaxId('');
    setFormError(null);
    setModalOpen(true);
  };

  const openEditModal = (c: Customer) => {
    setEditingCustomer(c);
    setFormNumber(c.customerNumber);
    setFormType(c.customerType);
    setFormName(c.fullName);
    setFormEmail(c.email || '');
    setFormPhone(c.phoneNumber || '');
    setFormAddress(c.billingAddress);
    setFormStatus(c.status);
    setFormTaxId(c.taxId || '');
    setFormError(null);
    setModalOpen(true);
  };

  const handleSaveCustomer = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);

    if (!formNumber.trim() || !formName.trim() || !formAddress.trim()) {
      setFormError('Customer code, full name, and billing address are required');
      return;
    }

    setFormSubmitting(true);
    try {
      const payload: Partial<Customer> = {
        customerNumber: formNumber.trim(),
        customerType: formType,
        fullName: formName.trim(),
        email: formEmail.trim() || undefined,
        phoneNumber: formPhone.trim() || undefined,
        billingAddress: formAddress.trim(),
        status: formStatus as Customer['status'],
        taxId: formTaxId.trim() || undefined,
      };

      if (editingCustomer) {
        await customerService.update(editingCustomer.id, payload);
      } else {
        await customerService.create(payload);
      }

      setModalOpen(false);
      fetchCustomers();
    } catch (err: unknown) {
      setFormError(err instanceof Error ? err.message : 'Error saving customer account');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleToggleStatus = async (customer: Customer) => {
    const nextStatus = customer.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    try {
      await customerService.setStatus(customer.id, nextStatus);
      fetchCustomers();
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Failed to update customer status');
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deleteConfirmId) return;
    try {
      await customerService.delete(deleteConfirmId);
      setDeleteConfirmId(null);
      fetchCustomers();
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Could not delete customer account');
      setDeleteConfirmId(null);
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type.toUpperCase()) {
      case 'COMMERCIAL':
      case 'CORPORATE':
        return <Building className="w-3.5 h-3.5 text-blue-400" />;
      case 'INDUSTRIAL':
        return <Briefcase className="w-3.5 h-3.5 text-purple-400" />;
      default:
        return <Home className="w-3.5 h-3.5 text-emerald-400" />;
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <Users className="w-6 h-6 text-cyan-400" />
            Customer Management
          </h1>
          <p className="text-slate-400 text-sm mt-0.5">
            Registered subscribers, multi-unit utility connections, and billing addresses
          </p>
        </div>
        <button
          onClick={openCreateModal}
          className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white font-medium text-sm transition-all shadow-lg shadow-cyan-600/20"
        >
          <Plus className="w-4 h-4" />
          <span>Register Customer</span>
        </button>
      </div>

      {/* Filters Bar */}
      <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 flex flex-col md:flex-row items-center gap-3">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search by customer name, code (e.g. CUST-TK-2026), or email..."
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(0);
            }}
            className="w-full pl-9 pr-4 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="relative w-full md:w-44">
            <Filter className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <select
              value={selectedType}
              onChange={(e) => {
                setSelectedType(e.target.value);
                setPage(0);
              }}
              className="w-full pl-8 pr-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-medium focus:outline-none focus:border-cyan-500"
            >
              <option value="">All Types</option>
              <option value="RESIDENTIAL">Residential</option>
              <option value="COMMERCIAL">Commercial</option>
              <option value="INDUSTRIAL">Industrial</option>
              <option value="CORPORATE">Corporate</option>
              <option value="GOVERNMENT">Government</option>
            </select>
          </div>

          <select
            value={selectedStatus}
            onChange={(e) => {
              setSelectedStatus(e.target.value);
              setPage(0);
            }}
            className="w-full md:w-36 px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-medium focus:outline-none focus:border-cyan-500"
          >
            <option value="">All Statuses</option>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
            <option value="SUSPENDED">Suspended</option>
            <option value="TERMINATED">Terminated</option>
          </select>
        </div>
      </div>

      {/* Customer Data Table */}
      <div className="rounded-xl bg-slate-900 border border-slate-800 overflow-hidden shadow-lg">
        {loading ? (
          <div className="p-12 text-center text-slate-400 space-y-3">
            <div className="w-8 h-8 border-2 border-cyan-500 border-t-transparent rounded-full animate-spin mx-auto" />
            <div className="text-sm">Querying subscriber registry from PostgreSQL...</div>
          </div>
        ) : error ? (
          <div className="p-8 text-center text-rose-400 space-y-2">
            <AlertCircle className="w-8 h-8 mx-auto" />
            <div className="text-sm font-medium">{error}</div>
            <button onClick={fetchCustomers} className="text-xs text-cyan-400 hover:underline">
              Retry Query
            </button>
          </div>
        ) : customers.length === 0 ? (
          <div className="p-12 text-center text-slate-400 space-y-3">
            <Users className="w-10 h-10 text-slate-600 mx-auto" />
            <div className="text-base font-medium text-slate-300">No Customers Found</div>
            <div className="text-xs text-slate-500 max-w-sm mx-auto">
              No matching accounts in the simulated utility database for current filters.
            </div>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/70 border-b border-slate-800 text-xs text-slate-400 font-mono uppercase tracking-wider">
                <tr>
                  <th className="px-4 py-3">Account Code</th>
                  <th className="px-4 py-3">Full Name</th>
                  <th className="px-4 py-3">Type</th>
                  <th className="px-4 py-3">Contact</th>
                  <th className="px-4 py-3">Billing Address</th>
                  <th className="px-4 py-3">Meters</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {customers.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3 font-mono text-cyan-400 font-medium text-xs whitespace-nowrap">
                      {c.customerNumber}
                    </td>
                    <td className="px-4 py-3 font-medium text-white whitespace-nowrap">{c.fullName}</td>
                    <td className="px-4 py-3 whitespace-nowrap">
                      <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-xs font-mono bg-slate-800 border border-slate-700 text-slate-300">
                        {getTypeIcon(c.customerType)}
                        {c.customerType}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-xs text-slate-300">
                      <div>{c.email || '—'}</div>
                      <div className="text-slate-500 font-mono text-[11px]">{c.phoneNumber || '—'}</div>
                    </td>
                    <td className="px-4 py-3 text-xs text-slate-300 max-w-xs truncate" title={c.billingAddress}>
                      {c.billingAddress}
                    </td>
                    <td className="px-4 py-3 font-mono text-xs text-slate-300">
                      <span className="inline-flex items-center gap-1 text-cyan-300 font-semibold">
                        <Gauge className="w-3.5 h-3.5 text-cyan-400" />
                        {c.meterCount}
                      </span>
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">
                      <span
                        className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono font-medium ${
                          c.status === 'ACTIVE'
                            ? 'bg-emerald-500/10 border border-emerald-500/30 text-emerald-400'
                            : 'bg-rose-500/10 border border-rose-500/30 text-rose-400'
                        }`}
                      >
                        {c.status === 'ACTIVE' ? <CheckCircle className="w-3 h-3" /> : <XCircle className="w-3 h-3" />}
                        {c.status}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-right whitespace-nowrap">
                      <div className="inline-flex items-center gap-1.5">
                        <button
                          onClick={() => setDetailCustomer(c)}
                          title="View Customer Profile"
                          className="p-1.5 text-slate-400 hover:text-cyan-400 hover:bg-slate-800 rounded-md transition-colors"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => openEditModal(c)}
                          title="Edit Customer"
                          className="p-1.5 text-slate-400 hover:text-amber-400 hover:bg-slate-800 rounded-md transition-colors"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleToggleStatus(c)}
                          title={c.status === 'ACTIVE' ? 'Deactivate Account' : 'Activate Account'}
                          className={`p-1.5 rounded-md transition-colors ${
                            c.status === 'ACTIVE'
                              ? 'text-slate-400 hover:text-amber-400 hover:bg-slate-800'
                              : 'text-slate-400 hover:text-emerald-400 hover:bg-slate-800'
                          }`}
                        >
                          {c.status === 'ACTIVE' ? <XCircle className="w-4 h-4" /> : <CheckCircle className="w-4 h-4" />}
                        </button>
                        <button
                          onClick={() => setDeleteConfirmId(c.id)}
                          title="Delete Account"
                          className="p-1.5 text-slate-400 hover:text-rose-400 hover:bg-slate-800 rounded-md transition-colors"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
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
            <span className="font-mono text-white">{totalPages || 1}</span> ({totalElements} total records)
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

      {/* Add / Edit Customer Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden">
            <div className="p-5 border-b border-slate-800 flex items-center justify-between">
              <h3 className="font-bold text-white text-base flex items-center gap-2">
                <Users className="w-5 h-5 text-cyan-400" />
                {editingCustomer ? 'Edit Customer Account' : 'Register New Customer'}
              </h3>
              <button
                onClick={() => setModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveCustomer} className="p-6 space-y-4">
              {formError && (
                <div className="p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs">
                  {formError}
                </div>
              )}

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Customer Code *</label>
                  <input
                    type="text"
                    required
                    value={formNumber}
                    onChange={(e) => setFormNumber(e.target.value)}
                    placeholder="e.g. CUST-TK-2026-0099"
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Customer Type *</label>
                  <select
                    value={formType}
                    onChange={(e) => setFormType(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                  >
                    <option value="RESIDENTIAL">Residential</option>
                    <option value="COMMERCIAL">Commercial</option>
                    <option value="INDUSTRIAL">Industrial</option>
                    <option value="CORPORATE">Corporate</option>
                    <option value="GOVERNMENT">Government</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Full Name / Entity Name *</label>
                <input
                  type="text"
                  required
                  value={formName}
                  onChange={(e) => setFormName(e.target.value)}
                  placeholder="e.g. Tanaka Kenji or Nippon Clean Tech"
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Email</label>
                  <input
                    type="email"
                    value={formEmail}
                    onChange={(e) => setFormEmail(e.target.value)}
                    placeholder="name@example.jp"
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Phone Number</label>
                  <input
                    type="text"
                    value={formPhone}
                    onChange={(e) => setFormPhone(e.target.value)}
                    placeholder="03-5321-0000"
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Tax ID / Corporation ID</label>
                <input
                  type="text"
                  value={formTaxId}
                  onChange={(e) => setFormTaxId(e.target.value)}
                  placeholder="JP-TAX-IND-0000 or JP-CORP-90123..."
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Billing Address *</label>
                <input
                  type="text"
                  required
                  value={formAddress}
                  onChange={(e) => setFormAddress(e.target.value)}
                  placeholder="e.g. 2-8-1 Nishi-Shinjuku, Shinjuku-ku, Tokyo"
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Account Status</label>
                <select
                  value={formStatus}
                  onChange={(e) => setFormStatus(e.target.value)}
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                >
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="INACTIVE">INACTIVE</option>
                  <option value="SUSPENDED">SUSPENDED</option>
                  <option value="TERMINATED">TERMINATED</option>
                </select>
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
                  {formSubmitting ? 'Saving...' : editingCustomer ? 'Update Customer' : 'Create Customer'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Customer Detail Drawer */}
      {detailCustomer && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md shadow-2xl p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-cyan-400" />
                <h3 className="font-bold text-white text-base">Customer Account Profile</h3>
              </div>
              <button
                onClick={() => setDetailCustomer(null)}
                className="p-1 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="space-y-3 text-xs">
              <div>
                <span className="text-slate-400">Customer Number:</span>
                <div className="font-mono text-cyan-400 font-bold text-sm mt-0.5">{detailCustomer.customerNumber}</div>
              </div>
              <div>
                <span className="text-slate-400">Full Name / Corporation:</span>
                <div className="text-white font-semibold text-sm mt-0.5">{detailCustomer.fullName}</div>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <span className="text-slate-400">Classification:</span>
                  <div className="text-slate-200 mt-0.5">{detailCustomer.customerType}</div>
                </div>
                <div>
                  <span className="text-slate-400">Account Status:</span>
                  <div className="text-emerald-400 font-semibold mt-0.5">{detailCustomer.status}</div>
                </div>
              </div>
              <div>
                <span className="text-slate-400">Tax Identification:</span>
                <div className="font-mono text-slate-300 mt-0.5">{detailCustomer.taxId || 'Not Registered'}</div>
              </div>
              <div>
                <span className="text-slate-400">Email Contact:</span>
                <div className="text-slate-300 mt-0.5">{detailCustomer.email || '—'}</div>
              </div>
              <div>
                <span className="text-slate-400">Phone Contact:</span>
                <div className="font-mono text-slate-300 mt-0.5">{detailCustomer.phoneNumber || '—'}</div>
              </div>
              <div>
                <span className="text-slate-400">Billing Address:</span>
                <div className="text-slate-200 mt-0.5">{detailCustomer.billingAddress}</div>
              </div>
              <div>
                <span className="text-slate-400">Deployed Water Meters:</span>
                <div className="text-cyan-300 font-mono font-bold mt-0.5">{detailCustomer.meterCount} linked meter(s)</div>
              </div>
            </div>

            <div className="pt-3 border-t border-slate-800 flex justify-end">
              <button
                onClick={() => setDetailCustomer(null)}
                className="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-white text-xs font-medium"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Dialog */}
      {deleteConfirmId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-rose-500/30 rounded-2xl w-full max-w-sm shadow-2xl p-6 text-center space-y-4">
            <div className="w-12 h-12 rounded-full bg-rose-500/10 border border-rose-500/20 text-rose-400 flex items-center justify-center mx-auto">
              <Trash2 className="w-6 h-6" />
            </div>
            <div>
              <h4 className="text-base font-bold text-white">Confirm Customer Deletion</h4>
              <p className="text-xs text-slate-400 mt-1">
                Are you sure you want to remove this account? Customers with active meters cannot be deleted.
              </p>
            </div>
            <div className="flex justify-center gap-3 pt-2">
              <button
                onClick={() => setDeleteConfirmId(null)}
                className="px-4 py-2 rounded-lg border border-slate-800 hover:bg-slate-800 text-slate-300 text-xs font-medium"
              >
                Cancel
              </button>
              <button
                onClick={handleDeleteConfirm}
                className="px-4 py-2 rounded-lg bg-rose-600 hover:bg-rose-500 text-white text-xs font-semibold shadow-md shadow-rose-600/25"
              >
                Delete Account
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
