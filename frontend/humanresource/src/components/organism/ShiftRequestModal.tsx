import { useState } from "react";
import './ShiftRequestModal.css';

export type ShiftRequest = {
    name: string;
    startTime: string;
    endTime: string;
    description: string;
    shiftBreaks: { startTime: string; endTime: string }[];
};

type Props = {
    onClose: () => void;
    onSubmit: (data: ShiftRequest) => void;
};


function ShiftRequestModal({ onClose, onSubmit }: Props) {
    const [name, setName] = useState("");
    const [startTime, setStartTime] = useState("");
    const [endTime, setEndTime] = useState("");
    const [description, setDescription] = useState("");
    const [shiftBreaks, setShiftBreaks] = useState<ShiftRequest["shiftBreaks"]>([]);

    const addBreak = () => {
        setShiftBreaks([...shiftBreaks, { startTime: "", endTime: "" }]);
    };

    const updateBreak = (index: number, field: "startTime" | "endTime", value: string) => {
        const updated = [...shiftBreaks];
        updated[index][field] = value;
        setShiftBreaks(updated);
    };


    const removeBreak = (index: number) => {
        const updated = shiftBreaks.filter((_, i) => i !== index);
        setShiftBreaks(updated);
    };

    const handleSubmit = async () => {
        if (new Date(startTime) > new Date(endTime)){
            alert("Start time cannot be after and time.");
            return;
        }
        const  shiftRequest: ShiftRequest = {
            name,
            startTime,
            endTime,
            description,
            shiftBreaks,
        };

        try {
            const token = localStorage.getItem("token");
            const response = await fetch("http://localhost:9090/add-shift", {

                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(shiftRequest),
            });
            if (!response.ok) {
                throw new Error("Shift request failed");
            }

            const data = await response.json();
            console.log("Shift created:", data);

            onSubmit(data);
            onClose();
        } catch (error) {
            if (error instanceof Error) {
                alert("Failed to create shift: " + error.message);
            } else {
                alert("Unknown error occurred.");
            }
        }
    };

    return (
        <div className="overlay1">
            <div className="modal-board1">
                <h3>Create Shift Request</h3>

                <label>Shift Name:</label>
                <input type="text" value={name} onChange={e => setName(e.target.value)} />

                <label>Start Time:</label>
                <input type="datetime-local" step="900" value={startTime} onChange={e => setStartTime(e.target.value)} />

                <label>End Time:</label>
                <input type="datetime-local" step="900" value={endTime} onChange={e => setEndTime(e.target.value)} />

                <label>Description:</label>
                <textarea className="description-box" rows={3} value={description} onChange={e => setDescription(e.target.value)} />

                <label>Shift Breaks:</label>
                {shiftBreaks.map((brk, i) => (
                    <div key={i} className="break-row">
                        <span>Break {i + 1}</span>
                        <input
                            type="datetime-local"
                            step="900"
                            value={brk.startTime}
                            onChange={e => updateBreak(i, "startTime", e.target.value)}
                        />
                        <input
                            type="datetime-local"
                            step="900"
                            value={brk.endTime}
                            onChange={e => updateBreak(i, "endTime", e.target.value)}
                        />
                        <button onClick={() => removeBreak(i)} className="delete-break-btn">❌</button>
                    </div>
                ))}
                <button onClick={addBreak} className="add-break-btn1">+ Add Break</button>

                <button onClick={handleSubmit} className="submit-btn1">Submit Shift</button>
                <button onClick={onClose} className="close-btn1">Cancel</button>
            </div>
        </div>
    );
}
export default ShiftRequestModal;
