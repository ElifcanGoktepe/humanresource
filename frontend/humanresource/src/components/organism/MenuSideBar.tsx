import React from 'react';

import {useNavigate,useLocation} from "react-router-dom";


function MenuSideBar() {
    const navigate=useNavigate();
    const location= useLocation();
    return (

        <div className="col-2 fixed-side-bar">
            <div className="fixed-bar-image">
                <img className="logo-left-menu" src="/img/logo1.png" alt="logo"/>

            </div>
            <hr/>
            <div className="fixed-bar-button-container">
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar" src="/img/adminpage.png"/>
                    Company Page
                </button>
                <button onClick={() =>{
                    if (location.pathname !== 'manager/employeelist')
                    {navigate('manager/employeelist')} }} className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar" src="/img/employee.png"/>
                    Employee
                </button>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar" src="/img/expens.png"/>
                    Salary
                </button>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar" src="/img/shift.png"/>
                    Shift
                </button>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar" src="/img/assets.png"/>
                    Assignment
                </button>
            </div>
            <hr/>
            <div className="bottom-bar">
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar" src="/img/profileicon.png"/>
                    Profile
                </button>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar" src="/img/settingsicon.png"/>
                    Settings
                </button>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar" src="/img/helpicon.png"/>
                    Help
                </button>
            </div>
        </div>


    )
}

export default MenuSideBar