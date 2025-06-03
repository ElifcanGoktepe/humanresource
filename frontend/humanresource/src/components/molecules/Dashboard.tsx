import ShiftRequestModal from "../../components/organism/ShiftRequestModal";
import type { ShiftRequest } from "../../components/organism/ShiftRequestModal";
import 'bootstrap/dist/css/bootstrap.min.css';
import './Dashboard.css';
import { useEffect, useState } from "react";
import axios from "axios";
import AddEmployeeModal from "../../components/organism/AddEmployeeModal.tsx";
import AssignLeavePanel from "../../components/atoms/AssignLeavePanel";

function Dashboard() {
    function parseJwt(token: string) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            return null;
        }
    }

    const [employeeList, setEmployeeList] = useState<{ fullName: string; title: string }[]>([]);
    const [managerFirstName, setManagerFirstName] = useState("");
    const [managerLastName, setManagerLastName] = useState("");
    const [titleName, setTitleName] = useState("");
    const [companyName, setCompanyName] = useState("");
    const [showShiftModal, setShowShiftModal] = useState(false);

    const token = localStorage.getItem("token");
    const payload = token ? parseJwt(token) : null;
    const managerId = payload?.userId;

    useEffect(() => {
        if (payload) {
            setManagerFirstName(payload.firstName || "");
            setManagerLastName(payload.lastName || "");
            setTitleName(payload.titleName || "");
            setCompanyName(payload.companyName || "");
        }
    }, []);

    const handleAddEmployee = async (employeeData: {
        firstName: string;
        lastName: string;
        email: string;
        phoneNumber: string;
        companyName: string;
        titleName: string;
        shiftId: number;
    }) => {
        const token = localStorage.getItem("token");
        try {
            const response = await axios.post("http://localhost:9090/api/v1/employee/add", employeeData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            alert(response.data.message);
            setEmployeeList(prev => [...prev, {
                fullName: `${employeeData.firstName} ${employeeData.lastName}`,
                title: employeeData.titleName
            }]);
        } catch (error) {
            console.error("Error adding employee:", error);
            alert("Failed to add employee.");
        }
    };

    const [showModal, setShowModal] = useState(false);

    type Leave = {
        id: number;
        startDate: string;
        endDate: string;
        description: string;
        leaveType: string;
        state: string;
        employeeId: number;
        firstName: string;
        lastName: string;
    };

    const [pendingLeaves, setPendingLeaves] = useState<Leave[]>([]);

    useEffect(() => {
        const fetchPendingLeaves = async () => {
            const token = localStorage.getItem("token");
            try {
                const response = await axios.get("http://localhost:9090/leaves/pending", {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                setPendingLeaves(response.data.data);
            } catch (error) {
                console.error("Failed to fetch pending leaves:", error);
            }
        };

        fetchPendingLeaves();
    }, []);

    const handleApprove = async (id: number) => {
        const token = localStorage.getItem("token");
        try {
            await axios.put(`http://localhost:9090/leaves/${id}/approve`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setPendingLeaves(prev => prev.filter(leave => leave.id !== id));
        } catch (error) {
            alert("Failed to approve leave.");
        }
    };

    const handleReject = async (id: number) => {
        const token = localStorage.getItem("token");
        try {
            await axios.put(`http://localhost:9090/leaves/${id}/reject`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setPendingLeaves(prev => prev.filter(leave => leave.id !== id));
        } catch (error) {
            alert("Failed to reject leave.");
        }
    };

    const [shiftEmployees, setShiftEmployees] = useState<EmployeeWithShift[]>([]);
    const fetchEmployeesWithShifts = async () => {
        const token = localStorage.getItem("token");
        try {
            const response = await axios.get("http://localhost:9090/api/v1/employees/with-shifts", {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            setShiftEmployees(response.data);
        } catch (error) {
            console.error("Failed to fetch employee shift data", error);
        }
    };

    useEffect(() => {
        fetchEmployeesWithShifts();
    }, []);

    const [selectedShift, setSelectedShift] = useState<ShiftRequest & { id: number } | null>(null);

    const handleEdit = (shift: any) => {
        setSelectedShift({
            id: shift.id,
            name: shift.name,
            startTime: shift.startTime,
            endTime: shift.endTime,
            description: shift.description,
            isRecurring: shift.isRecurring,
            daysOfWeek: shift.daysOfWeek || [],
            shiftBreaks: shift.shiftBreaks || [],
        });
        setShowShiftModal(true); // ✨ Modalı tekrar göster
    };

    const [weeklyShifts, setWeeklyShifts] = useState<any[]>([]);
    const fetchWeeklyShifts = async () => {
        const token = localStorage.getItem("token");
        try {
            const response = await axios.get("http://localhost:9090/shifts-this-week", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setWeeklyShifts(response.data);
        } catch (error) {
            console.error("Failed to fetch weekly shifts:", error);
        }
    };

    useEffect(() => {
        fetchWeeklyShifts();
    }, []);

    const handleShiftUpdate = (updatedShift: ShiftRequest) => {
        setSelectedShift(null);
        fetchEmployeesWithShifts();
        fetchWeeklyShifts();
        setShowShiftModal(false);
    };

    const handleShiftDelete = async (id: number) => {
        const token = localStorage.getItem("token");
        const confirm = window.confirm("Are you sure?");
        if (!confirm) return;

        try {
            await axios.delete(`http://localhost:9090/delete-shift/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            fetchEmployeesWithShifts();
            fetchWeeklyShifts();
            setShowShiftModal(false);
        } catch (error) {
            alert("Error deleting shift");
        }
    };

    return (
        <>
            <div className="row">
                <div className="col-md-3 box-dashboard">
                    <div className="box1-dashboard">
                        <div className="profile-settings-header">
                            <div className="col-8 profile-settings-header-name">
                                <h3>{managerFirstName} {managerLastName}</h3>
                            </div>
                            <div className="col-4 profile-settings-header-icon">
                                <img className="small-image-fixed-bar2" src="/img/profileicon.png" />
                            </div>
                        </div>
                        <div className="profile-settings-body">
                            <div>
                                <h4>{titleName}</h4>
                                <h6>{companyName}</h6>
                            </div>
                            <hr />
                            <div className="account-button-container">
                                <button className="accountbutton">
                                    Account →
                                </button>
                            </div>
                        </div>
                    </div>
                    <div className="box1-dashboard p-3">
                        <p>Employee Number: {employeeList.length}</p>
                    </div>
                </div>

                <div className="col-md-3 box-dashboard">
                    <div className="pending-leaves-panel p-1">
                        <h3>Pending Leave Requests</h3>
                        <hr />
                        {pendingLeaves.length === 0 ? (
                            <p>No leave requests yet.</p>
                        ) : (
                            <ul>
                                {pendingLeaves.map(leave => (
                                    <li key={leave.id}>
                                        <strong>{leave.leaveType}</strong> | {leave.startDate} → {leave.endDate} <br />
                                        <em>Requested by: {leave.firstName} {leave.lastName}</em>
                                        {leave.description}
                                        <div className="action-buttons">
                                            <button className="approve-button" onClick={() => handleApprove(leave.id)}>Approve</button>
                                            <button className="reject-button" onClick={() => handleReject(leave.id)}>Reject</button>
                                        </div>
                                        <hr />
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>
                    <div className="col-md-3 box-dashboard">
                        <AssignLeavePanel />
                    </div>
                </div>

                <div className="col-md-3 box-dashboard">
                    <div className="box1-dashboard p-1">
                        <div>
                            <h3>This Week's Shifts</h3>
                            {weeklyShifts.map(shift => (
                                <div key={shift.id}>
                                    <p>
                                        {shift.name} - {new Date(shift.startTime).toLocaleString()} to {new Date(shift.endTime).toLocaleString()}
                                    </p>
                                    <button onClick={() => handleEdit(shift)}>Edit</button>
                                    <button onClick={() => handleShiftDelete(shift.id)}>Delete</button>
                                </div>
                            ))}


                            <hr />
                        </div>
                        {shiftEmployees.map((emp, index) => (
                            <div key={index} className="d-flex justify-content-between align-items-center">
                                <p>
                                    {emp.firstName} {emp.lastName} <br />
                                    {new Date(emp.shift.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} - {new Date(emp.shift.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                </p>
                                <button
                                    className="edit-button"
                                    onClick={() => {
                                        setSelectedShift({
                                            id: 123,
                                            name: "Shift Name",
                                            startTime: emp.shift.startTime,
                                            endTime: emp.shift.endTime,
                                            description: "Optional",
                                            isRecurring: false,
                                            daysOfWeek: [],
                                            shiftBreaks: [],
                                        });
                                        setShowShiftModal(true);
                                    }}
                                >
                                    ✏️
                                </button>
                            </div>
                        ))}
                        <hr />
                        <button className="add-employee" onClick={() => setShowShiftModal(true)}>+ Add Shift</button>
                    </div>
                </div>

                <div className="col-md-3 box-dashboard">
                    <div className="box2-dashboard p-1">
                        <div>
                            <h3>Manage Employee</h3>
                            <hr />
                        </div>
                        <div className="fontstyle-shiftnames">
                            {employeeList.length === 0 ? (
                                <p>No employees yet.</p>
                            ) : (
                                employeeList.map((emp, index) => (
                                    <p key={index}>{emp.fullName} - {emp.title}</p>
                                ))
                            )}
                        </div>
                        <hr />
                        <div className="request-button-container">
                            <button className="add-employee" onClick={() => setShowShiftModal(true)}>Create Shift →</button>
                        </div>
                    </div>
                </div>
            </div>

            {showModal && (
                <AddEmployeeModal
                    onClose={() => setShowModal(false)}
                    onSubmit={(formData) => {
                        if (!managerId) {
                            alert("Manager ID not found.");
                            return;
                        }
                        const adaptedData = {
                            firstName: formData.firstName,
                            lastName: formData.lastName,
                            email: formData.email,
                            phoneNumber: formData.phoneNumber,
                            companyName: formData.companyName,
                            titleName: formData.titleName,
                            shiftId: formData.shiftId,
                        };
                        handleAddEmployee(adaptedData);
                        setShowModal(false);
                    }}
                />
            )}

            {showShiftModal && (
                <ShiftRequestModal
                    onClose={() => {
                        setShowShiftModal(false);
                        setSelectedShift(null);
                    }}
                    onSubmit={handleEdit}
                    onDelete={handleShiftDelete}
                    existingShift={selectedShift || undefined}
                />
            )}
        </>
    );
}

export default Dashboard;
