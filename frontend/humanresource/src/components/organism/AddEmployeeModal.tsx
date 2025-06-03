import { useEffect, useState } from "react";
import './AddEmployeeModal.css';

type Shift = {
    id: number;
    name: string;
    startTime: string;
    endTime: string;
};

type Props = {
    onClose: () => void;
    onSubmit: (data: {
        firstName: string;
        lastName: string;
        email: string;
        phoneNumber: string;
        companyName: string;
        titleName: string;
        shiftId: number;
    }) => void;
};

function AddEmployeeModal({ onClose, onSubmit }: Props) {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setemail] = useState("");
    const [phoneNumber, setphoneNumber] = useState("");
    const [companyName, setCompanyName] = useState("");
    const [titleName, setTitleName] = useState("");
    const [shiftId, setShiftId] = useState<number | null>(null);
    const [shiftList, setShiftList] = useState<Shift[]>([]);

    useEffect(() => {
        const fetchShifts = async () => {
            const token = localStorage.getItem("token");
            try {
                const res = await fetch("http://localhost:9090/api/v1/shift/list", {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                const data = await res.json();
                setShiftList(data.data);
            } catch (err) {
                console.error("Failed to fetch shift list", err);
            }
        };
        fetchShifts();
    }, []);

    const handleSubmit = () => {
        if (shiftId === null) {
            alert("Please select a shift for the employee.");
            return;
        }

        const employeeData = {
            firstName,
            lastName,
            email,
            phoneNumber,
            companyName,
            titleName,
            shiftId
        };

        onSubmit(employeeData);
        onClose();
    };

    return (
        <div className="overlay-employee">
            <div className="modal-board2">
                <h3>Add New Employee</h3>

                <label>First Name:</label>
                <input type="text" value={firstName} onChange={e => setFirstName(e.target.value)} />

                <label>Last Name:</label>
                <input type="text" value={lastName} onChange={e => setLastName(e.target.value)} />

                <label>Work Email:</label>
                <input type="email" value={email} onChange={e => setemail(e.target.value)} />

                <label>Work Phone:</label>
                <input type="text" value={phoneNumber} onChange={e => setphoneNumber(e.target.value)} />

                <label>Company Name:</label>
                <input type="text" value={companyName} onChange={e => setCompanyName(e.target.value)} />

                <label>Title:</label>
                <input type="text" value={titleName} onChange={e => setTitleName(e.target.value)} />

                <label>Select Shift:</label>
                <select value={shiftId ?? ''} onChange={e => setShiftId(Number(e.target.value))}>
                    <option value="">-- Select Shift --</option>
                    {shiftList.map((shift) => (
                        <option key={shift.id} value={shift.id}>
                            {shift.name} ({new Date(shift.startTime).toLocaleTimeString()} - {new Date(shift.endTime).toLocaleTimeString()})
                        </option>
                    ))}
                </select>

                <button onClick={handleSubmit} className="submit-btn">Submit</button>
                <button onClick={onClose} className="close-btn">Cancel</button>
            </div>
        </div>
    );
}

export default AddEmployeeModal;
