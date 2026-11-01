import React from 'react';
import { ShieldCheck, Activity, BarChart3, MapPin } from 'lucide-react';
import StatusBadge from '../common/StatusBadge';

export const ModuleRoadmapCard: React.FC = () => {
  const modules = [
    {
      id: 'MOD-1',
      title: 'Module 1: Architecture & Project Foundation',
      description: 'Monorepo setup, Spring Boot 3 + Java 21, React + TS + Tailwind, PostgreSQL + Flyway baseline, health diagnostics.',
      status: 'UP' as const,
      statusLabel: 'Completed / Operational',
      icon: ShieldCheck,
      color: 'text-emerald-600 bg-emerald-50',
    },
    {
      id: 'MOD-2',
      title: 'Module 2: Water Network Topology & Facilities',
      description: 'Treatment plants, pumping stations, distribution reservoirs, and pipeline network graph models with JPA entities.',
      status: 'PLANNED' as const,
      statusLabel: 'Planned (Next Module)',
      icon: MapPin,
      color: 'text-sky-600 bg-sky-50',
    },
    {
      id: 'MOD-3',
      title: 'Module 3: Sensor Telemetry & Timeseries Ingestion',
      description: 'Flow meter rates, pressure, turbidity, and chemical dosage timeseries ingestion pipelines with database partitioning.',
      status: 'PLANNED' as const,
      statusLabel: 'Planned',
      icon: Activity,
      color: 'text-indigo-600 bg-indigo-50',
    },
    {
      id: 'MOD-4',
      title: 'Module 4: Anomaly Detection & Leak Analytics',
      description: 'Pressure surge anomalies, pipe leak pattern recognition algorithms, and real-time operational alerts.',
      status: 'PLANNED' as const,
      statusLabel: 'Planned',
      icon: BarChart3,
      color: 'text-purple-600 bg-purple-50',
    },
  ];

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-xs p-6">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-base font-semibold text-slate-900">Simulation System Architecture Roadmap</h3>
          <p className="text-xs text-slate-500">
            Educational water-utility simulation project lifecycle and module breakdown
          </p>
        </div>
        <span className="text-xs text-slate-400 font-mono">ISO/IEC 25010</span>
      </div>

      <div className="space-y-3">
        {modules.map((m) => {
          const Icon = m.icon;
          return (
            <div
              key={m.id}
              className="flex items-start justify-between p-3.5 rounded-lg border border-slate-100 hover:border-slate-200 bg-slate-50/50 transition-colors"
            >
              <div className="flex items-start space-x-3">
                <div className={`p-2 rounded-lg ${m.color} shrink-0 mt-0.5`}>
                  <Icon className="w-4 h-4" />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-mono font-semibold text-slate-500">{m.id}</span>
                    <h4 className="text-sm font-semibold text-slate-800">{m.title}</h4>
                  </div>
                  <p className="text-xs text-slate-600 mt-0.5">{m.description}</p>
                </div>
              </div>
              <div className="shrink-0 ml-4">
                <StatusBadge status={m.status} label={m.statusLabel} size="sm" />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default ModuleRoadmapCard;
