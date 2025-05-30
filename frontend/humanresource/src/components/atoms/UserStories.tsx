import 'bootstrap/dist/css/bootstrap.min.css';
import './UserStories.css';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

function UserStories() {
    const profileRef = useRef(null);
    const commentRef = useRef(null);
    const [profileVisible, setProfileVisible] = useState(false);
    const [commentVisible, setCommentVisible] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const observer1 = new IntersectionObserver(
            ([entry]) => {
                if (entry.isIntersecting) {
                    setProfileVisible(true);
                }
            },
            { threshold: 0.3 }
        );

        const observer2 = new IntersectionObserver(
            ([entry]) => {
                if (entry.isIntersecting) {
                    setCommentVisible(true);
                }
            },
            { threshold: 0.3 }
        );

        if (profileRef.current) observer1.observe(profileRef.current);
        if (commentRef.current) observer2.observe(commentRef.current);

        return () => {
            observer1.disconnect();
            observer2.disconnect();
        };
    }, []);

    return (
        <div className="user-stories">
            <div className="row">
                <div className="col-7">
                    <div
                        ref={profileRef}
                        className={`user-profile row ${profileVisible ? 'show' : ''}`}
                    >
                        <div className="col-5" >
                            <img className="human-image"   src="/img/humanik.png"/>
                        </div>

                        <div className="col-7">
                            <h2>
                                “One of the most enjoyable aspects of working with Humin is offering a great customer experience service.”
                            </h2>
                            <h6 className="greycolor">
                                Müge Tuğcu, Senior Employee Happiness and Corporate Transformation Manager
                            </h6>
                            <button className="user-story-buton2">
                                Read the User Story →
                            </button>
                        </div>
                    </div>
                </div>
                <div className="col-5  ">
                    <div
                        ref={commentRef}
                        className={`user-story-comment ${commentVisible ? 'show' : ''}`}
                    >
                        <h1>
                            User Stories
                        </h1>
                        <hr/>
                        <h4 className="greycolor">
                            We’re simplifying HR together with thousands of human resources professionals. Discover how HR experts got started with Humin.

                        </h4>
                        <button
                            className="user-story-buton"
                            onClick={() => navigate('/user-stories')}
                        >
                            Show All Stories →
                        </button>

                    </div>
                </div>
            </div>
        </div>
    );
}

export default UserStories;
