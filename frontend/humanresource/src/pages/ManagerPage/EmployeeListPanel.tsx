

import React, { useEffect, useState } from "react";
import SearchBarComponents from "../../components/molecules/SearchBarComponents";
import EmployeeTable from "../../components/organism/EmployeeTable";
import type { Employee } from "../../pages/ManagerPage/Employee";
import { toast } from 'react-toastify';

import "./EmployeeListPanel.css";

const EmployeeListPanel: React.FC = () => {
    const ITEMS_PER_PAGE = 15;
    const [query, setQuery] = useState<string>("");
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [employees, setEmployees] = useState<Employee[]>([]);

    // 1) Sunucudan veri çeken fonksiyon
    const fetchEmployees = async () => {
        try {
            const token = localStorage.getItem("token");
            if (!token) return;

            const response = await fetch("http://localhost:9090/active-employees", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) throw new Error("Failed to fetch");
            const data = await response.json();
            // data.data içinde Employee[] gelip gelmediğine dikkat edin
            setEmployees(data.data || []);
        } catch (error) {
            console.error("Failed to fetch employees", error);
        }
    };

    useEffect(() => {
        fetchEmployees();
    }, []);

    // 2) Aktif/Pasif toggle işlemi
    // 2) Durum toggle (aktif <-> pasif) (PUT isteği atıp yeniden liste getir):
    const handleToggleStatus = async (id: number, currentlyActive: boolean) => {
        try {
            const token = localStorage.getItem('token');
            const endpoint = currentlyActive ? 'deactivate' : 'activate';

            const response = await fetch(
                `http://localhost:9090/employee/${endpoint}/${id}`,
                {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (!response.ok) throw new Error('Güncelleme başarısız');
            toast.success(
                `Çalışan ${currentlyActive ? 'pasif' : 'aktif'} yapıldı`
            );
            fetchEmployees();
        } catch (error) {
            console.error('Durum değiştirilemedi', error);
            toast.error('Durum değiştirilemedi');
        }
    };

    // 3) Çalışan silme
    const handleDelete = async (id: number) => {
        try {
            const token = localStorage.getItem("token");
            await fetch(`http://localhost:9090/employee/delete/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchEmployees();
        } catch (err) {
            console.error("Failed to delete employee", err);
        }
    };

    // 4) Arama filtrasyonu
    const filtered = employees.filter((emp) =>
        emp.fullName.toLowerCase().includes(query.toLowerCase()) ||
        emp.email.toLowerCase().includes(query.toLowerCase())
    );
    const totalPages = Math.ceil(filtered.length / ITEMS_PER_PAGE);
    const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
    const currentEmployees = filtered.slice(startIndex, startIndex + ITEMS_PER_PAGE);

    return (
        <div className="employee-list-panel container-fluid p-0">
            {/* --- ÜST KISIM: Başlık + Arama --- */}

                <div className="searchbar-wrapper mt-3">
                    <SearchBarComponents
                        query={query}
                        onChange={(q) => {
                            setQuery(q);
                            setCurrentPage(1);
                        }}

                    />
                </div>


            {/* --- TABLO: Çalışan listesi --- */}
            <div className="table-container px-4 pb-5">
                <EmployeeTable
                    employees={currentEmployees}
                    onToggleStatus={handleToggleStatus}
                    onDelete={handleDelete}
                    startIndex={startIndex}
                />
            </div>

            {/* --- SAYFALAMA --- */}
            {totalPages > 1 && (
                <div className="d-flex justify-content-center mb-4">
                    <ul className="pagination">
                        {Array.from({ length: totalPages }, (_, i) => (
                            <li
                                key={i}
                                className={`page-item ${currentPage === i + 1 ? "active" : ""}`}
                            >
                                <button
                                    className="page-link"
                                    onClick={() => setCurrentPage(i + 1)}
                                >
                                    {i + 1}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default EmployeeListPanel;
