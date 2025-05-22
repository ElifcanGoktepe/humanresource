import React, { useState } from "react";
import { useSearchParams } from "react-router-dom";
import axios from "axios";

const CreatePassword = () => {
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
        } catch (err: any) {
            setMessage("");
            setError(err.response?.data || "❌ Şifre oluşturulamadı.");
        }
    };

    return (
        <div style={{ maxWidth: 400, margin: "100px auto", textAlign: "center" }}>
            <h2>🔐 Şifre Oluştur</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="password"
                    placeholder="Yeni şifre"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    style={{ width: "100%", padding: 10, marginBottom: 12 }}
                />
                <input
                    type="password"
                    placeholder="Şifreyi tekrar girin"
                    value={rePassword}
                    onChange={(e) => setRePassword(e.target.value)}
                    required
                    style={{ width: "100%", padding: 10, marginBottom: 12 }}
                />
                <button type="submit" style={{ width: "100%", padding: 10 }}>
                    Oluştur
                </button>
            </form>
            {error && <p style={{ color: "red", marginTop: 16 }}>{error}</p>}
            {message && <p style={{ color: "green", marginTop: 16 }}>{message}</p>}
        </div>
    );
};

export default CreatePassword;
