import './AdminPage.css'






function AdminPage(){
    const today = new Date();
    const days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    const dayIndex = today.getDay();
    const dayName = days[dayIndex];
    const dateString = today.toLocaleDateString("en-US");








    return (
<div className="row">

    <div className="col-2 APfixed-side-bar">
        <div className="APfixed-bar-image">
        <img className="APlogo-left-menu" src="/img/logo1.png" alt="logo" />
        </div>
        <hr />

             <div className="h2JQ">
                <h2 className="JobQueueText">JOB QUEUE</h2>

            </div>

        <div className="JobQueue">
            <div className="h5">
                <h5>Waiting Tasks</h5>
            </div>
            <div className="Waiting-Approval">
                <ol className="Waiting-Approval-List">
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>

                </ol>
            </div>
            <div className="h5">
                <h5>Completed Tasks</h5>
            </div>
            <div className="Accepted-Approval">
                <ol className="Accepted-Approval-List">
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                    <li className="JobItem"></li>
                </ol>
            </div>



        </div>

        <div className="APbottom-bar">
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
        <div className="APmanager-page-header">
            <h2>Hello !</h2>
            <h3>Today's Date: {dateString}, {dayName}</h3>
            <hr/>
        </div>

    </div>

</div>
    )
}
export default AdminPage