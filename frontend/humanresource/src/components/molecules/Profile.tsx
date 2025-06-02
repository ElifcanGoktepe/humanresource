import React, { useEffect, useState } from "react";
import axios from "axios";
import './Profile.css';

interface Comment {
    id: number;
    commentText: string;
    photoUrl?: string | null;
    managerName: string;

}

const Profile: React.FC = () => {
    const [comments, setComments] = useState<Comment[]>([]);
    const [editingComment, setEditingComment] = useState<Comment | null>(null);
    const [text, setText] = useState("");
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [previewUrl, setPreviewUrl] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    const token = localStorage.getItem("token");
    const authHeader = token ? { Authorization: `Bearer ${token}` } : {};


    useEffect(() => {
        if (!token) return;

        setLoading(true);
        axios
            .get(`http://localhost:9090/comments`, { headers: authHeader })
            .then((res) => {
                const data = res.data?.data || res.data;
                setComments(Array.isArray(data) ? data : data ? [data] : []);
            })
            .catch(() => setComments([]))
            .finally(() => setLoading(false));
    }, [token]);

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            setSelectedFile(file);
            setPreviewUrl(URL.createObjectURL(file));
        }
    };

    const startEditing = (comment: Comment) => {
        setEditingComment(comment);
        setText(comment.commentText);
        setPreviewUrl(comment.photoUrl || null);
        setSelectedFile(null);
        setMessage("");
    };

    const cancelEditing = () => {
        setEditingComment(null);
        setText("");
        setSelectedFile(null);
        setPreviewUrl(null);
        setMessage("");
    };

    const handleDelete = async (id: number) => {
        if (!window.confirm("Are you sure you want to delete this comment?")) return;
        setLoading(true);
        try {
            await axios.delete(`http://localhost:9090/${id}`, { headers: authHeader });
            setComments((prev) => prev.filter((c) => c.id !== id));
            if (editingComment?.id === id) cancelEditing();
            setMessage("Comment deleted successfully.");
        } catch {
            setMessage("Failed to delete comment.");
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!text.trim()) {
            setMessage("Comment text cannot be empty.");
            return;
        }

        setLoading(true);
        setMessage("");
        let commentId: number | undefined;

        try {
            if (editingComment) {
                const payload = { commentText: text.trim() };
                const res = await axios.put(
                    `http://localhost:9090/comment/${editingComment.id}`,
                    payload,
                    { headers: authHeader }
                );
                const updatedComment = res.data.data || res.data;
                setComments((prev) =>
                    prev.map((c) => (c.id === updatedComment.id ? updatedComment : c))
                );
                commentId = updatedComment.id;
            } else {
                const payload = { commentText: text.trim(), photoUrl: null };
                const res = await axios.post(
                    `http://localhost:9090/dev/v1/addcomment`,
                    payload,
                    { headers: authHeader }
                );
                const newComment = res.data.data || res.data;
                setComments((prev) => [newComment, ...prev]);
                commentId = newComment.id;
            }

            if (selectedFile && commentId) {
                const formData = new FormData();
                formData.append("file", selectedFile);

                const uploadRes = await axios.post(
                    `http://localhost:9090/${commentId}/upload-profile`,
                    formData,
                    {
                        headers: {
                            ...authHeader,
                            "Content-Type": "multipart/form-data",
                        },
                    }
                );
                const photoUrl = uploadRes.data?.url || uploadRes.data;
                console.log("Upload response:", uploadRes.data);

                setComments((prev) =>
                    prev.map((c) =>
                        c.id === commentId ? { ...c, photoUrl } : c
                    )
                );
            }

            setMessage(editingComment ? "Comment updated successfully." : "Comment added successfully.");
            cancelEditing();
        } catch {
            setMessage("Error saving comment or uploading photo.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="profile-comment-container">
            <h3>Your Comments on the Application</h3>
            {loading && <p>Loading...</p>}
            {!loading && comments.length === 0 && <p>No comments yet.</p>}

            <ul className="comment-list">
                {comments.map((comment, index) => (
                    <li key={comment.id ?? `comment-${index}`} className="comment-item">

                    <p>
                            <strong>{comment.managerName}</strong> -{" "}

                        </p>
                        <p>{comment.commentText}</p>
                        {comment.photoUrl && (
                            <img
                                src={comment.photoUrl}
                                alt="Comment"
                                style={{ maxWidth: "100px", maxHeight: "100px", borderRadius: "6px" }}
                            />
                        )}
                        <button onClick={() => startEditing(comment)} className="edit-button">
                            Düzenle
                        </button>
                        <button
                            onClick={() => handleDelete(comment.id)}
                            className="delete-button"
                            style={{ marginLeft: "10px", color: "red" }}
                        >
                            Sil
                        </button>
                    </li>
                ))}
            </ul>

            <form onSubmit={handleSubmit} className="profile-comment-form">
                <textarea
                    placeholder="Write your comment here..."
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    rows={4}
                />
                <label className="custom-file-upload" style={{ marginLeft: "15px", marginRight: "15px" }}>
                    <input type="file" accept="image/*" onChange={handleFileChange} />
                    Dosya Seç
                </label>
                <button type="submit" disabled={loading} style={{ marginLeft: "15px" }}>
                    {editingComment ? "Update Comment" : "Add Comment"}
                </button>
                {editingComment && (
                    <button
                        type="button"
                        onClick={cancelEditing}
                        className="cancel-button"
                        style={{ marginLeft: "10px" }}
                    >
                        İptal
                    </button>
                )}
            </form>

            {message && <p className="profile-message">{message}</p>}

            {previewUrl && (
                <div className="profile-photo-preview">
                    <h4>Photo Preview:</h4>
                    <img
                        src={previewUrl}
                        alt="Preview"
                        style={{ maxWidth: "150px", maxHeight: "150px", borderRadius: "8px" }}
                    />
                </div>
            )}
        </div>
    );
};

export default Profile;
