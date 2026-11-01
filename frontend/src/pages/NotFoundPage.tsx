import React from 'react';
import { Link } from 'react-router-dom';
import { HelpCircle, ArrowLeft } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <div className="py-20 text-center">
      <div className="inline-flex p-4 bg-slate-100 rounded-full text-slate-500 mb-4">
        <HelpCircle className="w-10 h-10" />
      </div>
      <h1 className="text-3xl font-extrabold text-slate-900 mb-2">404 - Page Not Found</h1>
      <p className="text-sm text-slate-500 max-w-md mx-auto mb-6">
        The requested resource or utility dashboard view does not exist in this module.
      </p>
      <Link
        to="/"
        className="inline-flex items-center gap-2 px-4 py-2 bg-water-600 text-white rounded-lg text-sm font-medium hover:bg-water-700 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        Return to Foundation Dashboard
      </Link>
    </div>
  );
};

export default NotFoundPage;
