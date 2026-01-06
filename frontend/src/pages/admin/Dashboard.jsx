import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../../components/ui/card';
import { Users, Calendar, CheckCircle, Clock } from 'lucide-react';
import { useEffect, useState } from 'react';
import api from '../../lib/axios';

export default function Dashboard() {
    const [stats, setStats] = useState({
        totalStudents: 0,
        verifiedStudents: 0,
        pendingStudents: 0,
        activeAcademicYear: '-'
    });

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const [studentsRes, academicYearRes] = await Promise.all([
                    api.get('/admin/students/registered'),
                    api.get('/admin/academic-years/active')
                ]);

                const students = studentsRes.data?.data || [];
                const academicYear = academicYearRes.data?.data || null;

                setStats({
                    totalStudents: students.length,
                    verifiedStudents: students.filter(s => s.status === 'VERIFIED' || s.status === 'ACCEPTED').length,
                    pendingStudents: students.filter(s => s.status === 'REGISTERED').length,
                    activeAcademicYear: academicYear ? academicYear.name : 'Tidak ada'
                });
            } catch (error) {
                console.error("Failed to fetch dashboard stats", error);
            }
        };

        fetchStats();
    }, []);

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Dashboard Overview</h1>
                <p className="text-gray-500">Ringkasan data pendaftaran SPS Nurul Ikhlas.</p>
            </div>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Total Pendaftar</CardTitle>
                        <Users className="h-4 w-4 text-muted-foreground text-gray-400" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.totalStudents}</div>
                        <p className="text-xs text-muted-foreground text-gray-500">Siswa terdaftar</p>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Sudah Diverifikasi</CardTitle>
                        <CheckCircle className="h-4 w-4 text-muted-foreground text-green-500" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.verifiedStudents}</div>
                        <p className="text-xs text-muted-foreground text-gray-500">Siswa</p>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Menunggu Verifikasi</CardTitle>
                        <Clock className="h-4 w-4 text-muted-foreground text-yellow-500" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.pendingStudents}</div>
                        <p className="text-xs text-muted-foreground text-gray-500">Siswa</p>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Tahun Ajaran Aktif</CardTitle>
                        <Calendar className="h-4 w-4 text-muted-foreground text-blue-500" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-blue-600">{stats.activeAcademicYear}</div>
                        <p className="text-xs text-muted-foreground text-gray-500">Semester ini</p>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
