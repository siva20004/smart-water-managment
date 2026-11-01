import React, { useState, useEffect, useCallback } from 'react';
import {
  Receipt,
  CreditCard,
  Plus,
  Filter,
  AlertCircle,
  CheckCircle,
  Clock,
  ChevronLeft,
  ChevronRight,
  Eye,
  HelpCircle,
  X,
  FileText,
} from 'lucide-react';
import type { WaterBill, Payment, PageResponse, Customer } from '../types/models';
import { billingService, customerService } from '../api/waterServices';

export const BillingPage: React.FC = () => {
  const [bills, setBills] = useState<WaterBill[]>([]);
  const [payments, setPayments] = useState<Payment[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Tabs: 'bills' | 'payments'
  const [activeTab, setActiveTab] = useState<'bills' | 'payments'>('bills');

  // Filters & Pagination
  const [selectedStatus, setSelectedStatus] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Modals
  const [generateModalOpen, setGenerateModalOpen] = useState(false);
  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [selectedBillForPayment, setSelectedBillForPayment] = useState<WaterBill | null>(null);
  const [billDetail, setBillDetail] = useState<WaterBill | null>(null);

  // Generate Bill Form
  const [genCustomerId, setGenCustomerId] = useState<number | ''>('');
  const [genStart, setGenStart] = useState('2026-10-01');
  const [genEnd, setGenEnd] = useState('2026-10-31');
  const [genBaseFee, setGenBaseFee] = useState(1500.0);
  const [genVolumetricRate, setGenVolumetricRate] = useState(180.0);
  const [genTaxRate, setGenTaxRate] = useState(10.0);
  const [genError, setGenError] = useState<string | null>(null);
  const [genSubmitting, setGenSubmitting] = useState(false);

  // Payment Form
  const [payAmount, setPayAmount] = useState<number | ''>('');
  const [payMethod, setPayMethod] = useState('BANK_TRANSFER');
  const [payNotes, setPayNotes] = useState('');
  const [payError, setPayError] = useState<string | null>(null);
  const [paySubmitting, setPaySubmitting] = useState(false);

  const fetchBills = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data: PageResponse<WaterBill> = await billingService.getAllBills({
        status: selectedStatus || undefined,
        page,
        size: pageSize,
      });
      setBills(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to query billing invoices');
    } finally {
      setLoading(false);
    }
  }, [selectedStatus, page, pageSize]);

  const fetchPayments = useCallback(async () => {
    setLoading(true);
    try {
      const data: PageResponse<Payment> = await billingService.getAllPayments({
        page,
        size: pageSize,
      });
      setPayments(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to query payment history');
    } finally {
      setLoading(false);
    }
  }, [page, pageSize]);

  useEffect(() => {
    if (activeTab === 'bills') {
      fetchBills();
    } else {
      fetchPayments();
    }
    customerService.getAll({ size: 100 }).then((res) => {
      setCustomers(res.content);
      if (res.content.length > 0 && !genCustomerId) {
        setGenCustomerId(res.content[0].id);
      }
    }).catch(console.warn);
  }, [activeTab, fetchBills, fetchPayments]);

  const handleGenerateBill = async (e: React.FormEvent) => {
    e.preventDefault();
    setGenError(null);

    if (!genCustomerId) {
      setGenError('Customer account selection is required');
      return;
    }

    setGenSubmitting(true);
    try {
      await billingService.generateBill({
        customerId: Number(genCustomerId),
        billingPeriodStart: genStart,
        billingPeriodEnd: genEnd,
        simulatedBaseFee: Number(genBaseFee),
        simulatedVolumetricRate: Number(genVolumetricRate),
        taxRatePercent: Number(genTaxRate),
      });
      setGenerateModalOpen(false);
      fetchBills();
    } catch (err: unknown) {
      setGenError(err instanceof Error ? err.message : 'Failed to generate billing invoice');
    } finally {
      setGenSubmitting(false);
    }
  };

  const openPaymentModal = (bill: WaterBill) => {
    setSelectedBillForPayment(bill);
    setPayAmount(bill.outstandingBalance > 0 ? bill.outstandingBalance : bill.totalAmount);
    setPayMethod('BANK_TRANSFER');
    setPayNotes('Simulated payment remittance');
    setPayError(null);
    setPaymentModalOpen(true);
  };

  const handleRecordPayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedBillForPayment || !payAmount) return;
    setPayError(null);

    setPaySubmitting(true);
    try {
      await billingService.recordPayment({
        billId: selectedBillForPayment.id,
        amount: Number(payAmount),
        paymentMethod: payMethod,
        notes: payNotes.trim() || undefined,
      });
      setPaymentModalOpen(false);
      fetchBills();
    } catch (err: unknown) {
      setPayError(err instanceof Error ? err.message : 'Payment recording rejected');
    } finally {
      setPaySubmitting(false);
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status.toUpperCase()) {
      case 'PAID':
        return (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono bg-emerald-500/10 border border-emerald-500/30 text-emerald-400">
            <CheckCircle className="w-3 h-3" /> PAID
          </span>
        );
      case 'PARTIALLY_PAID':
        return (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono bg-cyan-500/10 border border-cyan-500/30 text-cyan-400">
            <Clock className="w-3 h-3" /> PARTIAL
          </span>
        );
      case 'OVERDUE':
        return (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono bg-rose-500/10 border border-rose-500/30 text-rose-400">
            <AlertCircle className="w-3 h-3" /> OVERDUE
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-mono bg-amber-500/10 border border-amber-500/30 text-amber-300">
            <Clock className="w-3 h-3" /> ISSUED
          </span>
        );
    }
  };

  return (
    <div className="space-y-6">
      {/* Simulation Disclaimer */}
      <div className="p-3.5 rounded-xl bg-amber-500/10 border border-amber-500/30 flex items-center gap-3 text-amber-300 text-xs">
        <HelpCircle className="w-4 h-4 shrink-0 text-amber-400" />
        <span>
          <strong>Simulated Billing System:</strong> This utility does not connect to real financial gateways or process real
          credit cards. Tariffs and rates represent simulated Tokyo metropolitan municipal water consumption scales.
        </span>
      </div>

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <Receipt className="w-6 h-6 text-cyan-400" />
            Water Billing & Payment Management
          </h1>
          <p className="text-slate-400 text-sm mt-0.5">
            Consumption invoice generation, volumetric tariff application, and remittance balances
          </p>
        </div>
        <button
          onClick={() => {
            setGenError(null);
            setGenerateModalOpen(true);
          }}
          className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white font-medium text-sm transition-all shadow-lg shadow-cyan-600/20"
        >
          <Plus className="w-4 h-4" />
          <span>Generate Customer Bill</span>
        </button>
      </div>

      {/* Tab Switcher */}
      <div className="flex items-center gap-2 border-b border-slate-800">
        <button
          onClick={() => {
            setActiveTab('bills');
            setPage(0);
          }}
          className={`pb-3 px-4 text-sm font-medium border-b-2 transition-all flex items-center gap-2 ${
            activeTab === 'bills'
              ? 'border-cyan-500 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Receipt className="w-4 h-4" />
          <span>Issued Utility Invoices</span>
        </button>
        <button
          onClick={() => {
            setActiveTab('payments');
            setPage(0);
          }}
          className={`pb-3 px-4 text-sm font-medium border-b-2 transition-all flex items-center gap-2 ${
            activeTab === 'payments'
              ? 'border-cyan-500 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <CreditCard className="w-4 h-4" />
          <span>Payment Remittance Receipts</span>
        </button>
      </div>

      {/* Filter Bar (Only for Bills) */}
      {activeTab === 'bills' && (
        <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 flex items-center justify-between gap-4">
          <div className="flex items-center gap-2 text-xs font-semibold text-slate-300">
            <Filter className="w-4 h-4 text-cyan-400" />
            <span>Filter by Invoice Payment Status:</span>
          </div>
          <div className="w-56">
            <select
              value={selectedStatus}
              onChange={(e) => {
                setSelectedStatus(e.target.value);
                setPage(0);
              }}
              className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
            >
              <option value="">All Invoices</option>
              <option value="ISSUED">ISSUED</option>
              <option value="PARTIALLY_PAID">PARTIALLY_PAID</option>
              <option value="PAID">PAID</option>
              <option value="OVERDUE">OVERDUE</option>
            </select>
          </div>
        </div>
      )}

      {/* Data Table */}
      <div className="rounded-xl bg-slate-900 border border-slate-800 overflow-hidden shadow-lg">
        {loading ? (
          <div className="p-12 text-center text-slate-400 space-y-3">
            <div className="w-8 h-8 border-2 border-cyan-500 border-t-transparent rounded-full animate-spin mx-auto" />
            <div className="text-sm">Querying billing records and payment logs...</div>
          </div>
        ) : error ? (
          <div className="p-8 text-center text-rose-400 space-y-2">
            <AlertCircle className="w-8 h-8 mx-auto" />
            <div className="text-sm font-medium">{error}</div>
            <button onClick={fetchBills} className="text-xs text-cyan-400 hover:underline">
              Retry
            </button>
          </div>
        ) : activeTab === 'bills' ? (
          bills.length === 0 ? (
            <div className="p-12 text-center text-slate-400 space-y-3">
              <Receipt className="w-10 h-10 text-slate-600 mx-auto" />
              <div className="text-base font-medium text-slate-300">No Invoices Found</div>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="bg-slate-950/70 border-b border-slate-800 text-xs text-slate-400 font-mono uppercase tracking-wider">
                  <tr>
                    <th className="px-4 py-3">Invoice Number</th>
                    <th className="px-4 py-3">Customer</th>
                    <th className="px-4 py-3">Period</th>
                    <th className="px-4 py-3">Consumption</th>
                    <th className="px-4 py-3">Total Amount</th>
                    <th className="px-4 py-3">Paid / Remaining</th>
                    <th className="px-4 py-3">Status</th>
                    <th className="px-4 py-3 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60 font-mono text-xs">
                  {bills.map((b) => (
                    <tr key={b.id} className="hover:bg-slate-800/40 transition-colors">
                      <td className="px-4 py-3 font-semibold text-cyan-400 whitespace-nowrap">{b.billNumber}</td>
                      <td className="px-4 py-3 font-sans text-white font-medium whitespace-nowrap">
                        {b.customerName}
                      </td>
                      <td className="px-4 py-3 text-slate-400 text-[11px] whitespace-nowrap">
                        {b.billingPeriodStart} ~ {b.billingPeriodEnd}
                      </td>
                      <td className="px-4 py-3 font-bold text-slate-200 whitespace-nowrap">
                        {b.consumptionM3.toFixed(2)} m³
                      </td>
                      <td className="px-4 py-3 font-bold text-white whitespace-nowrap">
                        ¥{b.totalAmount.toLocaleString()}
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap">
                        <div className="text-emerald-400 font-semibold">¥{b.paidAmount.toLocaleString()} paid</div>
                        {b.outstandingBalance > 0 && (
                          <div className="text-amber-400 text-[11px]">¥{b.outstandingBalance.toLocaleString()} due</div>
                        )}
                      </td>
                      <td className="px-4 py-3 whitespace-nowrap">{getStatusBadge(b.status)}</td>
                      <td className="px-4 py-3 text-right whitespace-nowrap">
                        <div className="inline-flex items-center gap-1.5 font-sans">
                          <button
                            onClick={() => setBillDetail(b)}
                            title="View Breakdown"
                            className="p-1.5 text-slate-400 hover:text-cyan-400 hover:bg-slate-800 rounded-md transition-colors"
                          >
                            <Eye className="w-4 h-4" />
                          </button>
                          {b.status !== 'PAID' && (
                            <button
                              onClick={() => openPaymentModal(b)}
                              className="px-2.5 py-1 rounded bg-emerald-600 hover:bg-emerald-500 text-white font-medium text-xs shadow-sm flex items-center gap-1"
                            >
                              <CreditCard className="w-3.5 h-3.5" />
                              <span>Pay</span>
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )
        ) : (
          /* Payment History Table */
          payments.length === 0 ? (
            <div className="p-12 text-center text-slate-400 space-y-3">
              <CreditCard className="w-10 h-10 text-slate-600 mx-auto" />
              <div className="text-base font-medium text-slate-300">No Payment Records Yet</div>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="bg-slate-950/70 border-b border-slate-800 text-xs text-slate-400 font-mono uppercase tracking-wider">
                  <tr>
                    <th className="px-4 py-3">Receipt Reference</th>
                    <th className="px-4 py-3">Bill Number</th>
                    <th className="px-4 py-3">Payment Date</th>
                    <th className="px-4 py-3">Amount</th>
                    <th className="px-4 py-3">Method</th>
                    <th className="px-4 py-3">Status</th>
                    <th className="px-4 py-3">Notes</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60 font-mono text-xs">
                  {payments.map((p) => (
                    <tr key={p.id} className="hover:bg-slate-800/40 transition-colors">
                      <td className="px-4 py-3 text-cyan-400 font-semibold whitespace-nowrap">{p.paymentReference}</td>
                      <td className="px-4 py-3 text-slate-300 whitespace-nowrap">{p.billNumber}</td>
                      <td className="px-4 py-3 text-slate-400 whitespace-nowrap">
                        {new Date(p.paymentDate).toLocaleString()}
                      </td>
                      <td className="px-4 py-3 text-emerald-400 font-bold whitespace-nowrap">
                        ¥{p.amount.toLocaleString()}
                      </td>
                      <td className="px-4 py-3 text-slate-300 whitespace-nowrap">{p.paymentMethod}</td>
                      <td className="px-4 py-3 whitespace-nowrap">
                        <span className="px-2 py-0.5 rounded-full text-[11px] bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                          {p.transactionStatus}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-slate-400 text-xs font-sans truncate max-w-xs">{p.notes || '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )
        )}

        {/* Pagination Footer */}
        <div className="p-4 border-t border-slate-800 bg-slate-950/60 flex items-center justify-between text-xs text-slate-400">
          <div>
            Showing page <span className="font-mono text-white">{page + 1}</span> of{' '}
            <span className="font-mono text-white">{totalPages || 1}</span> ({totalElements} total items)
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

      {/* Generate Bill Modal */}
      {generateModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden">
            <div className="p-5 border-b border-slate-800 flex items-center justify-between">
              <h3 className="font-bold text-white text-base flex items-center gap-2">
                <Receipt className="w-5 h-5 text-cyan-400" />
                Generate Water Consumption Invoice
              </h3>
              <button
                onClick={() => setGenerateModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleGenerateBill} className="p-6 space-y-4">
              {genError && (
                <div className="p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs">
                  {genError}
                </div>
              )}

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Subscriber Customer *</label>
                <select
                  value={genCustomerId}
                  onChange={(e) => setGenCustomerId(Number(e.target.value))}
                  required
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                >
                  <option value="">Select Account</option>
                  {customers.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.customerNumber} — {c.fullName}
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Period Start Date *</label>
                  <input
                    type="date"
                    required
                    value={genStart}
                    onChange={(e) => setGenStart(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Period End Date *</label>
                  <input
                    type="date"
                    required
                    value={genEnd}
                    onChange={(e) => setGenEnd(e.target.value)}
                    className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="p-3.5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
                <div className="text-xs font-semibold text-cyan-400 font-mono">
                  SIMULATED TOKYO WATER TARIFF CONFIGURATION
                </div>
                <div className="grid grid-cols-3 gap-3">
                  <div>
                    <label className="block text-[11px] text-slate-400 mb-1">Base Fee (JPY)</label>
                    <input
                      type="number"
                      step="50"
                      value={genBaseFee}
                      onChange={(e) => setGenBaseFee(Number(e.target.value))}
                      className="w-full px-2.5 py-1.5 rounded bg-slate-900 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500"
                    />
                  </div>
                  <div>
                    <label className="block text-[11px] text-slate-400 mb-1">Rate (JPY/m³)</label>
                    <input
                      type="number"
                      step="5"
                      value={genVolumetricRate}
                      onChange={(e) => setGenVolumetricRate(Number(e.target.value))}
                      className="w-full px-2.5 py-1.5 rounded bg-slate-900 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500"
                    />
                  </div>
                  <div>
                    <label className="block text-[11px] text-slate-400 mb-1">Tax (%)</label>
                    <input
                      type="number"
                      step="1"
                      value={genTaxRate}
                      onChange={(e) => setGenTaxRate(Number(e.target.value))}
                      className="w-full px-2.5 py-1.5 rounded bg-slate-900 border border-slate-800 text-slate-200 text-xs font-mono focus:border-cyan-500"
                    />
                  </div>
                </div>
              </div>

              <div className="pt-4 border-t border-slate-800 flex justify-end gap-2.5">
                <button
                  type="button"
                  onClick={() => setGenerateModalOpen(false)}
                  className="px-4 py-2 rounded-lg border border-slate-800 hover:bg-slate-800 text-slate-300 text-xs font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={genSubmitting}
                  className="px-4 py-2 rounded-lg bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-semibold shadow-md shadow-cyan-600/20 disabled:opacity-50"
                >
                  {genSubmitting ? 'Calculating...' : 'Generate Bill'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Record Payment Modal */}
      {paymentModalOpen && selectedBillForPayment && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md shadow-2xl p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <h3 className="font-bold text-white text-base flex items-center gap-2">
                <CreditCard className="w-5 h-5 text-emerald-400" />
                Record Simulated Payment
              </h3>
              <button
                onClick={() => setPaymentModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleRecordPayment} className="space-y-4">
              {payError && (
                <div className="p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs">
                  {payError}
                </div>
              )}

              <div className="p-3 rounded-lg bg-slate-950 border border-slate-800 space-y-1 text-xs">
                <div className="text-slate-400">Invoice: <span className="font-mono text-cyan-400 font-bold">{selectedBillForPayment.billNumber}</span></div>
                <div className="text-slate-400">Customer: <span className="text-white font-medium">{selectedBillForPayment.customerName}</span></div>
                <div className="text-slate-400">Remaining Balance: <span className="text-amber-400 font-mono font-bold">¥{selectedBillForPayment.outstandingBalance.toLocaleString()}</span></div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Payment Amount (JPY) *</label>
                <input
                  type="number"
                  step="1"
                  min="1"
                  max={selectedBillForPayment.outstandingBalance}
                  required
                  value={payAmount}
                  onChange={(e) => setPayAmount(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-white font-mono text-sm focus:border-cyan-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Simulated Payment Method *</label>
                <select
                  value={payMethod}
                  onChange={(e) => setPayMethod(e.target.value)}
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                >
                  <option value="BANK_TRANSFER">Bank Transfer (Furikomi)</option>
                  <option value="DIRECT_DEBIT">Direct Debit (Kouza Furikae)</option>
                  <option value="CREDIT_CARD">Credit Card</option>
                  <option value="CONVENIENCE_STORE_PAY">Convenience Store Barcode (Konbini)</option>
                  <option value="ELECTRONIC_WALLET">PayPay / Electronic Wallet</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Transaction Notes</label>
                <input
                  type="text"
                  value={payNotes}
                  onChange={(e) => setPayNotes(e.target.value)}
                  placeholder="e.g. Branch ATM payment voucher"
                  className="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-800 text-slate-200 text-xs focus:border-cyan-500 focus:outline-none"
                />
              </div>

              <div className="pt-3 border-t border-slate-800 flex justify-end gap-2.5">
                <button
                  type="button"
                  onClick={() => setPaymentModalOpen(false)}
                  className="px-4 py-2 rounded-lg border border-slate-800 hover:bg-slate-800 text-slate-300 text-xs font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={paySubmitting}
                  className="px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold shadow-md shadow-emerald-600/20 disabled:opacity-50"
                >
                  {paySubmitting ? 'Processing...' : 'Confirm Simulated Remittance'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Bill Detail Modal */}
      {billDetail && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md shadow-2xl p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <h3 className="font-bold text-white text-base flex items-center gap-2">
                <FileText className="w-5 h-5 text-cyan-400" />
                Invoice Breakdown Details
              </h3>
              <button
                onClick={() => setBillDetail(null)}
                className="p-1 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-2.5 text-xs">
              <div className="flex justify-between py-1 border-b border-slate-800/80">
                <span className="text-slate-400">Invoice Number:</span>
                <span className="font-mono text-cyan-400 font-bold">{billDetail.billNumber}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800/80">
                <span className="text-slate-400">Customer:</span>
                <span className="text-white font-medium">{billDetail.customerName}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800/80">
                <span className="text-slate-400">Period:</span>
                <span className="font-mono text-slate-300">{billDetail.billingPeriodStart} to {billDetail.billingPeriodEnd}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800/80">
                <span className="text-slate-400">Consumption:</span>
                <span className="font-mono text-white font-bold">{billDetail.consumptionM3.toFixed(3)} m³</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800/80">
                <span className="text-slate-400">Base Subscription Fee:</span>
                <span className="font-mono text-slate-300">¥{billDetail.baseFee.toLocaleString()}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800/80">
                <span className="text-slate-400">Volumetric Charge ({billDetail.volumetricRate} JPY/m³):</span>
                <span className="font-mono text-slate-300">¥{billDetail.consumptionCharge.toLocaleString()}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800/80">
                <span className="text-slate-400">Municipal Consumption Tax (10%):</span>
                <span className="font-mono text-slate-300">¥{billDetail.taxAmount.toLocaleString()}</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-slate-700 text-sm font-bold">
                <span className="text-white">Total Invoiced:</span>
                <span className="text-cyan-400 font-mono">¥{billDetail.totalAmount.toLocaleString()}</span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-slate-400">Total Remitted:</span>
                <span className="text-emerald-400 font-mono font-bold">¥{billDetail.paidAmount.toLocaleString()}</span>
              </div>
              <div className="flex justify-between py-1 text-sm font-bold">
                <span className="text-slate-300">Remaining Balance Due:</span>
                <span className="text-amber-400 font-mono">¥{billDetail.outstandingBalance.toLocaleString()}</span>
              </div>
            </div>

            <div className="pt-3 border-t border-slate-800 flex justify-end">
              <button
                onClick={() => setBillDetail(null)}
                className="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-white text-xs font-medium"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
