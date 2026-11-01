import React, { useEffect, useState, useCallback } from 'react';
import { healthService } from '../api/healthService';
import type { HealthResponse } from '../types/health';
import HealthCard from '../components/dashboard/HealthCard';
import DatabaseStatusCard from '../components/dashboard/DatabaseStatusCard';
import SystemMetricsCard from '../components/dashboard/SystemMetricsCard';
import ModuleRoadmapCard from '../components/dashboard/ModuleRoadmapCard';
import { Terminal, Check, Copy, ShieldCheck, RefreshCw } from 'lucide-react';

interface LandingDashboardProps {
  onStatusChange?: (status: 'UP' | 'DOWN' | 'DEGRADED' | 'NOT_CONFIGURED') => void;
  triggerRefreshKey?: number;
}

export const LandingDashboard: React.FC<LandingDashboardProps> = ({
  onStatusChange,
  triggerRefreshKey = 0,
}) => {
  const [health, setHealth] = useState<HealthResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [lastChecked, setLastChecked] = useState<Date | null>(null);
  const [copiedCmd, setCopiedCmd] = useState<string | null>(null);

  const fetchHealth = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await healthService.checkHealth();
      setHealth(data);
      setLastChecked(new Date());
      onStatusChange?.(data.status);
    } catch (err: any) {
      const msg = err.message || 'Unable to establish connection with the backend server.';
      setError(msg);
      setHealth(null);
      setLastChecked(new Date());
      onStatusChange?.('DOWN');
    } finally {
      setLoading(false);
    }
  }, [onStatusChange]);

  useEffect(() => {
    fetchHealth();
  }, [fetchHealth, triggerRefreshKey]);

  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedCmd(id);
    setTimeout(() => setCopiedCmd(null), 2000);
  };

  return (
    <div className="space-y-8">
      {/* Hero Banner */}
      <section className="bg-gradient-to-r from-water-900 via-water-800 to-slate-900 rounded-2xl p-6 sm:p-8 text-white shadow-xl">
        <div className="max-w-3xl">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-water-500/20 text-water-200 border border-water-400/30 text-xs font-semibold mb-4">
            <ShieldCheck className="w-4 h-4 text-water-400" />
            Module 1: Project Foundation &amp; Environment Verification
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-white mb-2">
            Smart Water Management &amp; Database Analytics System
          </h1>
          <p className="text-sm sm:text-base text-slate-300 leading-relaxed">
            Educational water-utility simulation platform engineered with Java 21, Spring Boot 3,
            PostgreSQL, Flyway migrations, and a reactive React/TypeScript control plane.
          </p>

          <div className="mt-6 flex flex-wrap items-center gap-4 text-xs text-slate-300">
            <span className="flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
              Live Diagnostic Probe
            </span>
            <span>&bull;</span>
            <span>
              Last Checked:{' '}
              {lastChecked ? lastChecked.toLocaleTimeString() : 'Pending verification...'}
            </span>
            <button
              onClick={fetchHealth}
              disabled={loading}
              className="inline-flex items-center gap-1.5 text-water-300 hover:text-white underline underline-offset-4 disabled:opacity-50 cursor-pointer"
            >
              <RefreshCw className={`w-3 h-3 ${loading ? 'animate-spin' : ''}`} />
              Re-check now
            </button>
          </div>
        </div>
      </section>

      {/* Health Diagnostic Cards Grid */}
      <section>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-bold text-slate-900 tracking-tight">System Diagnostic Health Check</h2>
          <span className="text-xs text-slate-500 font-mono">GET /api/health</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <HealthCard health={health} loading={loading} error={error} />
          <DatabaseStatusCard
            dbHealth={health?.database}
            loading={loading}
            backendError={error !== null}
          />
          <SystemMetricsCard system={health?.system} loading={loading} />
        </div>
      </section>

      {/* Developer Quick-Start on Windows */}
      <section className="bg-white rounded-xl border border-slate-200 shadow-xs p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <Terminal className="w-5 h-5 text-water-600" />
            <h2 className="text-base font-bold text-slate-900">Windows Startup &amp; Development Commands</h2>
          </div>
          <span className="text-xs text-slate-400">PowerShell / Command Prompt</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Backend command */}
          <div className="bg-slate-900 rounded-lg p-4 text-slate-100 font-mono text-xs">
            <div className="flex justify-between items-center text-slate-400 pb-2 mb-2 border-b border-slate-800">
              <span className="font-semibold text-slate-300">1. Start Backend (Spring Boot 3)</span>
              <button
                onClick={() => copyToClipboard('cd backend; .\\mvnw.cmd spring-boot:run', 'cmd-be')}
                className="hover:text-white flex items-center gap-1 cursor-pointer"
                title="Copy command"
              >
                {copiedCmd === 'cmd-be' ? (
                  <Check className="w-3.5 h-3.5 text-emerald-400" />
                ) : (
                  <Copy className="w-3.5 h-3.5" />
                )}
                <span>{copiedCmd === 'cmd-be' ? 'Copied' : 'Copy'}</span>
              </button>
            </div>
            <p className="text-emerald-400">cd backend</p>
            <p className="text-white">.\mvnw.cmd spring-boot:run</p>
            <p className="text-slate-500 mt-2 text-[11px]"># Runs on http://localhost:8080</p>
          </div>

          {/* Frontend command */}
          <div className="bg-slate-900 rounded-lg p-4 text-slate-100 font-mono text-xs">
            <div className="flex justify-between items-center text-slate-400 pb-2 mb-2 border-b border-slate-800">
              <span className="font-semibold text-slate-300">2. Start Frontend (React + Vite)</span>
              <button
                onClick={() => copyToClipboard('cd frontend; npm run dev', 'cmd-fe')}
                className="hover:text-white flex items-center gap-1 cursor-pointer"
                title="Copy command"
              >
                {copiedCmd === 'cmd-fe' ? (
                  <Check className="w-3.5 h-3.5 text-emerald-400" />
                ) : (
                  <Copy className="w-3.5 h-3.5" />
                )}
                <span>{copiedCmd === 'cmd-fe' ? 'Copied' : 'Copy'}</span>
              </button>
            </div>
            <p className="text-emerald-400">cd frontend</p>
            <p className="text-white">npm run dev</p>
            <p className="text-slate-500 mt-2 text-[11px]"># Runs on http://localhost:5173</p>
          </div>
        </div>
      </section>

      {/* Module Roadmap Card */}
      <section>
        <ModuleRoadmapCard />
      </section>
    </div>
  );
};

export default LandingDashboard;
