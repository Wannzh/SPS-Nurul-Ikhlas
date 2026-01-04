import { Link } from 'react-router-dom';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardFooter, CardHeader, CardTitle, CardDescription } from '../../components/ui/card';
import { XCircle, RefreshCcw, HelpCircle } from 'lucide-react';

export default function PaymentFailed() {
    return (
        <div className="min-h-[80vh] flex items-center justify-center p-4 bg-gradient-to-b from-red-50 to-white">
            <Card className="w-full max-w-md shadow-2xl border-t-8 border-t-red-500 animate-in shake duration-500">
                <CardHeader className="text-center pb-2">
                    <div className="mx-auto bg-red-100 p-4 rounded-full w-20 h-20 flex items-center justify-center mb-4">
                        <XCircle className="h-10 w-10 text-red-600" />
                    </div>
                    <CardTitle className="text-3xl font-bold text-red-700">Pembayaran Gagal</CardTitle>
                    <CardDescription className="text-lg text-gray-600 mt-2">
                        Maaf, kami tidak dapat memproses pembayaran Anda.
                    </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4 text-center">
                    <p className="text-gray-500 text-sm">
                        Hal ini mungkin terjadi karena gangguan koneksi, saldo tidak mencukupi, atau pembayaran kadaluarsa.
                    </p>
                    <div className="bg-red-50 p-4 rounded-lg border border-red-100 text-red-800 text-sm">
                        Silakan coba lakukan pembayaran ulang atau hubungi admin jika masalah berlanjut.
                    </div>
                </CardContent>
                <CardFooter className="flex flex-col space-y-3 pt-2">
                    <Link to="/register" className="w-full">
                        {/* Note: In a real app, this might link back to a specific invoice or retry endpoint */}
                        <Button className="w-full bg-red-600 hover:bg-red-700 h-12 text-base shadow-lg shadow-red-200">
                            <RefreshCcw className="mr-2 h-4 w-4" /> Coba Lagi
                        </Button>
                    </Link>
                    <Link to="/" className="w-full">
                        <Button variant="outline" className="w-full">
                            Kembali ke Beranda
                        </Button>
                    </Link>
                    <Button variant="link" className="w-full text-gray-400 text-xs">
                        <HelpCircle className="mr-1 h-3 w-3" /> Butuh bantuan? Hubungi Admin
                    </Button>
                </CardFooter>
            </Card>

            {/* Decorative Elements */}
            <div className="fixed top-0 left-0 w-full h-full pointer-events-none overflow-hidden -z-10">
                <div className="absolute bottom-1/4 right-1/4 w-64 h-64 bg-red-200/20 rounded-full blur-3xl mix-blend-multiply animate-blob"></div>
            </div>
        </div>
    );
}
