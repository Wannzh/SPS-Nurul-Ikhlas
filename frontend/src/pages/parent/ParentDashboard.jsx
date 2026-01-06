import { useState, useEffect } from 'react';
import api from '../../lib/axios';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Loader2, GraduationCap, BadgeCheck, Calendar, Book, Edit, CalendarDays, User, ShoppingCart } from 'lucide-react';

export default function ParentDashboard() {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await api.get('/parents/my-data');
                setData(response.data?.data);
            } catch (err) {
                console.error("Failed to fetch parent data", err);
                setError(err.response?.data?.message || "Gagal memuat data. Silakan coba lagi.");
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
                <Card className="w-full max-w-md text-center">
                    <CardHeader>
                        <CardTitle className="text-red-600">Terjadi Kesalahan</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-gray-600">{error}</p>
                        <Button onClick={() => window.location.reload()} className="mt-4">
                            Coba Lagi
                        </Button>
                    </CardContent>
                </Card>
            </div>
        );
    }

    const statusColors = {
        REGISTERED: 'bg-yellow-100 text-yellow-800',
        PAID: 'bg-blue-100 text-blue-800',
        ACCEPTED: 'bg-green-100 text-green-800',
        VERIFIED: 'bg-green-100 text-green-800',
        REJECTED: 'bg-red-100 text-red-800',
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header/Navbar - simplified for parent */}
            <header className="bg-white shadow-sm border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <GraduationCap className="h-8 w-8 text-blue-600" />
                        <span className="text-xl font-bold text-gray-900">SPS Nurul Ikhlas</span>
                    </div>
                    <Button variant="outline" onClick={() => {
                        localStorage.removeItem('token');
                        localStorage.removeItem('user');
                        window.location.href = '/login';
                    }}>
                        Logout
                    </Button>
                </div>
            </header>

            <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
                {/* Welcome Banner */}
                <Card className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white border-0">
                    <CardHeader>
                        <CardDescription className="text-blue-100">Portal Wali Murid</CardDescription>
                        <CardTitle className="text-2xl">
                            Selamat Datang, Wali Murid dari <span className="font-bold">{data?.studentName || 'Anak Anda'}</span>
                        </CardTitle>
                    </CardHeader>
                </Card>

                {/* Status Card */}
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium text-gray-500">Status Pendaftaran</CardTitle>
                        <BadgeCheck className="h-5 w-5 text-gray-400" />
                    </CardHeader>
                    <CardContent>
                        <span className={`inline-flex items-center rounded-full px-3 py-1 text-sm font-semibold ${statusColors[data?.status] || 'bg-gray-100 text-gray-800'}`}>
                            {data?.status || '-'}
                        </span>
                        <p className="text-xs text-gray-500 mt-2">
                            {data?.status === 'ACCEPTED' || data?.status === 'VERIFIED'
                                ? 'Pendaftaran anak Anda telah diverifikasi oleh admin.'
                                : data?.status === 'PAID'
                                    ? 'Pembayaran telah diterima, menunggu verifikasi admin.'
                                    : data?.status === 'REGISTERED'
                                        ? 'Pendaftaran tercatat, menunggu pembayaran.'
                                        : 'Status tidak diketahui.'}
                        </p>
                    </CardContent>
                </Card>

                {/* Student Info Grid */}
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                    <Card>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium text-gray-500">NISN</CardTitle>
                            <User className="h-4 w-4 text-gray-400" />
                        </CardHeader>
                        <CardContent>
                            <div className="text-xl font-bold">{data?.nisn || 'Belum ada'}</div>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium text-gray-500">Tahun Ajaran</CardTitle>
                            <Calendar className="h-4 w-4 text-gray-400" />
                        </CardHeader>
                        <CardContent>
                            <div className="text-xl font-bold">{data?.academicYear || 'Belum ada'}</div>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium text-gray-500">Kelas</CardTitle>
                            <Book className="h-4 w-4 text-gray-400" />
                        </CardHeader>
                        <CardContent>
                            <div className="text-xl font-bold">{data?.currentClass || 'Belum ditetapkan'}</div>
                        </CardContent>
                    </Card>
                </div>

                {/* Action Buttons - Placeholder */}
                <Card>
                    <CardHeader>
                        <CardTitle>Aksi Cepat</CardTitle>
                        <CardDescription>Beberapa fitur yang dapat Anda akses.</CardDescription>
                    </CardHeader>
                    <CardContent className="flex flex-wrap gap-3">
                        <Button variant="outline" disabled>
                            <Edit className="mr-2 h-4 w-4" /> Edit Biodata
                        </Button>
                        <Button variant="outline" disabled>
                            <CalendarDays className="mr-2 h-4 w-4" /> Lihat Jadwal
                        </Button>
                        <Button onClick={() => window.location.href = '/parent/uniform-order'}>
                            <ShoppingCart className="mr-2 h-4 w-4" /> Pesan Seragam
                        </Button>
                    </CardContent>
                </Card>
            </main>
        </div>
    );
}
