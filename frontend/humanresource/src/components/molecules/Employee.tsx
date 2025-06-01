// import React, { useEffect, useState } from "react";
// import SearchBarComponents from "../../components/molecules/SearchBarComponents";
// import EmployeeTable from "../organism/EmployeeTable.tsx";
// import type { Employee } from "../../components/molecules/EmployeeRow";
//
//
// import axios from "axios";
//
// const EmployeeListPanel: React.FC = () => {
//     const ITEMS_PER_PAGE = 15;
//     const [query, setQuery] = useState('');
//     const [currentPage, setCurrentPage] = useState(1);
//     const [employees, setEmployees] = useState<Employee[]>([]);
//
//     const handleToggleStatus = async (id: number, isActive: boolean) => {
//         const token = localStorage.getItem("token");
//         const url = isActive
//             ? `http://localhost:9090/employee/deactive/${id}`
//             : `http://localhost:9090/employee/activate/${id}`;
//
//         try {
//             await axios.put(url, {}, {
//                 headers: { Authorization: `Bearer ${token}` }
//             });
//
//             setEmployees(prev =>
//                 prev.map(emp =>
//                     emp.id === id ? { ...emp, active: !isActive } : emp
//                 )
//             );
//         } catch (error) {
//             console.error("Status toggle failed", error);
//         }
//     };
//
//     const handleDelete = async (id: number) => {
//         const token = localStorage.getItem("token");
//         try {
//             await axios.delete(`http://localhost:9090/employee/delete/${id}`, {
//                 headers: {
//                     Authorization: `Bearer ${token}`,
//                 }
//             });
//
//             setEmployees(prev => prev.filter(emp => emp.id !== id));
//         } catch (error) {
//             console.error("Failed to delete employee", error);
//         }
//     };
//
//     useEffect(() => {
//         const fetchEmployees = async () => {
//             try {
//                 const token = localStorage.getItem("token");
//                 console.log("JWT TOKEN:", token); // ➕ Bunu ekleyerek console'da görebilirsin
//                 const response = await axios.get("http://localhost:9090/employee/get-all", {
//                     headers: {
//                         Authorization: `Bearer ${token}`
//                     }
//                 });
//                 setEmployees(response.data.data);
//             } catch (error) {
//                 console.error("Failed to fetch employees", error);
//             }
//         };
//
//         fetchEmployees();
//     }, []);
//
//     const filtered = employees.filter(emp =>
//         emp.fullName.toLowerCase().includes(query.toLowerCase()) ||
//         emp.Email.toLowerCase().includes(query.toLowerCase())
//     );
//
//     const totalPages = Math.ceil(filtered.length / ITEMS_PER_PAGE);
//     const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
//     const currentEmployees = filtered.slice(startIndex, startIndex + ITEMS_PER_PAGE);
//
//     return (
//         <div className="row m-0" style={{ minHeight: '100vh', backgroundColor: '#f8f9fa' }}>
//
//             <div className="col-10 p-4">
//                 <div className="d-flex justify-content-end mb-3">
//                     <SearchBarComponents query={query} onChange={(q) => {
//                         setQuery(q);
//                         setCurrentPage(1);
//                     }} />
//                 </div>
//                 <div key={currentPage} className="card shadow-sm p-3 bg-white rounded">
//                     <EmployeeTable
//                         employees={currentEmployees}
//                         onToggleStatus={handleToggleStatus}
//                         onDelete={handleDelete}
//                     />
//                     <div className="d-flex justify-content-center mt-3">
//                         <nav>
//                             <ul className="pagination">
//                                 {Array.from({ length: totalPages }, (_, i) => (
//                                     <li key={i} className={`page-item ${currentPage === i + 1 ? 'active' : ''}`}>
//                                         <button className="page-link" onClick={() => setCurrentPage(i + 1)}>
//                                             {i + 1}
//                                         </button>
//                                     </li>
//                                 ))}
//                             </ul>
//                         </nav>
//                     </div>
//                 </div>
//             </div>
//         </div>
//     );
// };
//
// export default EmployeeListPanel;
