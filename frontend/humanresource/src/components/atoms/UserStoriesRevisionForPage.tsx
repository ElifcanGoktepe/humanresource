import  { useEffect, useState } from 'react';
import './UserStoriesRevisionForPage.css';

interface Props {
    username: string;
    commentText: string;
    photoUrl?: string | null;
}

function UserStoriesRevisionForPage({ username, commentText, photoUrl }: Props) {
    const [isVisible, setIsVisible] = useState(false);

    useEffect(() => {
        const timer = setTimeout(() => setIsVisible(true), 300);
        return () => clearTimeout(timer);
    }, []);

    const fullPhotoUrl = photoUrl
        ? (photoUrl.startsWith("http") ? photoUrl : `http://localhost:9090${photoUrl}`)
        : "/img/employee.png";

    return (
        <div className={`story-row ${isVisible ? 'visible' : ''}`}>
            <div className="story-profile">
                <img
                    src={fullPhotoUrl}
                    alt={username || "User"}
                    className="story-image"
                />
            </div>
            <div className="story-comment">
                <h4 className="story-username">{username}</h4>
                <p className="story-comment-text">{commentText}</p>
            </div>
        </div>
    );
}

export default UserStoriesRevisionForPage;
