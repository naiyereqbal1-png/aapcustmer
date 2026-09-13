import React from 'react';
import { Home, Grid, Heart, ShoppingBag, User } from 'lucide-react';

interface MobileBottomNavProps {
  activeTab: string;
  onSelectTab: (tab: string) => void;
  cartCount: number;
  wishlistCount: number;
  isLoggedIn: boolean;
  onOpenLogin: () => void;
}

export const MobileBottomNav: React.FC<MobileBottomNavProps> = ({
  activeTab,
  onSelectTab,
  cartCount,
  wishlistCount,
  isLoggedIn,
  onOpenLogin,
}) => {
  const tabs = [
    { id: 'HOME', label: 'Home', icon: Home },
    { id: 'CATEGORIES', label: 'Categories', icon: Grid },
    { id: 'WISHLIST', label: 'Wishlist', icon: Heart, badge: wishlistCount },
    { id: 'CART', label: 'Cart', icon: ShoppingBag, badge: cartCount },
    { id: 'PROFILE', label: 'Profile', icon: User },
  ];

  const handleTabClick = (tabId: string) => {
    if (tabId === 'PROFILE' && !isLoggedIn) {
      onOpenLogin();
    } else {
      onSelectTab(tabId);
    }
  };

  return (
    <div
      id="mobile-bottom-navigation"
      className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-white border-t border-slate-200/80 px-2 py-2 flex items-center justify-around shadow-2xl safe-bottom"
    >
      {tabs.map((tab) => {
        const Icon = tab.icon;
        const isActive = activeTab === tab.id;
        return (
          <button
            key={tab.id}
            id={`mobile-nav-${tab.id.toLowerCase()}-btn`}
            onClick={() => handleTabClick(tab.id)}
            className={`flex flex-col items-center justify-center min-w-[60px] py-1 text-[10px] font-bold tracking-wide transition-all relative cursor-pointer ${
              isActive ? 'text-indigo-600 scale-105' : 'text-slate-500 hover:text-slate-800'
            }`}
          >
            <div className="relative p-1">
              <Icon className={`w-5 h-5 transition-transform ${isActive ? 'stroke-[2.5]' : 'stroke-[2]'}`} />
              {tab.badge !== undefined && tab.badge > 0 && (
                <span className="absolute -top-1 -right-2 bg-rose-600 text-white text-[9px] font-black px-1.5 py-0.2 rounded-full min-w-[16px] text-center border border-white">
                  {tab.badge}
                </span>
              )}
            </div>
            <span className="mt-0.5">{tab.label}</span>
            {isActive && (
              <span className="absolute bottom-0 w-1 h-1 rounded-full bg-indigo-600"></span>
            )}
          </button>
        );
      })}
    </div>
  );
};
