import React, { useEffect, useState } from "react";
import axios from "axios";
import './Opinions.css';

interface Comment {
    id: number;
    commentText: string;
    photoUrl?: string | null;
    managerName: string;
}

const getFullPhotoUrl = (url?: string | null) => {
    return url ? (url.startsWith("http") ? url : `http://localhost:9090${url}`) : "/img/employee.png";
};

const Opinions: React.FC = () => {
    const [comment, setComment] = useState<Comment[]>([]);
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
        axios.get("http://localhost:9090/dev/v1/comments", { headers: authHeader })
            .then((res) => {
                const rawData = res.data?.data ?? res.data;
                const safeData = Array.isArray(rawData) ? rawData.filter((c) => typeof c.id === "number") : [];
                setComment(safeData);
            })
            .catch((error) => {
                if (error.response?.status === 401) {
                    setMessage("Oturum süresi dolmuş. Lütfen yeniden giriş yapın.");
                } else {
                    setMessage("Yorumlar alınamadı.");
                }
                setComment([]);
            })
            .finally(() => {
                setLoading(false);
            });
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

    const handleDelete = async (id?: number) => {
        if (!id || typeof id !== "number") {
            setMessage("Geçersiz yorum ID’si.");
            console.error("Hatalı silme çağrısı, ID:", id);
            return;
        }
        if (!window.confirm("Bu yorumu silmek istediğinize emin misiniz?")) return;
        setLoading(true);
        try {
            await axios.delete(`http://localhost:9090/dev/v1/comments/${id}`, { headers: authHeader });
            setComment((prev) => prev.filter((c) => c.id !== id));
            if (editingComment?.id === id) cancelEditing();
            setMessage("Yorum başarıyla silindi.");
        } catch {
            setMessage("Yorum silinemedi.");
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!text?.trim?.()) {
            setMessage("Yorum metni boş olamaz.");
            return;
        }

        setLoading(true);
        setMessage("");

        try {
            const formData = new FormData();
            formData.append("commentText", text.trim());
            if (selectedFile) formData.append("file", selectedFile);

            const res = await axios.post(
                "http://localhost:9090/dev/v1/comments/with-photo",
                formData,
                {
                    headers: {
                        ...authHeader,
                        "Content-Type": "multipart/form-data",
                    },
                }
            );

            const newComment = res.data.data;
            setComment((prev) => [newComment, ...prev]);
            setMessage("Yorum başarıyla eklendi.");
            cancelEditing();
        } catch {
            setMessage("Yorum eklenemedi.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="profile-comment-container">
            <h3>Your Comments on the Application</h3>
            {loading && <p>Loading...</p>}
            {!loading && comment.length === 0 && <p>No comments yet.</p>}

            <ul className="comment-list">
                {comment.map((comment, index) => (
                    <li key={comment.id ?? `comment-${index}`} className="comment-item">
                        <p><strong>{comment.managerName}</strong></p>
                        <p>{comment.commentText}</p>
                        <img
                            src={getFullPhotoUrl(comment.photoUrl)}
                            alt="Comment"
                            style={{ maxWidth: "100px", maxHeight: "100px", borderRadius: "6px" }}
                        />
                        <button onClick={() => startEditing(comment)} className="edit-button">Düzenle</button>
                        <button
                            onClick={() => handleDelete(comment.id)}
                            disabled={!comment.id}
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

export default Opinions;
