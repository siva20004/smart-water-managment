import React from 'react';
import { Cpu, HardDrive } from 'lucide-react';
import type { SystemMetrics } from '../../types/health';

interface SystemMetricsCardProps {
  system?: SystemMetrics;
  loading: boolean;
}

export const SystemMetricsCard: React.FC<SystemMetricsCardProps> = ({ system, loading }) => {
  const usedMemoryMb = system ? system.totalMemoryMb - system.freeMemoryMb : 0;
  const memoryPercent = system && system.maxMemoryMb > 0
    ? Math.round((usedMemoryMb / system.maxMemoryMb) * 100)
    : 0;

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-xs p-6 hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center space-x-3">
          <div className="p-2.5 bg-emerald-50 text-emerald-600 rounded-lg border border-emerald-100">
            <Cpu className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-semibold text-slate-900">JVM &amp; Host Metrics</h3>
            <p className="text-xs text-slate-500">Resource telemetry (Spring Boot Actuator)</p>
          </div>
        </div>
      </div>

      {loading ? (
        <span className="text-xs text-slate-400 animate-pulse">Gathering metrics...</span>
      ) : system ? (
        <div className="space-y-4 mt-2 text-xs">
          <div>
            <div className="flex justify-between text-slate-600 mb-1">
              <span className="flex items-center gap-1">
                <HardDrive className="w-3.5 h-3.5" /> Heap Usage
              </span>
              <span className="font-mono font-medium text-slate-800">
                {usedMemoryMb} MB / {system.maxMemoryMb} MB ({memoryPercent}%)
              </span>
            </div>
            <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
              <div
                className="bg-emerald-500 h-2 rounded-full transition-all duration-500"
                style={{ width: `${Math.min(memoryPercent, 100)}%` }}
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 pt-2">
            <div className="p-3 bg-slate-50 rounded-lg border border-slate-100">
              <span className="text-slate-500 block text-[11px]">Available Processors</span>
              <span className="text-lg font-bold text-slate-800">{system.availableProcessors} Cores</span>
            </div>
            <div className="p-3 bg-slate-50 rounded-lg border border-slate-100">
              <span className="text-slate-500 block text-[11px]">Total Allocated</span>
              <span className="text-lg font-bold text-slate-800">{system.totalMemoryMb} MB</span>
            </div>
          </div>
        </div>
      ) : (
        <div className="py-8 text-center text-xs text-slate-400">
          Telemetry unavailable when backend is disconnected.
        </div>
      )}
    </div>
  );
};

export default SystemMetricsCard;
