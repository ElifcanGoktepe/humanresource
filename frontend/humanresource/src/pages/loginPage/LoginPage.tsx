import 'bootstrap/dist/css/bootstrap.min.css';
import './LoginPage.css'
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function LoginPage() {
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

            // 🎯 TOKEN'dan payload çıkar ve roles güvenli şekilde kontrol et
            const payload = JSON.parse(atob(token.split('.')[1]));
            let roles = payload.roles || [];

            if (typeof roles === "string") {
                roles = [roles];
            }

            if (roles.includes("Manager")) {
                navigate("/manager");
            } else if (roles.includes("Employee")) {
                navigate("/employee");
            } else if (roles.includes("Admin")) {
                navigate("/admin");
            } else {
                alert("Yetkisiz rol.");
            }

        } catch (error) {
            alert("Login error: " + (error as Error).message);
        }
    };

    return (
        <div className="loginPage-loginpage">
            <div className="container-loginpage-loginpage">
                <div className="inputarea-loginpage">
                    <form className="loginform-loginpage" onSubmit={handleLogin}>



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

                        <div className="button-login-loginpage">
                            <button type="submit" className="login-btn-loginpage">LOG IN</button>
                        </div>
                        <div className='gotoregisterpage-loginpage'>
                            <a id="registerlinkwords-loginpage" href="/register">create new account</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
