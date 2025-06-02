
import { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import './CompanyArea.css';

interface Company {
    id: number;
    companyName: string;
    companyEmail?: string;
    companyPhoneNumber?: string;
    // İstersen ek alanlar ekleyebilirsin
}

const ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split('');

function CompanyArea() {
    const [companies, setCompanies] = useState<Company[]>([]);
    const [filteredCompanies, setFilteredCompanies] = useState<Company[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Ref'ler: Her harfe ait bir div referansı tutalım
    const letterRefs = useRef<Record<string, HTMLDivElement | null>>({});

    useEffect(() => {
        async function fetchAllCompanies() {
            setLoading(true);
            setError(null);
            try {
                const token = localStorage.getItem('token');
                if (!token) throw new Error('Authorization token missing.');

                const response = await axios.get<Company[]>('http://localhost:9090/dev/v1/admin/company/getAllCompanies', {
                    headers: { Authorization: `Bearer ${token}` },
                });

                // Duplicate filtrele
                const uniqueCompanies = Array.from(new Map(response.data.map(c => [c.id, c])).values());
                const sorted = uniqueCompanies.sort((a, b) => a.companyName.localeCompare(b.companyName));
                setCompanies(sorted);
                setFilteredCompanies(sorted);
            } catch (e: any) {
                setError(e.message || 'Failed to fetch companies.');
            } finally {
                setLoading(false);
            }
        }

        fetchAllCompanies();
    }, []);


    useEffect(() => {
        let active = true;
        setLoading(true);
        setError(null);

        async function fetchFiltered() {
            try {
                if (searchTerm.trim() === '') {
                    if (active) {
                        setFilteredCompanies(companies);
                        setLoading(false);
                    }
                } else {
                    const token = localStorage.getItem('token');
                    if (!token) throw new Error('Authorization token missing.');

                    const response = await axios.get<Company[]>(`http://localhost:9090/dev/v1/admin/company/searchCompaniesByName?name=${encodeURIComponent(searchTerm)}`, {
                        headers: { Authorization: `Bearer ${token}` },
                    });

                    const uniqueCompanies = Array.from(new Map(response.data.map(c => [c.id, c])).values());
                    const sorted = uniqueCompanies.sort((a, b) => a.companyName.localeCompare(b.companyName));

                    if (active) {
                        setFilteredCompanies(sorted);
                        setLoading(false);
                    }
                }
            } catch (e: any) {
                if (active) {
                    setError(e.message || 'Failed to search companies.');
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
    }, [searchTerm, companies]);


    function scrollToLetter(letter: string) {
        const ref = letterRefs.current[letter];
        if (ref) {
            ref.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }

    const groupedCompanies: Record<string, Company[]> = {};
    ALPHABETS.forEach(letter => {
        groupedCompanies[letter] = [];
    });
    filteredCompanies.forEach(company => {
        const firstLetter = company.companyName[0].toUpperCase();
        if (groupedCompanies[firstLetter]) {
            // Aynı company.id zaten var mı kontrol et
            if (!groupedCompanies[firstLetter].some(c => c.id === company.id)) {
                groupedCompanies[firstLetter].push(company);
            }
        } else {
            if (!groupedCompanies['#']) groupedCompanies['#'] = [];
            if (!groupedCompanies['#'].some(c => c.id === company.id)) {
                groupedCompanies['#'].push(company);
            }
        }
    });


    return (
        <div className="company-area-container">
            <h2>Companies</h2>

            <input
                type="text"
                placeholder="Search companies by name..."
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                className="company-search-input"
            />

            <div className="alphabet-bar">
                {ALPHABETS.map(letter => (
                    <button
                        key={letter}
                        className="alphabet-button"
                        onClick={() => scrollToLetter(letter)}
                        disabled={groupedCompanies[letter].length === 0}
                    >
                        {letter}
                    </button>
                ))}
            </div>

            {loading && <p>Loading companies...</p>}
            {error && <p className="text-danger">{error}</p>}


            <div className="companies-list">
                {ALPHABETS.map(letter => {
                    const companiesForLetter = groupedCompanies[letter];
                    if (companiesForLetter.length === 0) return null; // boş grubu render etme
                    return (
                        <div key={letter} ref={el => { letterRefs.current[letter] = el; }}>
                            <h3 className="company-letter-header">{letter}</h3>
                            <div className="company-cards-container">
                                {companiesForLetter.map(company => (
                                    <div key={company.id} className="company-card">
                                        <h5>{company.companyName}</h5>
                                        {company.companyEmail && <p>Email: {company.companyEmail}</p>}
                                        {company.companyPhoneNumber && <p>Phone: {company.companyPhoneNumber}</p>}
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

export default CompanyArea;
