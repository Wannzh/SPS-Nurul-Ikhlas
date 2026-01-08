import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../../components/ui/card';
import { Loader2, ArrowLeft, CreditCard, CheckCircle, Clock, AlertTriangle, Wallet, AlertCircle, ShoppingCart } from 'lucide-react';

export default function MonthlyPayment() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [data, setData] = useState(null);
    const [activeTab, setActiveTab] = useState('unpaid');
    const [selectedItems, setSelectedItems] = useState([]);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const response = await api.get('/parent/finance/monthly-details');
            setData(response.data?.data);

            // Auto-select all arrears and due items
            const autoSelected = [];
            response.data?.data?.infaqItems?.forEach(item => {
                if (item.status === 'ARREARS' || item.status === 'DUE') {
                    autoSelected.push({ category: 'INFAQ', month: item.month });
                }
            });
            response.data?.data?.kasItems?.forEach(item => {
                if (item.status === 'ARREARS' || item.status === 'DUE') {
                    autoSelected.push({ category: 'KAS', month: item.month });
                }
            });
            setSelectedItems(autoSelected);
        } catch (err) {
            console.error("Failed to fetch monthly details", err);
            setError("Gagal memuat data tagihan.");
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

    const isSelected = (category, month) => {
        return selectedItems.some(item => item.category === category && item.month === month);
    };

    const toggleSelection = (category, month, status) => {
        if (status === 'PAID') return;

        const exists = isSelected(category, month);
        if (exists) {
            setSelectedItems(prev => prev.filter(item => !(item.category === category && item.month === month)));
        } else {
            setSelectedItems(prev => [...prev, { category, month }]);
        }
    };

    const calculateTotal = () => {
        let total = 0;
        selectedItems.forEach(item => {
            if (item.category === 'INFAQ') {
                total += data?.infaqMonthlyFee || 0;
            } else if (item.category === 'KAS') {
                total += data?.kasMonthlyFee || 0;
            }
        });
        return total;
    };

    const handlePayment = async () => {
        if (selectedItems.length === 0) return;

        setSubmitting(true);
        setError('');

        try {
            const response = await api.post('/parent/payments/pay-bills', {
                items: selectedItems
            });
            const transaction = response.data?.data;

            if (transaction?.xenditPaymentUrl) {
                window.location.href = transaction.xenditPaymentUrl;
            } else {
                fetchData();
                setSelectedItems([]);
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
            PAID: { bg: 'bg-green-100', text: 'text-green-800', icon: CheckCircle, label: 'Lunas' },
            DUE: { bg: 'bg-yellow-100', text: 'text-yellow-800', icon: Clock, label: 'Tagihan' },
            ARREARS: { bg: 'bg-red-100', text: 'text-red-800', icon: AlertTriangle, label: 'Nunggak' },
        };
        const s = styles[status] || styles.DUE;
        const Icon = s.icon;
        return (
            <span className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-semibold ${s.bg} ${s.text}`}>
                <Icon className="h-3 w-3" /> {s.label}
            </span>
        );
    };

    const unpaidInfaq = data?.infaqItems?.filter(i => i.status !== 'PAID') || [];
    const unpaidKas = data?.kasItems?.filter(i => i.status !== 'PAID') || [];
    const paidInfaq = data?.infaqItems?.filter(i => i.status === 'PAID') || [];
    const paidKas = data?.kasItems?.filter(i => i.status === 'PAID') || [];

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 pb-24">
            {/* Header */}
            <header className="bg-white shadow-sm border-b sticky top-0 z-40">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center gap-4">
                    <Button variant="ghost" size="icon" onClick={() => navigate('/parent/dashboard')}>
                        <ArrowLeft className="h-5 w-5" />
                    </Button>
                    <div className="flex-1">
                        <h1 className="text-xl font-bold text-gray-900">Pembayaran Bulanan</h1>
                        <p className="text-sm text-gray-500">Pilih tagihan yang ingin dibayar</p>
                    </div>
                </div>

                {/* Tabs */}
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex border-b">
                        <button
                            onClick={() => setActiveTab('unpaid')}
                            className={`px-4 py-3 text-sm font-medium border-b-2 -mb-px transition-colors ${activeTab === 'unpaid'
                                    ? 'border-blue-600 text-blue-600'
                                    : 'border-transparent text-gray-500 hover:text-gray-700'
                                }`}
                        >
                            Belum Bayar ({unpaidInfaq.length + unpaidKas.length})
                        </button>
                        <button
                            onClick={() => setActiveTab('paid')}
                            className={`px-4 py-3 text-sm font-medium border-b-2 -mb-px transition-colors ${activeTab === 'paid'
                                    ? 'border-blue-600 text-blue-600'
                                    : 'border-transparent text-gray-500 hover:text-gray-700'
                                }`}
                        >
                            Riwayat Lunas ({paidInfaq.length + paidKas.length})
                        </button>
                    </div>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                {error && (
                    <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm mb-4">{error}</div>
                )}

                {activeTab === 'unpaid' && (
                    <div className="grid md:grid-cols-2 gap-6">
                        {/* Infaq Column */}
                        <Card>
                            <CardHeader className="bg-green-50 border-b">
                                <CardTitle className="text-green-800 flex items-center gap-2">
                                    <Wallet className="h-5 w-5" /> Uang Infaq
                                </CardTitle>
                                <CardDescription>
                                    {formatRupiah(data?.infaqMonthlyFee)} / bulan
                                </CardDescription>
                            </CardHeader>
                            <CardContent className="p-0">
                                {unpaidInfaq.length === 0 ? (
                                    <p className="text-center text-gray-500 py-8">Tidak ada tagihan</p>
                                ) : (
                                    <ul className="divide-y">
                                        {unpaidInfaq.map(item => (
                                            <li
                                                key={item.month}
                                                className={`flex items-center justify-between p-4 hover:bg-gray-50 cursor-pointer ${isSelected('INFAQ', item.month) ? 'bg-blue-50' : ''
                                                    }`}
                                                onClick={() => toggleSelection('INFAQ', item.month, item.status)}
                                            >
                                                <div className="flex items-center gap-3">
                                                    <input
                                                        type="checkbox"
                                                        checked={isSelected('INFAQ', item.month)}
                                                        onChange={() => toggleSelection('INFAQ', item.month, item.status)}
                                                        className="h-4 w-4 text-blue-600 rounded"
                                                    />
                                                    <div>
                                                        <p className="font-medium">{item.monthLabel}</p>
                                                        <p className="text-sm text-gray-500">{formatRupiah(item.amount)}</p>
                                                    </div>
                                                </div>
                                                {getStatusBadge(item.status)}
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </CardContent>
                        </Card>

                        {/* Kas Column */}
                        <Card>
                            <CardHeader className="bg-purple-50 border-b">
                                <CardTitle className="text-purple-800 flex items-center gap-2">
                                    <Wallet className="h-5 w-5" /> Uang Kas
                                </CardTitle>
                                <CardDescription>
                                    {formatRupiah(data?.kasMonthlyFee)} / bulan
                                </CardDescription>
                            </CardHeader>
                            <CardContent className="p-0">
                                {unpaidKas.length === 0 ? (
                                    <p className="text-center text-gray-500 py-8">Tidak ada tagihan</p>
                                ) : (
                                    <ul className="divide-y">
                                        {unpaidKas.map(item => (
                                            <li
                                                key={item.month}
                                                className={`flex items-center justify-between p-4 hover:bg-gray-50 cursor-pointer ${isSelected('KAS', item.month) ? 'bg-blue-50' : ''
                                                    }`}
                                                onClick={() => toggleSelection('KAS', item.month, item.status)}
                                            >
                                                <div className="flex items-center gap-3">
                                                    <input
                                                        type="checkbox"
                                                        checked={isSelected('KAS', item.month)}
                                                        onChange={() => toggleSelection('KAS', item.month, item.status)}
                                                        className="h-4 w-4 text-blue-600 rounded"
                                                    />
                                                    <div>
                                                        <p className="font-medium">{item.monthLabel}</p>
                                                        <p className="text-sm text-gray-500">{formatRupiah(item.amount)}</p>
                                                    </div>
                                                </div>
                                                {getStatusBadge(item.status)}
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </CardContent>
                        </Card>
                    </div>
                )}

                {activeTab === 'paid' && (
                    <Card>
                        <CardHeader>
                            <CardTitle>Riwayat Pembayaran Lunas</CardTitle>
                        </CardHeader>
                        <CardContent>
                            {paidInfaq.length === 0 && paidKas.length === 0 ? (
                                <p className="text-center text-gray-500 py-8">Belum ada riwayat pembayaran.</p>
                            ) : (
                                <div className="overflow-auto">
                                    <table className="w-full text-sm">
                                        <thead>
                                            <tr className="border-b">
                                                <th className="px-4 py-3 text-left font-medium">Jenis</th>
                                                <th className="px-4 py-3 text-left font-medium">Bulan</th>
                                                <th className="px-4 py-3 text-left font-medium">Jumlah</th>
                                                <th className="px-4 py-3 text-left font-medium">Tanggal Bayar</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {paidInfaq.map(item => (
                                                <tr key={`infaq-${item.month}`} className="border-b">
                                                    <td className="px-4 py-3">
                                                        <span className="inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium bg-green-100 text-green-800">Infaq</span>
                                                    </td>
                                                    <td className="px-4 py-3">{item.monthLabel}</td>
                                                    <td className="px-4 py-3 font-semibold">{formatRupiah(item.amount)}</td>
                                                    <td className="px-4 py-3 text-gray-500">{item.paidAt ? new Date(item.paidAt).toLocaleDateString('id-ID') : '-'}</td>
                                                </tr>
                                            ))}
                                            {paidKas.map(item => (
                                                <tr key={`kas-${item.month}`} className="border-b">
                                                    <td className="px-4 py-3">
                                                        <span className="inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium bg-purple-100 text-purple-800">Kas</span>
                                                    </td>
                                                    <td className="px-4 py-3">{item.monthLabel}</td>
                                                    <td className="px-4 py-3 font-semibold">{formatRupiah(item.amount)}</td>
                                                    <td className="px-4 py-3 text-gray-500">{item.paidAt ? new Date(item.paidAt).toLocaleDateString('id-ID') : '-'}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </CardContent>
                    </Card>
                )}
            </main>

            {/* Bottom Payment Bar */}
            {activeTab === 'unpaid' && selectedItems.length > 0 && (
                <div className="fixed bottom-0 left-0 right-0 bg-white border-t shadow-lg p-4 z-50">
                    <div className="max-w-7xl mx-auto flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <ShoppingCart className="h-5 w-5 text-gray-500" />
                            <div>
                                <p className="text-sm text-gray-500">{selectedItems.length} item dipilih</p>
                                <p className="text-xl font-bold text-blue-600">{formatRupiah(calculateTotal())}</p>
                            </div>
                        </div>
                        <Button
                            onClick={handlePayment}
                            disabled={submitting}
                            size="lg"
                            className="min-w-[180px]"
                        >
                            {submitting ? (
                                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                            ) : (
                                <CreditCard className="mr-2 h-4 w-4" />
                            )}
                            Bayar Sekarang
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
}
