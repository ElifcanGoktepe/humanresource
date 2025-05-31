import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './CompanyPage.css';
import 'bootstrap/dist/css/bootstrap.min.css';

interface Company {
    id: number;
    companyName: string;
    companyAddress: string;
    companyPhoneNumber: string;
    companyEmail: string;
}

interface CompanyBranch {
    id: number;
    companyBranchCode: string;
    companyBranchAddress: string;
    companyBranchPhoneNumber: string;
    companyBranchEmailAddress: string;
    company?: Company;
}

interface Department {
    id: number;
    departmentCode: string;
    departmentName: string;
    companyId?: number;
}

function ManagerCompanyForm({ onCompanyAdded }: { onCompanyAdded: () => void }) {
    const [companyName, setCompanyName] = useState('');
    const [companyAddress, setCompanyAddress] = useState('');
    const [companyPhoneNumber, setCompanyPhoneNumber] = useState('');
    const [companyEmail, setCompanyEmail] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [message, setMessage] = useState<string | null>(null);

    const token = localStorage.getItem('token');

    const showMessage = (msg: string, duration = 3000) => {
        setMessage(msg);
        setTimeout(() => setMessage(null), duration);
    };

    const showError = (msg: string, duration = 3000) => {
        setError(msg);
        setTimeout(() => setError(null), duration);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!token) return showError('Authorization token not found.');
        if (!companyName || !companyAddress || !companyPhoneNumber || !companyEmail)
            return showError('Please fill all fields.');

        setLoading(true);
        try {
            const payload = {
                companyName,
                companyAddress,
                companyPhoneNumber,
                companyEmail,
            };
            await axios.post('http://localhost:9090/dev/v1/company/add', payload, {
                headers: { Authorization: `Bearer ${token}` },
            });
            showMessage('Company added successfully!');
            setCompanyName('');
            setCompanyAddress('');
            setCompanyPhoneNumber('');
            setCompanyEmail('');
            onCompanyAdded();
        } catch (err) {
            showError('Failed to add company.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container mt-3">
            <h2>Register Your Company</h2>
            {error && <div className="alert alert-danger">{error}</div>}
            {message && <div className="alert alert-success">{message}</div>}
            {loading && <p>Loading...</p>}
            <form onSubmit={handleSubmit} noValidate>
                <input
                    type="text"
                    className="form-control mb-2"
                    placeholder="Company Name"
                    value={companyName}
                    onChange={(e) => setCompanyName(e.target.value)}
                    required
                />
                <input
                    type="text"
                    className="form-control mb-2"
                    placeholder="Company Address"
                    value={companyAddress}
                    onChange={(e) => setCompanyAddress(e.target.value)}
                    required
                />
                <input
                    type="text"
                    className="form-control mb-2"
                    placeholder="Company Phone Number"
                    value={companyPhoneNumber}
                    onChange={(e) => setCompanyPhoneNumber(e.target.value)}
                    required
                />
                <input
                    type="email"
                    className="form-control mb-2"
                    placeholder="Company Email"
                    value={companyEmail}
                    onChange={(e) => setCompanyEmail(e.target.value)}
                    required
                />
                <button className="btn btn-primary" type="submit" disabled={loading}>
                    Add Company
                </button>
            </form>
        </div>
    );
}

function CompanyPage() {
    const [companies, setCompanies] = useState<Company[]>([]);
    const [selectedCompany, setSelectedCompany] = useState<Company | null>(null);

    // Branch state
    const [branchCode, setBranchCode] = useState('');
    const [branchAddress, setBranchAddress] = useState('');
    const [branchPhoneNumber, setBranchPhoneNumber] = useState('');
    const [branchEmailAddress, setBranchEmailAddress] = useState('');
    const [selectedBranchId, setSelectedBranchId] = useState('');
    const [selectedDepartmentId, setSelectedDepartmentId] = React.useState<string>('');



    // Department state
    const [departmentCode, setDepartmentCode] = useState('');
    const [departmentName, setDepartmentName] = useState('');

    // Branch ve Department listeleri
       const [branches, setBranches] = useState<CompanyBranch[]>([]);
    const [departments, setDepartments] = useState<Department[]>([]);

    // Filtreler (branch arama)
    //   const [searchPhone, setSearchPhone] = useState('');
    //   const [searchEmail, setSearchEmail] = useState('');
    //  const [searchAddress, setSearchAddress] = useState('');

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [message, setMessage] = useState<string | null>(null);
    const [activeTab, setActiveTab] = useState<'company' | 'branch' | 'department'>('company');

    const token = localStorage.getItem('token');

    const showMessage = (msg: string, duration = 3000) => {
        setMessage(msg);
        setTimeout(() => setMessage(null), duration);
    };

    const showError = (msg: string, duration = 3000) => {
        setError(msg);
        setTimeout(() => setError(null), duration);
    };

    // Backend'deki yeni endpoint: kendi şirketini çek
    const fetchCompanies = async () => {
        if (!token) return showError('Authorization token not found.');
        setLoading(true);
        try {
            const res = await axios.get('http://localhost:9090/dev/v1/company/myCompany', {
                headers: { Authorization: `Bearer ${token}` },
            });
            const data = res.data;
            if (data) {
                setCompanies([data]);
                setSelectedCompany(data);
            } else {
                setCompanies([]);
                setSelectedCompany(null);
            }
        } catch {
            showError('Failed to fetch companies.');
            setCompanies([]);
            setSelectedCompany(null);
        } finally {
            setLoading(false);
        }
    };

    // Branch ve Department fetch fonksiyonları aynen kalıyor
    const fetchBranches = async () => {
        if (!token) return showError('Authorization token not found.');
        if (!selectedCompany?.id) return;

        setLoading(true);
        try {
            const res = await axios.get(
                `http://localhost:9090/dev/v1/companybranch/listAll/${selectedCompany.id}`,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            console.log('Branches response:', res.data);
            setBranches(Array.isArray(res.data.data) ? res.data.data : []);
        } catch (e){
            console.error('Fetch branches error:', e);
            showError('Failed to fetch branches.');
            setBranches([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (selectedCompany) {
            fetchBranches();
            fetchDepartments();
        } else {
            setBranches([]);
            setDepartments([]);
        }
    }, [selectedCompany]);

    useEffect(() => {
        fetchCompanies();
    }, []);

    const fetchDepartments = async () => {
        if (!token) return showError('Authorization token not found.');
        if (!selectedBranchId) return;

        setLoading(true);
        try {
            const res = await axios.get(
                `http://localhost:9090/dev/v1/department/listAllByBranchId/${selectedBranchId}`,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            setDepartments(
                Array.isArray(res.data.data)
                    ? res.data.data.filter((d: Department) => d && d.id != null)
                    : []
            );

        } catch {
            showError('Failed to fetch departments.');
            setDepartments([]);
        } finally {
            setLoading(false);
        }
    };
    useEffect(() => {
        if (selectedBranchId) {
            fetchDepartments();
        } else {
            setDepartments([]);
        }
    }, [selectedBranchId]);









    // Branch ekle
    const addBranch = async () => {
        if (!token) return showError('Authorization token not found.');
        if (!selectedCompany?.id) {
            await fetchCompanies();
            const latestCompany = (await axios.get('http://localhost:9090/dev/v1/company/getMyCompany', {
                headers: { Authorization: `Bearer ${token}` },
            })).data;

            setSelectedCompany(latestCompany);
            return fetchBranches();
        }

        if (!branchCode || !branchAddress || !branchPhoneNumber || !branchEmailAddress) {
            return showError('Please fill all branch fields.');
        }

        setLoading(true);
        try {
            const payload = {
                companyBranchCode: branchCode,
                companyBranchAddress: branchAddress,
                companyBranchPhoneNumber: branchPhoneNumber,
                companyBranchEmailAddress: branchEmailAddress,
                companyId: selectedCompany.id,
            };

            await axios.post('http://localhost:9090/dev/v1/companybranch/add', payload, {
                headers: { Authorization: `Bearer ${token}` },
            });

            showMessage('Branch added successfully!');
            setBranchCode('');
            setBranchAddress('');
            setBranchPhoneNumber('');
            setBranchEmailAddress('');

            await fetchBranches(); // branch ekledikten sonra yenile (ÇOK ÖNEMLİ)
        } catch {
            showError('Failed to add branch.');
        } finally {
            setLoading(false);
        }
    };


    const deleteBranch = async (id: number) => {
        if (!token) return showError('Authorization token not found.');

        if (!window.confirm('Are you sure you want to delete this branch?')) return;

        setLoading(true);
        try {
            await axios.delete(`http://localhost:9090/dev/v1/companybranch/delete/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            showMessage('Branch deleted.');
            fetchBranches();
        } catch {
            showError('Failed to delete branch.');
        } finally {
            setLoading(false);
        }
    };

    // Department ekle
    const addDepartment = async () => {
        if (!token) return showError('Authorization token not found.');

        if (!departmentCode || !departmentName) {
            return showError('Please fill all fields.');
        }

        if (!selectedBranchId) {
            return showError('Please select a branch first.');
        }

        setLoading(true);
        try {
            const payload = {
                departmentCode,
                departmentName,
                companyBranchId: selectedBranchId,
            };

            await axios.post('http://localhost:9090/dev/v1/department/add', payload, {
                headers: { Authorization: `Bearer ${token}` },
            });

            showMessage('Department added successfully.');
            setDepartmentCode('');
            setDepartmentName('');
            // Yeni eklenen department listesine hemen fetch yap
            fetchDepartments();
        } catch (error) {
            showError('Failed to add department.');
        } finally {
            setLoading(false);
        }
    };



    const deleteDepartment = async (id: number) => {
        console.log("Deleting department with id:", id); // id undefined mi kontrol et
        if (!token) return showError('Authorization token not found.');
        if (!id) return showError('Invalid department id.');

        if (!window.confirm('Are you sure you want to delete this department?')) return;

        setLoading(true);
        try {
            await axios.delete(`http://localhost:9090/dev/v1/department/delete/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            showMessage('Department deleted.');
            fetchDepartments();
        } catch {
            showError('Failed to delete department.');
        } finally {
            setLoading(false);
        }
    };


    // Branch filtreleme
    {/* const filteredBranches = branches.filter(
        (b) =>
            b.companyBranchPhoneNumber?.toLowerCase().includes(searchPhone.toLowerCase()) &&
            b.companyBranchEmailAddress?.toLowerCase().includes(searchEmail.toLowerCase()) &&
            b.companyBranchAddress?.toLowerCase().includes(searchAddress.toLowerCase())
    );    */}

    // Burada render kısmı aynen kalabilir, sadece aktif tablara göre form ve liste gösterimi

    return (
        <div className="container mt-4">
            <h1>Company Management</h1>
            {loading && <p>Loading...</p>}
            {error && <div className="alert alert-danger">{error}</div>}
            {message && <div className="alert alert-success">{message}</div>}

            {/* Eğer şirket yoksa form göster */}
            {(!selectedCompany || companies.length === 0) && (
                <ManagerCompanyForm onCompanyAdded={fetchCompanies} />
            )}

            {/* Şirket varsa sekmeler göster */}
            {selectedCompany && (
                <>
                    <ul className="nav nav-tabs">
                        <li className="nav-item">
                            <button
                                className={`nav-link ${activeTab === 'company' ? 'active' : ''}`}
                                onClick={() => setActiveTab('company')}
                            >
                                Company
                            </button>
                        </li>
                        <li className="nav-item">
                            <button
                                className={`nav-link ${activeTab === 'branch' ? 'active' : ''}`}
                                onClick={() => setActiveTab('branch')}
                            >
                                Branches
                            </button>
                        </li>
                        <li className="nav-item">
                            <button
                                className={`nav-link ${activeTab === 'department' ? 'active' : ''}`}
                                onClick={() => setActiveTab('department')}
                            >
                                Departments
                            </button>
                        </li>
                    </ul>

                    <div className="tab-content mt-3">
                        {activeTab === 'company' && (
                            <div>
                                <h3>Company Info</h3>
                                <p><b>Name:</b> {selectedCompany.companyName}</p>
                                <p><b>Address:</b> {selectedCompany.companyAddress}</p>
                                <p><b>Phone:</b> {selectedCompany.companyPhoneNumber}</p>
                                <p><b>Email:</b> {selectedCompany.companyEmail}</p>
                            </div>
                        )}

                        {activeTab === 'branch' && (
                            <div>
                                <h3>Add Branch</h3>
                                <div className="mb-3">
                                    <input
                                        type="text"
                                        placeholder="Branch Code"
                                        className="form-control mb-2"
                                        value={branchCode}
                                        onChange={(e) => setBranchCode(e.target.value)}
                                    />
                                    <input
                                        type="text"
                                        placeholder="Branch Address"
                                        className="form-control mb-2"
                                        value={branchAddress}
                                        onChange={(e) => setBranchAddress(e.target.value)}
                                    />
                                    <input
                                        type="text"
                                        placeholder="Branch Phone Number"
                                        className="form-control mb-2"
                                        value={branchPhoneNumber}
                                        onChange={(e) => setBranchPhoneNumber(e.target.value)}
                                    />
                                    <input
                                        type="email"
                                        placeholder="Branch Email Address"
                                        className="form-control mb-2"
                                        value={branchEmailAddress}
                                        onChange={(e) => setBranchEmailAddress(e.target.value)}
                                    />
                                    <button className="btn btn-primary" onClick={addBranch}>
                                        Add Branch
                                    </button>
                                </div>

                                <h4>Branches List</h4>

                                <div className="form-group mt-3">
                                    <label htmlFor="branchSelect">Select Branch</label>
                                    <select
                                        className="form-control"
                                        value={selectedBranchId}
                                        onChange={(e) => setSelectedBranchId(e.target.value)}
                                    >
                                        <option value="">-- Select a Branch --</option>
                                        {branches.map((b) => (
                                            <option key={b.id} value={b.id.toString()}>
                                                {b.companyBranchCode} - {b.companyBranchAddress}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <table className="table table-striped">
                                    <thead>
                                    <tr>
                                        <th>Code</th>
                                        <th>Address</th>
                                        <th>Phone</th>
                                        <th>Email</th>
                                        <th>Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {branches.length === 0 && (
                                        <tr>
                                            <td colSpan={5} className="text-center">
                                                No branches found.
                                            </td>
                                        </tr>
                                    )}
                                    {branches.map((b) => (
                                        <tr key={b.id}>
                                            <td>{b.companyBranchCode}</td>
                                            <td>{b.companyBranchAddress}</td>
                                            <td>{b.companyBranchPhoneNumber}</td>
                                            <td>{b.companyBranchEmailAddress}</td>
                                            <td>
                                                <button
                                                    className="btn btn-danger btn-sm"
                                                    onClick={() => deleteBranch(b.id)}
                                                >
                                                    Delete
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                        {activeTab === 'department' && (
                            <div>
                                <h3>Add Department</h3>
                                {selectedBranchId ? (
                                    <>
                                        <div className="alert alert-secondary mb-3">
                                            <strong>Selected Branch:</strong>{' '}
                                            {
                                                branches.find((b) => b.id.toString() === selectedBranchId)?.companyBranchCode
                                            } -{' '}
                                            {
                                                branches.find((b) => b.id.toString() === selectedBranchId)?.companyBranchAddress
                                            }
                                        </div>

                                        <div className="mb-3">
                                            <input
                                                type="text"
                                                placeholder="Department Code"
                                                className="form-control mb-2"
                                                value={departmentCode}
                                                onChange={(e) => setDepartmentCode(e.target.value)}
                                            />
                                            <input
                                                type="text"
                                                placeholder="Department Name"
                                                className="form-control mb-2"
                                                value={departmentName}
                                                onChange={(e) => setDepartmentName(e.target.value)}
                                            />
                                            <button className="btn btn-primary" onClick={addDepartment}>
                                                Add Department
                                            </button>
                                        </div>

                                        <h4>Departments List</h4>
                                        <div className="form-group mt-3">
                                            <label htmlFor="departmentSelect">Select Department</label>
                                            <select
                                                className="form-control"
                                                value={selectedDepartmentId}
                                                onChange={(e) => setSelectedDepartmentId(e.target.value)}
                                            >
                                                <option value="">-- Select a Department --</option>
                                                {departments.map((d) => (
                                                    d && d.id != null ? (
                                                        <option key={d.id} value={d.id ? d.id.toString(): ''
                                                            }>
                                                            {d.departmentCode} - {d.departmentName}
                                                        </option>
                                                    ) : null
                                                ))}

                                            </select>
                                        </div>


                                        <table className="table table-striped">
                                            <thead>
                                            <tr>
                                                <th>Code</th>
                                                <th>Name</th>
                                                <th>Actions</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {departments.length > 0 ? (
                                                departments.map((d) => (
                                                    <tr key={d.id}>
                                                        <td>{d.departmentCode}</td>
                                                        <td>{d.departmentName}</td>
                                                        <td>
                                                            <button
                                                                className="btn btn-danger btn-sm"
                                                                onClick={() => deleteDepartment(d.id)}
                                                            >
                                                                Delete
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))
                                            ) : (
                                                <tr>
                                                    <td colSpan={3} className="text-center">
                                                        No departments found.
                                                    </td>
                                                </tr>
                                            )}
                                            </tbody>
                                        </table>
                                    </>
                                ) : (
                                    <div className="alert alert-warning">
                                        Please select a branch in the Branches tab first.
                                    </div>
                                )}

                            </div>
                        )}


                    </div>
                </>
            )}
        </div>
    );
}

export default CompanyPage;
