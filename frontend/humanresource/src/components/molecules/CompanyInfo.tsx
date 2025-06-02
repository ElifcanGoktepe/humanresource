
import React, { useState } from "react";
import axios from "axios";

interface CompanyInfo {
    companyName: string;
    companyAddress: string;
    companyPhoneNumber: string;
    companyEmail: string;
}

const CompanyToInform: React.FC = () => {
    const [companyInfo, setCompanyInfo] = useState<CompanyInfo>({
        companyName: "",
        companyAddress: "",
        companyPhoneNumber: "",
        companyEmail: "",
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setCompanyInfo((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        try {
            const token = localStorage.getItem("token"); // auth için
            const response = await axios.post(
                "http://localhost:9090/api/company/update", // backend endpoint adresi
                companyInfo,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );
            alert("Company information updated successfully!");
        } catch (error) {
            console.error("Failed to update company info:", error);
            alert("Failed to update company info.");
        }
    };

    return (
        <form onSubmit={handleSubmit} style={{ maxWidth: 600, margin: "auto" }}>
            <h2>Update Company Information</h2>

            <div className="form-group">
                <label htmlFor="companyName">Company Name</label>
                <input
                    type="text"
                    id="companyName"
                    name="companyName"
                    value={companyInfo.companyName}
                    onChange={handleChange}
                    required
                    className="form-control"
                />
            </div>

            <div className="form-group">
                <label htmlFor="companyAddress">Company Address</label>
                <input
                    type="text"
                    id="companyAddress"
                    name="companyAddress"
                    value={companyInfo.companyAddress}
                    onChange={handleChange}
                    required
                    className="form-control"
                />
            </div>

            <div className="form-group">
                <label htmlFor="companyPhoneNumber">Company Phone Number</label>
                <input
                    type="tel"
                    id="companyPhoneNumber"
                    name="companyPhoneNumber"
                    value={companyInfo.companyPhoneNumber}
                    onChange={handleChange}
                    required
                    className="form-control"
                />
            </div>

            <div className="form-group">
                <label htmlFor="companyEmail">Company Email</label>
                <input
                    type="email"
                    id="companyEmail"
                    name="companyEmail"
                    value={companyInfo.companyEmail}
                    onChange={handleChange}
                    required
                    className="form-control"
                />
            </div>

            <button type="submit" className="btn btn-primary mt-3">
                Submit
            </button>
        </form>
    );
};

export default CompanyToInform;
