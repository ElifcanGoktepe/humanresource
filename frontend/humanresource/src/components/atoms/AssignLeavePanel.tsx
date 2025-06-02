import { useEffect, useState } from "react";
import axios from "axios";

const AssignLeavePanel = () => {
    const [employees, setEmployees] = useState<any[]>([]);

    const fetchEmployees = async () => {
        const token = localStorage.getItem("token");
        const res = await axios.get("http://localhost:9090/actives-employees", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        setEmployees(res.data.data);
    };

    const assignLeave = async (id: number) => {
        const token = localStorage.getItem("token");
        await axios.put(`http://localhost:9090/leaves/${id}/assign`, {}, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        alert("Leave assigned.");
    };

    useEffect(() => {
        fetchEmployees();
    }, []);

    return (
        <div className="box-dashboard p-3">
            <h3>Assign Leave to Employee</h3>
            <hr />
            {employees.length === 0 ? (
                <p>No employees available.</p>
            ) : (
                <ul>
                    {employees.map((emp) => (
                        <li key={emp.id}>
                            {emp.firstName} {emp.lastName} - {emp.titleName}
                            <button className="btn btn-sm btn-success ms-3" onClick={() => assignLeave(emp.id)}>
                                Assign 20 Days
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default AssignLeavePanel;