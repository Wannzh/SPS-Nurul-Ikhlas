import { Link } from 'react-router-dom';
import { Button } from '../components/ui/button';
import { ShieldAlert } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function Unauthorized() {
    const navigate = useNavigate();

    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
            <ShieldAlert className="h-24 w-24 text-red-100 mb-6" />
            <h1 className="text-4xl font-bold tracking-tight text-red-600 sm:text-5xl mb-4">403</h1>
            <h2 className="text-xl font-semibold text-gray-700 mb-2">Akses Ditolak</h2>
            <p className="text-gray-500 max-w-md mb-8">
                Maaf, Anda tidak memiliki izin untuk mengakses halaman ini.
            </p>
            <div className="flex gap-4">
                <Button variant="outline" onClick={() => navigate(-1)}>Kembali</Button>
                <Link to="/">
                    <Button>Ke Beranda</Button>
                </Link>
            </div>
        </div>
    );
}
