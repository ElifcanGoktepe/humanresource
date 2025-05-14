
import { BrowserRouter, Routes, Route } from 'react-router'
import RegisterPage from "./pages/registerPage/RegisterPage.tsx";
import LoginPage from "./pages/loginPage/LoginPage";
import HomePage from "./pages/HomePage/HomePage.tsx";
import ManagerPage from "./pages/ManagerPage/ManagerPage.tsx";


function RoutingPage() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={<HomePage />}/>
                <Route path='/login' element={<LoginPage />}/>
                <Route path='/register' element={<RegisterPage />}/>
                <Route path='/manager' element={<ManagerPage />}/>
            </Routes>
        </BrowserRouter>
    )
}

export default RoutingPage