import React, { useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  Gauge,
  Activity,
  Receipt,
  AlertTriangle,
  FlaskConical,
  Wrench,
  HeartPulse,
  LogOut,
  Menu,
  X,
  ExternalLink,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const AppLayout: React.FC = () => {
  const { user, logout, switchRole } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [switching, setSwitching] = useState(false);

  const navItems = [
    { name: 'Executive Dashboard', path: '/', icon: LayoutDashboard },
    { name: 'Customer Accounts', path: '/customers', icon: Users },
    { name: 'Water Meter Fleet', path: '/meters', icon: Gauge },
    { name: 'Telemetry Readings', path: '/readings', icon: Activity },
    { name: 'Billing & Payments', path: '/billing', icon: Receipt },
    { name: 'Leak Alerts', path: '/leak-alerts', icon: AlertTriangle },
    { name: 'Water Quality', path: '/water-quality', icon: FlaskConical },
    { name: 'Work Orders', path: '/maintenance', icon: Wrench },
    { name: 'Infrastructure Health', path: '/health', icon: HeartPulse },
  ];

  const handleRoleSwitch = async (role: string) => {
    setSwitching(true);
    try {
      await switchRole(role);
    } catch {
      console.warn('Could not switch role via backend; continuing locally');
    } finally {
      setSwitching(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100 font-sans">
      {/* Top Utility Header */}
      <header className="h-16 border-b border-slate-800 bg-slate-900/90 backdrop-blur sticky top-0 z-40 px-4 lg:px-8 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="lg:hidden p-2 rounded-md hover:bg-slate-800 text-slate-300"
          >
            {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-lg bg-gradient-to-br from-cyan-500 to-blue-600 flex items-center justify-center shadow-lg shadow-cyan-500/20">
              <Gauge className="w-5 h-5 text-white" />
            </div>
            <div>
              <span className="font-bold tracking-tight text-white text-base">VELOIA</span>
              <span className="text-xs text-cyan-400 font-mono ml-1.5 px-1.5 py-0.5 rounded bg-cyan-950/80 border border-cyan-800/60">
                AQUA-SCADA 2.0
              </span>
            </div>
          </div>
        </div>

        {/* Center Educational Simulation Banner */}
        <div className="hidden md:flex items-center gap-2 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-300 text-xs font-mono">
          <span className="w-2 h-2 rounded-full bg-amber-400 animate-pulse" />
          SIMULATED TOKYO-KANTO WATER UTILITY NETWORK
        </div>

        {/* Right User & Role Switcher */}
        <div className="flex items-center gap-3">
          {/* Quick Role Switcher */}
          <div className="hidden sm:flex items-center gap-1.5 bg-slate-800/80 p-1 rounded-lg border border-slate-700/60 text-xs">
            <span className="text-slate-400 text-[11px] px-2 font-medium">Role:</span>
            {(['ADMIN', 'OPERATOR', 'TECHNICIAN', 'ANALYST'] as const).map((r) => (
              <button
                key={r}
                disabled={switching}
                onClick={() => handleRoleSwitch(r)}
                className={`px-2 py-1 rounded transition-colors font-mono ${
                  user?.role?.toUpperCase() === r
                    ? 'bg-cyan-600 text-white font-semibold shadow-sm'
                    : 'text-slate-300 hover:bg-slate-700/60'
                }`}
              >
                {r}
              </button>
            ))}
          </div>

          {/* User Badge */}
          <div className="flex items-center gap-2 pl-2 border-l border-slate-800">
            <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-cyan-400 text-xs font-bold">
              {user?.username?.substring(0, 2).toUpperCase() || 'AD'}
            </div>
            <div className="hidden xl:block text-left">
              <div className="text-xs font-semibold text-slate-200">{user?.fullName || 'Administrator'}</div>
              <div className="text-[10px] text-cyan-400 font-mono">{user?.role || 'ADMIN'}</div>
            </div>
            <button
              onClick={() => {
                logout();
                navigate('/login');
              }}
              title="Sign Out"
              className="p-1.5 text-slate-400 hover:text-rose-400 hover:bg-slate-800 rounded-md transition-colors"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>
      </header>

      {/* Main Container */}
      <div className="flex-1 flex overflow-hidden">
        {/* Left Sidebar */}
        <aside
          className={`fixed inset-y-16 left-0 z-30 w-64 bg-slate-900 border-r border-slate-800 flex flex-col transition-transform duration-200 lg:translate-x-0 lg:static ${
            mobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
          }`}
        >
          <div className="p-4 flex-1 space-y-1 overflow-y-auto">
            <div className="text-[11px] font-semibold uppercase tracking-wider text-slate-500 px-3 py-2 font-mono">
              Core Operations
            </div>
            {navItems.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                end={item.path === '/'}
                onClick={() => setMobileMenuOpen(false)}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all ${
                    isActive
                      ? 'bg-gradient-to-r from-cyan-900/60 to-blue-900/40 text-cyan-300 font-medium border border-cyan-700/50 shadow-inner'
                      : 'text-slate-300 hover:bg-slate-800/70 hover:text-white'
                  }`
                }
              >
                <item.icon className="w-4 h-4 shrink-0 text-cyan-400/80" />
                <span>{item.name}</span>
              </NavLink>
            ))}
          </div>

          {/* Sidebar Footer info */}
          <div className="p-4 border-t border-slate-800 bg-slate-900/50 space-y-2 text-xs text-slate-400">
            <div className="flex items-center justify-between font-mono text-[11px]">
              <span>Swagger UI:</span>
              <a
                href="http://localhost:8080/swagger-ui.html"
                target="_blank"
                rel="noreferrer"
                className="text-cyan-400 hover:underline inline-flex items-center gap-1"
              >
                Docs <ExternalLink className="w-3 h-3" />
              </a>
            </div>
            <div className="flex items-center gap-1.5 text-emerald-400 text-[11px]">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-ping" />
              SCADA Engine Online
            </div>
          </div>
        </aside>

        {/* Content Outlet */}
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 bg-slate-950">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
