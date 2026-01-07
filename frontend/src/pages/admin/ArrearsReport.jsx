import { useState, useEffect } from 'react';
import api from '../../lib/axios';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Loader2, AlertTriangle, TrendingDown } from 'lucide-react';

export default function ArrearsReport() {
    const [arrears, setArrears] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchArrears();
    }, []);

    const fetchArrears = async () => {
        try {
            const response = await api.get('/admin/finance/arrears');
            setArrears(response.data?.data || []);
        } catch (error) {
            console.error("Failed to fetch arrears", error);
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

    const totalArrears = arrears.reduce((sum, s) => sum + (s.totalArrears || 0), 0);

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Laporan Tunggakan SPP</h1>
                <p className="text-gray-500">Daftar siswa dengan tunggakan pembayaran SPP.</p>
            </div>

            {/* Summary Card */}
            <div className="grid gap-4 md:grid-cols-3">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Total Siswa Menunggak</CardTitle>
                        <AlertTriangle className="h-4 w-4 text-red-500" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-red-600">{arrears.length}</div>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Total Tunggakan</CardTitle>
                        <TrendingDown className="h-4 w-4 text-red-500" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-red-600">{formatRupiah(totalArrears)}</div>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Rata-rata Tunggakan</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">
                            {arrears.length > 0 ? formatRupiah(totalArrears / arrears.length) : '-'}
                        </div>
                    </CardContent>
                </Card>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Detail Tunggakan per Siswa</CardTitle>
                </CardHeader>
                <CardContent>
                    {loading ? (
                        <div className="flex justify-center p-8"><Loader2 className="animate-spin" /></div>
                    ) : (
                        <div className="relative w-full overflow-auto">
                            <table className="w-full caption-bottom text-sm text-left">
                                <thead className="[&_tr]:border-b">
                                    <tr className="border-b">
                                        <th className="h-12 px-4 font-medium">Nama Siswa</th>
                                        <th className="h-12 px-4 font-medium">Kelas</th>
                                        <th className="h-12 px-4 font-medium">Bulan Tertunggak</th>
                                        <th className="h-12 px-4 font-medium">SPP per Bulan</th>
                                        <th className="h-12 px-4 font-medium">Total Tunggakan</th>
                                    </tr>
                                </thead>
                                <tbody className="[&_tr:last-child]:border-0">
                                    {arrears.length > 0 ? arrears.map((item) => (
                                        <tr key={item.studentId} className="border-b">
                                            <td className="p-4 font-medium">{item.studentName}</td>
                                            <td className="p-4">{item.className}</td>
                                            <td className="p-4">
                                                <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold ${item.monthsUnpaid > 3 ? 'bg-red-100 text-red-800' : 'bg-yellow-100 text-yellow-800'
                                                    }`}>
                                                    {item.monthsUnpaid} bulan
                                                </span>
                                            </td>
                                            <td className="p-4">{formatRupiah(item.sppAmount)}</td>
                                            <td className="p-4 font-bold text-red-600">{formatRupiah(item.totalArrears)}</td>
                                        </tr>
                                    )) : (
                                        <tr><td colSpan={5} className="p-4 text-center text-green-600">Tidak ada tunggakan.</td></tr>
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
