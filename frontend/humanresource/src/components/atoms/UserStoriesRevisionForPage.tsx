import  { useEffect, useState } from 'react';
import './UserStoriesRevisionForPage.css';

function UserStoriesRevisionForPage() {
    const [isVisible, setIsVisible] = useState(false);

    useEffect(() => {
        const timer = setTimeout(() => setIsVisible(true), 300);
        return () => clearTimeout(timer);
    }, []);

    return (
        <div className={`story-row ${isVisible ? 'visible' : ''}`}>
            <div className="story-profile">
                <img src="/img/AdminProfilePhoto.png" alt="User" className="story-image" />
            </div>
            <div className="story-comment">
                <h4 className="story-username">Username</h4>
                <p className="story-comment-text">
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla fermentum, nisl a
                    ultricies aliquet, justo sapien convallis lacus, nec gravida odio ipsum eget purus.
                </p>
            </div>
        </div>
    );
}

export default UserStoriesRevisionForPage;
