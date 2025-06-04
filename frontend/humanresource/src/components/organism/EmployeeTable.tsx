import React, {} from 'react';

import EmployeeRow from '../molecules/EmployeeRow';
import type {Employee} from '../../pages/ManagerPage/Employee.ts';
//import axios from "axios"; // interface buradan import ediliyor!

interface EmployeeTableProps {
    employees: Employee[];
    onToggleStatus: (id: number, currentStatus: boolean) => void;
    onDelete: (id: number) => void;
    startIndex: number;
}

const EmployeeTable: React.FC<EmployeeTableProps> = ({  employees, onToggleStatus, onDelete, startIndex}) => {


    return (
        <div className="overflow-x-auto">
            <table className="table table-hover shadow-sm rounded border bg-white">
                <thead className="thead-light">
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Title</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {employees.map((emp, index) => (
                    <EmployeeRow
                        key={emp.id}
                        employee={emp}
                        index={startIndex + index + 1}
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