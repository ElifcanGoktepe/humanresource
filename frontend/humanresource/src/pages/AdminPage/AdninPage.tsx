
import CompanyArea  from "../../components/molecules/CompanyArea.tsx";
import BranchArea from "../../components/molecules/BranchArea.tsx";
import DepartmentArea   from "../../components/molecules/DepartmentArea..tsx";
import './AdminPage.css';
import { useState } from 'react';
import ManagerArea from "../../components/molecules/ManagerArea.tsx";


function AdminPage() {
    const today = new Date();
    const days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    const dateString = today.toLocaleDateString("en-US");
    const dayName = days[today.getDay()];

    const [selectedContent, setSelectedContent] = useState('ManagerApproval');

    const renderContent = () => {
        switch (selectedContent) {
            case 'ManagerApproval':
                return  <ManagerArea/>
            case 'CompanyArea':
                return <CompanyArea />;
            case 'BranchArea':
                return <BranchArea />;
            case 'DepartmentArea':
                return <DepartmentArea />;
            default:
                return <div>Select a menu item</div>;
        }
    };

    return (
        <div className="row">
            <div className="col-2 fixed-side-bar">
                <div className="fixed-bar-image">
                    <img className="logo-left-menu" src="/img/logo1.png" alt="logo" />
                </div>
                <hr/>
                <div className="fixed-bar-button-container">
                    <button className="fixed-bar-buttons" onClick={() => setSelectedContent('ManagerArea')}>
                        <img className="small-image-fixed-bar" src="/img/list-task.svg" />
                        Register Manager
                    </button>
                    <button className="fixed-bar-buttons" onClick={() => setSelectedContent('CompanyArea')}>
                        <img className="small-image-fixed-bar" src="/img/list-task.svg" />
                        Company Area
                    </button>
                    <button className="fixed-bar-buttons" onClick={() => setSelectedContent('BranchArea')}>
                        <img className="small-image-fixed-bar" src="/img/list-task.svg" />
                        Branch Area
                    </button>
                    <button className="fixed-bar-buttons" onClick={() => setSelectedContent('DepartmentArea')}>
                        <img className="small-image-fixed-bar" src="/img/list-task.svg" />
                        Department Area
                    </button>
                </div>

                <div className="bottom-bar">
                    <hr />
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
                    <h2>Hello!</h2>
                    <h3>Today's Date: {dateString}, {dayName}</h3>
                    <hr/>
                </div>

                <div className="admin-page-content">
                    {renderContent()}
                </div>
            </div>
        </div>
    )
}

export default AdminPage;
