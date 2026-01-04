import { Link } from 'react-router-dom';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '../components/ui/card';
import { CheckCircle } from 'lucide-react';

export default function PaymentSuccess() {
    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] py-12">
            <Card className="max-w-md w-full text-center">
                <CardHeader>
                    <div className="flex justify-center mb-4">
                        <CheckCircle className="h-20 w-20 text-green-500" />
                    </div>
                    <CardTitle className="text-2xl text-green-700">Pendaftaran Berhasil!</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                    <p className="text-gray-600">
                        Terima kasih telah melakukan pendaftaran dan pembayaran.
                        Data Anda telah kami terima dan sedang dalam proses verifikasi.
                    </p>
                    <div className="bg-blue-50 p-4 rounded-lg text-sm text-blue-800">
                        Silakan periksa <strong>Email</strong> Anda secara berkala untuk informasi status pendaftaran dan jadwal wawancara/observasi selanjutnya.
                    </div>
                </CardContent>
                <CardFooter className="justify-center">
                    <Link to="/">
                        <Button>Kembali ke Beranda</Button>
                    </Link>
                </CardFooter>
            </Card>
        </div>
    );
}
