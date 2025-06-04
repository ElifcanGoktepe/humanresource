import React, { useEffect, useState } from 'react';
import SearchBarComponents from '../../components/molecules/SearchBarComponents';
import EmployeeTable from '../../components/organism/EmployeeTable';
import type {Employee} from '../../pages/ManagerPage/Employee';



const EmployeeListPanel: React.FC = () => {
    const ITEMS_PER_PAGE = 15;
    const [query, setQuery] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [employees, setEmployees] = useState<Employee[]>([]);

    const fetchEmployees = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await fetch('http://localhost:9090/active-employees', {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) throw new Error('Failed to fetch');
            const data = await response.json();
            setEmployees(data.data);
        } catch (error) {
            console.error("Failed to fetch employees", error);
        }
    };

    useEffect(() => {
        fetchEmployees();
    }, []);

    const handleToggleStatus = async (id: number, currentStatus: boolean) => {
        const token = localStorage.getItem("token");
        const endpoint = currentStatus ? "deactive" : "activate";

        await fetch(`http://localhost:9090/employee/${endpoint}/${id}`, {
            method: 'PUT',
            headers: { Authorization: `Bearer ${token}` }
        });

        fetchEmployees();
    };

    const handleDelete = async (id: number) => {
        const token = localStorage.getItem("token");
        await fetch(`http://localhost:9090/employee/delete/${id}`, {
            method: 'DELETE',
            headers: { Authorization: `Bearer ${token}` }
        });

        fetchEmployees();
    };

    const filtered = employees.filter(emp =>
        emp.fullName.toLowerCase().includes(query.toLowerCase()) ||
        emp.email.toLowerCase().includes(query.toLowerCase())
    );

    const totalPages = Math.ceil(filtered.length / ITEMS_PER_PAGE);
    const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
    const currentEmployees = filtered.slice(startIndex, startIndex + ITEMS_PER_PAGE);

    return (
        <div className="p-4">
            <div className="mb-3">
                <SearchBarComponents query={query} onChange={q => {
                    setQuery(q);
                    setCurrentPage(1);
                }} />
            </div>
            <EmployeeTable
                employees={currentEmployees}
                onToggleStatus={handleToggleStatus}
                onDelete={handleDelete}
                startIndex={startIndex}
            />
            <div className="d-flex justify-content-center mt-3">
                <ul className="pagination">
                    {Array.from({ length: totalPages }, (_, i) => (
                        <li key={i} className={`page-item ${currentPage === i + 1 ? 'active' : ''}`}>
                            <button className="page-link" onClick={() => setCurrentPage(i + 1)}>
                                {i + 1}
                            </button>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );

};

export default EmployeeListPanel;
