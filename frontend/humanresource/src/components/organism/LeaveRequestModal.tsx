import { useState } from "react";
import './LeaveRequestModal.css';

type LeaveRequest = {
    startDate: string;
    endDate: string;
    description: string;
    leaveType: string;
    state: string;
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
    const state = "Pending"; // Default

    const handleSubmit = async () => {
        if (new Date(startDate) > new Date(endDate)) {
            alert("Start date cannot be after end date.");
            return;
        }

        const leaveRequest = {
            startDate,
            endDate,
            description,
            leaveType,
            state
        };

        try {
            const response = await fetch("http://localhost:9090/request-leave", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    // Eğer JWT kullanıyorsan buraya token da ekle:
                    // "Authorization": "Bearer " + localStorage.getItem("token")
                },
                body: JSON.stringify(leaveRequest)
            });

            if (!response.ok) {
                throw new Error("Leave request failed");
            }

            const data = await response.json();
            console.log("Leave created:", data);

            // Başarılı olduğunda modal kapanır
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
                    <option>Annual Leave</option>
                    <option>Maternity Leave</option>
                    <option>Paid Leave</option>
                    <option>Unpaid Leave</option>
                    <option>Sick Leave</option>
                    <option>Study Leave</option>
                    <option>Compassionate Leave</option>
                    <option>Wedding Leave</option>
                    <option>Paternity Leave</option>
                    <option>Parental Leave</option>
                    <option>Military Leave</option>
                    <option>Excused Leave</option>
                    <option>Travel Leave</option>
                    <option>Job Search Leave</option>
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
