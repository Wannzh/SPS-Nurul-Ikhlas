import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from '../../components/ui/card';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Loader2, ArrowLeft, CreditCard, CheckCircle, Clock, AlertTriangle, Wallet } from 'lucide-react';

export default function SppPayment() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [sppInfo, setSppInfo] = useState(null);
    const [history, setHistory] = useState([]);
    const [months, setMonths] = useState(1);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const [infoRes, historyRes] = await Promise.all([
                api.get('/parent/finance/spp-info'),
                api.get('/parent/payments/spp-history')
            ]);
            setSppInfo(infoRes.data?.data);
            setHistory(historyRes.data?.data || []);
        } catch (err) {
            console.error("Failed to fetch SPP data", err);
            setError("Gagal memuat data SPP.");
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

    const handlePayment = async () => {
        if (months < 1) return;

        setSubmitting(true);
        setError('');

        try {
            const response = await api.post('/parent/payments/spp', { months });
            const transaction = response.data?.data;

            if (transaction?.xenditPaymentUrl) {
                window.location.href = transaction.xenditPaymentUrl;
            } else {
                fetchData();
            }
        } catch (err) {
            console.error("Failed to create SPP payment", err);
            setError(err.response?.data?.message || "Gagal membuat pembayaran SPP.");
        } finally {
            setSubmitting(false);
        }
    };

    const getStatusBadge = (status) => {
        const styles = {
            PENDING: { color: 'bg-yellow-100 text-yellow-800', icon: Clock },
            PAID: { color: 'bg-green-100 text-green-800', icon: CheckCircle },
            EXPIRED: { color: 'bg-gray-100 text-gray-800', icon: AlertTriangle },
        };
        const s = styles[status] || styles.PENDING;
        const Icon = s.icon;
        return (
            <span className={`inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-semibold ${s.color}`}>
                <Icon className="h-3 w-3" /> {status}
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

    const calculatedAmount = (sppInfo?.monthlyFee || 0) * months;

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm border-b sticky top-0 z-40">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center gap-4">
                    <Button variant="ghost" size="icon" onClick={() => navigate('/parent/dashboard')}>
                        <ArrowLeft className="h-5 w-5" />
                    </Button>
                    <div>
                        <h1 className="text-xl font-bold text-gray-900">Pembayaran SPP</h1>
                        <p className="text-sm text-gray-500">Bayar SPP bulanan dengan metode online</p>
                    </div>
                </div>
            </header>

            <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
                {error && (
                    <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm">{error}</div>
                )}

                {/* Info Card */}
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <Wallet className="h-5 w-5" />
                            Informasi SPP
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                            <div>
                                <p className="text-sm text-gray-500">Biaya per Bulan</p>
                                <p className="text-xl font-bold text-blue-600">{formatRupiah(sppInfo?.monthlyFee)}</p>
                            </div>
                            <div>
                                <p className="text-sm text-gray-500">Bulan Aktif</p>
                                <p className="text-xl font-bold">{sppInfo?.totalMonthsActive || 0} bln</p>
                            </div>
                            <div>
                                <p className="text-sm text-gray-500">Sudah Dibayar</p>
                                <p className="text-xl font-bold text-green-600">{sppInfo?.totalMonthsPaid || 0} bln</p>
                            </div>
                            <div>
                                <p className="text-sm text-gray-500">Status</p>
                                {sppInfo?.monthsUnpaidCount > 0 ? (
                                    <span className="inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-sm font-semibold bg-red-100 text-red-800">
                                        <AlertTriangle className="h-4 w-4" /> Nunggak {sppInfo.monthsUnpaidCount} bln
                                    </span>
                                ) : (
                                    <span className="inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-sm font-semibold bg-green-100 text-green-800">
                                        <CheckCircle className="h-4 w-4" /> Lunas
                                    </span>
                                )}
                            </div>
                        </div>
                        {sppInfo?.totalArrears > 0 && (
                            <div className="mt-4 p-3 rounded-md bg-red-50 text-red-700">
                                Total Tunggakan: <span className="font-bold">{formatRupiah(sppInfo.totalArrears)}</span>
                            </div>
                        )}
                    </CardContent>
                </Card>

                {/* Payment Card */}
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <CreditCard className="h-5 w-5" />
                            Bayar SPP
                        </CardTitle>
                        <CardDescription>Pilih jumlah bulan yang ingin dibayar</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-2">
                            <Label>Jumlah Bulan</Label>
                            <Input
                                type="number"
                                min={1}
                                max={12}
                                value={months}
                                onChange={(e) => setMonths(Math.max(1, parseInt(e.target.value) || 1))}
                            />
                        </div>
                        <div className="p-4 rounded-md bg-blue-50">
                            <p className="text-sm text-gray-600">Total Pembayaran:</p>
                            <p className="text-2xl font-bold text-blue-600">{formatRupiah(calculatedAmount)}</p>
                            <p className="text-xs text-gray-500">{months} bulan × {formatRupiah(sppInfo?.monthlyFee)}</p>
                        </div>
                    </CardContent>
                    <CardFooter>
                        <Button onClick={handlePayment} disabled={submitting} className="w-full">
                            {submitting ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <CreditCard className="mr-2 h-4 w-4" />}
                            Bayar via Xendit
                        </Button>
                    </CardFooter>
                </Card>

                {/* History Card */}
                <Card>
                    <CardHeader>
                        <CardTitle>Riwayat Pembayaran SPP</CardTitle>
                    </CardHeader>
                    <CardContent>
                        {history.length === 0 ? (
                            <p className="text-center text-gray-500 py-4">Belum ada riwayat pembayaran.</p>
                        ) : (
                            <div className="relative w-full overflow-auto">
                                <table className="w-full caption-bottom text-sm">
                                    <thead>
                                        <tr className="border-b">
                                            <th className="h-12 px-4 text-left font-medium">Tanggal</th>
                                            <th className="h-12 px-4 text-left font-medium">Jumlah</th>
                                            <th className="h-12 px-4 text-left font-medium">Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {history.map((tx) => (
                                            <tr key={tx.id} className="border-b">
                                                <td className="p-4">
                                                    {new Date(tx.createdAt).toLocaleDateString('id-ID', {
                                                        day: 'numeric', month: 'short', year: 'numeric'
                                                    })}
                                                </td>
                                                <td className="p-4 font-bold text-blue-600">{formatRupiah(tx.amount)}</td>
                                                <td className="p-4">{getStatusBadge(tx.status)}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </CardContent>
                </Card>
            </main>
        </div>
    );
}
