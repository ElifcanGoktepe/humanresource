import React, { useState, type ChangeEvent, type FormEvent, useEffect } from 'react';
import ExpensesModal from "../../components/molecules/ExpensesModal";
import { Button } from 'react-bootstrap';
import './ExpensesPanel.css';
import {
    CreditCard,
    FileText,
    Calendar,
    CheckCircle,
    XCircle,
    Eye,
    Save,
    Wallet,
    Tag,
} from 'lucide-react';

// -------------------------------------------
// TypeScript interface for an expense record
// -------------------------------------------
interface Expense {
    id: number;
    category: string;
    amount: number;
    notes: string;
    date: string; // ISO date string
    status: 'Pending Approval' | 'Approved' | 'Rejected';
    receiptFileName: string | null;
}

const ExpensesPanel: React.FC = () => {
    // 1. Form fields state
    const [category, setCategory] = useState<string>('');
    const [amount, setAmount] = useState<number>(0);
    const [notes, setNotes] = useState<string>('');
    const [date, setDate] = useState<string>('');
    const [receiptFile, setReceiptFile] = useState<File | null>(null);

    // 2. List of created expenses
    const [expenses, setExpenses] = useState<Expense[]>([]);
    const [modalVisible, setModalVisible] = useState<boolean>(false);
    const [selectedExpense, setSelectedExpense] = useState<Expense | null>(null);

    // On component mount, load sample data (sorted by date descending)
    useEffect(() => {
        const sampleData: Expense[] = [
            {
                id: 1,
                category: 'Food and Beverage',
                amount: 75.5,
                notes: 'Lunch meeting',
                date: '2025-05-10',
                status: 'Approved',
                receiptFileName: 'receipt1.pdf',
            },
            {
                id: 2,
                category: 'Travel and Transportation',
                amount: 12.0,
                notes: 'Metro ticket',
                date: '2025-05-12',
                status: 'Pending Approval',
                receiptFileName: null,
            },
            {
                id: 3,
                category: 'Office Supplies',
                amount: 45.75,
                notes: 'Pens and paper',
                date: '2025-05-08',
                status: 'Rejected',
                receiptFileName: 'receipt2.jpg',
            },
        ];

        // Sort by date (newest first)
        const sorted = sampleData.sort(
            (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
        );
        setExpenses(sorted);
    }, []);

    // Handle Save: add new expense and re-sort list
    const handleSave = (e: FormEvent) => {
        e.preventDefault();
        if (!category || amount <= 0 || !date) return;

        const newExpense: Expense = {
            id: Date.now(), // simple unique ID
            category,
            amount,
            notes,
            date,
            status: 'Pending Approval',
            receiptFileName: receiptFile ? receiptFile.name : null,
        };

        setExpenses((prev) => {
            const updatedList = [newExpense, ...prev];
            return updatedList.sort(
                (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
            );
        });

        // Reset form
        setCategory('');
        setAmount(0);
        setNotes('');
        setDate('');
        setReceiptFile(null);
    };

    // Show expense details in modal
    const showDetails = (exp: Expense) => {
        setSelectedExpense(exp);
        setModalVisible(true);
    };

    return (
        <div className="container-fluid">
            <div className="row">

                {/* --- SIDE BAR (2 columns fixed) --- */}
                <div className="col-2 position-fixed vh-100 bg-sidebar-light">
                    <div className="fixed-bar-image text-center my-3">
                        <img className="logo-left-menu" src="/img/logo1.png" alt="logo" />
                    </div>
                    <hr className="sidebar-divider" />
                    <div className="fixed-bar-button-container px-2">
                        <button className="fixed-bar-buttons">
                            <img className="small-image-fixed-bar" src="/img/adminpage.png" alt="Company Page" />
                            <span className="sidebar-text">Company Page</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img className="small-image-fixed-bar" src="/img/employee.png" alt="Employee" />
                            <span className="sidebar-text">Employee</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img className="small-image-fixed-bar" src="/img/expens.png" alt="Salary" />
                            <span className="sidebar-text">Salary</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img className="small-image-fixed-bar" src="/img/shift.png" alt="Shift" />
                            <span className="sidebar-text">Shift</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img className="small-image-fixed-bar" src="/img/assets.png" alt="Assignment" />
                            <span className="sidebar-text">Assignment</span>
                        </button>
                    </div>
                    <hr className="sidebar-divider" />
                    <div className="bottom-bar px-2 mt-4">
                        <button className="fixed-bar-buttons">
                            <img className="small-image-fixed-bar" src="/img/profileicon.png" alt="Profile" />
                            <span className="sidebar-text">Profile</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img className="small-image-fixed-bar" src="/img/settingsicon.png" alt="Settings" />
                            <span className="sidebar-text">Settings</span>
                        </button>
                        <button className="fixed-bar-buttons">
                            <img className="small-image-fixed-bar" src="/img/helpicon.png" alt="Help" />
                            <span className="sidebar-text">Help</span>
                        </button>
                    </div>
                </div>

                {/* --- MAIN CONTENT (10 columns offset-2) --- */}
                <div className="col-9 offset-2">
                    <div className="manager-page-header px-3">
                        <h2>Hello Ahmet</h2>
                        <h3>Today's Date: 15/5/2025, Saturday</h3>
                        <hr />
                    </div>

                    <div className="harcama-wrapper px-3">
                        {/* ---------- EXPENSE FORM ---------- */}
                        <div className="card mb-5 border-0 shadow-sm">
                            <div className="card-header bg-white border rounded-top">
                                <div className="d-flex align-items-center">
                                    <FileText size={20} className="me-2 text-teal" />
                                    <h6 className="mb-0 text-teal">Add New Expense</h6>
                                </div>
                            </div>
                            <div className="card-body bg-white border-bottom border-start border-end rounded-bottom">
                                <form onSubmit={handleSave}>
                                    <div className="row g-3">
                                        {/* Category */}
                                        <div className="col-md-4">
                                            <label className="form-label d-flex align-items-center text-teal">
                                                <Tag size={18} className="me-1" /> Category
                                            </label>
                                            <select
                                                className="form-select"
                                                value={category}
                                                onChange={(e: ChangeEvent<HTMLSelectElement>) => setCategory(e.target.value)}
                                                required
                                            >
                                                <option value="">Choose...</option>
                                                <option value="Business Expenses">Business Expenses</option>
                                                <option value="Bills and Utilities">Bills and Utilities</option>
                                                <option value="Training and Courses">Training and Courses</option>
                                                <option value="Food and Beverage">Food and Beverage</option>
                                                <option value="Travel and Transportation">Travel and Transportation
                                                </option>
                                                <option value="Other">Other</option>


                                            </select>
                                        </div>

                                        {/* Amount */}
                                        <div className="col-md-4">
                                            <label className="form-label d-flex align-items-center text-teal">
                                                <Wallet size={18} className="me-1" /> Amount (₺)
                                            </label>
                                            <input
                                                type="number"
                                                className="form-control"
                                                value={amount > 0 ? amount : ''}
                                                onChange={(e: ChangeEvent<HTMLInputElement>) =>
                                                    setAmount(parseFloat(e.target.value) || 0)
                                                }
                                                min="0"
                                                step="0.01"
                                                required
                                            />
                                        </div>

                                        {/* Date */}
                                        <div className="col-md-4">
                                            <label className="form-label d-flex align-items-center text-teal">
                                                <Calendar size={18} className="me-1" /> Date
                                            </label>
                                            <input
                                                type="date"
                                                className="form-control"
                                                value={date}
                                                onChange={(e: ChangeEvent<HTMLInputElement>) => setDate(e.target.value)}
                                                required
                                            />
                                        </div>

                                        {/* Receipt Upload */}
                                        <div className="col-md-6">
                                            <label className="form-label d-flex align-items-center text-teal">
                                                <FileText size={18} className="me-1" /> Upload Receipt
                                            </label>
                                            <input
                                                type="file"
                                                className="form-control"
                                                onChange={(e: ChangeEvent<HTMLInputElement>) => {
                                                    if (e.target.files && e.target.files[0]) {
                                                        setReceiptFile(e.target.files[0]);
                                                    } else {
                                                        setReceiptFile(null);
                                                    }
                                                }}
                                            />
                                        </div>

                                        {/* Save Button */}
                                        <div className="col-md-6 d-flex align-items-end justify-content-end">
                                            <button type="submit" className="btn-save-modern">
                                                <Save size={20} className="me-2" /> Save
                                            </button>
                                        </div>

                                        {/* Notes (Full Width) */}
                                        <div className="col-12">
                                            <label className="form-label d-flex align-items-center text-teal">
                                                <CreditCard size={18} className="me-1" /> Notes
                                            </label>
                                            <textarea
                                                className="form-control"
                                                rows={2}
                                                value={notes}
                                                onChange={(e: ChangeEvent<HTMLTextAreaElement>) =>
                                                    setNotes(e.target.value)
                                                }
                                                placeholder="(Optional)"
                                            ></textarea>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>

                        {/* ---------- EXPENSE LIST ---------- */}
                        <div className="card mb-4 border-0 shadow-sm">
                            <div className="card-header bg-white border rounded-top">
                                <div className="d-flex align-items-center">
                                    <FileText size={20} className="me-2 text-teal" />
                                    <h6 className="mb-0 text-teal">Created Expenses</h6>
                                </div>
                            </div>
                            <div className="card-body bg-white border-bottom border-start border-end rounded-bottom">
                                <div className="table-responsive">
                                    <table className="table table-hover align-middle mb-0">
                                        <thead className="table-light">
                                        <tr>
                                            <th>#</th>
                                            <th>Category</th>
                                            <th>Amount (₺)</th>
                                            <th>Date</th>
                                            <th>Status</th>
                                            <th>Details</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {expenses.length === 0 ? (
                                            <tr>
                                                <td colSpan={6} className="text-center text-muted py-3">
                                                    No expenses created yet.
                                                </td>
                                            </tr>
                                        ) : (
                                            expenses.map((exp, idx) => (
                                                <tr key={exp.id}>
                                                    <td>{idx + 1}</td>
                                                    <td>{exp.category}</td>
                                                    <td>₺{exp.amount.toFixed(2)}</td>
                                                    <td>{new Date(exp.date).toLocaleDateString('tr-TR')}</td>
                                                    <td>
                                                        {exp.status === 'Pending Approval' && (
                                                            <span className="text-warning d-flex align-items-center">
                                  <Calendar size={16} className="me-1" /> {exp.status}
                                </span>
                                                        )}
                                                        {exp.status === 'Approved' && (
                                                            <span className="text-success d-flex align-items-center">
                                  <CheckCircle size={16} className="me-1" /> {exp.status}
                                </span>
                                                        )}
                                                        {exp.status === 'Rejected' && (
                                                            <span className="text-danger d-flex align-items-center">
                                  <XCircle size={16} className="me-1" /> {exp.status}
                                </span>
                                                        )}
                                                    </td>
                                                    <td>
                                                        <Button
                                                            variant="outline-info"
                                                            size="sm"
                                                            onClick={() => showDetails(exp)}
                                                            className="d-flex align-items-center btn-detail"
                                                        >
                                                            <Eye size={16} className="me-1 text-teal" />
                                                            <span className="text-teal">View</span>
                                                        </Button>
                                                    </td>
                                                </tr>
                                            ))
                                        )}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        {/* ---------- EXPENSE DETAIL MODAL ---------- */}
                        <ExpensesModal
                            show={modalVisible}
                            onHide={() => setModalVisible(false)}
                            expense={selectedExpense}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ExpensesPanel;