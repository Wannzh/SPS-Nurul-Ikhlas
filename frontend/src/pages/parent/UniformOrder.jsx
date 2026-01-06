import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from '../../components/ui/card';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Loader2, ShoppingCart, Plus, Minus, Trash2, ArrowLeft, CheckCircle } from 'lucide-react';

export default function UniformOrder() {
    const navigate = useNavigate();
    const [uniforms, setUniforms] = useState([]);
    const [cart, setCart] = useState([]);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchUniforms = async () => {
            try {
                const response = await api.get('/parent/uniforms');
                setUniforms(response.data?.data || []);
            } catch (err) {
                console.error("Failed to fetch uniforms", err);
                setError("Gagal memuat daftar seragam.");
            } finally {
                setLoading(false);
            }
        };
        fetchUniforms();
    }, []);

    const addToCart = (uniform) => {
        const existingItem = cart.find(item => item.uniformId === uniform.id);
        if (existingItem) {
            if (existingItem.quantity < uniform.stock) {
                setCart(cart.map(item =>
                    item.uniformId === uniform.id
                        ? { ...item, quantity: item.quantity + 1 }
                        : item
                ));
            }
        } else {
            setCart([...cart, {
                uniformId: uniform.id,
                name: uniform.name,
                size: uniform.size,
                price: uniform.price,
                stock: uniform.stock,
                quantity: 1
            }]);
        }
    };

    const updateQuantity = (uniformId, delta) => {
        setCart(cart.map(item => {
            if (item.uniformId === uniformId) {
                const newQty = item.quantity + delta;
                if (newQty <= 0) return null;
                if (newQty > item.stock) return item;
                return { ...item, quantity: newQty };
            }
            return item;
        }).filter(Boolean));
    };

    const removeFromCart = (uniformId) => {
        setCart(cart.filter(item => item.uniformId !== uniformId));
    };

    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

    const formatRupiah = (amount) => {
        return new Intl.NumberFormat('id-ID', {
            style: 'currency',
            currency: 'IDR',
            minimumFractionDigits: 0
        }).format(amount);
    };

    const handleSubmit = async () => {
        if (cart.length === 0) return;
        setSubmitting(true);
        setError('');

        try {
            await api.post('/parent/orders/uniform', {
                items: cart.map(item => ({
                    uniformId: item.uniformId,
                    quantity: item.quantity
                }))
            });
            navigate('/parent/dashboard');
        } catch (err) {
            console.error("Failed to create order", err);
            setError(err.response?.data?.message || "Gagal membuat pesanan.");
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 pb-28">
            {/* Header */}
            <header className="bg-white shadow-sm border-b sticky top-0 z-40">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center gap-4">
                    <Button variant="ghost" size="icon" onClick={() => navigate('/parent/dashboard')}>
                        <ArrowLeft className="h-5 w-5" />
                    </Button>
                    <div>
                        <h1 className="text-xl font-bold text-gray-900">Pesan Seragam</h1>
                        <p className="text-sm text-gray-500">Pilih seragam yang dibutuhkan</p>
                    </div>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                {error && (
                    <div className="mb-4 bg-red-50 text-red-600 p-3 rounded-md text-sm">{error}</div>
                )}

                {/* Uniform Grid */}
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                    {uniforms.map((uniform) => {
                        const inCart = cart.find(item => item.uniformId === uniform.id);
                        return (
                            <Card key={uniform.id} className="overflow-hidden">
                                <div className="h-32 bg-gradient-to-br from-blue-100 to-indigo-100 flex items-center justify-center">
                                    {uniform.imageUrl ? (
                                        <img src={uniform.imageUrl} alt={uniform.name} className="h-full w-full object-cover" />
                                    ) : (
                                        <ShoppingCart className="h-10 w-10 text-blue-300" />
                                    )}
                                </div>
                                <CardContent className="p-4 space-y-2">
                                    <div className="flex justify-between items-start">
                                        <div>
                                            <h3 className="font-semibold text-gray-900">{uniform.name}</h3>
                                            <span className="inline-flex items-center justify-center h-5 px-2 rounded-full bg-gray-100 text-xs font-bold text-gray-700">
                                                {uniform.size}
                                            </span>
                                        </div>
                                        <span className={`text-xs ${uniform.stock < 5 ? 'text-red-500' : 'text-green-600'}`}>
                                            Stok: {uniform.stock}
                                        </span>
                                    </div>
                                    <p className="text-lg font-bold text-blue-600">{formatRupiah(uniform.price)}</p>
                                    {inCart ? (
                                        <div className="flex items-center justify-between gap-2 pt-2">
                                            <Button variant="outline" size="icon" onClick={() => updateQuantity(uniform.id, -1)}>
                                                <Minus className="h-4 w-4" />
                                            </Button>
                                            <span className="font-medium">{inCart.quantity}</span>
                                            <Button variant="outline" size="icon" onClick={() => updateQuantity(uniform.id, 1)} disabled={inCart.quantity >= uniform.stock}>
                                                <Plus className="h-4 w-4" />
                                            </Button>
                                            <Button variant="destructive" size="icon" onClick={() => removeFromCart(uniform.id)}>
                                                <Trash2 className="h-4 w-4" />
                                            </Button>
                                        </div>
                                    ) : (
                                        <Button className="w-full mt-2" onClick={() => addToCart(uniform)} disabled={uniform.stock === 0}>
                                            <Plus className="mr-2 h-4 w-4" /> Tambah
                                        </Button>
                                    )}
                                </CardContent>
                            </Card>
                        );
                    })}
                </div>

                {uniforms.length === 0 && (
                    <div className="text-center py-12 text-gray-500">
                        Tidak ada seragam tersedia saat ini.
                    </div>
                )}
            </main>

            {/* Bottom Summary Bar */}
            {cart.length > 0 && (
                <div className="fixed bottom-0 left-0 right-0 bg-white border-t shadow-lg z-50">
                    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center justify-between">
                        <div>
                            <p className="text-sm text-gray-500">Total {totalItems} item</p>
                            <p className="text-xl font-bold text-blue-600">{formatRupiah(totalPrice)}</p>
                        </div>
                        <Button onClick={handleSubmit} disabled={submitting} size="lg">
                            {submitting ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <CheckCircle className="mr-2 h-4 w-4" />}
                            Buat Pesanan
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
}
