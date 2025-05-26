import React, { useState, useEffect } from 'react';
import './EmployeePersonalFile.css'

interface EnumOption {
    key: string;
    value: string;
}

interface EnumData {
    gender: EnumOption[];
    educationLevel: EnumOption[];
    maritalStatus: EnumOption[];
    bloodType: EnumOption[];
    bankAccountType: EnumOption[];
}

export default function EmployeePersonalFilePage() {
    const [isEditing, setIsEditing] = useState(false);
    const [enums, setEnums] = useState<EnumData>({
        gender: [],
        educationLevel: [],
        maritalStatus: [],
        bloodType: [],
        bankAccountType: []
    });

    const [employeeData, setEmployeeData] = useState({
        name: 'Ahmet Yılmaz',
        title: 'Yazılım Geliştirici',
        company: 'ABC Teknoloji A.Ş.',
        employeeNumber: '20',
        profileImage: null as string | null,
        gender: 'MALE',
        birthdate: '1985-06-15',
        personalPhone: '+90 532 123 45 67',
        personalEmail: 'ahmet.yilmaz@email.com',
        nationalId: '12345678901',
        educationLevel: 'BACHELOR',
        maritalStatus: 'MARRIED',
        bloodType: 'A_POSITIVE',
        numberOfChildren: 2,
        address: 'Atatürk Mahallesi, 123. Sokak No:45/7',
        city: 'İstanbul',
        iban: 'TR33 0006 1005 1978 6457 8413 26',
        bankName: 'Türkiye İş Bankası',
        bankAccountNumber: '1234567890',
        bankAccountType: 'SALARY'
    });

    const [editData, setEditData] = useState({ ...employeeData });

    const fetchEnums = async () => {
        const mockEnumData: EnumData = {
            gender: [
                { key: 'MALE', value: 'Erkek' },
                { key: 'FEMALE', value: 'Kadın' },
                { key: 'OTHER', value: 'Diğer' }
            ],
            educationLevel: [
                { key: 'PRIMARY', value: 'İlkokul' },
                { key: 'SECONDARY', value: 'Ortaokul' },
                { key: 'HIGH_SCHOOL', value: 'Lise' },
                { key: 'BACHELOR', value: 'Lisans' },
                { key: 'MASTER', value: 'Yüksek Lisans' },
                { key: 'PHD', value: 'Doktora' }
            ],
            maritalStatus: [
                { key: 'SINGLE', value: 'Bekar' },
                { key: 'MARRIED', value: 'Evli' },
                { key: 'DIVORCED', value: 'Boşanmış' },
                { key: 'WIDOWED', value: 'Dul' }
            ],
            bloodType: [
                { key: 'A_POSITIVE', value: 'A+' },
                { key: 'A_NEGATIVE', value: 'A-' },
                { key: 'B_POSITIVE', value: 'B+' },
                { key: 'B_NEGATIVE', value: 'B-' },
                { key: 'AB_POSITIVE', value: 'AB+' },
                { key: 'AB_NEGATIVE', value: 'AB-' },
                { key: 'O_POSITIVE', value: '0+' },
                { key: 'O_NEGATIVE', value: '0-' }
            ],
            bankAccountType: [
                { key: 'CHECKING', value: 'Vadesiz' },
                { key: 'SAVINGS', value: 'Vadeli' },
                { key: 'SALARY', value: 'Maaş Hesabı' }
            ]
        };

        setEnums(mockEnumData);
    };

    useEffect(() => {
        fetchEnums();
    }, []);

    const getCurrentDate = () =>
        new Date().toLocaleDateString('tr-TR', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

    const handleInputChange = (field: keyof typeof editData, value: string | number) => {
        setEditData(prev => ({ ...prev, [field]: value }));
    };

    const renderInput = (field: keyof typeof editData, label: string, type: string = 'text') => (
        <div>
            <div className="label">{label}</div>
            <input
                type={type}
                disabled={!isEditing}
                value={String(editData[field])}
                onChange={(e) =>
                    handleInputChange(field, type === 'number' ? parseInt(e.target.value) || 0 : e.target.value)
                }
            />
        </div>
    );

    const renderSelect = (field: keyof typeof editData, label: string, enumKey: keyof EnumData) => (
        <div>
            <div className="label">{label}</div>
            <select
                disabled={!isEditing}
                value={String(editData[field])}
                onChange={(e) => handleInputChange(field, e.target.value)}
            >
                {enums[enumKey].map((opt) => (
                    <option key={opt.key} value={opt.key}>
                        {opt.value}
                    </option>
                ))}
            </select>
        </div>
    );

    const renderTextarea = (field: keyof typeof editData, label: string) => (
        <div className="col-span-2">
            <div className="label">{label}</div>
            <textarea
                rows={3}
                disabled={!isEditing}
                value={String(editData[field])}
                onChange={(e) => handleInputChange(field, e.target.value)}
            />
        </div>
    );

    return (
        <div style={{ display: 'flex', width: '100%' }}>
            <div className="sidebar">
                <div style={{ fontWeight: 'bold', marginBottom: '2rem' }}>humin</div>
                <div className="nav-item">Company Page</div>
                <div className="nav-item">Employee</div>
                <div className="nav-item">Salary</div>
                <div className="nav-item">Shift</div>
                <div className="nav-item">Assignment</div>
                <div style={{ marginTop: '2rem' }} className="nav-item">Profile</div>
                <div className="nav-item">Settings</div>
                <div className="nav-item">Help</div>
            </div>
            <div className="main">
                <div className="header">
                    <div>
                        <h1>Merhaba {employeeData.name}!</h1>
                        <p>{getCurrentDate()}</p>
                    </div>
                    <div>
                        {isEditing ? (
                            <>
                                <button onClick={() => { setEmployeeData(editData); setIsEditing(false); }}>Kaydet</button>
                                <button onClick={() => { setEditData(employeeData); setIsEditing(false); }}>İptal</button>
                            </>
                        ) : (
                            <button onClick={() => setIsEditing(true)}>Düzenle</button>
                        )}
                    </div>
                </div>

                <div className="card profile-card">
                    <div className="profile-pic">👤</div>
                    <div>
                        <h2>{employeeData.name}</h2>
                        <div>{employeeData.title}</div>
                        <div>{employeeData.company}</div>
                        <div>Personel No: {employeeData.employeeNumber}</div>
                    </div>
                </div>

                <div className="card">
                    <h3>Kimlik Bilgileri</h3>
                    <div className="grid-2">
                        {renderInput('nationalId', 'TC Kimlik No')}
                        {renderSelect('gender', 'Cinsiyet', 'gender')}
                        {renderInput('birthdate', 'Doğum Tarihi', 'date')}
                        {renderSelect('bloodType', 'Kan Grubu', 'bloodType')}
                        {renderSelect('educationLevel', 'Eğitim Seviyesi', 'educationLevel')}
                        {renderSelect('maritalStatus', 'Medeni Durum', 'maritalStatus')}
                        {renderInput('numberOfChildren', 'Çocuk Sayısı', 'number')}
                    </div>
                </div>

                <div className="card">
                    <h3>İletişim Bilgileri</h3>
                    <div className="grid-2">
                        {renderInput('personalPhone', 'Telefon')}
                        {renderInput('personalEmail', 'E-posta')}
                        {renderInput('city', 'Şehir')}
                    </div>
                </div>

                <div className="card">
                    <h3>Banka Bilgileri</h3>
                    <div className="grid-2">
                        {renderInput('bankName', 'Banka Adı')}
                        {renderInput('bankAccountNumber', 'Hesap Numarası')}
                        {renderInput('iban', 'IBAN')}
                        {renderSelect('bankAccountType', 'Hesap Türü', 'bankAccountType')}
                    </div>
                </div>

                <div className="card">
                    <h3>Adres</h3>
                    <div>{renderTextarea('address', 'Adres')}</div>
                </div>
            </div>
        </div>
    );
}
