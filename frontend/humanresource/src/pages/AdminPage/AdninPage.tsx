import CompanyArea from "../../components/molecules/CompanyArea.tsx";
import BranchArea from "../../components/molecules/BranchArea.tsx";
import DepartmentArea from "../../components/molecules/DepartmentArea..tsx";
import Settings from "../../components/molecules/Settings.tsx";
import './AdminPage.css';
import { useState } from 'react';
import Applications from "../../components/molecules/Applications.tsx";

function AdminPage() {
    const today = new Date();
    const days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    const dateString = today.toLocaleDateString("en-US");
    const dayName = days[today.getDay()];

    const [selectedContent, setSelectedContent] = useState<string>('Applications'); // default olarak Applications seçili

    const renderContent = () => {
        switch (selectedContent) {
            case 'Applications':
                return <Applications />;
            case 'CompanyArea':
                return <CompanyArea />;
            case 'BranchArea':
                return <BranchArea />;
            case 'DepartmentArea':
                return <DepartmentArea />;
            case 'Settings':
                return <Settings />;
            default:
                return <div>Select a menu item</div>;
        }
    };

    return (
        <div className="row">
            <div className="col-2 adminpage fixed-side-bar">
                <div className="fixed-bar-image">
                    <img className="logo-left-menu" src="/img/logo1.png" alt="logo" />
                </div>
                <hr />
                <div className="fixed-bar-button-container">
                    <button
                        className="fixed-bar-buttons"
                        onClick={() => setSelectedContent('Applications')}
                        style={{ fontWeight: selectedContent === 'Applications' ? 'bold' : 'normal' }}
                    >
                        <img className="small-image-fixed-bar" src="/img/list-task.svg" alt="Applications Icon" />
                        Applications
                    </button>
                    <button
                        className="fixed-bar-buttons"
                        onClick={() => setSelectedContent('CompanyArea')}
                        style={{ fontWeight: selectedContent === 'CompanyArea' ? 'bold' : 'normal' }}
                    >
                        <img className="small-image-fixed-bar" src="/img/list-task.svg" alt="Companies Icon" />
                        Companies
                    </button>
                    <button
                        className="fixed-bar-buttons"
                        onClick={() => setSelectedContent('BranchArea')}
                        style={{ fontWeight: selectedContent === 'BranchArea' ? 'bold' : 'normal' }}
                    >
                        <img className="small-image-fixed-bar" src="/img/list-task.svg" alt="Branches Icon" />
                        Branches
                    </button>
                    <button
                        className="fixed-bar-buttons"
                        onClick={() => setSelectedContent('DepartmentArea')}
                        style={{ fontWeight: selectedContent === 'DepartmentArea' ? 'bold' : 'normal' }}
                    >
                        <img className="small-image-fixed-bar" src="/img/list-task.svg" alt="Departments Icon" />
                        Departments
                    </button>
                </div>

                <div className="bottombar-adminpage-fixedsidebar">
                    <hr />
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/profileicon.png" alt="Opinions Icon" />
                        Opinions
                    </button>
                    <button
                        className="fixed-bar-buttons"
                        onClick={() => setSelectedContent('Settings')}
                        style={{ fontWeight: selectedContent === 'Settings' ? 'bold' : 'normal' }}
                    >
                        <img className="small-image-fixed-bar"  src="/img/settingsicon.png" alt="Settings Icon" />
                        Settings
                    </button>
                    <button className="fixed-bar-buttons">
                        <img className="small-image-fixed-bar" src="/img/helpicon.png" alt="Help Icon" />
                        Help
                    </button>
                </div>
            </div>

            <div className="col-10 adminpage fixed-body-area">
                <div className="adminpage-pageheader">
                    <h3>Hello!</h3>
                    <h4>Today's Date: {dateString}, {dayName}</h4>
                    <hr />
                </div>

                <div className="adminpage-content fixed-body-area">
                    {renderContent()}
                </div>
            </div>
        </div>
    );
}

export default AdminPage;
