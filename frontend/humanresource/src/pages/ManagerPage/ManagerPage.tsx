import 'bootstrap/dist/css/bootstrap.min.css';
import './ManagerPage.css';

function ManagerPage(){
    return(

        <div className="row">

            <div className="col-2 fixed-side-bar">
                <img className="logo-left-menu" src="/img/logo1.png" alt="logo" />
                <hr/>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar"   src="/img/adminpage.png"/>
                    Company Page
                </button>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar"   src="/img/employee.png"/>
                    Employee
                </button>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar"   src="/img/expens.png"/>
                    Salary
                </button>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar"   src="/img/shift.png"/>
                    Shift
                </button>
                <button className="fixed-bar-buttons">
                    <img className="small-image-fixed-bar"   src="/img/assets.png"/>
                    Assignment
                </button>
                <hr className="line"/>
                <div className="bottom-bar">


                </div>
            </div>

            <div className="col-10 manager-page">

            </div>

        </div>
    )


}
export default ManagerPage