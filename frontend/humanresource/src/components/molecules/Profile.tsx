import React, { useEffect, useState } from "react";
import axios from "axios";
import "./Profile.css";

interface Comment {
    id: number;
    commentText: string;
    photoUrl?: string | null;
    managerName: string;
    createdAt: string;
}

const Profile: React.FC = () => {
    const [comment, setComment] = useState<Comment | null>(null);
    const [text, setText] = useState("");
    const [photoUrl, setPhotoUrl] = useState("");
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    const token = localStorage.getItem("token");
    const authHeader = token ? { Authorization: `Bearer ${token}` } : {};

    useEffect(() => {
        if (!token) return;

        setLoading(true);
        axios.get(`http://localhost:9090/comments`, { headers: authHeader })
            .then(res => {
                if (res.data && res.data.data) {
                    setComment(res.data.data);
                    setText(res.data.data.commentText || "");
                    setPhotoUrl(res.data.data.photoUrl || "");
                } else if (res.data) {
                    setComment(res.data);
                    setText(res.data.commentText || "");
                    setPhotoUrl(res.data.photoUrl || "");
                }
                setLoading(false);
            })
            .catch(() => {
                setLoading(false);
                setComment(null);
            });

    }, [token]);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!text.trim()) {
            setMessage("Comment text cannot be empty.");
            return;
        }

        setLoading(true);
        const payload = {
            commentText: text.trim(),
            photoUrl: photoUrl.trim() === "" ? null : photoUrl.trim(),
        };

        if (comment && comment.id) {
            // Güncelleme (PUT) sadece id varsa yapılacak
            axios.put(`http://localhost:9090/comment/${comment.id}`, payload, { headers: authHeader })
                .then(res => {
                    setComment(res.data.data || res.data); // backend'in data sarmalayıcı varsa
                    setMessage("Comment updated successfully.");
                    setLoading(false);
                })
                .catch(() => {
                    setMessage("Error updating comment.");
                    setLoading(false);
                });
        } else {
            // Yeni yorum (POST)
            axios.post(`http://localhost:9090/addcomment`, payload, { headers: authHeader })
                .then(res => {
                    setComment(res.data.data || res.data);
                    setMessage("Comment added successfully.");
                    setLoading(false);
                })
                .catch(() => {
                    setMessage("Error adding comment.");
                    setLoading(false);
                });
        }
    };


    return (
        <div className="profile-comment-container">
            <h3>Your Comment on the Application</h3>
            {loading && <p>Loading...</p>}

            <form onSubmit={handleSubmit} className="profile-comment-form">
                <div>
                    <textarea
                        placeholder="Write your comment here..."
                        value={text}
                        onChange={(e) => setText(e.target.value)}
                        rows={4}
                    />
                </div>
                <div>
                    <input
                        type="text"
                        placeholder="Photo URL (optional)"
                        value={photoUrl}
                        onChange={(e) => setPhotoUrl(e.target.value)}
                    />
                </div>
                <button type="submit" disabled={loading}>
                    {comment ? "Update Comment" : "Add Comment"}
                </button>
            </form>

            {message && <p className="profile-message">{message}</p>}

            {comment && comment.photoUrl && (
                <div className="profile-photo-preview">
                    <h4>Photo Preview:</h4>
                    <img
                        src={comment.photoUrl}
                        alt="Manager or Company Logo"
                        style={{ maxWidth: "150px", maxHeight: "150px", borderRadius: "8px" }}
                    />
                </div>
            )}
        </div>
    );
};

export default Profile;
