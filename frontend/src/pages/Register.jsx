import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../lib/axios';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Select } from '../components/ui/select';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../components/ui/card';
import { Loader2, Check, ChevronRight, ChevronLeft } from 'lucide-react';

export default function Register() {
    const navigate = useNavigate();
    const [step, setStep] = useState(1);
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        // Child Info
        childFullName: '',
        birthPlace: '',
        birthDate: '',
        gender: 'MALE',
        religion: 'ISLAM',
        // Parent Info
        fatherName: '',
        fatherJob: '',
        motherName: '',
        motherJob: '',
        phoneNumber: '',
        email: '',
        // Address
        address: '',
        provinceId: '',
        regencyId: '',
        districtId: '',
        villageId: '',
        // Agreement
        isAgreed: false
    });

    // Address Data States
    const [provinces, setProvinces] = useState([]);
    const [regencies, setRegencies] = useState([]);
    const [districts, setDistricts] = useState([]);
    const [villages, setVillages] = useState([]);

    // Fetch Provinces on Mount
    useEffect(() => {
        const fetchProvinces = async () => {
            try {
                const res = await api.get('/wilayah/provinces');
                setProvinces(res.data.data);
            } catch (err) {
                console.error("Failed to fetch provinces", err);
            }
        };
        fetchProvinces();
    }, []);

    // Fetch Regencies when Province changes
    useEffect(() => {
        if (formData.provinceId) {
            const fetchRegencies = async () => {
                try {
                    const res = await api.get(`/wilayah/regencies/${formData.provinceId}`);
                    setRegencies(res.data.data);
                    setDistricts([]);
                    setVillages([]);
                } catch (err) {
                    console.error("Failed to fetch regencies", err);
                }
            };
            fetchRegencies();
        }
    }, [formData.provinceId]);

    // Fetch Districts when Regency changes
    useEffect(() => {
        if (formData.regencyId) {
            const fetchDistricts = async () => {
                try {
                    const res = await api.get(`/wilayah/districts/${formData.regencyId}`);
                    setDistricts(res.data.data);
                    setVillages([]);
                } catch (err) {
                    console.error("Failed to fetch districts", err);
                }
            };
            fetchDistricts();
        }
    }, [formData.regencyId]);

    // Fetch Villages when District changes
    useEffect(() => {
        if (formData.districtId) {
            const fetchVillages = async () => {
                try {
                    const res = await api.get(`/wilayah/villages/${formData.districtId}`);
                    setVillages(res.data.data);
                } catch (err) {
                    console.error("Failed to fetch villages", err);
                }
            };
            fetchVillages();
        }
    }, [formData.districtId]);


    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async () => {
        setLoading(true);
        try {
            const response = await api.post('/auth/register', formData);
            const { invoiceUrl } = response.data.data;

            // Redirect to Payment (Xendit)
            if (invoiceUrl) {
                window.location.href = invoiceUrl;
            } else {
                // Fallback if no invoice URL (should not happen usually)
                navigate('/payment/success');
            }
        } catch (error) {
            console.error("Registration failed", error);
            alert(error.response?.data?.message || "Pendaftaran gagal. Silakan coba lagi.");
        } finally {
            setLoading(false);
        }
    };

    const nextStep = () => setStep(prev => Math.min(prev + 1, 4));
    const prevStep = () => setStep(prev => Math.max(prev - 1, 1));

    return (
        <div className="max-w-2xl mx-auto py-8">
            <Card>
                <CardHeader>
                    <CardTitle>Formulir Pendaftaran Siswa Baru</CardTitle>
                    <CardDescription>Lengkapi data berikut untuk mendaftar. Langkah {step} dari 4.</CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">

                    {/* STEP 1: IDENTITAS ANAK */}
                    {step === 1 && (
                        <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-300">
                            <h3 className="text-lg font-medium">Identitas Anak</h3>
                            <div className="space-y-2">
                                <Label htmlFor="childFullName">Nama Lengkap Anak</Label>
                                <Input name="childFullName" id="childFullName" value={formData.childFullName} onChange={handleChange} placeholder="Contoh: Muhammad Alatih" required />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div className="space-y-2">
                                    <Label htmlFor="birthPlace">Tempat Lahir</Label>
                                    <Input name="birthPlace" id="birthPlace" value={formData.birthPlace} onChange={handleChange} placeholder="Contoh: Bandung" required />
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="birthDate">Tanggal Lahir</Label>
                                    <Input type="date" name="birthDate" id="birthDate" value={formData.birthDate} onChange={handleChange} required />
                                </div>
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div className="space-y-2">
                                    <Label htmlFor="gender">Jenis Kelamin</Label>
                                    <Select name="gender" id="gender" value={formData.gender} onChange={handleChange}>
                                        <option value="MALE">Laki-laki</option>
                                        <option value="FEMALE">Perempuan</option>
                                    </Select>
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="religion">Agama</Label>
                                    <Select name="religion" id="religion" value={formData.religion} onChange={handleChange}>
                                        <option value="ISLAM">Islam</option>
                                        <option value="KRISTEN">Kristen</option>
                                        <option value="KATOLIK">Katolik</option>
                                        <option value="HINDU">Hindu</option>
                                        <option value="BUDDHA">Buddha</option>
                                        <option value="KONGHUCU">Konghucu</option>
                                    </Select>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* STEP 2: DATA ORANG TUA */}
                    {step === 2 && (
                        <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-300">
                            <h3 className="text-lg font-medium">Data Orang Tua</h3>

                            <div className="space-y-2">
                                <Label htmlFor="fatherName">Nama Ayah</Label>
                                <Input name="fatherName" id="fatherName" value={formData.fatherName} onChange={handleChange} required />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="fatherJob">Pekerjaan Ayah</Label>
                                <Input name="fatherJob" id="fatherJob" value={formData.fatherJob} onChange={handleChange} placeholder="Contoh: Wiraswasta" />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="motherName">Nama Ibu</Label>
                                <Input name="motherName" id="motherName" value={formData.motherName} onChange={handleChange} required />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="motherJob">Pekerjaan Ibu</Label>
                                <Input name="motherJob" id="motherJob" value={formData.motherJob} onChange={handleChange} placeholder="Contoh: Ibu Rumah Tangga" />
                            </div>

                            <div className="grid grid-cols-2 gap-4 border-t pt-4 mt-2">
                                <div className="space-y-2">
                                    <Label htmlFor="phoneNumber">No. Telepon / WA</Label>
                                    <Input name="phoneNumber" id="phoneNumber" value={formData.phoneNumber} onChange={handleChange} placeholder="08xxxxxxxxxx" required />
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="email">Email Aktif</Label>
                                    <Input type="email" name="email" id="email" value={formData.email} onChange={handleChange} placeholder="email@contoh.com" required />
                                    <p className="text-xs text-muted-foreground text-gray-500">Info pembayaran akan dikirim ke email ini.</p>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* STEP 3: ALAMAT */}
                    {step === 3 && (
                        <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-300">
                            <h3 className="text-lg font-medium">Alamat Domisili</h3>
                            <div className="space-y-2">
                                <Label>Provinsi</Label>
                                <Select name="provinceId" value={formData.provinceId} onChange={handleChange}>
                                    <option value="">Pilih Provinsi</option>
                                    {provinces.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label>Kabupaten / Kota</Label>
                                <Select name="regencyId" value={formData.regencyId} onChange={handleChange} disabled={!formData.provinceId}>
                                    <option value="">Pilih Kab/Kota</option>
                                    {regencies.map(r => <option key={r.id} value={r.id}>{r.name}</option>)}
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label>Kecamatan</Label>
                                <Select name="districtId" value={formData.districtId} onChange={handleChange} disabled={!formData.regencyId}>
                                    <option value="">Pilih Kecamatan</option>
                                    {districts.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label>Desa / Kelurahan</Label>
                                <Select name="villageId" value={formData.villageId} onChange={handleChange} disabled={!formData.districtId}>
                                    <option value="">Pilih Desa/Kelurahan</option>
                                    {villages.map(v => <option key={v.id} value={v.id}>{v.name}</option>)}
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="address">Alamat Lengkap (Jalan, RT/RW)</Label>
                                <Input name="address" id="address" value={formData.address} onChange={handleChange} placeholder="Jl. Mawar No. 12 RT 01 RW 02" />
                            </div>
                        </div>
                    )}

                    {/* STEP 4: CONFIRMATION */}
                    {step === 4 && (
                        <div className="space-y-6 animate-in fade-in slide-in-from-right-4 duration-300">
                            <h3 className="text-lg font-medium">Konfirmasi Data</h3>
                            <div className="bg-gray-50 p-4 rounded-md space-y-2 text-sm border">
                                <div className="grid grid-cols-3"><span className="font-semibold">Nama Anak:</span> <span className="col-span-2">{formData.childFullName}</span></div>
                                <div className="grid grid-cols-3"><span className="font-semibold">TTL:</span> <span className="col-span-2">{formData.birthPlace}, {formData.birthDate}</span></div>
                                <div className="grid grid-cols-3"><span className="font-semibold">Ortu:</span> <span className="col-span-2">{formData.fatherName} / {formData.motherName}</span></div>
                                <div className="grid grid-cols-3"><span className="font-semibold">Email:</span> <span className="col-span-2">{formData.email}</span></div>
                            </div>

                            <div className="flex items-start space-x-2">
                                <input
                                    type="checkbox"
                                    id="isAgreed"
                                    name="isAgreed"
                                    checked={formData.isAgreed}
                                    onChange={handleChange}
                                    className="mt-1 h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-600"
                                />
                                <Label htmlFor="isAgreed" className="text-sm leading-relaxed font-normal">
                                    Saya menyatakan bahwa data yang saya isi adalah benar dan saya bersedia mengikuti segala peraturan dan ketentuan yang berlaku di SPS Nurul Ikhlas.
                                    Saya bersedia melakukan pembayaran pendaftaran untuk melanjutkan proses verifikasi.
                                </Label>
                            </div>
                        </div>
                    )}

                </CardContent>
                <CardFooter className="flex justify-between">
                    <Button variant="outline" onClick={prevStep} disabled={step === 1 || loading}>
                        <ChevronLeft className="mr-2 h-4 w-4" /> Kembali
                    </Button>

                    {step < 4 ? (
                        <Button onClick={nextStep} disabled={step === 1 && !formData.childFullName}>
                            Selanjutnya <ChevronRight className="ml-2 h-4 w-4" />
                        </Button>
                    ) : (
                        <Button onClick={handleSubmit} disabled={!formData.isAgreed || loading}>
                            {loading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <Check className="mr-2 h-4 w-4" />}
                            Bayar Sekarang
                        </Button>
                    )}
                </CardFooter>
            </Card>
        </div>
    );
}
