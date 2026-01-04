import { useState, useEffect } from 'react';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../../components/ui/card';
import { Loader2, CheckCircle, XCircle, Search } from 'lucide-react';
import { Input } from '../../components/ui/input';

export default function StudentList() {
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [verifying, setVerifying] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');

    const fetchStudents = async () => {
        try {
            const response = await api.get('/admin/students/registered');
            setStudents(response.data.data);
        } catch (error) {
            console.error("Failed to fetch students", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStudents();
    }, []);

    const handleVerify = async (studentId) => {
        if (!window.confirm("Apakah Anda yakin ingin memverifikasi siswa ini?")) return;

        setVerifying(studentId);
        try {
            await api.post(`/admin/verify/${studentId}`);
            // Refresh data
            await fetchStudents();
            alert("Verifikasi berhasil!");
        } catch (error) {
            console.error("Verification failed", error);
            alert("Gagal memverifikasi siswa.");
        } finally {
            setVerifying(null);
        }
    };

    const filteredStudents = students.filter(student =>
        student.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        student.registrationNumber?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Dashboard Admin</h1>
                    <p className="text-gray-500">Kelola pendaftaran siswa baru.</p>
                </div>
                <Button onClick={fetchStudents} variant="outline" size="sm">Refresh Data</Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Data Pendaftar</CardTitle>
                    <CardDescription>Daftar siswa yang telah melakukan pendaftaran.</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="mb-4 flex items-center gap-2">
                        <Search className="h-4 w-4 text-gray-400" />
                        <Input
                            placeholder="Cari nama siswa..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="max-w-sm"
                        />
                    </div>

                    <div className="rounded-md border">
                        <div className="w-full overflow-auto">
                            <table className="w-full caption-bottom text-sm text-left">
                                <thead className="[&_tr]:border-b">
                                    <tr className="border-b transition-colors hover:bg-gray-100/50">
                                        <th className="h-12 px-4 align-middle font-medium text-gray-500">No. Registrasi</th>
                                        <th className="h-12 px-4 align-middle font-medium text-gray-500">Nama Siswa</th>
                                        <th className="h-12 px-4 align-middle font-medium text-gray-500">Jenis Kelamin</th>
                                        <th className="h-12 px-4 align-middle font-medium text-gray-500">Status</th>
                                        <th className="h-12 px-4 align-middle font-medium text-gray-500 text-right">Aksi</th>
                                    </tr>
                                </thead>
                                <tbody className="[&_tr:last-child]:border-0">
                                    {loading ? (
                                        <tr>
                                            <td colSpan={5} className="h-24 text-center">
                                                <Loader2 className="mx-auto h-6 w-6 animate-spin text-gray-400" />
                                            </td>
                                        </tr>
                                    ) : filteredStudents.length === 0 ? (
                                        <tr>
                                            <td colSpan={5} className="h-24 text-center text-gray-500">
                                                Belum ada data pendaftar.
                                            </td>
                                        </tr>
                                    ) : (
                                        filteredStudents.map((student) => (
                                            <tr key={student.id} className="border-b transition-colors hover:bg-gray-100/50">
                                                <td className="p-4 align-middle font-medium">{student.registrationNumber || '-'}</td>
                                                <td className="p-4 align-middle">{student.fullName}</td>
                                                <td className="p-4 align-middle">{student.gender}</td>
                                                <td className="p-4 align-middle">
                                                    <div className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 ${student.status === 'VERIFIED' || student.status === 'ACCEPTED'
                                                        ? 'bg-green-100 text-green-800'
                                                        : student.status === 'PAID'
                                                            ? 'bg-blue-100 text-blue-800'
                                                            : 'bg-yellow-100 text-yellow-800'
                                                        }`}>
                                                        {student.status}
                                                    </div>
                                                </td>
                                                <td className="p-4 align-middle text-right">
                                                    {student.status !== 'VERIFIED' && student.status !== 'ACCEPTED' && (
                                                        <Button
                                                            size="sm"
                                                            onClick={() => handleVerify(student.id)}
                                                            disabled={verifying === student.id}
                                                        >
                                                            {verifying === student.id ? <Loader2 className="h-4 w-4 animate-spin" /> : "Verifikasi"}
                                                        </Button>
                                                    )}
                                                </td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
}
