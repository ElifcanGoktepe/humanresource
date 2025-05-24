// geçici bir sayfadır deneme için yapılmıştır.
import { useState } from "react";
import './ManagerLoginPage.css';
import { useNavigate } from "react-router-dom";


function ManagerLoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleLogin = async () => {
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
        <div className="manager-login-container">
            <div className="login-box">
                <h2>Manager Login</h2>
                <input
                    type="email"
                    placeholder="Work Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button onClick={handleLogin}>Login</button>
            </div>
        </div>
    );
}

export default ManagerLoginPage;
