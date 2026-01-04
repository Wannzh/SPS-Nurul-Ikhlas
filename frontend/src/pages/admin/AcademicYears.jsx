import { useState, useEffect } from 'react';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../../components/ui/card';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Select } from '../../components/ui/select';
import { Loader2, Plus, Edit, Trash2 } from 'lucide-react';

export default function AcademicYears() {
    const [academicYears, setAcademicYears] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        name: '',
        status: 'OPEN',
        registrationFee: 0
    });

    const fetchAcademicYears = async () => {
        try {
            const response = await api.get('/admin/academic-years');
            setAcademicYears(response.data.data);
        } catch (error) {
            console.error("Failed to fetch academic years", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAcademicYears();
    }, []);

    const handleOpenModal = (year = null) => {
        if (year) {
            setEditingId(year.id);
            setFormData({
                name: year.name,
                status: year.status,
                registrationFee: year.registrationFee
            });
        } else {
            setEditingId(null);
            setFormData({
                name: '',
                status: 'OPEN',
                registrationFee: 0
            });
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setEditingId(null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingId) {
                await api.put(`/admin/academic-years/${editingId}`, formData);
            } else {
                await api.post('/admin/academic-years', formData);
            }
            fetchAcademicYears();
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save academic year", error);
            alert("Gagal menyimpan data tahun ajaran.");
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Apakah Anda yakin ingin menghapus tahun ajaran ini?")) return;
        try {
            await api.delete(`/admin/academic-years/${id}`);
            fetchAcademicYears();
        } catch (error) {
            console.error("Failed to delete academic year", error);
            alert("Gagal menghapus tahun ajaran.");
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Kelola Tahun Ajaran</h1>
                    <p className="text-gray-500">Buat dan atur tahun ajaran baru.</p>
                </div>
                <Button onClick={() => handleOpenModal()}>
                    <Plus className="mr-2 h-4 w-4" />
                    Tambah Tahun Ajaran
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Daftar Tahun Ajaran</CardTitle>
                </CardHeader>
                <CardContent>
                    {loading ? (
                        <div className="flex justify-center p-8"><Loader2 className="animate-spin" /></div>
                    ) : (
                        <div className="relative w-full overflow-auto">
                            <table className="w-full caption-bottom text-sm text-left">
                                <thead className="[&_tr]:border-b">
                                    <tr className="border-b">
                                        <th className="h-12 px-4 font-medium">Nama Tahun Ajaran</th>
                                        <th className="h-12 px-4 font-medium">Biaya Pendaftaran</th>
                                        <th className="h-12 px-4 font-medium">Status</th>
                                        <th className="h-12 px-4 font-medium text-right">Aksi</th>
                                    </tr>
                                </thead>
                                <tbody className="[&_tr:last-child]:border-0">
                                    {academicYears.length > 0 ? academicYears.map((year) => (
                                        <tr key={year.id} className="border-b">
                                            <td className="p-4 font-medium">{year.name}</td>
                                            <td className="p-4">Rp {year.registrationFee.toLocaleString()}</td>
                                            <td className="p-4">
                                                <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold ${year.status === 'OPEN' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'}`}>
                                                    {year.status}
                                                </span>
                                            </td>
                                            <td className="p-4 text-right space-x-2">
                                                <Button variant="outline" size="sm" onClick={() => handleOpenModal(year)}>
                                                    <Edit className="h-4 w-4" />
                                                </Button>
                                                <Button variant="destructive" size="sm" onClick={() => handleDelete(year.id)}>
                                                    <Trash2 className="h-4 w-4" />
                                                </Button>
                                            </td>
                                        </tr>
                                    )) : (
                                        <tr><td colSpan={4} className="p-4 text-center">Belum ada data.</td></tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    )}
                </CardContent>
            </Card>

            {/* MODAL (Simple overlay for now) */}
            {isModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
                    <Card className="w-full max-w-lg mx-4">
                        <CardHeader>
                            <CardTitle>{editingId ? "Edit Tahun Ajaran" : "Tambah Tahun Ajaran"}</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <form onSubmit={handleSubmit} className="space-y-4">
                                <div className="space-y-2">
                                    <Label>Nama Tahun Ajaran</Label>
                                    <Input value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} placeholder="Contoh: 2025/2026" required />
                                </div>
                                <div className="space-y-2">
                                    <Label>Biaya Pendaftaran</Label>
                                    <Input type="number" value={formData.registrationFee} onChange={e => setFormData({ ...formData, registrationFee: Number(e.target.value) })} required />
                                </div>
                                <div className="space-y-2">
                                    <Label>Status</Label>
                                    <Select value={formData.status} onChange={e => setFormData({ ...formData, status: e.target.value })}>
                                        <option value="OPEN">OPEN</option>
                                        <option value="CLOSED">CLOSED</option>
                                    </Select>
                                </div>
                                <div className="flex justify-end gap-2 mt-6">
                                    <Button type="button" variant="ghost" onClick={handleCloseModal}>Batal</Button>
                                    <Button type="submit">Simpan</Button>
                                </div>
                            </form>
                        </CardContent>
                    </Card>
                </div>
            )}
        </div>
    );
}
