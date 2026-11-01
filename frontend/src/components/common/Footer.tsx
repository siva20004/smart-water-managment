import React from 'react';
import { Database, Server, Code2, ShieldCheck } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-white border-t border-slate-200 mt-12 py-6 text-xs text-slate-500">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row justify-between items-center gap-4">
        <div className="flex items-center space-x-4">
          <div className="flex items-center gap-1.5">
            <Server className="w-3.5 h-3.5 text-water-600" />
            <span>Java 21 / Spring Boot 3</span>
          </div>
          <span>&bull;</span>
          <div className="flex items-center gap-1.5">
            <Database className="w-3.5 h-3.5 text-indigo-600" />
            <span>PostgreSQL + Flyway</span>
          </div>
          <span>&bull;</span>
          <div className="flex items-center gap-1.5">
            <Code2 className="w-3.5 h-3.5 text-sky-600" />
            <span>React + TypeScript + Vite</span>
          </div>
        </div>

        <div className="flex items-center gap-2 text-slate-400">
          <ShieldCheck className="w-4 h-4 text-emerald-500" />
          <span>Module 1 Foundation &bull; Educational Water-Utility Simulation Architecture</span>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
