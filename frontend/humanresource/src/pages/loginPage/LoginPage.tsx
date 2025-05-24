import 'bootstrap/dist/css/bootstrap.min.css';
import './LoginPage.css'
import {useState} from "react";
import {useNavigate} from "react-router-dom";



function LoginPage(){
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:9090/api/users/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });

            if (!response.ok) {
                alert("Login failed!");
                return;
            }

            const data = await response.json();
            const token = data.data;

            localStorage.setItem("token", token);

            // 🎯 TOKEN'dan payload çıkar ve yönlendir
            const payload = JSON.parse(atob(token.split('.')[1]));

            if (payload.roles.includes("Manager")) {
                navigate("/manager");
            } else if (payload.roles.includes("Employee")) {
                navigate("/employee");
            } else if (payload.roles.includes("Admin")) {
                navigate("/admin");
            } else {
                alert("Yetkisiz rol.");
            }

        } catch (error) {
            alert("Login error: " + (error as Error).message);
        }
    };

    return (
        <div className="loginPage">
            <div className="container-loginpage">
                <div className="input-area">
                    <form className="login-form" onSubmit={handleLogin}>
                        <div className="logol">
                            <img src="/img/logo.png" alt="logo" width="200px" />
                        </div>
                        <div className="E-mail-group">
                            <label className="Label">E-mail</label>
                            <input
                                type="email"
                                name="Email Area"
                                placeholder="Work Email"
                                className="E-mail Input"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>
                        <div className="Password-group">
                            <label className="Label">Password</label>
                            <input
                                type="password"
                                name="Password Area"
                                placeholder="Password"
                                className="Password Input"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                        <div className="button-loginm">
                            <button type="submit" className="login-btnm">LOG IN</button>
                        </div>
                        <div className='to-login'>
                            <a id="al" href="/register">create new account</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}
export default LoginPage;
