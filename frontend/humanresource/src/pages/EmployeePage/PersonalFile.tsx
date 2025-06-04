// src/components/PersonalFile.tsx

import 'bootstrap/dist/css/bootstrap.min.css';
import './PersonalFile.css'; // Yeni eklediğimiz stil dosyası
import React, { useState, useEffect, type ChangeEvent } from 'react';
import {
    User,
    Calendar,
    CreditCard,
    GraduationCap,
    Heart,
    Baby,
    Droplet,
    Phone,
    Mail,
    MapPin,
    Building2,
    Banknote,
    Edit3,
    Save,
    X,
} from 'lucide-react';

interface EnumOption {
    key: string;
    value: string;
}

interface EnumData {
    gender: EnumOption[];
    educationLevel: EnumOption[];
    maritalStatus: EnumOption[];
    bloodType: EnumOption[];
}

interface EmployeeData {
    name: string;
    title: string;
    company: string;
    employeeNumber: string;
    gender: string;
    birthdate: string;
    personalPhone: string;
    personalEmail: string;
    city: string;
    nationalId: string;
    educationLevel: string;
    maritalStatus: string;
    bloodType: string;
    numberOfChildren: number;
    bankName: string;
    bankAccountNumber: string;
    iban: string;
}

const PersonalFile: React.FC = () => {
    const [isEditing, setIsEditing] = useState<boolean>(false);
    const [enums, setEnums] = useState<EnumData>({
        gender: [],
        educationLevel: [],
        maritalStatus: [],
        bloodType: [],
    });

    const [employeeData, setEmployeeData] = useState<EmployeeData>({
        name: 'Ahmet Yılmaz',
        title: 'Yazılım Geliştirici',
        company: 'ABC Teknoloji A.Ş.',
        employeeNumber: '20',
        gender: 'MALE',
        birthdate: '1985-06-15',
        personalPhone: '+90 532 123 45 67',
        personalEmail: 'ahmet.yilmaz@email.com',
        city: 'İstanbul',
        nationalId: '12345678901',
        educationLevel: 'BACHELOR',
        maritalStatus: 'MARRIED',
        bloodType: 'A_POSITIVE',
        numberOfChildren: 2,
        bankName: 'Türkiye İş Bankası',
        bankAccountNumber: '1234567890',
        iban: 'TR33 0006 1005 1978 6457 8413 26',
    });

    const [editData, setEditData] = useState<EmployeeData>({ ...employeeData });

    // Enum verilerini mock olarak yükleme
    const fetchEnums = (): void => {
        setEnums({
            gender: [
                { key: 'MALE', value: 'Erkek' },
                { key: 'FEMALE', value: 'Kadın' },
                { key: 'OTHER', value: 'Diğer' },
            ],
            educationLevel: [
                { key: 'PRIMARY', value: 'İlkokul' },
                { key: 'SECONDARY', value: 'Ortaokul' },
                { key: 'HIGH_SCHOOL', value: 'Lise' },
                { key: 'BACHELOR', value: 'Lisans' },
                { key: 'MASTER', value: 'Yüksek Lisans' },
                { key: 'PHD', value: 'Doktora' },
            ],
            maritalStatus: [
                { key: 'SINGLE', value: 'Bekar' },
                { key: 'MARRIED', value: 'Evli' },
                { key: 'DIVORCED', value: 'Boşanmış' },
                { key: 'WIDOWED', value: 'Dul' },
            ],
            bloodType: [
                { key: 'A_POSITIVE', value: 'A+' },
                { key: 'A_NEGATIVE', value: 'A-' },
                { key: 'B_POSITIVE', value: 'B+' },
                { key: 'B_NEGATIVE', value: 'B-' },
                { key: 'AB_POSITIVE', value: 'AB+' },
                { key: 'AB_NEGATIVE', value: 'AB-' },
                { key: 'O_POSITIVE', value: '0+' },
                { key: 'O_NEGATIVE', value: '0-' },
            ],
        });
    };

    useEffect(() => {
        fetchEnums();
    }, []);

    const handleEdit = (): void => {
        setIsEditing(true);
        setEditData({ ...employeeData });
    };

    const handleSave = (): void => {
        setEmployeeData({ ...editData });
        setIsEditing(false);
    };

    const handleCancel = (): void => {
        setEditData({ ...employeeData });
        setIsEditing(false);
    };

    const handleInputChange = (
        field: keyof EmployeeData,
        value: string | number
    ): void => {
        setEditData((prev) => ({ ...prev, [field]: value }));
    };

    // Tek bir satırda etiket+ikon+input için ortak yapı
    const renderInput = (
        field: keyof EmployeeData,
        label: string,
        IconComponent: React.ElementType,
        type: string = 'text'
    ): React.ReactElement => (
        <div className="mb-3 row align-items-center">
            <label className="col-sm-3 col-form-label d-flex align-items-center">
                <IconComponent size={18} className="me-1 text-teal" />
                <span className="text-teal">{label}</span>
            </label>
            <div className="col-sm-9">
                <input
                    type={type}
                    className={`form-control ${isEditing ? '' : 'bg-light'}`}
                    value={String(editData[field])}
                    onChange={(e: ChangeEvent<HTMLInputElement>) =>
                        handleInputChange(
                            field,
                            type === 'number' ? parseInt(e.target.value) || 0 : e.target.value
                        )
                    }
                    disabled={!isEditing}
                />
            </div>
        </div>
    );

    // Tek bir satırda etiket+ikon+select için ortak yapı
    const renderSelect = (
        field: keyof EmployeeData,
        label: string,
        optionsKey: keyof EnumData,
        IconComponent: React.ElementType
    ): React.ReactElement => {
        const options: EnumOption[] = enums[optionsKey] || [];
        return (
            <div className="mb-3 row align-items-center">
                <label className="col-sm-3 col-form-label d-flex align-items-center">
                    <IconComponent size={18} className="me-1 text-teal" />
                    <span className="text-teal">{label}</span>
                </label>
                <div className="col-sm-9">
                    <select
                        className={`form-select ${isEditing ? '' : 'bg-light'}`}
                        value={String(editData[field])}
                        onChange={(e: ChangeEvent<HTMLSelectElement>) =>
                            handleInputChange(field, e.target.value)
                        }
                        disabled={!isEditing}
                    >
                        {options.map((opt: EnumOption) => (
                            <option key={opt.key} value={opt.key}>
                                {opt.value}
                            </option>
                        ))}
                    </select>
                </div>
            </div>
        );
    };

    return (
        <div className="container-fluid">
            <div className="row">
                {/* --- SOL MENÜ (2 birim, sabit pozisyonda) --- */}
                <div className="col-2 position-fixed vh-100 bg-sidebar-light">
                    <div className="fixed-bar-image text-center my-3">
                        <img className="logo-left-menu" src="/img/logo1.png" alt="logo" />
                    </div>
                    <hr className="sidebar-divider" />
                    <div className="fixed-bar-button-container px-2">
                        <button className="fixed-bar-buttons">
                            <img
                                className="small-image-fixed-bar"
                                src="/img/adminpage.png"
                                alt="Company"
                            />
                            <span className="sidebar-text">Company Page</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img
                                className="small-image-fixed-bar"
                                src="/img/employee.png"
                                alt="Employee"
                            />
                            <span className="sidebar-text">Employee</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img
                                className="small-image-fixed-bar"
                                src="/img/expens.png"
                                alt="Salary"
                            />
                            <span className="sidebar-text">Salary</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img
                                className="small-image-fixed-bar"
                                src="/img/shift.png"
                                alt="Shift"
                            />
                            <span className="sidebar-text">Shift</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img
                                className="small-image-fixed-bar"
                                src="/img/assets.png"
                                alt="Assignment"
                            />
                            <span className="sidebar-text">Assignment</span>
                        </button>
                    </div>
                    <hr className="sidebar-divider" />
                    <div className="bottom-bar px-2 mt-4">
                        <button className="fixed-bar-buttons">
                            <img
                                className="small-image-fixed-bar"
                                src="/img/profileicon.png"
                                alt="Opinions"
                            />
                            <span className="sidebar-text">Opinions</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img
                                className="small-image-fixed-bar"
                                src="/img/settingsicon.png"
                                alt="Settings"
                            />
                            <span className="sidebar-text">Settings</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img
                                className="small-image-fixed-bar"
                                src="/img/helpicon.png"
                                alt="Help"
                            />
                            <span className="sidebar-text">Help</span>
                        </button>
                    </div>
                </div>

                {/* --- ANA İÇERİK (10 birim, offset-2) --- */}
                <div className="col-10 offset-2">
                    <div className="p-4" style={{ maxHeight: '100vh', overflowY: 'auto' }}>
                        {/* Başlık ve Düzenle/Kaydet/İptal Butonları */}
                        <div className="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h2 className="h2 text-dark">Merhaba {employeeData.name}!</h2>
                                <p className="text-muted">
                                    Bugünün Tarihi:{' '}
                                    {new Date().toLocaleDateString('tr-TR', {
                                        weekday: 'long',
                                        year: 'numeric',
                                        month: 'long',
                                        day: 'numeric',
                                    })}
                                </p>
                            </div>
                            <div>
                                {isEditing ? (
                                    <>
                                        <button onClick={handleSave} className="btn-save me-3">
                                            <Save size={20} className="me-2" />
                                            Kaydet
                                        </button>

                                        <button onClick={handleCancel} className="btn-cancel">
                                            <X size={20} className="me-2" />
                                            İptal
                                        </button>
                                    </>
                                ) : (
                                    <button onClick={handleEdit} className="btn-edit">
                                        <Edit3 size={20} className="me-2" />
                                        Düzenle
                                    </button>
                                )}
                            </div>
                        </div>

                        {/* --- Profil Kartı --- */}
                        <div className="card mb-4 shadow-sm border-0">
                            <div className="card-body d-flex align-items-center bg-white rounded">
                                <div
                                    className="rounded-circle bg-teal text-white d-flex justify-content-center align-items-center me-3"
                                    style={{ width: '60px', height: '60px' }}
                                >
                                    <User size={28} />
                                </div>
                                <div>
                                    <h5 className="card-title mb-1 text-dark">{employeeData.name}</h5>
                                    <p className="card-subtitle text-muted mb-1">
                                        {employeeData.title}
                                    </p>
                                    <p className="text-muted mb-2">{employeeData.company}</p>
                                    <span className="badge bg-teal text-white">
                    Personel No: {employeeData.employeeNumber}
                  </span>
                                </div>
                            </div>
                        </div>

                        {/* --- Kimlik ve Eğitim Bölümü --- */}
                        <div className="card mb-4 border-0">
                            <div className="card-header bg-white border rounded-top">
                                <div className="d-flex align-items-center">
                                    <CreditCard size={20} className="me-2 text-teal" />
                                    <h6 className="mb-0 text-teal">Kimlik ve Eğitim</h6>
                                </div>
                            </div>
                            <div className="card-body bg-white border-bottom border-start border-end rounded-bottom">
                                <div className="row">
                                    <div className="col-md-6">
                                        {renderInput('nationalId', 'TC Kimlik No', CreditCard)}
                                    </div>
                                    <div className="col-md-6">
                                        {renderSelect(
                                            'educationLevel',
                                            'Eğitim Seviyesi',
                                            'educationLevel',
                                            GraduationCap
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* --- Kişisel Bilgiler Bölümü --- */}
                        <div className="card mb-4 border-0">
                            <div className="card-header bg-white border rounded-top">
                                <div className="d-flex align-items-center">
                                    <Heart size={20} className="me-2 text-teal" />
                                    <h6 className="mb-0 text-teal">Kişisel Bilgiler</h6>
                                </div>
                            </div>
                            <div className="card-body bg-white border-bottom border-start border-end rounded-bottom">
                                <div className="row">
                                    <div className="col-md-6">
                                        {renderSelect('gender', 'Cinsiyet', 'gender', User)}
                                    </div>
                                    <div className="col-md-6">
                                        {renderInput('birthdate', 'Doğum Tarihi', Calendar, 'date')}
                                    </div>
                                </div>
                                <div className="row mt-3">
                                    <div className="col-md-6">
                                        {renderSelect(
                                            'maritalStatus',
                                            'Medeni Durum',
                                            'maritalStatus',
                                            Heart
                                        )}
                                    </div>
                                    <div className="col-md-6">
                                        {renderInput('numberOfChildren', 'Çocuk Sayısı', Baby, 'number')}
                                    </div>
                                </div>
                                <div className="row mt-3">
                                    <div className="col-md-6">
                                        {renderSelect('bloodType', 'Kan Grubu', 'bloodType', Droplet)}
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* --- İletişim Bilgileri Bölümü --- */}
                        <div className="card mb-4 border-0">
                            <div className="card-header bg-white border rounded-top">
                                <div className="d-flex align-items-center">
                                    <Phone size={20} className="me-2 text-teal" />
                                    <h6 className="mb-0 text-teal">İletişim Bilgileri</h6>
                                </div>
                            </div>
                            <div className="card-body bg-white border-bottom border-start border-end rounded-bottom">
                                <div className="row">
                                    <div className="col-md-6">
                                        {renderInput('personalPhone', 'Kişisel Telefon', Phone, 'tel')}
                                    </div>
                                    <div className="col-md-6">
                                        {renderInput('personalEmail', 'E-posta', Mail, 'email')}
                                    </div>
                                </div>
                                <div className="row mt-3">
                                    <div className="col-md-6">
                                        {renderInput('city', 'Şehir', MapPin)}
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* --- Banka Bilgileri Bölümü --- */}
                        <div className="card mb-4 border-0">
                            <div className="card-header bg-white border rounded-top">
                                <div className="d-flex align-items-center">
                                    <Banknote size={20} className="me-2 text-teal" />
                                    <h6 className="mb-0 text-teal">Banka Bilgileri</h6>
                                </div>
                            </div>
                            <div className="card-body bg-white border-bottom border-start border-end rounded-bottom">
                                <div className="row">
                                    <div className="col-md-6">
                                        {renderInput('bankName', 'Banka Adı', Building2)}
                                    </div>
                                    <div className="col-md-6">
                                        {renderInput('bankAccountNumber', 'Hesap Numarası', CreditCard)}
                                    </div>
                                </div>
                                <div className="row mt-3">
                                    <div className="col-md-6">
                                        {renderInput('iban', 'IBAN', CreditCard)}
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* --- Adres Bilgileri Bölümü --- */}
                        <div className="card mb-4 border-0">
                            <div className="card-header bg-white border rounded-top">
                                <div className="d-flex align-items-center">
                                    <MapPin size={20} className="me-2 text-teal" />
                                    <h6 className="mb-0 text-teal">Adres Bilgileri</h6>
                                </div>
                            </div>
                            <div className="card-body bg-white border-bottom border-start border-end rounded-bottom">
                                <div className="mb-3 row align-items-center">
                                    <label className="col-sm-3 col-form-label d-flex align-items-center">
                                        <MapPin size={18} className="me-1 text-teal" /> Adres
                                    </label>
                                    <div className="col-sm-9">
                    <textarea
                        className={`form-control ${isEditing ? '' : 'bg-light'}`}
                        rows={3}
                        value={String(editData.city)}
                        onChange={(e: ChangeEvent<HTMLTextAreaElement>) =>
                            handleInputChange('city', e.target.value)
                        }
                        disabled={!isEditing}
                    />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PersonalFile;
