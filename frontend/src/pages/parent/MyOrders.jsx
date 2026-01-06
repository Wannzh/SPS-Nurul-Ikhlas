import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from '../../components/ui/card';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Loader2, ArrowLeft, ShoppingBag, CreditCard, CheckCircle, Clock, AlertCircle } from 'lucide-react';

export default function MyOrders() {
    const navigate = useNavigate();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Modal State
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [payAmount, setPayAmount] = useState(0);
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        try {
            const response = await api.get('/parent/orders/uniform');
            setOrders(response.data?.data || []);
        } catch (err) {
            console.error("Failed to fetch orders", err);
            setError("Gagal memuat data pesanan.");
        } finally {
            setLoading(false);
        }
    };

    const formatRupiah = (amount) => {
        return new Intl.NumberFormat('id-ID', {
            style: 'currency',
            currency: 'IDR',
            minimumFractionDigits: 0
        }).format(amount || 0);
    };

    const openPaymentModal = (order) => {
        const remaining = order.totalAmount - order.totalPaid;
        setSelectedOrder(order);
        setPayAmount(remaining);
        setIsModalOpen(true);
        setError('');
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedOrder(null);
        setPayAmount(0);
    };

    const handlePayment = async () => {
        if (!selectedOrder || payAmount <= 0) return;

        const remaining = selectedOrder.totalAmount - selectedOrder.totalPaid;
        if (payAmount > remaining) {
            setError("Jumlah melebihi sisa tagihan.");
            return;
        }

        setSubmitting(true);
        setError('');

        try {
            const response = await api.post('/parent/payments/create', {
                orderId: selectedOrder.id,
                amount: payAmount,
                paymentType: 'UNIFORM'
            });

            const transaction = response.data?.data;
            if (transaction?.xenditPaymentUrl) {
                window.location.href = transaction.xenditPaymentUrl;
            } else {
                closeModal();
                fetchOrders();
            }
        } catch (err) {
            console.error("Failed to create payment", err);
            setError(err.response?.data?.message || "Gagal membuat pembayaran.");
        } finally {
            setSubmitting(false);
        }
    };

    const getStatusBadge = (status) => {
        const styles = {
            UNPAID: 'bg-red-100 text-red-800',
            PARTIAL: 'bg-yellow-100 text-yellow-800',
            PAID: 'bg-green-100 text-green-800',
        };
        const labels = {
            UNPAID: 'Belum Bayar',
            PARTIAL: 'Cicilan',
            PAID: 'Lunas',
        };
        return (
            <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold ${styles[status] || 'bg-gray-100 text-gray-800'}`}>
                {labels[status] || status}
            </span>
        );
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm border-b sticky top-0 z-40">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center gap-4">
                    <Button variant="ghost" size="icon" onClick={() => navigate('/parent/dashboard')}>
                        <ArrowLeft className="h-5 w-5" />
                    </Button>
                    <div>
                        <h1 className="text-xl font-bold text-gray-900">Pesanan Saya</h1>
                        <p className="text-sm text-gray-500">Riwayat pesanan seragam</p>
                    </div>
                </div>
            </header>

            <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-4">
                {orders.length === 0 ? (
                    <Card className="text-center py-12">
                        <CardContent>
                            <ShoppingBag className="h-12 w-12 text-gray-300 mx-auto mb-4" />
                            <p className="text-gray-500">Belum ada pesanan.</p>
                            <Button onClick={() => navigate('/parent/uniform-order')} className="mt-4">
                                Pesan Seragam
                            </Button>
                        </CardContent>
                    </Card>
                ) : (
                    orders.map((order) => {
                        const remaining = order.totalAmount - order.totalPaid;
                        return (
                            <Card key={order.id}>
                                <CardHeader className="flex flex-row items-start justify-between space-y-0 pb-2">
                                    <div>
                                        <CardTitle className="text-base">
                                            Pesanan #{order.id.substring(0, 8).toUpperCase()}
                                        </CardTitle>
                                        <CardDescription>
                                            {new Date(order.orderDate).toLocaleDateString('id-ID', {
                                                day: 'numeric', month: 'long', year: 'numeric'
                                            })}
                                        </CardDescription>
                                    </div>
                                    {getStatusBadge(order.paymentStatus)}
                                </CardHeader>
                                <CardContent className="space-y-3">
                                    <div className="grid grid-cols-3 gap-4 text-sm">
                                        <div>
                                            <p className="text-gray-500">Total</p>
                                            <p className="font-bold text-blue-600">{formatRupiah(order.totalAmount)}</p>
                                        </div>
                                        <div>
                                            <p className="text-gray-500">Sudah Bayar</p>
                                            <p className="font-medium text-green-600">{formatRupiah(order.totalPaid)}</p>
                                        </div>
                                        <div>
                                            <p className="text-gray-500">Sisa</p>
                                            <p className={`font-medium ${remaining > 0 ? 'text-red-600' : 'text-green-600'}`}>
                                                {formatRupiah(remaining)}
                                            </p>
                                        </div>
                                    </div>

                                    {order.items && order.items.length > 0 && (
                                        <div className="border-t pt-3">
                                            <p className="text-xs text-gray-500 mb-2">Item:</p>
                                            <div className="space-y-1">
                                                {order.items.map((item, idx) => (
                                                    <div key={idx} className="flex justify-between text-sm">
                                                        <span>{item.uniform?.name} ({item.uniform?.size}) x{item.quantity}</span>
                                                        <span>{formatRupiah(item.subTotal)}</span>
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    )}
                                </CardContent>
                                {remaining > 0 && (
                                    <CardFooter>
                                        <Button onClick={() => openPaymentModal(order)} className="w-full">
                                            <CreditCard className="mr-2 h-4 w-4" /> Bayar / Cicil
                                        </Button>
                                    </CardFooter>
                                )}
                            </Card>
                        );
                    })
                )}
            </main>

            {/* Payment Modal */}
            {isModalOpen && selectedOrder && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
                    <Card className="w-full max-w-md">
                        <CardHeader>
                            <CardTitle>Pembayaran Cicilan</CardTitle>
                            <CardDescription>
                                Sisa Tagihan: {formatRupiah(selectedOrder.totalAmount - selectedOrder.totalPaid)}
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            {error && (
                                <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm flex items-center gap-2">
                                    <AlertCircle className="h-4 w-4" /> {error}
                                </div>
                            )}
                            <div className="space-y-2">
                                <Label>Nominal Pembayaran (Rp)</Label>
                                <Input
                                    type="number"
                                    value={payAmount}
                                    onChange={(e) => setPayAmount(Number(e.target.value))}
                                    max={selectedOrder.totalAmount - selectedOrder.totalPaid}
                                />
                                <p className="text-xs text-gray-500">
                                    Max: {formatRupiah(selectedOrder.totalAmount - selectedOrder.totalPaid)}
                                </p>
                            </div>
                        </CardContent>
                        <CardFooter className="flex justify-end gap-2">
                            <Button variant="ghost" onClick={closeModal}>Batal</Button>
                            <Button onClick={handlePayment} disabled={submitting || payAmount <= 0}>
                                {submitting ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <CheckCircle className="mr-2 h-4 w-4" />}
                                Bayar Sekarang
                            </Button>
                        </CardFooter>
                    </Card>
                </div>
            )}
        </div>
    );
}
