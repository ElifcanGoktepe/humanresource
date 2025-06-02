
import { useEffect, useState } from 'react';
import axios from 'axios';
import './BranchArea.css';

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
    branches: Branch[];
}

function BranchArea() {
    const [companies, setCompanies] = useState<Company[]>([]);
    const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
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

                if (!Array.isArray(res.data)) throw new Error('Unexpected response format.');
                setCompanies(res.data);
            } catch (e: any) {
                setError(e.message || 'Failed to fetch companies.');
            } finally {
                setLoading(false);
            }
        }

        fetchCompanies();
    }, []);

    const selectedCompany = companies.find(c => c.id === selectedCompanyId);

    return (
        <div className="branch-area-container">
            <h2>Company Branches</h2>

            <div className="company-select-container">
                <label htmlFor="company-select">Select Company:</label>
                <select
                    id="company-select"
                    value={selectedCompanyId ?? ''}
                    onChange={e => {
                        const val = e.target.value;
                        setSelectedCompanyId(val ? Number(val) : null);
                    }}
                >
                    <option value="">-- Select Company --</option>
                    {companies.map(company => (
                        <option key={company.id} value={company.id}>
                            {company.companyName}
                        </option>
                    ))}
                </select>
            </div>

            {loading && <p>Loading...</p>}
            {error && <p className="text-danger">{error}</p>}

            {selectedCompany && (
                <div className="branches-list">
                    {selectedCompany.branches.map(branch => (
                        <div key={branch.id} className="branch-card">
                            <h4>Branch Code: {branch.companyBranchCode}</h4>
                            <p><strong>Address:</strong> {branch.companyBranchAddress}</p>
                            <p><strong>Email:</strong> {branch.companyBranchEmailAddress}</p>
                            <p><strong>Phone:</strong> {branch.companyBranchPhoneNumber}</p>
                            <div>
                                <strong>Departments:</strong>
                                <ul>
                                    {branch.departments.map(dep => (
                                        <li key={dep.id}>
                                            {dep.departmentName} ({dep.departmentCode})
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default BranchArea;
