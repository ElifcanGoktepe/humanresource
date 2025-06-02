// src/components/organism/EmployeeTable.tsx

import React from 'react';
import { Badge, Button, OverlayTrigger, Tooltip } from 'react-bootstrap';
import {
    Edit3,
    Power,
    FileText,
    Trash2,
} from 'lucide-react';
import type { Employee } from '../../pages/ManagerPage/Employee';
import './EmployeeTable.css';

interface EmployeeTableProps {
    employees: Employee[];
    onToggleStatus: (id: number, currentStatus: boolean) => void;
    onDelete: (id: number) => void;
    startIndex: number;
}

const EmployeeTable: React.FC<EmployeeTableProps> = ({
                                                         employees,
                                                         onToggleStatus,
                                                         onDelete,
                                                         startIndex,
                                                     }) => {
    return (
        <div className="employee-table-container">
            <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                <tr>
                    <th className="text-center no-col">No</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Title</th>
                    <th className="text-center status-col">Status</th>
                    <th className="text-center actions-col">Actions</th>
                </tr>
                </thead>
                <tbody>
                {employees.length === 0 ? (
                    <tr>
                        <td colSpan={7} className="text-center text-muted py-3">
                            No employees found.
                        </td>
                    </tr>
                ) : (
                    employees.map((emp, idx) => {
                        const globalIndex = startIndex + idx + 1;
                        const isActive = emp.isActive;

                        return (
                            <tr key={emp.employeeId}>
                                <td className="text-center">{globalIndex}</td>
                                <td>{emp.fullName}</td>
                                <td>{emp.email}</td>
                                <td>{emp.phoneNumber}</td>
                                <td>{emp.title}</td>
                                <td className="text-center">
                                    {isActive ? (
                                        <Badge pill bg="success" className="status-badge">
                                            Active
                                        </Badge>
                                    ) : (
                                        <Badge pill bg="danger" className="status-badge">
                                            Passive
                                        </Badge>
                                    )}
                                </td>
                                <td className="text-center">
                                    <div className="actions-wrapper">
                                        {/* Düzenle (Edit) İkonu */}
                                        <OverlayTrigger
                                            placement="top"
                                            overlay={<Tooltip>Edit Employee</Tooltip>}
                                        >
                                            <Button
                                                variant="light"
                                                className="action-btn"
                                                size="sm"
                                                onClick={() => onToggleStatus(emp.employeeId, isActive)}
                                            >
                                                <Edit3 className="text-teal" />
                                            </Button>
                                        </OverlayTrigger>

                                        {/* Activate / Deactivate (Power) İkonu */}
                                        <OverlayTrigger
                                            placement="top"
                                            overlay={<Tooltip>{isActive ? 'Deactivate' : 'Activate'}</Tooltip>}
                                        >
                                            <Button
                                                variant="light"
                                                className="action-btn"
                                                size="sm"
                                                onClick={() => onToggleStatus(emp.employeeId, isActive)}
                                            >
                                                <Power
                                                    className={isActive ? 'text-danger' : 'text-success'}
                                                />
                                            </Button>
                                        </OverlayTrigger>

                                        {/* Detay (FileText) İkonu */}
                                        <OverlayTrigger
                                            placement="top"
                                            overlay={<Tooltip>Details</Tooltip>}
                                        >
                                            <Button
                                                variant="light"
                                                className="action-btn"
                                                size="sm"
                                                onClick={() => {
                                                    /* detay modal tetikle */
                                                }}
                                            >
                                                <FileText className="text-teal" />
                                            </Button>
                                        </OverlayTrigger>

                                        {/* Sil (Trash2) İkonu */}
                                        <OverlayTrigger
                                            placement="top"
                                            overlay={<Tooltip>Delete Employee</Tooltip>}
                                        >
                                            <Button
                                                variant="light"
                                                className="action-btn"
                                                size="sm"
                                                onClick={() => onDelete(emp.employeeId)}
                                            >
                                                <Trash2 className="text-danger" />
                                            </Button>
                                        </OverlayTrigger>
                                    </div>
                                </td>
                            </tr>
                        );
                    })
                )}
                </tbody>
            </table>
        </div>
    );
};

export default EmployeeTable;
