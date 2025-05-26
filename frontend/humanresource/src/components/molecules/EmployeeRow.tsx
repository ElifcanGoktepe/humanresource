import React from 'react';
import './EmployeeRow.css'

import { Pencil, Trash2, FileText, Power } from 'lucide-react';
import IconButton from '../atoms/IconButton.tsx';

 export interface Employee {
    id: number;
    fullName: string;
    emailWork: string;
    phoneWork: string;
    title: string;
    active: boolean;
}

interface EmployeeRowProps {
    employee: Employee;
    onToggleStatus: (id: number, isActive: boolean) => void;
    onDelete: (id: number)=>void;
}

const EmployeeRow: React.FC<EmployeeRowProps> = ({ employee, onToggleStatus , onDelete }) => {
    return (
        <tr className="border-b hover:bg-gray-50">
            <td className="p-2">{employee.fullName}</td>
            <td className="p-2">{employee.emailWork}</td>
            <td className="p-2">{employee.phoneWork}</td>
            <td className="p-2">{employee.title}</td>
            <td className="p-2">
        <span className={`text-sm font-medium ${employee.active ? 'status-active' : 'status-passive'}`}>
    {employee.active ? 'Active' : 'Passive'}
        </span>
            </td>
            <td className="p-2 flex gap-2">
                <IconButton icon={Pencil} onClick={() => {
                }} title="Edit"/>
                <IconButton
                    icon={Power}
                    onClick={() => onToggleStatus(employee.id, employee.active)}
                    title="Activate/Deactivate"
                />
                <IconButton icon={FileText} onClick={() => {}} title="Details" />
                <IconButton icon={Trash2} onClick={() => onDelete(employee.id)} title="Delete" />
            </td>
        </tr>
    );
};

export default EmployeeRow;
