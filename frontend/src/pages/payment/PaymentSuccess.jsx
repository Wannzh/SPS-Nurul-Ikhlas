import { Link } from 'react-router-dom';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardFooter, CardHeader, CardTitle, CardDescription } from '../../components/ui/card';
import { CheckCircle, ArrowRight, Download } from 'lucide-react';

export default function PaymentSuccess() {
    return (
        <div className="min-h-[80vh] flex items-center justify-center p-4 bg-gradient-to-b from-green-50 to-white">
            <Card className="w-full max-w-md shadow-2xl border-t-8 border-t-green-500 animate-in fade-in zoom-in duration-500">
                <CardHeader className="text-center pb-2">
                    <div className="mx-auto bg-green-100 p-4 rounded-full w-20 h-20 flex items-center justify-center mb-4">
                        <CheckCircle className="h-10 w-10 text-green-600" />
                    </div>
                    <CardTitle className="text-3xl font-bold text-green-700">Pembayaran Berhasil!</CardTitle>
                    <CardDescription className="text-lg text-gray-600 mt-2">
                        Terima kasih telah melakukan pembayaran.
                    </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4 text-center">
                    <p className="text-gray-500 text-sm">
                        Status pendaftaran Anda kini telah diperbarui. Silakan cek email Anda untuk bukti transaksi dan informasi selanjutnya.
                    </p>
                    <div className="bg-gray-50 p-4 rounded-lg border border-dashed border-gray-200">
                        <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide">Nomor Referensi</p>
                        <p className="text-lg font-mono font-bold text-gray-800 tracking-wider">#{(Math.random().toString(36).substr(2, 9)).toUpperCase()}</p>
                    </div>
                </CardContent>
                <CardFooter className="flex flex-col space-y-3 pt-2">
                    <Link to="/" className="w-full">
                        <Button className="w-full bg-green-600 hover:bg-green-700 h-12 text-base shadow-lg shadow-green-200">
                            Kembali ke Beranda <ArrowRight className="ml-2 h-4 w-4" />
                        </Button>
                    </Link>
                    <Button variant="ghost" className="w-full text-gray-500">
                        <Download className="mr-2 h-4 w-4" /> Unduh Bukti Pembayaran
                    </Button>
                </CardFooter>
            </Card>

            {/* Decorative Elements */}
            <div className="fixed top-0 left-0 w-full h-full pointer-events-none overflow-hidden -z-10">
                <div className="absolute top-1/4 left-1/4 w-64 h-64 bg-green-200/20 rounded-full blur-3xl mix-blend-multiply animate-blob"></div>
                <div className="absolute top-1/3 right-1/4 w-64 h-64 bg-blue-200/20 rounded-full blur-3xl mix-blend-multiply animate-blob animation-delay-2000"></div>
            </div>
        </div>
    );
}
