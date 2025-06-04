import { useState, useEffect } from "react";
import './ShiftRequestModal.css';

export type ShiftRequest = {
    name: string;
    startTime: string;
    endTime: string;
    description: string;
    isRecurring: boolean;
    daysOfWeek: number[];
    shiftBreaks: { startTime: string; endTime: string }[];
    employeeIds: number[];
};

type Employee = {
    id: number;
    firstName: string;
    lastName: string;
};

type Props = {
    onClose: () => void;
    onSubmit: (data: ShiftRequest) => void;
    existingShift?: ShiftRequest & { id: number };
    onDelete?: (id: number) => void;
};

function ShiftRequestModal({ onClose, onSubmit, existingShift, onDelete }: Props) {
    const [tab, setTab] = useState<'recurring' | 'specific'>('recurring');
    const [name, setName] = useState("");
    const [startTime, setStartTime] = useState("");
    const [endTime, setEndTime] = useState("");
    const [description, setDescription] = useState("");
    const [shiftBreaks, setShiftBreaks] = useState<{ startTime: string; endTime: string }[]>([]);
    const [selectedDays, setSelectedDays] = useState<number[]>([]);
    const [employeeIds, setEmployeeIds] = useState<number[]>([]);
    const [employeeList, setEmployeeList] = useState<Employee[]>([]);

    useEffect(() => {
        const fetchEmployees = async () => {
            const token = localStorage.getItem("token");
            try {
                const res = await fetch("http://localhost:9090/api/v1/employees", {
                    headers: { Authorization: `Bearer ${token}` }
                });
                const result = await res.json();
                setEmployeeList(result.data); // ⬅️ API result
            } catch (err) {
                console.error("Failed to fetch employees", err);
            }
        };

        fetchEmployees();
    }, []);

    useEffect(() => {
        if (existingShift) {
            setName(existingShift.name);
            setStartTime(existingShift.startTime);
            setEndTime(existingShift.endTime);
            setDescription(existingShift.description);
            setSelectedDays(existingShift.daysOfWeek);
            setShiftBreaks(existingShift.shiftBreaks);
            setTab(existingShift.isRecurring ? 'recurring' : 'specific');
            setEmployeeIds(existingShift.employeeIds);
        }
    }, [existingShift]);

    const toggleDay = (day: number) => {
        setSelectedDays(prev =>
            prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
        );
    };

    const addBreak = () => {
        setShiftBreaks([...shiftBreaks, { startTime: "", endTime: "" }]);
    };

    const updateBreak = (index: number, field: "startTime" | "endTime", value: string) => {
        const updated = [...shiftBreaks];
        updated[index][field] = value;
        setShiftBreaks(updated);
    };

    const removeBreak = (index: number) => {
        setShiftBreaks(shiftBreaks.filter((_, i) => i !== index));
    };

    const handleSubmit = async () => {
        const nowDate = new Date().toISOString().split("T")[0];
        const formattedStartTime = tab === "recurring" ? `${nowDate}T${startTime}` : startTime;
        const formattedEndTime = tab === "recurring" ? `${nowDate}T${endTime}` : endTime;

        const payload = {
            name,
            startTime: formattedStartTime,
            endTime: formattedEndTime,
            description,
            isRecurring: tab === 'recurring',
            daysOfWeek: tab === 'recurring' ? selectedDays : [],
            shiftBreaks: shiftBreaks.map(b => ({
                startTime: tab === "recurring" ? `${nowDate}T${b.startTime}` : b.startTime,
                endTime: tab === "recurring" ? `${nowDate}T${b.endTime}` : b.endTime,
            })),
            employeeIds
        };

        try {
            const token = localStorage.getItem("token");
            const url = existingShift
                ? "http://localhost:9090/dev/v1/shift/update"
                : "http://localhost:9090/dev/v1/shift/add";
            const method = existingShift ? "PUT" : "POST";

            const response = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ ...(existingShift ? { shiftId: existingShift.id } : {}), ...payload })
            });

            if (!response.ok) throw new Error("Shift operation failed");
            const data = await response.json();
            onSubmit(data);
            onClose();
        } catch (error: any) {
            alert("Failed: " + error.message);
        }
    };

    const handleDelete = async () => {
        if (!existingShift || !onDelete) return;
        if (!window.confirm("Are you sure you want to delete this shift?")) return;

        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`http://localhost:9090/delete-shift/${existingShift.id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!response.ok) throw new Error("Delete failed");
            onDelete(existingShift.id);
            onClose();
        } catch (error: any) {
            alert("Failed to delete shift: " + error.message);
        }
    };

    return (
        <div className="overlay1">
            <div className="modal-board1">
                <div className="tab-buttons">
                    <button onClick={() => setTab('recurring')} className={tab === 'recurring' ? 'active-tab' : ''}>
                        Recurring Shift
                    </button>
                    <button onClick={() => setTab('specific')} className={tab === 'specific' ? 'active-tab' : ''}>
                        Specific Date Shift
                    </button>
                </div>

                <h3>{existingShift ? "Update Shift" : "Create Shift"}</h3>

                <label>Shift Name:</label>
                <input type="text" value={name} onChange={e => setName(e.target.value)} />

                {tab === 'recurring' ? (
                    <>
                        <label>Start Time (HH:mm):</label>
                        <input type="time" value={startTime} onChange={e => setStartTime(e.target.value)} />

                        <label>End Time (HH:mm):</label>
                        <input type="time" value={endTime} onChange={e => setEndTime(e.target.value)} />

                        <label>Select Days of the Week:</label>
                        <div className="days-of-week">
                            {["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"].map((day, index) => (
                                <label key={index}>
                                    <input
                                        type="checkbox"
                                        checked={selectedDays.includes(index + 1)}
                                        onChange={() => toggleDay(index + 1)}
                                    />
                                    {day}
                                </label>
                            ))}
                        </div>
                    </>
                ) : (
                    <>
                        <label>Start Date & Time:</label>
                        <input type="datetime-local" value={startTime} onChange={e => setStartTime(e.target.value)} />

                        <label>End Date & Time:</label>
                        <input type="datetime-local" value={endTime} onChange={e => setEndTime(e.target.value)} />
                    </>
                )}

                <label>Description:</label>
                <textarea value={description} onChange={e => setDescription(e.target.value)} />

                <label>Assign Employees:</label>
                <select
                    multiple
                    value={employeeIds.map(String)}
                    onChange={(e) =>
                        setEmployeeIds(Array.from(e.target.selectedOptions, option => Number(option.value)))
                    }
                >
                    {employeeList.map(emp => (
                        <option key={emp.id} value={emp.id}>
                            {emp.firstName} {emp.lastName}
                        </option>
                    ))}
                </select>

                <label>Shift Breaks:</label>
                {shiftBreaks.map((brk, i) => (
                    <div key={i} className="break-row">
                        <span>Break {i + 1}</span>
                        <input
                            type={tab === 'recurring' ? "time" : "datetime-local"}
                            step="900"
                            value={brk.startTime}
                            onChange={e => updateBreak(i, "startTime", e.target.value)}
                        />
                        <input
                            type={tab === 'recurring' ? "time" : "datetime-local"}
                            step="900"
                            value={brk.endTime}
                            onChange={e => updateBreak(i, "endTime", e.target.value)}
                        />
                        <button onClick={() => removeBreak(i)} className="delete-break-btn">❌</button>
                    </div>
                ))}
                <button onClick={addBreak} className="add-break-btn1">+ Add Break</button>

                <button onClick={handleSubmit} className="submit-btn1">
                    {existingShift ? "Update Shift" : "Create Shift"}
                </button>
                {existingShift && (
                    <button onClick={handleDelete} className="delete-btn1">Delete Shift</button>
                )}
                <button onClick={onClose} className="close-btn1">Cancel</button>
            </div>
        </div>
    );
}

export default ShiftRequestModal;
