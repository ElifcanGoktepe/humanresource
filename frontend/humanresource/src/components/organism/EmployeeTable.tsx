import React, {} from 'react';

import EmployeeRow, {type Employee } from '../molecules/EmployeeRow';
//import axios from "axios"; // interface buradan import ediliyor!

interface EmployeeTableProps {
    employees: Employee[];
    onToggleStatus: (id: number, isActive: boolean) => void;
    onDelete: (id:number)=>void;
}

const EmployeeTable: React.FC<EmployeeTableProps> = ({  employees,onToggleStatus ,onDelete}) => {
    // const [employees, setEmployees] = useState<Employee[]>([]);
    // useEffect(() => {
    //     const fetchEmployees = async () => {
    //         try {
    //             const token = localStorage.getItem("token");
    //             const response = await axios.get("http://localhost:9090/employee/get-all", {
    //                 headers: {
    //                     Authorization: `Bearer ${token}`
    //                 }
    //             });
    //             setEmployees(response.data.data);
    //         } catch (error) {
    //             console.error("Failed to fetch employees", error);
    //         }
    //     };
    //
    //     fetchEmployees();
    // }, []);

    return (
        <div className="overflow-x-auto">
            <table className="table table-hover shadow-sm rounded border bg-white">
                <thead className="thead-light">
                <tr>
                    <th className="text-left px-4 py-2">Name</th>
                    <th className="text-left px-4 py-2">Email</th>
                    <th className="text-left px-4 py-2">Phone</th>
                    <th className="text-left px-4 py-2">Title</th>
                    <th className="text-left px-4 py-2">Status</th>
                    <th className="text-left px-4 py-2">Actions</th>
                </tr>
                </thead>
                <tbody>
                {employees.map(emp => (
                    <EmployeeRow
                        key={emp.id}
                        employee={emp}
                        onToggleStatus={onToggleStatus}
                        onDelete={onDelete}
                    />
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default EmployeeTable;