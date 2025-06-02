import 'bootstrap/dist/css/bootstrap.min.css';
import './EmployePage.css';
import LeaveRequestModal from "../../components/organism/LeaveRequestModal.tsx";
import {
    Chart as ChartJS,
    ArcElement,
    Tooltip,
    Legend
} from 'chart.js';
import {useEffect, useState} from "react";
import ShiftRequestModal from "../../components/organism/ShiftRequestModal.tsx";
import axios from "axios";
import {Doughnut} from "react-chartjs-2";
import Settings from "../../components/molecules/Settings.tsx";


ChartJS.register(ArcElement, Tooltip, Legend);
function EmployeePage() {
    const today = new Date();
    const days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    const dayIndex = today.getDay();
    const dayName = days[dayIndex];
    const dateString = today.toLocaleDateString("en-US");

    const [chartData, setChartData] = useState({ total: 0, used: 0, remaining: 0 });
    const [showModal, setShowModal] = useState(false);
    const [showShiftModal, setShowShiftModal] = useState(false);
    const [selectedTab, setSelectedTab] = useState('Dashboard');

    type LeaveChartProps = {
        used: number;
        remaining: number;
    };

    const LeaveChart = ({ used, remaining }: LeaveChartProps) => {
        const data = {
            labels: ['Used', 'Remaining'],
            datasets: [
                {
                    data: [used, remaining],
                    backgroundColor: ['#00796B', '#00FFF0'],
                },
            ],
        };

        return (
            <div style={{ width: '100%', height: '100%' }}>
                <Doughnut data={data} />
            </div>
        );
    };
    useEffect(() => {
        const fetchApprovedLeaves = async () => {
            const token = localStorage.getItem("token");
            try {
                const response = await axios.get("http://localhost:9090/leaves/approved", {
                    headers: { Authorization: `Bearer ${token}` }
                });
                const approvedLeaves = response.data.data;

                const used = approvedLeaves.reduce((acc: number, leave: any) => {
                    const start = new Date(leave.startDate);
                    const end = new Date(leave.endDate);
                    const days = (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);
                    return acc + days;
                }, 0);

                const total = 20;
                const remaining = total - used;

                setChartData({ total, used, remaining });

            } catch (error) {
                console.error("Failed to fetch approved leaves:", error);
            }
        };

        fetchApprovedLeaves();
    }, []);
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
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [titleName, setTitleName] = useState("");
    const [companyName, setCompanyName] = useState("");

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            const payload = parseJwt(token);
            if (payload) {
                setFirstName(payload.firstName || "");
                setLastName(payload.lastName || "");
                setTitleName(payload.titleName || "");
                setCompanyName(payload.companyName || "");
            }
        }
    }, []);
    type Shift = {
        id: number;
        name: string;
        startTime: string;
        endTime: string;
        description: string;
        employeeIds: number[];
        shiftBreakIds: number[];
    };

    const [shifts, setShifts] = useState<Shift[]>([]);

    useEffect(() => {
        const fetchShifts = async () => {
            try {
                const response = await axios.get("http://localhost:9090/list-shift");
                setShifts(response.data.data);
            } catch (error) {
                console.error("Error fetching shifts", error);
            }
        };
        fetchShifts();
    }, []);

    return (
        <div className="row">
            <div className="col-2 fixed-side-bar">
                <div className="fixed-bar-image">
                    <img className="logo-left-menu" src="/img/logo1.png" alt="logo" />
                </div>
         <hr/>
                <div className="fixed-bar-button-container">
                    <button className="fixed-bar-buttons" onClick={() => setSelectedTab('Dashboard')}>
                        <img className="small-image-fixed-bar" src="/img/adminpage.png" alt="Dashboard" />
                        Dashboard
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
                {selectedTab === 'Dashboard' && (
                    <>
                        <div className="employee-page-header">
                            <h2>Hello {firstName}!</h2>
                            <h3>Today's Date: {dateString}, {dayName}</h3>
                            <hr/>
                        </div>
                        <div className="row">
                            <div className="col-3 box-dashboard">
                                <div className="box1-dashboard">
                                    <div className="profile-settings-header">
                                        <div className="col-8 profile-settings-header-name">
                                            <h3>{firstName} {lastName}</h3>
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
                                    <p> Manager Name : {} </p>
                                </div>
                            </div>
                            <div className="col-3 box-dashboard">
                                <div className="box1-dashboard">
                                    <div className="leave-settings-body">
                                        <div>
                                            <LeaveChart used={chartData.used} remaining={chartData.remaining} />
                                            <p> Total : {chartData.total} </p>
                                            <p> Used :  {chartData.used} </p>
                                            <p> Remaining : {chartData.remaining}</p>
                                        </div>
                                        <hr/>
                                        <div className="request-button-container">
                                            <button className="accountbutton" onClick={() => setShowModal(true)}>
                                                Request →
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="col-3 box-dashboard">
                                <div className="box1-dashboard p-2">
                                    <div>
                                        <h3>Weekly Shift List</h3>
                                        <hr/>
                                    </div>
                                    <div className="row">
                                        <div className="col-7 fontstyle-shiftnames mb-2">
                                            {shifts.length === 0 ? (
                                                <p>No shifts assigned.</p>
                                            ) : (
                                                shifts.map((shift) => (
                                                    <div key={shift.id} style={{ marginBottom: "10px" }}>
                                                        <strong>{shift.name}</strong><br />
                                                        {new Date(shift.startTime).toLocaleString()} - {new Date(shift.endTime).toLocaleString()}
                                                        <hr />
                                                    </div>
                                                ))
                                            )}
                                        </div>
                                    </div>


                                    <hr/>
                                    <div className="request-button-container mb-2">
                                        <button className="accountbutton" onClick={() => setShowShiftModal(true)}>
                                            Request →
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div className="col-3 box-dashboard">

                            </div>
                        </div>
                    </>
                )}

                {selectedTab === 'Settings' && (
                    <div className="p-4">
                        <Settings />
                    </div>
                )}
            </div>

            {showModal && (
                <LeaveRequestModal
                    onClose={() => setShowModal(false)}
                    onSubmit={(data: any) => {
                        console.log("Leave Request Data:", data);
                        // API isteği gönderilebilir
                    }}
                />
            )}
            {showShiftModal && (
                <ShiftRequestModal
                    onClose={() => setShowShiftModal(false)}
                    onSubmit={(data) => {
                        console.log("Shift request submitted:", data);
                        // Burada fetch ile backend'e gönderebilirsin
                        setShowShiftModal(false);
                    }}
                />
            )}


        </div>


    );
}

export default EmployeePage;
