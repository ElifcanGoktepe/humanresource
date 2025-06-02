
import  { useEffect, useState } from "react";
import axios from "axios";
import 'bootstrap/dist/css/bootstrap.min.css';
import './UserStoriesPage.css';
import UserStoriesRevisionForPage from '../../components/atoms/UserStoriesRevisionForPage.tsx';

interface Comment {
    id: number;
    commentText: string;
    photoUrl?: string | null;
    managerName: string;
    createdAt: string;
}

function UserStoriesPage() {
    const [comments, setComments] = useState<Comment[]>([]);
    const [loading, setLoading] = useState(false);
    const token = localStorage.getItem("token");
    const authHeader = token ? { Authorization: `Bearer ${token}` } : {};

    useEffect(() => {
        setLoading(true);
        axios.get("http://localhost:9090/comments", { headers: authHeader })
            .then(res => {
                // res.data muhtemelen Comment[] listesi
                setComments(res.data);
                setLoading(false);
            })
            .catch(() => setLoading(false));
    }, [token]);

    return (
        <div className="user-stories-background">
            <img src="/img/logo4.png" alt="Logo" className="logo-on-background" />
            <div className="user-stories-scroll-container">
                <div className="user-stories-content">
                    {loading && <p>Loading comments...</p>}
                    {!loading && comments.length === 0 && <p>No comments yet.</p>}
                    {!loading && comments.map(comment => (
                        <UserStoriesRevisionForPage
                            key={comment.id}
                            username={comment.managerName}
                            commentText={comment.commentText}
                            photoUrl={comment.photoUrl}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
}

export default UserStoriesPage;
