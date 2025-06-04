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
    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [companyName, setCompanyName] = useState("");
    const [titleName, setTitleName] = useState("");
    const [shiftId, setShiftId] = useState<number | null>(null);
    const [shiftList, setShiftList] = useState<Shift[]>([]);

    useEffect(() => {
        const fetchShifts = async () => {
            const token = localStorage.getItem("token");
            try {
                const response = await fetch("http://localhost:9090/dev/v1/shift/list", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const result = await response.json();
                setShiftList(result.data);
            } catch (err) {
                console.error("Failed to fetch shift list", err);
            }
        };

        fetchShifts();
    }, []);

    const handleSubmit = () => {
        if (!shiftId) {
            alert("Please select a shift for the employee.");
            return;
        }

        onSubmit({
            firstName,
            lastName,
            email,
            phoneNumber,
            companyName,
            titleName,
            shiftId,
        });

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
                <input type="email" value={email} onChange={e => setEmail(e.target.value)} />

                <label>Work Phone:</label>
                <input type="text" value={phoneNumber} onChange={e => setPhoneNumber(e.target.value)} />

                <label>Company Name:</label>
                <input type="text" value={companyName} onChange={e => setCompanyName(e.target.value)} />

                <label>Title:</label>
                <input type="text" value={titleName} onChange={e => setTitleName(e.target.value)} />

                <label>Select Shift:</label>
                <select value={shiftId ?? ''} onChange={e => setShiftId(Number(e.target.value))}>
                    <option value="">-- Select Shift --</option>
                    {shiftList.map((shift) => (
                        <option key={shift.id} value={shift.id}>
                            {shift.name} (
                            {new Date(shift.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} -
                            {new Date(shift.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })})
                        </option>
                    ))}
                </select>

                <div className="button-group">
                    <button onClick={handleSubmit} className="submit-btn">Submit</button>
                    <button onClick={onClose} className="close-btn">Cancel</button>
                </div>
            </div>
        </div>
    );
}

export default AddEmployeeModal;
