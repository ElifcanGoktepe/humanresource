import { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import './BranchArea.css';

interface Branch {
    id: number;
    branchName: string;
    branchEmail?: string;
    branchPhoneNumber?: string;
    companyId?: number;
    // Ek alanlar eklenebilir
}

const ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split('');

function BranchArea() {
    const [branches, setBranches] = useState<Branch[]>([]);
    const [filteredBranches, setFilteredBranches] = useState<Branch[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const letterRefs = useRef<Record<string, HTMLDivElement | null>>({});

    // Tüm şubeleri veya seçili şirketin şubelerini getir
    useEffect(() => {
        async function fetchBranches() {
            setLoading(true);
            setError(null);

            try {
                const token = localStorage.getItem('token');
                if (!token) throw new Error('Authorization token missing.');

                let url = 'http://localhost:9090/dev/v1/admin/company/get_all_company_branches';
                if (selectedCompanyId !== null) {
                    url = `http://localhost:9090/dev/v1/admin/company/get_all_company_branches_of_selected_company?companyId=${selectedCompanyId}`;
                }

                const response = await axios.get<Branch[]>(url, {
                    headers: { Authorization: `Bearer ${token}` },
                });

                const uniqueBranches = Array.from(new Map(response.data.map(b => [b.id, b])).values());
                const sorted = uniqueBranches.sort((a, b) => a.branchName.localeCompare(b.branchName));
                setBranches(sorted);
                setFilteredBranches(sorted);
            } catch (e: any) {
                setError(e.message || 'Failed to fetch branches.');
            } finally {
                setLoading(false);
            }
        }

        fetchBranches();
    }, [selectedCompanyId]);

    // Arama filtrelemesi (debounce ile)
    useEffect(() => {
        let active = true;
        setLoading(true);
        setError(null);

        async function fetchFiltered() {
            try {
                if (searchTerm.trim() === '') {
                    if (active) {
                        setFilteredBranches(branches);
                        setLoading(false);
                    }
                } else {
                    const filtered = branches.filter(b =>
                        b.branchName.toLowerCase().includes(searchTerm.toLowerCase())
                    );

                    if (active) {
                        setFilteredBranches(filtered);
                        setLoading(false);
                    }
                }
            } catch (e: any) {
                if (active) {
                    setError(e.message || 'Failed to search branches.');
                    setLoading(false);
                }
            }
        }

        const debounceTimer = setTimeout(() => {
            fetchFiltered();
        }, 400);

        return () => {
            active = false;
            clearTimeout(debounceTimer);
        };
    }, [searchTerm, branches]);

    function scrollToLetter(letter: string) {
        const ref = letterRefs.current[letter];
        if (ref) {
            ref.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }

    // Şubeleri alfabetik gruplandırma
    const groupedBranches: Record<string, Branch[]> = {};
    ALPHABETS.forEach(letter => {
        groupedBranches[letter] = [];
    });
    groupedBranches['#'] = []; // '#' harfi için boş dizi ekle

    filteredBranches.forEach(branch => {
        const firstLetter = branch.branchName[0]?.toUpperCase() || '#';
        if (groupedBranches[firstLetter]) {
            if (!groupedBranches[firstLetter].some(b => b.id === branch.id)) {
                groupedBranches[firstLetter].push(branch);
            }
        } else {
            if (!groupedBranches['#'].some(b => b.id === branch.id)) {
                groupedBranches['#'].push(branch);
            }
        }
    });

    const allGroupsToRender = [...ALPHABETS];
    if (groupedBranches['#'].length > 0) {
        allGroupsToRender.push('#');
    }

    // Şirket listesi için state
    const [companies, setCompanies] = useState<{ id: number, companyName: string }[]>([]);

    useEffect(() => {
        async function fetchCompanies() {
            try {
                const token = localStorage.getItem('token');
                if (!token) throw new Error('Authorization token missing.');

                const res = await axios.get('http://localhost:9090/dev/v1/admin/company/getAllCompanies', {
                    headers: { Authorization: `Bearer ${token}` }
                });

                // ✅ Format kontrolü
                if (Array.isArray(res.data)) {
                    setCompanies(res.data);
                } else if (Array.isArray(res.data.companies)) {
                    setCompanies(res.data.companies);
                } else {
                    console.error('Invalid companies list format:', res.data);
                    setCompanies([]);
                }

            } catch (error) {
                console.error("Failed to fetch companies", error);
            }
        }

        fetchCompanies();
    }, []);





    return (
        <div className="branch-area-container">
            <h2>Branches</h2>

            <div className="company-select-container">
                <label htmlFor="company-select">Filter by Company:</label>
                <select
                    id="company-select"
                    value={selectedCompanyId ?? ''}
                    onChange={e => {
                        const val = e.target.value;
                        setSelectedCompanyId(val ? Number(val) : null);
                    }}
                >
                    <option value="">All Companies</option>
                    {companies.map(c => (
                        <option key={c.id} value={c.id}>{c.companyName}</option>
                    ))}
                </select>
            </div>

            <input
                type="text"
                placeholder="Search branches by name..."
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                className="branch-search-input"
            />

            <div className="alphabet-bar">
                {ALPHABETS.map(letter => (
                    <button
                        key={letter}
                        className="alphabet-button"
                        onClick={() => scrollToLetter(letter)}
                        disabled={groupedBranches[letter].length === 0}
                    >
                        {letter}
                    </button>
                ))}
            </div>

            {loading && <p>Loading branches...</p>}
            {error && <p className="text-danger">{error}</p>}

            <div className="branches-list">
                {allGroupsToRender.map(letter => {
                    const branchesForLetter = groupedBranches[letter];
                    if (!branchesForLetter || branchesForLetter.length === 0) return null;
                    return (
                        <div key={letter} ref={el => { letterRefs.current[letter] = el; }}>
                            <h3 className="branch-letter-header">{letter}</h3>
                            <div className="branch-cards-container">
                                {branchesForLetter.map(branch => (
                                    <div key={branch.id} className="branch-card">
                                        <h5>{branch.branchName}</h5>
                                        {branch.branchEmail && <p>Email: {branch.branchEmail}</p>}
                                        {branch.branchPhoneNumber && <p>Phone: {branch.branchPhoneNumber}</p>}
                                    </div>
                                ))}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}

export default BranchArea;
