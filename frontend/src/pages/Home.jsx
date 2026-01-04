import { Link } from 'react-router-dom';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../components/ui/card';
import { BookOpen, Calendar, Users, Star } from 'lucide-react';

export default function Home() {
    return (
        <div className="space-y-12">
            {/* Hero Section */}
            <section className="text-center space-y-4 py-12 md:py-20">
                <h1 className="text-4xl md:text-6xl font-extrabold tracking-tight text-gray-900">
                    Selamat Datang di <br className="hidden sm:inline" />
                    <span className="text-blue-600">SPS Nurul Ikhlas</span>
                </h1>
                <p className="max-w-2xl mx-auto text-lg md:text-xl text-gray-600">
                    Membangun generasi cerdas, berakhlak mulia, dan kreatif untuk masa depan yang gemilang.
                </p>
                <div className="flex justify-center gap-4">
                    <Link to="/register">
                        <Button size="lg" className="px-8 text-lg">
                            Daftar Sekarang
                        </Button>
                    </Link>
                    <Link to="#info">
                        <Button variant="outline" size="lg" className="px-8 text-lg">
                            Pelajari Lebih Lanjut
                        </Button>
                    </Link>
                </div>

                {/* Registration Info Banner */}
                <div className="max-w-3xl mx-auto mt-8 p-4 bg-blue-50 border border-blue-200 rounded-xl flex items-center gap-4 justify-center">
                    <Calendar className="h-6 w-6 text-blue-600 flex-shrink-0" />
                    <div className="text-left">
                        <h3 className="font-semibold text-blue-900">Pendaftaran Tahun Ajaran 2025/2026 Dibuka!</h3>
                        <p className="text-sm text-blue-700">Segera daftarkan putra-putri Anda sebelum kuota terpenuhi.</p>
                    </div>
                </div>
            </section>

            {/* Features / Info Section */}
            <section id="info" className="grid gap-8 md:grid-cols-3">
                <Card className="border-none shadow-lg bg-white/50 backdrop-blur-sm hover:bg-white transition-all duration-300">
                    <CardHeader>
                        <BookOpen className="h-10 w-10 text-emerald-500 mb-2" />
                        <CardTitle>Kurikulum Unggulan</CardTitle>
                        <CardDescription>
                            Memadukan kurikulum nasional dengan nilai-nilai keislaman.
                        </CardDescription>
                    </CardHeader>
                </Card>
                <Card className="border-none shadow-lg bg-white/50 backdrop-blur-sm hover:bg-white transition-all duration-300">
                    <CardHeader>
                        <Users className="h-10 w-10 text-indigo-500 mb-2" />
                        <CardTitle>Pengajar Kompeten</CardTitle>
                        <CardDescription>
                            Dididik oleh guru-guru berpengalaman dan penuh kasih sayang.
                        </CardDescription>
                    </CardHeader>
                </Card>
                <Card className="border-none shadow-lg bg-white/50 backdrop-blur-sm hover:bg-white transition-all duration-300">
                    <CardHeader>
                        <Star className="h-10 w-10 text-amber-500 mb-2" />
                        <CardTitle>Fasilitas Lengkap</CardTitle>
                        <CardDescription>
                            Ruang belajar nyaman, area bermain aman, dan sarana edukatif.
                        </CardDescription>
                    </CardHeader>
                </Card>
            </section>

            {/* About Section */}
            <section className="bg-white rounded-2xl p-8 shadow-sm border border-gray-100">
                <h2 className="text-3xl font-bold text-center mb-8">Tentang Kami</h2>
                <div className="prose prose-lg mx-auto text-gray-600">
                    <p>
                        SPS Nurul Ikhlas adalah Satuan PAUD Sejenis yang berkomitmen untuk memberikan layanan pendidikan anak usia dini yang berkualitas.
                        Kami percaya bahwa setiap anak adalah unik dan memiliki potensi yang luar biasa. Melalui pendekatan yang menyenangkan dan berpusat pada anak,
                        kami membantu mereka tumbuh kembang secara optimal.
                    </p>
                    <p className="mt-4">
                        Visi kami adalah mewujudkan generasi yang beriman, bertakwa, cerdas, terampil, dan mandiri.
                    </p>
                </div>
            </section>
        </div>
    );
}
