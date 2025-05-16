import './AdminPage.css'





function AdminPage(){








    return (

        <div className="AdminPage">
            <div className="AdminPage Wrapper">

                <div className="AdminIdentityBackground">
                    <div className="AdminIdentity1">
                         <div className="HuminLogo">
                             <img src="/img/logo.png" width="250px"/>
                         </div>
                    </div>
                    <div className="AdminIdentity2">
                        <div className="AdminIdentityPhoto">
                             <img src="/img/AdminProfilePhoto.png" height="50px"  />
                        </div>
                        <div className="AdminIdentityInfo">
                        <p className="AdminIdentityDetail">
                            Name: Serkan Kılıçdere <br/>
                            E-mail: serkan.kilicdere@outlook.com<br/>
                            Phone: 0545 831 18 22
                        </p>
                        </div>
                    </div>
                    <div className="AdminWaitingRequest">


                    </div>
                    <div className="AdminCompletedRequest">


                    </div>





                </div>

                <div className="AdminTask">
                    <div className="CompanySection">

                    </div>
                    <div className="CompanyManagerSection"></div>

                </div>


            </div>

        </div>

    )
}
export default AdminPage