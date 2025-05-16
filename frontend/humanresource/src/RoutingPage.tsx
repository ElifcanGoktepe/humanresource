
import { BrowserRouter, Routes, Route } from 'react-router'
import RegisterPage from "./pages/registerPage/RegisterPage.tsx";
import LoginPage from "./pages/loginPage/LoginPage";
import HomePage from "./pages/HomePage/HomePage.tsx";
import ManagerPage from "./pages/ManagerPage/ManagerPage.tsx";
import AdminPage from "./pages/AdminPage/AdninPage.tsx";


function RoutingPage() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={<HomePage />}/>
                <Route path='/login' element={<LoginPage />}/>
                <Route path='/register' element={<RegisterPage />}/>
                <Route path='/manager' element={<ManagerPage />}/>
                <Route path='/admin' element={<AdminPage />}/>
            </Routes>
        </BrowserRouter>
    )
}

export default RoutingPage