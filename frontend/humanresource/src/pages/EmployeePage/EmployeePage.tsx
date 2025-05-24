import 'bootstrap/dist/css/bootstrap.min.css';
import './EmployeePage.css';
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
                <div className="employee-page-header">
                    <h2>Hello Employee!</h2>
                    <h3>Today's Date: {dateString}, {dayName}</h3>
                    <hr/>
                </div>
                <div className="row">
                    <div className="col-3 box-dashboard">
                        <div className="box1-dashboard">
                            <div className="profile-settings-header">
                                <div className="col-8 profile-settings-header-name">
                                    <h3>Employee Name</h3>
                                </div>
                                <div className="col-4 profile-settings-header-icon">
                                    <img className="small-image-fixed-bar2" src="/img/profileicon.png" />
                                </div>
                            </div>
                            <div className="profile-settings-body">
                                <div>
                                    <h4>Title</h4>
                                    <h6>Company</h6>
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
                        <div className="box1-dashboard row p-2">
                            <h3>Weekly Shift List</h3>
                            <hr/>
                            <div className="col-7 fontstyle-shiftnames mb-2">
                                <p>Monday</p>
                                <p>Tuesday</p>
                                <p>Wednesday</p>
                                <p>Thursday</p>
                                <p>Friday</p>
                                <p>Saturday</p>
                                <p>Sunday</p>
                            </div>
                            <div className="col-5 fontstyle-shifthours mb-2">
                                <p>08:00-12:00</p>
                                <p>08:00-12:00</p>
                                <p>13:00-17:00</p>
                                <p>13:00-17:00</p>
                                <p>18:00-22:00</p>
                                <p>18:00-22:00</p>
                                <p>---</p>
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
            </div>
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
        </>
    );
}

export default EmployeePage;
