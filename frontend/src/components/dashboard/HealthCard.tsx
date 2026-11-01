import React from 'react';
import { Server, Clock, Tag, AlertTriangle } from 'lucide-react';
import type { HealthResponse } from '../../types/health';
import StatusBadge from '../common/StatusBadge';

interface HealthCardProps {
  health: HealthResponse | null;
  loading: boolean;
  error: string | null;
}

export const HealthCard: React.FC<HealthCardProps> = ({ health, loading, error }) => {
  const formatUptime = (totalSeconds: number): string => {
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
    return `${hours}h ${minutes}m ${seconds}s`;
  };

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-xs p-6 hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center space-x-3">
          <div className="p-2.5 bg-water-50 text-water-600 rounded-lg border border-water-100">
            <Server className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-semibold text-slate-900">Backend Service</h3>
            <p className="text-xs text-slate-500">Spring Boot REST API Core</p>
          </div>
        </div>
        {loading ? (
          <span className="text-xs text-slate-400 animate-pulse">Checking status...</span>
        ) : error ? (
          <StatusBadge status="DOWN" label="Offline" />
        ) : health ? (
          <StatusBadge status={health.status} />
        ) : (
          <StatusBadge status="NOT_CONFIGURED" />
        )}
      </div>

      {error ? (
        <div className="mt-3 p-3 bg-red-50 border border-red-200 rounded-lg text-xs text-red-700 flex items-start space-x-2">
          <AlertTriangle className="w-4 h-4 mt-0.5 shrink-0 text-red-500" />
          <div>
            <p className="font-medium">Connection failed</p>
            <p className="mt-0.5 text-red-600">{error}</p>
            <p className="mt-1 text-slate-500 text-[11px]">
              Verify that the Spring Boot backend is running on <code className="font-mono bg-red-100 px-1 py-0.5 rounded">http://localhost:8080</code>.
            </p>
          </div>
        </div>
      ) : health ? (
        <div className="space-y-3 mt-4 text-xs">
          <div className="flex justify-between items-center py-1.5 border-b border-slate-100">
            <span className="text-slate-500 flex items-center gap-1.5">
              <Tag className="w-3.5 h-3.5" /> Service Name
            </span>
            <span className="font-medium text-slate-800">{health.service}</span>
          </div>

          <div className="flex justify-between items-center py-1.5 border-b border-slate-100">
            <span className="text-slate-500 flex items-center gap-1.5">
              <Clock className="w-3.5 h-3.5" /> Service Uptime
            </span>
            <span className="font-mono font-medium text-slate-800">{formatUptime(health.uptimeSeconds)}</span>
          </div>

          <div className="flex justify-between items-center py-1.5 border-b border-slate-100">
            <span className="text-slate-500">Active Profile</span>
            <span className="px-2 py-0.5 bg-slate-100 rounded text-slate-700 font-mono text-[11px]">
              {health.environment}
            </span>
          </div>

          <div className="flex justify-between items-center py-1.5">
            <span className="text-slate-500">API Version</span>
            <span className="font-mono text-slate-700">{health.version}</span>
          </div>
        </div>
      ) : (
        <div className="py-8 text-center text-xs text-slate-400">
          No telemetry received yet. Click "Ping API" above.
        </div>
      )}
    </div>
  );
};

export default HealthCard;
