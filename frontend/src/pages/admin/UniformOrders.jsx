import { useState, useEffect } from 'react';
import api from '../../lib/axios';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Select } from '../../components/ui/select';
import { Loader2, Package, CheckCircle, Clock, XCircle, Truck } from 'lucide-react';

export default function UniformOrders() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState('');

    useEffect(() => {
        fetchOrders();
    }, [filter]);

    const fetchOrders = async () => {
        try {
            const params = filter ? `?status=${filter}` : '';
            const response = await api.get(`/admin/orders/uniform${params}`);
            setOrders(response.data?.data || []);
        } catch (error) {
            console.error("Failed to fetch orders", error);
        } finally {
            setLoading(false);
        }
    };

    const handleStatusChange = async (orderId, newStatus) => {
        try {
            await api.put(`/admin/orders/uniform/${orderId}/status?status=${newStatus}`);
            fetchOrders();
        } catch (error) {
            console.error("Failed to update status", error);
            alert("Gagal memperbarui status pesanan.");
        }
    };

    const formatRupiah = (amount) => {
        return new Intl.NumberFormat('id-ID', {
            style: 'currency',
            currency: 'IDR',
            minimumFractionDigits: 0
        }).format(amount || 0);
    };

    const getPaymentBadge = (status) => {
        const styles = {
            UNPAID: 'bg-red-100 text-red-800',
            PARTIAL: 'bg-yellow-100 text-yellow-800',
            PAID: 'bg-green-100 text-green-800',
        };
        const labels = { UNPAID: 'Belum Lunas', PARTIAL: 'Cicilan', PAID: 'Lunas' };
        return (
            <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-semibold ${styles[status] || 'bg-gray-100'}`}>
                {labels[status] || status}
            </span>
        );
    };

    const getOrderStatusBadge = (status) => {
        const config = {
            PENDING: { color: 'bg-gray-100 text-gray-800', icon: Clock, label: 'Pending' },
            READY_TO_PICKUP: { color: 'bg-blue-100 text-blue-800', icon: Package, label: 'Siap Diambil' },
            TAKEN: { color: 'bg-green-100 text-green-800', icon: CheckCircle, label: 'Sudah Diambil' },
            CANCELLED: { color: 'bg-red-100 text-red-800', icon: XCircle, label: 'Dibatalkan' },
        };
        const c = config[status] || config.PENDING;
        const Icon = c.icon;
        return (
            <span className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-semibold ${c.color}`}>
                <Icon className="h-3 w-3" /> {c.label}
            </span>
        );
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Pesanan Seragam</h1>
                    <p className="text-gray-500">Kelola pesanan seragam dari orang tua.</p>
                </div>
                <Select value={filter} onChange={(e) => setFilter(e.target.value)} className="w-48">
                    <option value="">Semua Status</option>
                    <option value="PENDING">Pending</option>
                    <option value="READY_TO_PICKUP">Siap Diambil</option>
                    <option value="TAKEN">Sudah Diambil</option>
                    <option value="CANCELLED">Dibatalkan</option>
                </Select>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Truck className="h-5 w-5" />
                        Daftar Pesanan
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    {loading ? (
                        <div className="flex justify-center p-8"><Loader2 className="animate-spin" /></div>
                    ) : (
                        <div className="relative w-full overflow-auto">
                            <table className="w-full caption-bottom text-sm text-left">
                                <thead className="[&_tr]:border-b">
                                    <tr className="border-b">
                                        <th className="h-12 px-4 font-medium">Tanggal</th>
                                        <th className="h-12 px-4 font-medium">Nama Siswa</th>
                                        <th className="h-12 px-4 font-medium">Items</th>
                                        <th className="h-12 px-4 font-medium">Total</th>
                                        <th className="h-12 px-4 font-medium">Pembayaran</th>
                                        <th className="h-12 px-4 font-medium">Status Pesanan</th>
                                        <th className="h-12 px-4 font-medium">Aksi</th>
                                    </tr>
                                </thead>
                                <tbody className="[&_tr:last-child]:border-0">
                                    {orders.length > 0 ? orders.map((order) => (
                                        <tr key={order.id} className="border-b">
                                            <td className="p-4 text-sm">
                                                {new Date(order.orderDate).toLocaleDateString('id-ID', {
                                                    day: 'numeric', month: 'short', year: 'numeric'
                                                })}
                                            </td>
                                            <td className="p-4 font-medium">{order.student?.person?.fullName || '-'}</td>
                                            <td className="p-4 text-sm text-gray-500">
                                                {order.items?.length || 0} item
                                            </td>
                                            <td className="p-4 font-bold text-blue-600">
                                                {formatRupiah(order.totalAmount)}
                                            </td>
                                            <td className="p-4">{getPaymentBadge(order.paymentStatus)}</td>
                                            <td className="p-4">{getOrderStatusBadge(order.orderStatus)}</td>
                                            <td className="p-4">
                                                <Select
                                                    value={order.orderStatus}
                                                    onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                                    className="text-xs"
                                                >
                                                    <option value="PENDING">Pending</option>
                                                    <option value="READY_TO_PICKUP">Siap Diambil</option>
                                                    <option value="TAKEN">Sudah Diambil</option>
                                                    <option value="CANCELLED">Batalkan</option>
                                                </Select>
                                            </td>
                                        </tr>
                                    )) : (
                                        <tr><td colSpan={7} className="p-4 text-center">Belum ada pesanan.</td></tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    )}
                </CardContent>
            </Card>
        </div>
    );
}
