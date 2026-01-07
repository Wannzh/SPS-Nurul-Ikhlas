import { Link, useLocation } from 'react-router-dom';
import { cn } from '../../lib/utils';
import { LayoutDashboard, Users, Calendar, LogOut, Wallet, Shirt, Truck, TrendingDown } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../ui/button';

const navigation = [
    { name: 'Dashboard', href: '/admin', icon: LayoutDashboard },
    { name: 'Kelola Pendaftar', href: '/admin/students', icon: Users },
    { name: 'Kelola Tahun Ajaran', href: '/admin/academic-years', icon: Calendar },
];

export function Sidebar() {
    const location = useLocation();
    const { logout } = useAuth();

    const getLinkClass = (path) => cn(
        "group flex items-center rounded-md px-3 py-2 text-sm font-medium transition-colors",
        location.pathname === path
            ? "bg-blue-50 text-blue-700 hover:bg-blue-100"
            : "text-gray-700 hover:bg-gray-100 hover:text-gray-900"
    );

    const getIconClass = (path) => cn(
        "mr-3 h-5 w-5 flex-shrink-0",
        location.pathname === path ? "text-blue-600" : "text-gray-400 group-hover:text-gray-500"
    );

    return (
        <div className="flex h-full w-64 flex-col border-r bg-white">
            <div className="flex h-16 items-center border-b px-6">
                <span className="text-xl font-bold text-blue-600">Admin Panel</span>
            </div>
            <div className="flex-1 overflow-y-auto py-4">
                <nav className="space-y-1 px-3">
                    {navigation.map((item) => {
                        const isActive = location.pathname === item.href || (item.href !== '/admin' && location.pathname.startsWith(item.href));
                        return (
                            <Link
                                key={item.name}
                                to={item.href}
                                className={cn(
                                    "group flex items-center rounded-md px-3 py-2 text-sm font-medium transition-colors",
                                    isActive
                                        ? "bg-blue-50 text-blue-700 hover:bg-blue-100"
                                        : "text-gray-700 hover:bg-gray-100 hover:text-gray-900"
                                )}
                            >
                                <item.icon
                                    className={cn(
                                        "mr-3 h-5 w-5 flex-shrink-0",
                                        isActive ? "text-blue-600" : "text-gray-400 group-hover:text-gray-500"
                                    )}
                                />
                                {item.name}
                            </Link>
                        );
                    })}

                    <div className="pt-4 pb-2">
                        <p className="px-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                            Keuangan & Inventaris
                        </p>
                    </div>

                    <Link to="/admin/bill-types" className={getLinkClass('/admin/bill-types')}>
                        <Wallet className={getIconClass('/admin/bill-types')} />
                        Jenis Tagihan
                    </Link>

                    <Link to="/admin/arrears" className={getLinkClass('/admin/arrears')}>
                        <TrendingDown className={getIconClass('/admin/arrears')} />
                        Laporan Tunggakan
                    </Link>

                    <Link to="/admin/uniforms" className={getLinkClass('/admin/uniforms')}>
                        <Shirt className={getIconClass('/admin/uniforms')} />
                        Master Seragam
                    </Link>

                    <Link to="/admin/uniform-orders" className={getLinkClass('/admin/uniform-orders')}>
                        <Truck className={getIconClass('/admin/uniform-orders')} />
                        Pesanan Seragam
                    </Link>
                </nav>
            </div>
            <div className="border-t p-4">
                <Button variant="ghost" className="w-full justify-start text-red-600 hover:bg-red-50 hover:text-red-700" onClick={logout}>
                    <LogOut className="mr-3 h-5 w-5" />
                    Logout
                </Button>
            </div>
        </div>
    );
}
