import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../lib/axios';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from '../../components/ui/card';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Loader2, ArrowLeft, Upload, FileText, CheckCircle, XCircle, Eye, AlertTriangle } from 'lucide-react';

export default function UploadDocuments() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [uploading, setUploading] = useState({ KK: false, AKTA: false, KTP: false });
    const [docStatus, setDocStatus] = useState({
        kkUploaded: false,
        aktaUploaded: false,
        ktpUploaded: false,
        kkPath: null,
        aktaPath: null,
        ktpPath: null
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        fetchStatus();
    }, []);

    const fetchStatus = async () => {
        try {
            const response = await api.get('/parent/documents/status');
            setDocStatus(response.data?.data || {});
        } catch (err) {
            console.error("Failed to fetch document status", err);
        } finally {
            setLoading(false);
        }
    };

    const handleUpload = async (docType, file) => {
        if (!file) return;

        // Validate file size (2MB)
        if (file.size > 2 * 1024 * 1024) {
            setError("Ukuran file maksimal 2MB");
            return;
        }

        // Validate file type
        const validTypes = ['image/jpeg', 'image/png', 'image/jpg', 'application/pdf'];
        if (!validTypes.includes(file.type)) {
            setError("Format file harus JPG, PNG, atau PDF");
            return;
        }

        setUploading(prev => ({ ...prev, [docType]: true }));
        setError('');
        setSuccess('');

        try {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('docType', docType);

            await api.post('/parent/documents/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });

            setSuccess(`Dokumen ${docType} berhasil diupload!`);
            fetchStatus();
        } catch (err) {
            console.error("Failed to upload document", err);
            setError(err.response?.data?.message || "Gagal mengupload dokumen.");
        } finally {
            setUploading(prev => ({ ...prev, [docType]: false }));
        }
    };

    const openDocument = (path) => {
        if (!path) return;
        window.open(`${import.meta.env.VITE_API_URL || ''}/api/documents/${path}`, '_blank');
    };

    const documents = [
        {
            type: 'KK',
            title: 'Kartu Keluarga',
            description: 'Upload scan/foto Kartu Keluarga',
            uploaded: docStatus.kkUploaded,
            path: docStatus.kkPath
        },
        {
            type: 'AKTA',
            title: 'Akta Kelahiran',
            description: 'Upload scan/foto Akta Kelahiran anak',
            uploaded: docStatus.aktaUploaded,
            path: docStatus.aktaPath
        },
        {
            type: 'KTP',
            title: 'KTP Orang Tua',
            description: 'Upload scan/foto KTP Ayah/Ibu',
            uploaded: docStatus.ktpUploaded,
            path: docStatus.ktpPath
        }
    ];

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
            </div>
        );
    }

    const allUploaded = docStatus.kkUploaded && docStatus.aktaUploaded && docStatus.ktpUploaded;

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm border-b sticky top-0 z-40">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center gap-4">
                    <Button variant="ghost" size="icon" onClick={() => navigate('/parent/dashboard')}>
                        <ArrowLeft className="h-5 w-5" />
                    </Button>
                    <div>
                        <h1 className="text-xl font-bold text-gray-900">Upload Dokumen</h1>
                        <p className="text-sm text-gray-500">Lengkapi dokumen persyaratan pendaftaran</p>
                    </div>
                </div>
            </header>

            <main className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
                {/* Status Banner */}
                {allUploaded ? (
                    <div className="bg-green-50 text-green-800 p-4 rounded-lg flex items-center gap-3">
                        <CheckCircle className="h-5 w-5" />
                        <span>Semua dokumen sudah lengkap!</span>
                    </div>
                ) : (
                    <div className="bg-yellow-50 text-yellow-800 p-4 rounded-lg flex items-center gap-3">
                        <AlertTriangle className="h-5 w-5" />
                        <span>Mohon lengkapi semua dokumen yang diperlukan.</span>
                    </div>
                )}

                {/* Messages */}
                {error && (
                    <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm">{error}</div>
                )}
                {success && (
                    <div className="bg-green-50 text-green-600 p-3 rounded-md text-sm">{success}</div>
                )}

                {/* Document Cards */}
                {documents.map((doc) => (
                    <Card key={doc.type} className={doc.uploaded ? 'border-green-200' : 'border-red-200'}>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <div>
                                <CardTitle className="text-lg flex items-center gap-2">
                                    <FileText className="h-5 w-5" />
                                    {doc.title}
                                </CardTitle>
                                <CardDescription>{doc.description}</CardDescription>
                            </div>
                            {doc.uploaded ? (
                                <span className="inline-flex items-center gap-1 text-sm text-green-600 bg-green-100 px-2 py-1 rounded-full">
                                    <CheckCircle className="h-4 w-4" /> Sudah Diupload
                                </span>
                            ) : (
                                <span className="inline-flex items-center gap-1 text-sm text-red-600 bg-red-100 px-2 py-1 rounded-full">
                                    <XCircle className="h-4 w-4" /> Belum Diupload
                                </span>
                            )}
                        </CardHeader>
                        <CardContent>
                            <div className="space-y-3">
                                <div className="space-y-2">
                                    <Label htmlFor={`file-${doc.type}`}>Pilih File (Max 2MB, PDF/JPG/PNG)</Label>
                                    <Input
                                        id={`file-${doc.type}`}
                                        type="file"
                                        accept=".pdf,.jpg,.jpeg,.png"
                                        onChange={(e) => {
                                            if (e.target.files?.[0]) {
                                                handleUpload(doc.type, e.target.files[0]);
                                            }
                                        }}
                                        disabled={uploading[doc.type]}
                                    />
                                </div>
                            </div>
                        </CardContent>
                        <CardFooter className="flex gap-2">
                            {uploading[doc.type] && (
                                <Button disabled>
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" /> Mengupload...
                                </Button>
                            )}
                            {doc.uploaded && doc.path && (
                                <Button variant="outline" onClick={() => openDocument(doc.path)}>
                                    <Eye className="mr-2 h-4 w-4" /> Lihat File
                                </Button>
                            )}
                        </CardFooter>
                    </Card>
                ))}
            </main>
        </div>
    );
}
