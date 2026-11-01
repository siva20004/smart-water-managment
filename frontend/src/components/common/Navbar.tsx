import React from 'react';
import { Droplets, Activity } from 'lucide-react';
import StatusBadge from './StatusBadge';

interface NavbarProps {
  backendStatus: 'UP' | 'DOWN' | 'DEGRADED' | 'NOT_CONFIGURED';
  isChecking: boolean;
  onRefresh: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ backendStatus, isChecking, onRefresh }) => {
  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-30 shadow-xs">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Brand */}
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-water-700 to-water-500 flex items-center justify-center text-white shadow-md shadow-water-200">
              <Droplets className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-bold text-lg text-slate-900 tracking-tight">HydroPulse Analytics</span>
                <span className="px-2 py-0.5 text-xs font-semibold uppercase tracking-wider rounded-md bg-water-100 text-water-800">
                  Module 1
                </span>
              </div>
              <p className="text-xs text-slate-500 hidden sm:block">
                Smart Water Management &amp; Database Analytics Architecture
              </p>
            </div>
          </div>

          {/* Status & Actions */}
          <div className="flex items-center space-x-3">
            <div className="flex items-center gap-2 bg-slate-50 border border-slate-200 rounded-lg px-3 py-1.5">
              <span className="text-xs text-slate-500 font-medium hidden md:inline">Backend API:</span>
              <StatusBadge status={backendStatus} size="sm" />
            </div>

            <button
              onClick={onRefresh}
              disabled={isChecking}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 active:bg-slate-100 disabled:opacity-50 transition-colors shadow-2xs cursor-pointer"
              title="Ping backend health endpoint"
            >
              <Activity className={`w-3.5 h-3.5 text-water-600 ${isChecking ? 'animate-spin' : ''}`} />
              <span>{isChecking ? 'Pinging...' : 'Ping API'}</span>
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
