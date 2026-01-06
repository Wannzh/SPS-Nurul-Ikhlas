import { useState, useEffect } from 'react';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../../components/ui/card';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Select } from '../../components/ui/select';
import { Loader2, Plus, Edit, Trash2, Shirt } from 'lucide-react';

export default function Uniforms() {
    const [uniforms, setUniforms] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        name: '',
        size: 'M',
        price: 0,
        stock: 0,
        description: ''
    });

    const fetchUniforms = async () => {
        try {
            const response = await api.get('/admin/uniforms');
            setUniforms(response.data?.data || []);
        } catch (error) {
            console.error("Failed to fetch uniforms", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUniforms();
    }, []);

    const handleOpenModal = (uniform = null) => {
        if (uniform) {
            setEditingId(uniform.id);
            setFormData({
                name: uniform.name,
                size: uniform.size,
                price: uniform.price,
                stock: uniform.stock,
                description: uniform.description || ''
            });
        } else {
            setEditingId(null);
            setFormData({
                name: '',
                size: 'M',
                price: 0,
                stock: 0,
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
                await api.put(`/admin/uniforms/${editingId}`, formData);
            } else {
                await api.post('/admin/uniforms', formData);
            }
            fetchUniforms();
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save uniform", error);
            alert("Gagal menyimpan data seragam.");
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Apakah Anda yakin ingin menghapus seragam ini?")) return;
        try {
            await api.delete(`/admin/uniforms/${id}`);
            fetchUniforms();
        } catch (error) {
            console.error("Failed to delete uniform", error);
            alert("Gagal menghapus seragam.");
        }
    };

    const formatRupiah = (amount) => {
        return new Intl.NumberFormat('id-ID', {
            style: 'currency',
            currency: 'IDR',
            minimumFractionDigits: 0
        }).format(amount);
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Inventaris Seragam</h1>
                    <p className="text-gray-500">Kelola stok dan harga seragam sekolah.</p>
                </div>
                <Button onClick={() => handleOpenModal()}>
                    <Plus className="mr-2 h-4 w-4" />
                    Tambah Seragam
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Shirt className="h-5 w-5" />
                        Daftar Seragam
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
                                        <th className="h-12 px-4 font-medium">Nama Seragam</th>
                                        <th className="h-12 px-4 font-medium">Ukuran</th>
                                        <th className="h-12 px-4 font-medium">Harga</th>
                                        <th className="h-12 px-4 font-medium">Stok</th>
                                        <th className="h-12 px-4 font-medium">Deskripsi</th>
                                        <th className="h-12 px-4 font-medium text-right">Aksi</th>
                                    </tr>
                                </thead>
                                <tbody className="[&_tr:last-child]:border-0">
                                    {uniforms.length > 0 ? uniforms.map((item) => (
                                        <tr key={item.id} className="border-b">
                                            <td className="p-4 font-medium">{item.name}</td>
                                            <td className="p-4">
                                                <span className="inline-flex items-center justify-center h-6 w-6 rounded-full bg-gray-100 text-xs font-bold text-gray-800">
                                                    {item.size}
                                                </span>
                                            </td>
                                            <td className="p-4">{formatRupiah(item.price)}</td>
                                            <td className="p-4">
                                                <span className={`font-medium ${item.stock < 5 ? 'text-red-600' : 'text-green-600'}`}>
                                                    {item.stock} unit
                                                </span>
                                            </td>
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
                                        <tr><td colSpan={6} className="p-4 text-center">Belum ada data.</td></tr>
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
                            <CardTitle>{editingId ? "Edit Seragam" : "Tambah Seragam"}</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <form onSubmit={handleSubmit} className="space-y-4">
                                <div className="space-y-2">
                                    <Label>Nama Seragam</Label>
                                    <Input value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} placeholder="Contoh: Seragam Olahraga" required />
                                </div>
                                <div className="grid grid-cols-2 gap-4">
                                    <div className="space-y-2">
                                        <Label>Ukuran</Label>
                                        <Select value={formData.size} onChange={e => setFormData({ ...formData, size: e.target.value })}>
                                            <option value="S">S</option>
                                            <option value="M">M</option>
                                            <option value="L">L</option>
                                            <option value="XL">XL</option>
                                            <option value="XXL">XXL</option>
                                        </Select>
                                    </div>
                                    <div className="space-y-2">
                                        <Label>Stok</Label>
                                        <Input type="number" value={formData.stock} onChange={e => setFormData({ ...formData, stock: Number(e.target.value) })} required />
                                    </div>
                                </div>
                                <div className="space-y-2">
                                    <Label>Harga (Rp)</Label>
                                    <Input type="number" value={formData.price} onChange={e => setFormData({ ...formData, price: Number(e.target.value) })} required />
                                </div>
                                <div className="space-y-2">
                                    <Label>Deskripsi</Label>
                                    <Input value={formData.description} onChange={e => setFormData({ ...formData, description: e.target.value })} placeholder="Keterangan..." />
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
