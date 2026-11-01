import React from 'react';
import { Database, Zap, FileCode } from 'lucide-react';
import type { DatabaseHealthInfo } from '../../types/health';
import StatusBadge from '../common/StatusBadge';

interface DatabaseStatusCardProps {
  dbHealth?: DatabaseHealthInfo;
  loading: boolean;
  backendError: boolean;
}

export const DatabaseStatusCard: React.FC<DatabaseStatusCardProps> = ({
  dbHealth,
  loading,
  backendError,
}) => {
  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-xs p-6 hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center space-x-3">
          <div className="p-2.5 bg-indigo-50 text-indigo-600 rounded-lg border border-indigo-100">
            <Database className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-semibold text-slate-900">Database Engine</h3>
            <p className="text-xs text-slate-500">PostgreSQL + Flyway Migrations</p>
          </div>
        </div>
        {loading ? (
          <span className="text-xs text-slate-400 animate-pulse">Checking DB...</span>
        ) : backendError ? (
          <StatusBadge status="NOT_CONFIGURED" label="Unknown" />
        ) : dbHealth ? (
          <StatusBadge status={dbHealth.status} />
        ) : (
          <StatusBadge status="NOT_CONFIGURED" />
        )}
      </div>

      {backendError ? (
        <p className="text-xs text-slate-400 py-4 text-center">
          Backend is offline. Unable to query database status.
        </p>
      ) : dbHealth ? (
        <div className="space-y-3 mt-4 text-xs">
          <div className="flex justify-between items-center py-1.5 border-b border-slate-100">
            <span className="text-slate-500">Engine / Version</span>
            <span className="font-mono text-slate-800 text-[11px] truncate max-w-[200px]" title={dbHealth.databaseProductName}>
              {dbHealth.databaseProductName}
            </span>
          </div>

          <div className="flex justify-between items-center py-1.5 border-b border-slate-100">
            <span className="text-slate-500 flex items-center gap-1.5">
              <Zap className="w-3.5 h-3.5 text-amber-500" /> Ping Latency
            </span>
            <span className="font-mono font-medium text-slate-800">
              {dbHealth.responseTimeMs !== undefined ? `${dbHealth.responseTimeMs} ms` : 'N/A'}
            </span>
          </div>

          <div className="flex justify-between items-center py-1.5 border-b border-slate-100">
            <span className="text-slate-500 flex items-center gap-1.5">
              <FileCode className="w-3.5 h-3.5 text-indigo-500" /> Flyway Baseline
            </span>
            <span className="inline-flex items-center gap-1 text-slate-700 font-mono text-[11px]">
              V1__initial_schema
            </span>
          </div>

          <div className="pt-1">
            <span className="text-slate-500 block mb-1">Connection Details:</span>
            <p className={`p-2 rounded text-[11px] font-mono break-all ${
              dbHealth.status === 'UP' ? 'bg-emerald-50 text-emerald-800 border border-emerald-100' : 'bg-amber-50 text-amber-800 border border-amber-100'
            }`}>
              {dbHealth.details}
            </p>
          </div>
        </div>
      ) : (
        <div className="py-8 text-center text-xs text-slate-400">
          Awaiting backend check.
        </div>
      )}
    </div>
  );
};

export default DatabaseStatusCard;
