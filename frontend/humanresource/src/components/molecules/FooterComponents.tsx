import 'bootstrap/dist/css/bootstrap.min.css';
import './FooterComponent.css';

function FooterComponent(){
    return(

        <div className="footer-container row">

            <div className="col-8">
                <div>
                    <img className="logo1" src="/img/logo1.png" alt="logo" />
                </div>
                <div className="mt-2 m-lg-3">
                    <a id="footer" href="https://www.google.com/maps/dir//YDA+Center,+K%C4%B1z%C4%B1l%C4%B1rmak,+06510+%C3%87ankaya%2FAnkara/data=!4m6!4m5!1m1!4e2!1m2!1m1!1s0x14d348b2a8a8bb1b:0x6fc94f4a1deecb8a?sa=X&ved=1t:57443&ictx=111">
                        📍 Dumlupınar Boulevard No: 9A | Çankaya, Ankara, Turkey</a>
                    <p>📞 Phone: +90 (312) 542 25 42</p>
                    <a style={{ marginRight: '10px' }} href="https://www.linkedin.com/in/kullanici-adin" target="_blank">
                        <img src="/img/icons8-linkedin-50.png" alt="LinkedIn" width="30"/>
                    </a>
                    <a style={{ marginRight: '10px' }} href="https://www.instagram.com/in/kullanici-adin" target="_blank">
                        <img src="/img/icons8-instagram-50.png" alt="Instagram" width="30" />
                    </a>
                    <a style={{ marginRight: '10px' }} href="https://www.x.com/in/kullanici-adin" target="_blank">
                        <img src="/img/icons8-x-50.png" alt="X" width="30" />
                    </a>
                    <a style={{ marginRight: '10px' }} href="https://www.youtube.com/in/kullanici-adin" target="_blank">
                        <img src="/img/icons8-youtube-50.png" alt="YouTube" width="30" />
                    </a>

                </div>
            </div>
            <div className="col-2">
                <h1>Products</h1>
                <a id="footer" href="/register">Employee Management</a>
                <br/>
                <a id="footer" href="/register">Salary Management</a>
                <br/>
                <a id="footer" href="/register">Shift Management</a>
                <br/>
                <a id="footer" href="/register">Asset Management</a>
            </div>
            <div className="col-2">
                <h1>Humin</h1>
                <a id="footer" href="/register">User Stories</a>
                <br/>
                <a id="footer" href="/register">Connect</a>
                <br/>
                <a id="footer" href="/register">About Us</a>
            </div>
        </div>
    )
}
export default FooterComponent