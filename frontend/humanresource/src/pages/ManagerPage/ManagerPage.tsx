import 'bootstrap/dist/css/bootstrap.min.css';
import './ManagerPage.css';


import { useEffect, useState } from "react";

import Dashboard from  "../../components/molecules/Dashboard.tsx";
import CompanyPage from  "../../components/molecules/CompanyPage.tsx";
import Employee from  "../../components/molecules/Employee.tsx";
import Salary from  "../../components/molecules/Salary.tsx";
import Shift from  "../../components/molecules/Shift.tsx";
import Assignment from  "../../components/molecules/Assignment.tsx";
import Settings from  "../../components/molecules/Settings.tsx";


function ManagerPage() {
    const [selectedTab, setSelectedTab] = useState('Dashboard');
    const [managerFirstName, setManagerFirstName] = useState("");
    const today = new Date();
    const days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    const dayIndex = today.getDay();
    const dayName = days[dayIndex];
    const dateString = today.toLocaleDateString("en-US");

    function parseJwt(token: string) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            return null;
        }
    }

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            const payload = parseJwt(token);
            if (payload && payload.firstName) {
                setManagerFirstName(payload.firstName);
            }
        }
    }, []);

    const renderContent = () => {
        switch (selectedTab) {
            case 'Dashboard':
                return <Dashboard />;
            case 'Company Page':
                return <CompanyPage />;
            case 'Employee':
                return <Employee />;
            case 'Salary':
                return <Salary />;
            case 'Shift':
                return <Shift />;
            case 'Assignment':
                return <Assignment />;
            case 'Settings' :
                return <Settings/>
            default:
                return <Dashboard />;
        }
    };

    return (
        <>
        <div className="row">
            <div className="col-2 fixed-side-bar">
                <div className="fixed-bar-image">
                    <img className="logo-left-menu" src="/img/logo1.png" alt="logo" />
                </div>
                <hr />
                <div className="fixed-bar-button-container">
                    <button className="fixed-bar-buttons" onClick={() => setSelectedTab('Dashboard')}>
                        <img className="small-image-fixed-bar" src="/img/adminpage.png" alt="Dashboard" />
                        Dashboard
                    </button>
                    <button className="fixed-bar-buttons" onClick={() => setSelectedTab('Company Page')}>
                        <img className="small-image-fixed-bar" src="/img/adminpage.png" alt="Company Page" />
                        Company Page
                    </button>
                    <button className="fixed-bar-buttons" onClick={() => setSelectedTab('Employee')}>
                        <img className="small-image-fixed-bar" src="/img/employee.png" alt="Employee" />
                        Employee
                    </button>
                    <button className="fixed-bar-buttons" onClick={() => setSelectedTab('Salary')}>
                        <img className="small-image-fixed-bar" src="/img/expens.png" alt="Salary" />
                        Salary
                    </button>
                    <button className="fixed-bar-buttons" onClick={() => setSelectedTab('Shift')}>
                        <img className="small-image-fixed-bar" src="/img/shift.png" alt="Shift" />
                        Shift
                    </button>
                    <button className="fixed-bar-buttons" onClick={() => setSelectedTab('Assignment')}>
                        <img className="small-image-fixed-bar" src="/img/assets.png" alt="Assignment" />
                        Assignment
                    </button>
                </div>

                <div className="bottom-bar">
                    <hr />
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/profileicon.png" />
                        Profile
                    </button>
                    <button className="fixed-bar-buttons" onClick={() => setSelectedTab('Settings')}>
                        <img className="small-image-fixed-bar" src="/img/settingsicon.png" alt="Settings" />
                        Setting
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
                    <div className="col-md-3 box-dashboard">
                        <div className="pending-leaves-panel p-1">
                            <h3>Pending Leave Requests</h3>
                            <hr/>
                            {pendingLeaves.length === 0 ? (
                                <p>No leave requests yet.</p>
                            ) : (
                                <ul>
                                    {pendingLeaves.map(leave => (
                                        <li key={leave.id}>
                                            <strong>{leave.leaveType}</strong> | {leave.startDate} → {leave.endDate} <br />
                                            <em>
                                                Requested by: {leave.firstName} {leave.lastName}
                                            </em>
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
                    <div className="col-md-3 box-dashboard">
                        <div className="box1-dashboard p-1">
                            <div>
                                <h3>Today's Shift List</h3>
                                <hr/>
                            </div>
                            <div className="row">
                                <div className="col-5 fontstyle-shiftnames">
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
                    </div>
                    <div className="col-md-3 box-dashboard">
                        <div className="box2-dashboard p-1">
                            <div>
                                <h3>Manage Employee</h3>
                                <hr/>
                            </div>
                            <div>
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
