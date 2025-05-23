import { useState } from "react";
import './AddEmployeeModal.css';

type Props = {
    onClose: () => void;
    onSubmit: (data: {
        firstName: string;
        lastName: string;
        emailWork: string;
        phoneWork: string;
        companyName: string;
        titleName: string;
    }) => void;
};

function AddEmployeeModal({ onClose, onSubmit }: Props) {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [emailWork, setEmailWork] = useState("");
    const [phoneWork, setPhoneWork] = useState("");
    const [companyName, setCompanyName] = useState("");
    const [titleName, setTitleName] = useState("");

    const handleSubmit = () => {
        const employeeData = {
            firstName,
            lastName,
            emailWork,
            phoneWork,
            companyName,
            titleName
        };

        onSubmit(employeeData); // ✅ sadece veriyi parent'a iletir
        onClose(); // ✅ modal kapanır
    };

    return (
        <div className="overlay-employee">
            <div className="modal-board1">
                <h3>Add New Employee</h3>

                <label>First Name:</label>
                <input type="text" value={firstName} onChange={e => setFirstName(e.target.value)} />

                <label>Last Name:</label>
                <input type="text" value={lastName} onChange={e => setLastName(e.target.value)} />

                <label>Work Email:</label>
                <input type="email" value={emailWork} onChange={e => setEmailWork(e.target.value)} />

                <label>Work Phone:</label>
                <input type="text" value={phoneWork} onChange={e => setPhoneWork(e.target.value)} />

                <label>Company Name:</label>
                <input type="text" value={companyName} onChange={e => setCompanyName(e.target.value)} />

                <label>Title:</label>
                <input type="text" value={titleName} onChange={e => setTitleName(e.target.value)} />

                <button onClick={handleSubmit} className="submit-btn">Submit</button>
                <button onClick={onClose} className="close-btn">Cancel</button>
            </div>
        </div>
    );
}

export default AddEmployeeModal;
