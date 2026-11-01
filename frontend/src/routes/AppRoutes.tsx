import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AppLayout } from '../components/layout/AppLayout';
import { LoginPage } from '../pages/LoginPage';
import { CustomersPage } from '../pages/CustomersPage';
import { MetersPage } from '../pages/MetersPage';
import { ReadingsPage } from '../pages/ReadingsPage';
import { BillingPage } from '../pages/BillingPage';
import { LeakAlertsPage } from '../pages/LeakAlertsPage';
import { WaterQualityPage } from '../pages/WaterQualityPage';
import { MaintenancePage } from '../pages/MaintenancePage';
import { ExecutiveDashboardPage } from '../pages/ExecutiveDashboardPage';
import LandingDashboard from '../pages/LandingDashboard';
import NotFoundPage from '../pages/NotFoundPage';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<AppLayout />}>
        <Route index element={<ExecutiveDashboardPage />} />
        <Route path="customers" element={<CustomersPage />} />
        <Route path="meters" element={<MetersPage />} />
        <Route path="readings" element={<ReadingsPage />} />
        <Route path="billing" element={<BillingPage />} />
        <Route path="leak-alerts" element={<LeakAlertsPage />} />
        <Route path="water-quality" element={<WaterQualityPage />} />
        <Route path="maintenance" element={<MaintenancePage />} />
        <Route path="health" element={<LandingDashboard />} />
      </Route>
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
};

export default AppRoutes;
