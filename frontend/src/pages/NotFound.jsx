import { Link } from 'react-router-dom';
import { Button } from '../components/ui/button';
import { FileQuestion } from 'lucide-react';

export default function NotFound() {
    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
            <FileQuestion className="h-24 w-24 text-gray-300 mb-6" />
            <h1 className="text-4xl font-bold tracking-tight text-gray-900 sm:text-5xl mb-4">404</h1>
            <h2 className="text-xl font-semibold text-gray-700 mb-2">Halaman Tidak Ditemukan</h2>
            <p className="text-gray-500 max-w-md mb-8">
                Maaf, halaman yang Anda cari tidak ada atau telah dipindahkan.
            </p>
            <Link to="/">
                <Button size="lg">Kembali ke Beranda</Button>
            </Link>
        </div>
    );
}
