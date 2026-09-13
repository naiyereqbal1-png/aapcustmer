import React from 'react';
import { useOnlineStatus } from '../../hooks/useOnlineStatus';
import { WifiOff } from 'lucide-react';

export const OfflineIndicator: React.FC = () => {
  const isOnline = useOnlineStatus();

  if (isOnline) return null;

  return (
    <div
      id="pwa-offline-indicator"
      className="fixed bottom-20 left-4 z-50 flex items-center gap-2 rounded-xl bg-slate-900 text-amber-400 px-3 py-2 text-xs font-bold border border-amber-500/20 shadow-2xl animate-bounce"
    >
      <WifiOff className="w-4 h-4 text-amber-500" />
      <span>Offline Mode — Cached data is in use.</span>
    </div>
  );
};
