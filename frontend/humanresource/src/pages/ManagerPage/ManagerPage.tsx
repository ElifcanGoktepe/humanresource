import 'bootstrap/dist/css/bootstrap.min.css';
import './ManagerPage.css';
import { useEffect, useState } from "react";
import axios from "axios";
import AddEmployeeModal from "../../components/organism/AddEmployeeModal.tsx";

function ManagerPage() {
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
    const today = new Date();
    const days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    const dayIndex = today.getDay();
    const dayName = days[dayIndex];
    const dateString = today.toLocaleDateString("en-US");


    const [employeeList, setEmployeeList] = useState<string[]>([]);
    const [managerFirstName, setManagerFirstName] = useState("");
    const [managerLastName, setManagerLastName] = useState("");
    const [titleName, setTitleName] = useState("");
    const [companyName, setCompanyName] = useState("");

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            const payload = parseJwt(token);
            if (payload) {
                setManagerFirstName(payload.firstName || "");
                setManagerLastName(payload.lastName || "");
                setTitleName(payload.titleName || "");
                setCompanyName(payload.companyName || "");
            }
        }
    }, []);

    const handleAddEmployee = async (employeeData: {
        firstName: string;
        lastName: string;
        emailWork: string;
        phoneWork: string;
        companyName: string;
        titleName: string;
    }) => {
        const token = localStorage.getItem("token");
        console.log("Token:", token);
        try {
            const response = await axios.post("http://localhost:9090/add-employee", employeeData, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            alert(response.data.message);
            // Yeni eklenen çalışanı listeye ekle
            setEmployeeList(prev => [...prev, `${employeeData.firstName} ${employeeData.lastName}`]);
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
            // Sayfayı güncelle
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

    return (
        <>
        <div className="row">
            <div className="col-2 fixed-side-bar">
                <div className="fixed-bar-image">
                    <img className="logo-left-menu" src="/img/logo1.png" alt="logo" />

                </div>
                <hr/>
                <div className="fixed-bar-button-container">
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/adminpage.png" />
                        Company Page
                    </button>
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/employee.png" />
                        Employee
                    </button>
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/expens.png" />
                        Salary
                    </button>
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/shift.png" />
                        Shift
                    </button>
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/assets.png" />
                        Assignment
                    </button>
                </div>
                <hr/>
                <div className="bottom-bar">
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/profileicon.png" />
                        Profile
                    </button>
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/settingsicon.png" />
                        Settings
                    </button>
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/helpicon.png" />
                        Help
                    </button>
                </div>
            </div>
            <div className="col-10">
                <div className="manager-page-header">
                    <h2>Hello {managerFirstName}!</h2>
                    <h3>Today's Date: {dateString}, {dayName}</h3>
                    <hr/>
                </div>
                <div className="row">
                    <div className="col-3 box-dashboard">
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
                                <hr/>
                                <div className="account-button-container">
                                    <button className="accountbutton">
                                        Account →
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div className="box1-dashboard p-3">
                            <p> Employee Number : 20 </p>
                        </div>
                    </div>
                    <div className="col-3 box-dashboard">
                        <div className="pending-leaves-panel row p-1">
                            <h3>Pending Leave Requests</h3>
                            <hr/>
                            {pendingLeaves.length === 0 ? (
                                <p>No leave requests yet.</p>
                            ) : (
                                <ul>
                                    {pendingLeaves.map(leave => (
                                        <li key={leave.id}>
                                            <strong>{leave.leaveType}</strong> | {leave.startDate} → {leave.endDate} <br />
                                            {leave.description}
                                            <div className="action-buttons">
                                                <button
                                                    className="approve-button"
                                                    onClick={() => handleApprove(leave.id)}
                                                >
                                                    Approve
                                                </button>
                                                <button
                                                    className="reject-button"
                                                    onClick={() => handleReject(leave.id)}
                                                >
                                                    Reject
                                                </button>
                                            </div>
                                            <hr />
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>

                    </div>

                    <div className="col-3 box-dashboard">
                        <div className="box1-dashboard row p-1">
                            <h3>Today's Shift List</h3>
                            <hr/>
                            <div className="col-7 fontstyle-shiftnames">
                                FirstName LastName1
                                FirstName LastName2
                                FirstName LastName3
                                FirstName LastName4
                                FirstName LastName5
                                FirstName LastName6
                            </div>
                            <div className="col-5 fontstyle-shifthours">
                                08:00-12:00
                                08:00-12:00
                                13:00-17:00
                                13:00-17:00
                                18:00-22:00
                                18:00-22:00
                            </div>
                        </div>
                    </div>
                    <div className="col-3 box-dashboard">
                        <div className="box2-dashboard row p-1">
                            <h3>Manage Employee</h3>
                            <hr/>
                            <div className="fontstyle-shiftnames">
                                <div className="fontstyle-shiftnames">
                                    {employeeList.length === 0 ? (
                                        <p>No employees yet.</p>
                                    ) : (
                                        employeeList.map((name, index) => <p key={index}>{name}</p>)
                                    )}
                                </div>
                            </div>
                            <hr/>
                            <div className="request-button-container">
                                <button className="add-employee" onClick={() => setShowModal(true)}>
                                    Add Employee →
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
            {showModal && (
                <AddEmployeeModal
                    onClose={() => setShowModal(false)}
                    onSubmit={(formData) => {
                        handleAddEmployee(formData);
                        setShowModal(false);
                    }}
                />
            )}

        </>
    );
}

export default ManagerPage;
