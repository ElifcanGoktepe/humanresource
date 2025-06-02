import { useEffect, useState } from 'react';
import axios from 'axios';
import './DepartmentArea.css';

interface Department {
    id: number;
    departmentName: string;
    departmentCode: string;
}

interface Branch {
    id: number;
    companyBranchCode: string;
    companyBranchAddress: string;
    companyBranchPhoneNumber: string;
    companyBranchEmailAddress: string;
    departments: Department[];
}

interface Company {
    id: number;
    companyName: string;
    companyPhoneNumber: string;
    companyAddress: string;
    companyEmail: string;

    branches: Branch[];
}

function DepartmentArea() {
    const [companies, setCompanies] = useState<Company[]>([]);
    const [branches, setBranches] = useState<Branch[]>([]);
    const [departments, setDepartments] = useState<Department[]>([]);
    const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
    const [selectedBranchId, setSelectedBranchId] = useState<number | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function fetchCompanies() {
            setLoading(true);
            setError(null);
            try {
                const token = localStorage.getItem('token');
                if (!token) throw new Error('Authorization token missing.');

                const res = await axios.get<Company[]>('http://localhost:9090/dev/v1/admin/company/getAllCompanies', {
                    headers: { Authorization: `Bearer ${token}` }
                });

                setCompanies(res.data);
            } catch (e: any) {
                setError(e.message || 'Failed to fetch companies.');
            } finally {
                setLoading(false);
            }
        }

        fetchCompanies();
    }, []);

    // Company seçildiğinde branchleri güncelle
    useEffect(() => {
        if (selectedCompanyId === null) {
            setBranches([]);
            setDepartments([]);
            setSelectedBranchId(null);
            return;
        }
        const company = companies.find(c => c.id === selectedCompanyId);
        setBranches(company?.branches ?? []);
        setDepartments([]);
        setSelectedBranchId(null);
    }, [selectedCompanyId, companies]);

    // Branch seçildiğinde departmanları güncelle
    useEffect(() => {
        if (selectedBranchId === null) {
            setDepartments([]);
            return;
        }
        const branch = branches.find(b => b.id === selectedBranchId);
        setDepartments(branch?.departments ?? []);
    }, [selectedBranchId, branches]);

    // Seçilen company bilgisi
    const selectedCompany = companies.find(c => c.id === selectedCompanyId);

    return (
        <div className="department-area-container">
            <h2>Departments</h2>

            {loading && <p>Loading...</p>}
            {error && <p className="text-danger">{error}</p>}

            <div className="selectors-vertical">
                <div className="form-group">
                    <label htmlFor="company-select">Select Company:</label>
                    <select
                        id="company-select"
                        className="form-control"
                        value={selectedCompanyId ?? ''}
                        onChange={e => setSelectedCompanyId(e.target.value ? Number(e.target.value) : null)}
                    >
                        <option value="">-- Select Company --</option>
                        {companies.map(c => (
                            <option key={c.id} value={c.id}>
                                {c.companyName}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Company Card */}
                {selectedCompany && (
                    <div className="company-card">
                        <h3>{selectedCompany.companyName}</h3>
                        <p><strong>Phone:</strong> {selectedCompany.companyPhoneNumber}</p>
                        <p><strong>Address:</strong> {selectedCompany.companyAddress}</p>
                        <p><strong>Email:</strong> {selectedCompany.companyEmail}</p>
                    </div>
                )}

                <div className="form-group" style={{ marginTop: '1rem' }}>
                    <label htmlFor="branch-select">Select Branch:</label>
                    <select
                        id="branch-select"
                        className="form-control"
                        value={selectedBranchId ?? ''}
                        onChange={e => setSelectedBranchId(e.target.value ? Number(e.target.value) : null)}
                        disabled={branches.length === 0}
                    >
                        <option value="">-- Select Branch --</option>
                        {branches.map(b => (
                            <option key={b.id} value={b.id}>
                                {b.companyBranchCode}
                            </option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="department-list mt-4">
                {departments.length === 0 ? (
                    <p>No departments available.</p>
                ) : (
                    <ul className="list-group">
                        {departments.map(dep => (
                            <li key={dep.id} className="list-group-item">
                                <strong>{dep.departmentName}</strong> ({dep.departmentCode})
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}

export default DepartmentArea;
