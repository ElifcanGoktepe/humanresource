
import React, { useState } from 'react';

interface ManagerCompanyFormProps {
    onCompanyAdded: () => void; // Şirket eklendikten sonra parent bileşene haber verir
}

const ManagerCompanyForm: React.FC<ManagerCompanyFormProps> = ({ onCompanyAdded }) => {
    const [companyName, setCompanyName] = useState('');
    const [companyAddress, setCompanyAddress] = useState('');
    const [companyPhoneNumber, setCompanyPhoneNumber] = useState('');
    const [companyEmail, setCompanyEmail] = useState('');
    const [errors, setErrors] = useState<{ [key: string]: string }>({});
    const [submitError, setSubmitError] = useState('');
    const [submitSuccess, setSubmitSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const apiUrl = 'http://localhost:8080/api/companies'; // Backend API url

    // Basit frontend validasyon fonksiyonu
    const validate = () => {
        const newErrors: { [key: string]: string } = {};

        if (!companyName.trim()) newErrors.companyName = 'Company name is required';
        if (!companyAddress.trim()) newErrors.companyAddress = 'Company address is required';

        // Telefon numarası boş değilse basit regex kontrolü
        if (companyPhoneNumber.trim() && !/^\+?\d{7,15}$/.test(companyPhoneNumber.trim())) {
            newErrors.companyPhoneNumber = 'Invalid phone number format';
        }

        // Email boş değilse basit email regex kontrolü
        if (companyEmail.trim() && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(companyEmail.trim())) {
            newErrors.companyEmail = 'Invalid email format';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!validate()) return;

        setLoading(true);
        setSubmitError('');
        setSubmitSuccess('');

        try {
            const response = await fetch(apiUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    companyName,
                    companyAddress,
                    companyPhoneNumber,
                    companyEmail,
                }),
            });

            if (!response.ok) {
                // Backend’den dönen hata mesajını yakala
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to add company');
            }

            // Başarılı ekleme sonrası
            setSubmitSuccess('Company added successfully!');
            setCompanyName('');
            setCompanyAddress('');
            setCompanyPhoneNumber('');
            setCompanyEmail('');
            setErrors({});
            onCompanyAdded();
        } catch (err: any) {
            setSubmitError(err.message || 'Unknown error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="card p-4 shadow-sm">
            <h2>Add a New Company</h2>

            {submitError && <div className="alert alert-danger">{submitError}</div>}
            {submitSuccess && <div className="alert alert-success">{submitSuccess}</div>}

            <form onSubmit={handleSubmit} noValidate>
                <div className="mb-3">
                    <label htmlFor="companyName" className="form-label">
                        Company Name *
                    </label>
                    <input
                        type="text"
                        id="companyName"
                        className={`form-control ${errors.companyName ? 'is-invalid' : ''}`}
                        value={companyName}
                        onChange={(e) => setCompanyName(e.target.value)}
                    />
                    {errors.companyName && <div className="invalid-feedback">{errors.companyName}</div>}
                </div>

                <div className="mb-3">
                    <label htmlFor="companyAddress" className="form-label">
                        Company Address *
                    </label>
                    <input
                        type="text"
                        id="companyAddress"
                        className={`form-control ${errors.companyAddress ? 'is-invalid' : ''}`}
                        value={companyAddress}
                        onChange={(e) => setCompanyAddress(e.target.value)}
                    />
                    {errors.companyAddress && <div className="invalid-feedback">{errors.companyAddress}</div>}
                </div>

                <div className="mb-3">
                    <label htmlFor="companyPhoneNumber" className="form-label">
                        Company Phone Number
                    </label>
                    <input
                        type="tel"
                        id="companyPhoneNumber"
                        className={`form-control ${errors.companyPhoneNumber ? 'is-invalid' : ''}`}
                        value={companyPhoneNumber}
                        onChange={(e) => setCompanyPhoneNumber(e.target.value)}
                        placeholder="+905551234567"
                    />
                    {errors.companyPhoneNumber && <div className="invalid-feedback">{errors.companyPhoneNumber}</div>}
                </div>

                <div className="mb-3">
                    <label htmlFor="companyEmail" className="form-label">
                        Company Email
                    </label>
                    <input
                        type="email"
                        id="companyEmail"
                        className={`form-control ${errors.companyEmail ? 'is-invalid' : ''}`}
                        value={companyEmail}
                        onChange={(e) => setCompanyEmail(e.target.value)}
                        placeholder="example@company.com"
                    />
                    {errors.companyEmail && <div className="invalid-feedback">{errors.companyEmail}</div>}
                </div>

                <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? 'Saving...' : 'Add Company'}
                </button>
            </form>
        </div>
    );
};

export default ManagerCompanyForm;
