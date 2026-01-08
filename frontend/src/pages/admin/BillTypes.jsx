import { useState, useEffect } from 'react';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Select } from '../../components/ui/select';
import { Loader2, Plus, Edit, Trash2, Wallet } from 'lucide-react';

export default function BillTypes() {
    const [billTypes, setBillTypes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        category: 'INFAQ',
        amount: 0,
        description: ''
    });

    const fetchBillTypes = async () => {
        try {
            const response = await api.get('/admin/bill-types');
            setBillTypes(response.data?.data || []);
        } catch (error) {
            console.error("Failed to fetch bill types", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBillTypes();
    }, []);

    const handleOpenModal = (billType = null) => {
        if (billType) {
            setEditingId(billType.id);
            setFormData({
                category: billType.category || 'INFAQ',
                amount: billType.amount,
                description: billType.description || ''
            });
        } else {
            setEditingId(null);
            setFormData({
                category: 'INFAQ',
                amount: 0,
                description: ''
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
                await api.put(`/admin/bill-types/${editingId}`, formData);
            } else {
                await api.post('/admin/bill-types', formData);
            }
            fetchBillTypes();
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save bill type", error);
            alert("Gagal menyimpan jenis tagihan.");
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Apakah Anda yakin ingin menghapus jenis tagihan ini?")) return;
        try {
            await api.delete(`/admin/bill-types/${id}`);
            fetchBillTypes();
        } catch (error) {
            console.error("Failed to delete bill type", error);
            alert("Gagal menghapus jenis tagihan.");
        }
    };

    const formatRupiah = (amount) => {
        return new Intl.NumberFormat('id-ID', {
            style: 'currency',
            currency: 'IDR',
            minimumFractionDigits: 0
        }).format(amount);
    };

    const getCategoryBadge = (category) => {
        const styles = {
            INFAQ: 'bg-green-100 text-green-800',
            KAS: 'bg-purple-100 text-purple-800'
        };
        const labels = { INFAQ: 'Infaq', KAS: 'Kas' };
        return (
            <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold ${styles[category] || 'bg-gray-100'}`}>
                {labels[category] || category}
            </span>
        );
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Jenis Tagihan</h1>
                    <p className="text-gray-500">Kelola tagihan Infaq & Kas bulanan.</p>
                </div>
                <Button onClick={() => handleOpenModal()}>
                    <Plus className="mr-2 h-4 w-4" />
                    Tambah Tagihan
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Wallet className="h-5 w-5" />
                        Daftar Tagihan Bulanan
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
                                        <th className="h-12 px-4 font-medium">Nama Tagihan</th>
                                        <th className="h-12 px-4 font-medium">Kategori</th>
                                        <th className="h-12 px-4 font-medium">Jumlah</th>
                                        <th className="h-12 px-4 font-medium">Deskripsi</th>
                                        <th className="h-12 px-4 font-medium text-right">Aksi</th>
                                    </tr>
                                </thead>
                                <tbody className="[&_tr:last-child]:border-0">
                                    {billTypes.length > 0 ? billTypes.map((item) => (
                                        <tr key={item.id} className="border-b">
                                            <td className="p-4 font-medium">{item.name}</td>
                                            <td className="p-4">{getCategoryBadge(item.category)}</td>
                                            <td className="p-4 font-bold text-blue-600">{formatRupiah(item.amount)}</td>
                                            <td className="p-4 text-gray-500">{item.description || '-'}</td>
                                            <td className="p-4 text-right space-x-2">
                                                <Button variant="outline" size="sm" onClick={() => handleOpenModal(item)}>
                                                    <Edit className="h-4 w-4" />
                                                </Button>
                                                <Button variant="destructive" size="sm" onClick={() => handleDelete(item.id)}>
                                                    <Trash2 className="h-4 w-4" />
                                                </Button>
                                            </td>
                                        </tr>
                                    )) : (
                                        <tr><td colSpan={5} className="p-4 text-center">Belum ada data.</td></tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    )}
                </CardContent>
            </Card>

            {/* MODAL */}
            {isModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
                    <Card className="w-full max-w-lg">
                        <CardHeader>
                            <CardTitle>{editingId ? "Edit Tagihan" : "Tambah Tagihan"}</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <form onSubmit={handleSubmit} className="space-y-4">
                                <div className="space-y-2">
                                    <Label>Jenis Tagihan</Label>
                                    <Select
                                        value={formData.category}
                                        onChange={e => setFormData({ ...formData, category: e.target.value })}
                                    >
                                        <option value="INFAQ">Uang Infaq</option>
                                        <option value="KAS">Uang Kas</option>
                                    </Select>
                                </div>
                                <div className="space-y-2">
                                    <Label>Jumlah per Bulan (Rp)</Label>
                                    <Input
                                        type="number"
                                        value={formData.amount}
                                        onChange={e => setFormData({ ...formData, amount: Number(e.target.value) })}
                                        required
                                    />
                                </div>
                                <div className="space-y-2">
                                    <Label>Deskripsi (Opsional)</Label>
                                    <Input
                                        value={formData.description}
                                        onChange={e => setFormData({ ...formData, description: e.target.value })}
                                        placeholder="Keterangan tambahan..."
                                    />
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
