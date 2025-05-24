import { useState } from "react";
import './LeaveRequestModal.css';

type LeaveRequest = {
    startDate: string;
    endDate: string;
    description: string;
    leaveType: string;
};

type Props = {
    onClose: () => void;
    onSubmit: (data: LeaveRequest) => void;
};

function LeaveRequestModal({ onClose, onSubmit }: Props) {
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [description, setDescription] = useState("");
    const [leaveType, setLeaveType] = useState("Annual_Leave");

    const handleSubmit = async () => {
        if (new Date(startDate) > new Date(endDate)) {
            alert("Start date cannot be after end date.");
            return;
        }

        const leaveRequest: LeaveRequest = {
            startDate,
            endDate,
            description,
            leaveType,
        };

        try {
            const token = localStorage.getItem("token");

            const response = await fetch("http://localhost:9090/request-leave", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(leaveRequest),
            });

            if (!response.ok) {
                throw new Error("Leave request failed");
            }

            const data = await response.json();
            console.log("Leave created:", data);

            onSubmit(data);
            onClose();
        } catch (error) {
            if (error instanceof Error) {
                alert("Failed to create leave: " + error.message);
            } else {
                alert("Unknown error occurred.");
            }
        }
    };

    return (
        <div className="overlay">
            <div className="modal-board">
                <h3>Create Leave Request</h3>

                <label>Start Date:</label>
                <input type="datetime-local" value={startDate} onChange={e => setStartDate(e.target.value)} />

                <label>End Date:</label>
                <input type="datetime-local" value={endDate} onChange={e => setEndDate(e.target.value)} />

                <label>Leave Type:</label>
                <select value={leaveType} onChange={e => setLeaveType(e.target.value)}>
                    <option value="Annual_Leave">Annual Leave</option>
                    <option value="Maternity_Leave">Maternity Leave</option>
                    <option value="Paid_Leave">Paid Leave</option>
                    <option value="Unpaid_Leave">Unpaid Leave</option>
                    <option value="Sick_Leave">Sick Leave</option>
                    <option value="Study_Leave">Study Leave</option>
                    <option value="Compassionate_Leave">Compassionate Leave</option>
                    <option value="Wedding_Leave">Wedding Leave</option>
                    <option value="Paternity_Leave">Paternity Leave</option>
                    <option value="Parental_Leave">Parental Leave</option>
                    <option value="Military_Leave">Military Leave</option>
                    <option value="Excused_Leave">Excused Leave</option>
                    <option value="Travel_Leave">Travel Leave</option>
                    <option value="Job_Search_Leave">Job Search Leave</option>
                </select>

                <label>Description:</label>
                <textarea value={description} onChange={e => setDescription(e.target.value)} rows={3} />

                <button onClick={handleSubmit} className="submit-btn">Submit Request</button>
                <button onClick={onClose} className="close-btn">Cancel</button>
            </div>
        </div>
    );
}

export default LeaveRequestModal;
