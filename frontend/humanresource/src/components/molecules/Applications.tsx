import { useEffect, useState } from 'react';
import axios from 'axios';
import './Applications.css';
import 'bootstrap/dist/css/bootstrap.min.css';

interface Application {
    id: number;
    firstName: string;
    lastName: string;
    emailWork: string;
    phoneWork: string;
    companyName: string;
    titleName: string;
    isApproved: boolean;
    isActivated: boolean;
}

function Applications() {
    const [applications, setApplications] = useState<Application[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function fetchApplications() {
        setLoading(true);
        setError(null);
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                setError("Authorization token not found.");
                setLoading(false);
                return;
            }

            const response = await axios.get<Application[]>(
                'http://localhost:9090/dev/v1/pendingapplications',
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            setApplications(response.data);
        } catch (err) {
            setError('Failed to fetch applications.');
        } finally {
            setLoading(false);
        }
    }

    async function updateStatus(id: number, status: 'accept' | 'reject') {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                alert('Authorization token missing.');
                return;
            }

            await axios.put(
                `http://localhost:9090/dev/v1/updateapplicationstatus/${id}?status=${status}`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                }
            );

            fetchApplications(); // Refresh
        } catch {
            alert(`Failed to ${status} application.`);
        }
    }

    useEffect(() => {
        fetchApplications();
    }, []);

    if (loading) return <p>Loading applications...</p>;
    if (error) return <p className="text-danger">{error}</p>;

    return (
        <div className="applications-container">
            <h2 className="applications-title">Pending Applications</h2>
            {applications.length === 0 ? (
                <p className="no-applications">No pending applications.</p>
            ) : (
                <div className="card-grid">
                    {applications.map((app) => (
                        <div className="app-card" key={app.id}>
                            <h5>{app.firstName} {app.lastName}</h5>
                            <p><strong>Email:</strong> {app.emailWork}</p>
                            <p><strong>Phone:</strong> {app.phoneWork}</p>
                            <p><strong>Company:</strong> {app.companyName}</p>
                            <p><strong>Title:</strong> {app.titleName}</p>
                            <p><strong>Approved:</strong> {app.isApproved ? 'Yes' : 'No'}</p>
                            <p><strong>Activated:</strong> {app.isActivated ? 'Yes' : 'No'}</p>
                            <div className="btn-group">
                                <button className="btn btn-success" onClick={() => updateStatus(app.id, 'accept')}>
                                    Accept
                                </button>
                                <button className="btn btn-danger" onClick={() => updateStatus(app.id, 'reject')}>
                                    Reject
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default Applications;
