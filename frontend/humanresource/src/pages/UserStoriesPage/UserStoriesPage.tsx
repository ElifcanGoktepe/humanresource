import 'bootstrap/dist/css/bootstrap.min.css';

import './UserStoriesPage.css';

import UserStoriesRevisionForPage from '../../components/atoms/UserStoriesRevisionForPage.tsx';

function UserStoriesPage() {
    return (
        <div className="user-stories-background">
            <img src="/img/logo4.png" alt="Logo" className="logo-on-background" />
            <div className="user-stories-scroll-container">
                <div className="user-stories-content">
                    <UserStoriesRevisionForPage />
                    <UserStoriesRevisionForPage />
                    <UserStoriesRevisionForPage />
                    <UserStoriesRevisionForPage />
                    <UserStoriesRevisionForPage />
                </div>
            </div>
        </div>
    );
}

export default UserStoriesPage;
