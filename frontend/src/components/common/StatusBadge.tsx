import React from 'react';
import { CheckCircle2, AlertCircle, XCircle, HelpCircle } from 'lucide-react';

interface StatusBadgeProps {
  status: 'UP' | 'DOWN' | 'DEGRADED' | 'NOT_CONFIGURED' | 'PLANNED';
  label?: string;
  size?: 'sm' | 'md';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, label, size = 'md' }) => {
  const configs = {
    UP: {
      bg: 'bg-emerald-50 text-emerald-700 border-emerald-200',
      dot: 'bg-emerald-500',
      icon: CheckCircle2,
      text: 'Operational',
    },
    DEGRADED: {
      bg: 'bg-amber-50 text-amber-700 border-amber-200',
      dot: 'bg-amber-500',
      icon: AlertCircle,
      text: 'Degraded',
    },
    DOWN: {
      bg: 'bg-rose-50 text-rose-700 border-rose-200',
      dot: 'bg-rose-500',
      icon: XCircle,
      text: 'Offline',
    },
    NOT_CONFIGURED: {
      bg: 'bg-slate-100 text-slate-700 border-slate-200',
      dot: 'bg-slate-400',
      icon: HelpCircle,
      text: 'Not Configured',
    },
    PLANNED: {
      bg: 'bg-sky-50 text-sky-700 border-sky-200',
      dot: 'bg-sky-400',
      icon: HelpCircle,
      text: 'Planned (Next Module)',
    },
  };

  const config = configs[status] || configs.NOT_CONFIGURED;
  const Icon = config.icon;
  const displayText = label || config.text;

  const sizeClasses = size === 'sm' ? 'px-2 py-0.5 text-xs' : 'px-3 py-1 text-sm';

  return (
    <span className={`inline-flex items-center gap-1.5 font-medium rounded-full border ${config.bg} ${sizeClasses}`}>
      <Icon className={size === 'sm' ? 'w-3.5 h-3.5' : 'w-4 h-4'} />
      <span>{displayText}</span>
    </span>
  );
};

export default StatusBadge;
