import { BrowserRouter, Routes, Route } from 'react-router'
import RegisterPage from "./pages/registerPage/RegisterPage.tsx";
import LoginPage from "./pages/loginPage/LoginPage";
import HomePage from "./pages/HomePage/HomePage.tsx";
import ManagerPage from "./pages/ManagerPage/ManagerPage.tsx";
import AdminPage from "./pages/AdminPage/AdninPage.tsx";
import EmployePage from "./pages/EmployeePage/EmployePage.tsx";
import CreatePasswordPage from "./pages/CreatePasswordPage/CreatePasswordPage.tsx";
import UserStoriesPage from "./pages/UserStoriesPage/UserStoriesPage.tsx";
import ExpensesPanel from "./pages/EmployeePage/ExpensesPanel.tsx";
import PersonalFile from "./pages/EmployeePage/PersonalFile.tsx";
import AssignmentPage from './pages/AssignmentPage/AssignmentPage';



function RoutingPage() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={<HomePage />}/>
                <Route path='/admin' element={<AdminPage />}/>
                <Route path='/login' element={<LoginPage />}/>
                <Route path='/register' element={<RegisterPage />}/>
                <Route path='/create-password' element={<CreatePasswordPage />}/>
                <Route path='/manager' element={<ManagerPage />}/>
                <Route path='/employee' element={<EmployePage />}/>
                <Route path="/create-password" element={<CreatePasswordPage />} />
                <Route path="/manager-login" element={<LoginPage />} />
                <Route path="/user-stories" element={<UserStoriesPage />} />
                <Route path="/personalfile" element={<PersonalFile />} />
                <Route path="/expenses" element={<ExpensesPanel />} />
                <Route path='/assignments' element={<AssignmentPage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default RoutingPage