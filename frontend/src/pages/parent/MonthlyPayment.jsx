import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from '../../components/ui/card';
import { Loader2, ArrowLeft, CreditCard, CheckCircle, Clock, AlertTriangle, Wallet, AlertCircle } from 'lucide-react';

export default function MonthlyPayment() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState({ infaq: false, kas: false });
    const [error, setError] = useState('');
    const [status, setStatus] = useState(null);

    useEffect(() => {
        fetchStatus();
    }, []);

    const fetchStatus = async () => {
        try {
            const response = await api.get('/parent/finance/monthly-status');
            setStatus(response.data?.data);
        } catch (err) {
            console.error("Failed to fetch monthly status", err);
            setError("Gagal memuat status tagihan.");
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

    const handlePayment = async (category) => {
        setSubmitting(prev => ({ ...prev, [category.toLowerCase()]: true }));
        setError('');

        try {
            const response = await api.post('/parent/payments/monthly', {
                billCategory: category,
                numberOfMonths: 1
            });
            const transaction = response.data?.data;

            if (transaction?.xenditPaymentUrl) {
                window.location.href = transaction.xenditPaymentUrl;
            } else {
                fetchStatus();
            }
        } catch (err) {
            console.error("Failed to create payment", err);
            setError(err.response?.data?.message || "Gagal membuat pembayaran.");
        } finally {
            setSubmitting(prev => ({ ...prev, [category.toLowerCase()]: false }));
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
            </div>
        );
    }

    const renderBillCard = (category, title, fee, monthsPaid, monthsUnpaid, totalArrears, isDue, isCritical, isSubmitting) => (
        <Card className={`flex-1 ${isCritical ? 'border-red-300 bg-red-50' : ''}`}>
            <CardHeader>
                <div className="flex items-center justify-between">
                    <CardTitle className="text-lg">{title}</CardTitle>
                    {isCritical && (
                        <span className="flex items-center gap-1 text-xs font-semibold text-red-600">
                            <AlertCircle className="h-4 w-4" /> Kritis
                        </span>
                    )}
                </div>
                <CardDescription>Tagihan Bulanan</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
                <div className="flex justify-between items-center">
                    <span className="text-gray-500">Biaya per Bulan</span>
                    <span className="font-bold text-lg text-blue-600">{formatRupiah(fee)}</span>
                </div>
                <div className="flex justify-between items-center">
                    <span className="text-gray-500">Sudah Dibayar</span>
                    <span className="font-semibold text-green-600">{monthsPaid} bulan</span>
                </div>
                <div className="flex justify-between items-center">
                    <span className="text-gray-500">Status</span>
                    {monthsUnpaid > 0 ? (
                        <span className={`inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-semibold ${isCritical ? 'bg-red-100 text-red-800' : 'bg-yellow-100 text-yellow-800'
                            }`}>
                            <AlertTriangle className="h-3 w-3" /> Nunggak {monthsUnpaid} bln
                        </span>
                    ) : (
                        <span className="inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-semibold bg-green-100 text-green-800">
                            <CheckCircle className="h-3 w-3" /> Lunas
                        </span>
                    )}
                </div>
                {totalArrears > 0 && (
                    <div className="p-3 rounded-md bg-red-100 text-red-700 text-sm">
                        Total Tunggakan: <span className="font-bold">{formatRupiah(totalArrears)}</span>
                    </div>
                )}
            </CardContent>
            <CardFooter>
                <Button
                    onClick={() => handlePayment(category)}
                    disabled={isSubmitting || fee === 0}
                    className="w-full"
                    variant={isCritical ? "destructive" : "default"}
                >
                    {isSubmitting ? (
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    ) : (
                        <CreditCard className="mr-2 h-4 w-4" />
                    )}
                    Bayar 1 Bulan ({formatRupiah(fee)})
                </Button>
            </CardFooter>
        </Card>
    );

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm border-b sticky top-0 z-40">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center gap-4">
                    <Button variant="ghost" size="icon" onClick={() => navigate('/parent/dashboard')}>
                        <ArrowLeft className="h-5 w-5" />
                    </Button>
                    <div>
                        <h1 className="text-xl font-bold text-gray-900">Pembayaran Bulanan</h1>
                        <p className="text-sm text-gray-500">Bayar Infaq & Kas bulanan</p>
                    </div>
                </div>
            </header>

            <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
                {error && (
                    <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm">{error}</div>
                )}

                {/* Info Banner */}
                <Card className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white border-0">
                    <CardContent className="py-4">
                        <div className="flex items-center gap-3">
                            <Wallet className="h-8 w-8" />
                            <div>
                                <p className="text-blue-100">Total Bulan Aktif</p>
                                <p className="text-2xl font-bold">{status?.totalMonthsActive || 0} Bulan</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/* Bill Cards */}
                <div className="grid md:grid-cols-2 gap-6">
                    {renderBillCard(
                        'INFAQ',
                        'Uang Infaq',
                        status?.infaqMonthlyFee,
                        status?.infaqMonthsPaid,
                        status?.infaqMonthsUnpaid,
                        status?.infaqTotalArrears,
                        status?.infaqIsDue,
                        status?.infaqIsCritical,
                        submitting.infaq
                    )}
                    {renderBillCard(
                        'KAS',
                        'Uang Kas',
                        status?.kasMonthlyFee,
                        status?.kasMonthsPaid,
                        status?.kasMonthsUnpaid,
                        status?.kasTotalArrears,
                        status?.kasIsDue,
                        status?.kasIsCritical,
                        submitting.kas
                    )}
                </div>

                {/* Critical Warning */}
                {(status?.infaqIsCritical || status?.kasIsCritical) && (
                    <Card className="border-red-300 bg-red-50">
                        <CardContent className="py-4">
                            <div className="flex items-start gap-3">
                                <AlertCircle className="h-6 w-6 text-red-600 flex-shrink-0 mt-0.5" />
                                <div>
                                    <p className="font-semibold text-red-800">Peringatan Tunggakan Kritis</p>
                                    <p className="text-sm text-red-700">
                                        Anda memiliki tunggakan lebih dari 3 bulan. Harap segera menyelesaikan pembayaran untuk menghindari sanksi administrasi.
                                    </p>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                )}
            </main>
        </div>
    );
}
