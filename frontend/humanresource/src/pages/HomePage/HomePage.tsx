import 'bootstrap/dist/css/bootstrap.min.css';
import './HomePage.css';
import HeaderComponent from "../../components/molecules/HeaderComponents.tsx";
import Body from "../../components/molecules/Body.tsx";
import FooterComponent from "../../components/molecules/FooterComponents.tsx";

function HomePage(){
    return(
        <div className="container-xxl px-0 background">
            <img src="/img/logo4.png" className="logo-on-background" alt="Logo"/>
            <div className="page-content">
                <HeaderComponent/>
                <Body/>
                <FooterComponent/>
            </div>
        </div>

    )
}

export default HomePage