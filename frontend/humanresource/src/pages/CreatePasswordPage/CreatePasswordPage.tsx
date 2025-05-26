import 'bootstrap/dist/css/bootstrap.min.css';
import './CreatePasswordPage.css'


import React, { useState } from "react";
import { useSearchParams } from "react-router-dom";
import axios from "axios";


function CreatePasswordPage(){

        const [params] = useSearchParams();
        const token = params.get("token");

        const [password, setPassword] = useState("");
        const [rePassword, setRePassword] = useState("");
        const [message, setMessage] = useState("");
        const [error, setError] = useState("");

        const handleSubmit = async (e: React.FormEvent) => {
            e.preventDefault();

            if (password !== rePassword) {
                setError("❌ Şifreler uyuşmuyor.");
                return;
            }

            try {
                const response = await axios.post(
                    `http://localhost:9090/api/set-password?token=${token}`,
                    password,
                    {
                        headers: {
                            "Content-Type": "text/plain"
                        }
                    }
                );
                setMessage(response.data);
                setError("");
            } catch (err: unknown) {
                if (axios.isAxiosError(err)) {
                    setError(err.response?.data || "❌ Şifre oluşturulamadı.");
                } else {
                    setError("❌ Beklenmeyen bir hata oluştu.");
                }
            }
        };



        return (
        <div className="createpassword-createpasswordpage" >


            <div className="container-createpasswordpage-createpasswordpage" >

                <div className="inputarea-createpasswordpage">

                    <form className="createpasswordform-createpasswordpage" >


                            <label className="label">Password</label>
                            <input type="password" name="Password Area" placeholder="Create New Password" className="passwordinput-createpasswordpage" value={password} onChange={(e) => setPassword(e.target.value)} required />


                            <label className="Label">Password</label>
                            <input type="password" name="rePassword Area" placeholder="Re-Enter Password" className="repasswordinput-createpasswordpage" value={rePassword} onChange={(e) => setRePassword(e.target.value)} required />


                            <button type="submit" className="createpassword-btn-createpasswordpage" onClick={handleSubmit} >CREATE</button>


                        <a id="loginlinkwords-registerpage" href="/login">back to login</a>

                    </form>
                    {error && <p style={{ color: "red", marginTop: 16 }}>{error}</p>}
                    {message && <p style={{ color: "green", marginTop: 16 }}>{message}</p>}

                </div>
            </div>
        </div>
    )

}

export default CreatePasswordPage;