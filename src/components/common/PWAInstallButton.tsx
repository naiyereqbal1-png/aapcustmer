import React, { useState } from 'react';
import { usePWAInstall } from '../../hooks/usePWAInstall';
import { Download, X, Share } from 'lucide-react';

export const PWAInstallButton: React.FC = () => {
  const { isInstallable, isInstalled, isIOS, install } = usePWAInstall();
  const [showIOSGuide, setShowIOSGuide] = useState(false);
  const [dismissed, setDismissed] = useState(false);

  if (isInstalled || dismissed) {
    return null;
  }

  // Android, Chrome, and Desktop prompt flow
  if (isInstallable) {
    return (
      <div
        id="pwa-install-banner"
        className="bg-indigo-900 text-white px-4 py-3 text-xs flex items-center justify-between gap-3 sticky top-0 z-50 animate-in slide-in-from-top duration-300 shadow-md"
      >
        <div className="flex items-center gap-2 min-w-0">
          <div className="w-8 h-8 rounded-lg bg-white/10 flex items-center justify-center shrink-0">
            <span className="font-extrabold text-amber-400">TH</span>
          </div>
          <div className="min-w-0">
            <p className="font-bold text-white truncate">Install TRYatHOME Mobile App</p>
            <p className="text-indigo-200 text-[10px] truncate">Fast shopping, seamless doorstep try & buy!</p>
          </div>
        </div>
        <div className="flex items-center gap-2 shrink-0">
          <button
            onClick={install}
            className="px-3 py-1.5 bg-amber-400 hover:bg-amber-300 active:scale-95 text-slate-950 font-bold rounded-lg text-[10px] uppercase tracking-wider flex items-center gap-1 transition-all cursor-pointer"
          >
            <Download className="w-3 h-3" />
            <span>Install</span>
          </button>
          <button
            onClick={() => setDismissed(true)}
            className="p-1 text-indigo-300 hover:text-white transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>
    );
  }

  // iOS Safari specific flow
  if (isIOS) {
    return (
      <>
        <div
          id="pwa-install-ios-banner"
          className="bg-indigo-900 text-white px-4 py-3 text-xs flex items-center justify-between gap-3 sticky top-0 z-50 animate-in slide-in-from-top duration-300 shadow-md"
        >
          <div className="flex items-center gap-2 min-w-0">
            <div className="w-8 h-8 rounded-lg bg-white/10 flex items-center justify-center shrink-0">
              <span className="font-extrabold text-amber-400">TH</span>
            </div>
            <div className="min-w-0">
              <p className="font-bold text-white truncate">Install TRYatHOME on iPhone</p>
              <p className="text-indigo-200 text-[10px] truncate">Add to Home Screen for the full app experience</p>
            </div>
          </div>
          <div className="flex items-center gap-2 shrink-0">
            <button
              onClick={() => setShowIOSGuide(true)}
              className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 active:scale-95 text-white font-bold rounded-lg text-[10px] uppercase tracking-wider flex items-center gap-1 transition-all cursor-pointer"
            >
              <Download className="w-3 h-3" />
              <span>Get App</span>
            </button>
            <button
              onClick={() => setDismissed(true)}
              className="p-1 text-indigo-300 hover:text-white transition-colors cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {showIOSGuide && (
          <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/60 p-4 animate-in fade-in duration-200">
            <div className="w-full max-w-sm rounded-2xl bg-white p-5 pb-6 shadow-2xl animate-in slide-in-from-bottom-8 duration-300 text-slate-800">
              <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                <h3 className="text-sm font-extrabold text-slate-900">Install TRYatHOME on iOS</h3>
                <button
                  onClick={() => setShowIOSGuide(false)}
                  className="p-1 text-slate-400 hover:text-slate-600 cursor-pointer"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
              <div className="mt-4 space-y-3.5 text-xs text-slate-600">
                <p className="leading-relaxed">
                  Open this site in your <strong className="text-slate-900">Safari browser</strong> and install the app with two quick taps:
                </p>
                <div className="flex items-start gap-3 bg-slate-50 p-3 rounded-xl border border-slate-100">
                  <div className="w-6 h-6 rounded-lg bg-indigo-100 text-indigo-600 flex items-center justify-center shrink-0 font-bold">1</div>
                  <p className="pt-0.5">
                    Tap the <strong className="text-indigo-600 inline-flex items-center gap-0.5 font-bold">Share <Share className="w-3.5 h-3.5 inline" /></strong> button in the Safari navigation bar.
                  </p>
                </div>
                <div className="flex items-start gap-3 bg-slate-50 p-3 rounded-xl border border-slate-100">
                  <div className="w-6 h-6 rounded-lg bg-indigo-100 text-indigo-600 flex items-center justify-center shrink-0 font-bold">2</div>
                  <p className="pt-0.5">
                    Scroll down and select <strong className="text-slate-900">Add to Home Screen</strong>.
                  </p>
                </div>
              </div>
              <button
                onClick={() => setShowIOSGuide(false)}
                className="mt-5 w-full rounded-xl bg-slate-900 py-2.5 text-xs font-bold text-white hover:bg-slate-800 transition-colors cursor-pointer"
              >
                Got It, Thanks
              </button>
            </div>
          </div>
        )}
      </>
    );
  }

  return null;
};
