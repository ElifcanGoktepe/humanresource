import 'bootstrap/dist/css/bootstrap.min.css';
import './ManagerPage.css';
import LeaveChart from "../../components/atoms/LeaveChart.tsx";


function ManagerPage() {
    const today = new Date();
    const days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    const dayIndex = today.getDay();
    const dayName = days[dayIndex];
    const dateString = today.toLocaleDateString("en-US");



    return (
        <div className="row">
            <div className="col-2 fixed-side-bar">
                <img className="logo-left-menu" src="/img/logo1.png" alt="logo" />
                <hr />
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
                    <h2>Hello Manager!</h2>
                    <h3>Today's Date: {dateString}, {dayName}</h3>
                    <hr/>
                </div>
                <div className="row">
                    <div className="col-3 box-dashboard">
                        <div className="box1-dashboard">
                            <div className="profile-settings-header">
                                <h3>Manager Name</h3>
                                <img className="small-image-fixed-bar2" src="/img/profileicon.png" />
                            </div>
                            <div className="profile-settings-body">
                                <h4>Title</h4>
                                <h6>Company</h6>
                                <hr/>
                                <button className="accountbutton">
                                    Account →
                                </button>
                            </div>
                        </div>
                    </div>
                    <div className="col-3 box-dashboard">
                        <div className="box1-dashboard">
                            <div className="leave-settings-body">
                                <LeaveChart/>
                                <hr/>
                                <button className="accountbutton">
                                    Request →
                                </button>
                            </div>
                        </div>
                    </div>
                    <div className="col-3 box-dashboard">

                    </div>
                    <div className="col-3 box-dashboard">

                    </div>
                </div>
            </div>
        </div>
    );
}

export default ManagerPage;
