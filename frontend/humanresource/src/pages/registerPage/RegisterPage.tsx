import 'bootstrap/dist/css/bootstrap.min.css';
import './RegisterPage.css';
import type { FormEvent } from "react";
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function RegisterPage() {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [companyName, setCompanyName] = useState("");
    const [titleName, setTitleName] = useState("");

    const navigate = useNavigate();

    const handleRegister = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:9090/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ firstName, lastName, email, companyName, phoneNumber, titleName })
            });

            if (!response.ok) {
                alert("Kayıt başarısız!");
                return;
            }

            // Kayıt başarılı → kullanıcıya mesaj göster
            alert("Kayıt başarılı. Lütfen e-posta adresinizi kontrol edin.");
            navigate("/login");

        } catch (error) {
            if (error instanceof Error) {
                alert("Kayıt hatası: " + error.message);
            } else {
                alert("Kayıt hatası: " + String(error));
            }
        }
    };

    return (
        <div className="RegisterPage">
            <div className="container-registerpage">
                <div className="input-area">
                    <form className="register-form" onSubmit={handleRegister}>
                        <div className="logor">
                            <img src="/img/logo.png" alt="logo" width="200px" />
                        </div>

                        <div className="Name-group">
                            <label className="Label">First Name</label>
                            <input type="text" className="Name Input"
                                   value={firstName}
                                   onChange={(e) => setFirstName(e.target.value)}
                                   required />
                        </div>

                        <div className="SurName-group">
                            <label className="Label">Last Name</label>
                            <input type="text" className="SurName Input"
                                   value={lastName}
                                   onChange={(e) => setLastName(e.target.value)}
                                   required />
                        </div>

                        <div className="E-mail-group">
                            <label className="Label">E-mail</label>
                            <input type="email" className="Email Input"
                                   value={email}
                                   onChange={(e) => setEmail(e.target.value)}
                                   required />
                        </div>

                        <div className="PhoneNumber-group">
                            <label className="Label">Phone Number</label>
                            <input type="text" className="PhoneNumber Input"
                                   value={phoneNumber}
                                   onChange={(e) => setPhoneNumber(e.target.value)}
                                   required />
                        </div>

                        <div className="CompanyName-group">
                            <label className="Label">Company Name</label>
                            <input type="text" className="CompanyName Input"
                                   value={companyName}
                                   onChange={(e) => setCompanyName(e.target.value)}
                                   required />
                        </div>

                        <div className="TitleName-group">
                            <label className="Label">Title</label>
                            <input type="text" className="TitleName Input"
                                   value={titleName}
                                   onChange={(e) => setTitleName(e.target.value)}
                                   required />
                        </div>

                        <div className="button-registerm">
                            <button type="submit" className="signup-btnm">SIGN UP</button>
                        </div>

                        <div className="to-register">
                            <a id="ar" href="/login">Back to Login</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default RegisterPage;
