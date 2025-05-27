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

        const requestBody = {
            firstName,
            lastName,
            email,
            companyName,
            phoneNumber,
            titleName
        };

        // 🔍 GÖNDERİLEN VERİYİ KONSOLA YAZ
        console.log("➡️ GÖNDERİLEN VERİ:", JSON.stringify(requestBody, null, 2));


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
        <div className="registerpage-registerpage">
            <div className="container-registerpage-registerpage">
                <div className="inputarea-registerpage">
                    <form className="registerform-registerpage" onSubmit={handleRegister}>


                            <label className="label">First Name</label>
                            <input type="text" className="firstnameinput-registerpage"
                                   value={firstName}
                                   onChange={(e) => setFirstName(e.target.value)}
                                   required />



                            <label className="label">Last Name</label>
                            <input type="text" className="lastnameinput-registerpage"
                                   value={lastName}
                                   onChange={(e) => setLastName(e.target.value)}
                                   required />



                            <label className="label">E-mail</label>
                            <input type="emailWork" className="emailinput-registerpage"
                                   value={email}
                                   onChange={(e) => setEmail(e.target.value)}
                                   required />



                            <label className="label">Phone Number</label>
                            <input type="text" className="phonenumberinput-registerpage"
                                   value={phoneNumber}
                                   onChange={(e) => setPhoneNumber(e.target.value)}
                                   required />



                            <label className="label">Company Name</label>
                            <input type="text" className="companynameinput-registerpage"
                                   value={companyName}
                                   onChange={(e) => setCompanyName(e.target.value)}
                                   required />



                            <label className="label">Title</label>
                            <input type="text" className="titlenameinput-registerpage"
                                   value={titleName}
                                   onChange={(e) => setTitleName(e.target.value)}
                                   required />



                            <button type="submit" className="signup-btn-registerpage">APPLY</button>


                            <a id="loginlinkwords-registerpage" href="/login">Back to Login</a>

                    </form>
                </div>
            </div>
        </div>
    );
}

export default RegisterPage;
