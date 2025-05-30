import React from 'react';
import './EmployeeRow.css'
import type {Employee} from "../../pages/ManagerPage/Employee";

import { Pencil, Trash2, FileText, Power } from 'lucide-react';
import IconButton from '../atoms/IconButton.tsx';



interface EmployeeRowProps {
    employee: Employee;
    index: number;
    onToggleStatus: (id: number, currentStatus: boolean) => void;
    onDelete: (id: number) => void;
}

const EmployeeRow: React.FC<EmployeeRowProps> = ({ employee,index, onToggleStatus , onDelete }) => {
    return (
        <tr className="border-b hover:bg-gray-50">
            <td className="p-2 font-weight-bold">{index}</td>
            <td className="p-2">{employee.fullName}</td>
            <td className="p-2">{employee.email}</td>
            <td className="p-2">{employee.phoneNumber}</td>
            <td className="p-2">{employee.title}</td>
            <td className="p-2">
                <span className={`text-sm font-medium ${employee.active ? 'text-green-600' : 'text-red-500'}`}>
                    {employee.active ? 'Active' : 'Passive'}
                </span>
            </td>
            <td className="p-2 d-flex gap-2">
                <IconButton icon={Pencil} onClick={() => {}} title="Edit" />
                <IconButton icon={Power} onClick={() => onToggleStatus(employee.id, employee.active)} title="Toggle" />
                <IconButton icon={FileText} onClick={() => {}} title="Details" />
                <IconButton icon={Trash2} onClick={() => onDelete(employee.id)} title="Delete" />
            </td>
        </tr>
    );
};

export default EmployeeRow;
