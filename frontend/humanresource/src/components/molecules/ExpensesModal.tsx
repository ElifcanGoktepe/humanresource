import React from 'react';
import { Modal, Button } from "react-bootstrap";

interface Expense {
    id: number;
    category: string;
    amount: number;
    notes: string;
    date: string;
    status: 'Pending Approval' | 'Approved' | 'Rejected';
}

interface ExpensesModalProps {
    show: boolean;
    onHide: () => void;
    expense: Expense | null;
}

const ExpensesModal: React.FC<ExpensesModalProps> = ({ show, onHide, expense }) => {
    if (!expense) return null;

    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header closeButton>
                <Modal.Title>Expense Details</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="mb-2">
                    <strong>Category:</strong> {expense.category}
                </div>
                <div className="mb-2">
                    <strong>Amount:</strong> ₺{expense.amount.toFixed(2)}
                </div>
                <div className="mb-2">
                    <strong>Notes:</strong> {expense.notes}
                </div>
                <div className="mb-2">
                    <strong>Date:</strong> {new Date(expense.date).toLocaleDateString('tr-TR')}
                </div>
                <div className="mb-2">
                    <strong>Status:</strong> {expense.status}
                </div>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onHide}>Close</Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ExpensesModal;
