import { useState, useEffect } from 'react';
import {
    Typography,
    TextField,
    Button,
    Container,
    Alert,
    Box,
    IconButton,
    CircularProgress,
    Tabs,
    Tab,
    Avatar,
    Badge
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import HomeIcon from '@mui/icons-material/Home';
import LockIcon from '@mui/icons-material/Lock';
import PersonIcon from '@mui/icons-material/Person';
import PhotoCameraIcon from '@mui/icons-material/PhotoCamera';
import DeleteIcon from '@mui/icons-material/Delete';
import './Settings.css';
import * as React from "react";
import { jwtDecode } from "jwt-decode";

// API Configuration - Correct port 9090
const API_BASE_URL = 'http://localhost:9090/api/users';

// Define an interface for the expected token structure
interface DecodedToken {
    userId: number;
    // roles: string[];
    // email: string;
    // firstName: string;
    // lastName: string;
    // titleName?: string;
    // companyName?: string;
    // exp: number;
    // iat: number;
}

const getCurrentUserId = (): number | null => {
    const token = localStorage.getItem('authToken');
    if (token) {
        try {
            const decoded = jwtDecode<DecodedToken>(token);
            return decoded.userId;
        } catch (error) {
            console.error("Failed to decode token or token is invalid/expired:", error);
            // Optionally, remove the invalid token
            // localStorage.removeItem('authToken');
            return null;
        }
    }
    return null;
};

// const CURRENT_USER_ID = getCurrentUserId(); // Removed

type UserData = {
    firstName: string;
    lastName: string;
    email: string;
    phone?: string;
    profileImageUrl?: string;
};

type PasswordData = {
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
};

type FormErrors = {
    firstName?: string;
    lastName?: string;
    email?: string;
    phone?: string;
    currentPassword?: string;
    newPassword?: string;
    confirmPassword?: string;
};

const Settings = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState<UserData>({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        profileImageUrl: ''
    });
    const [formData, setFormData] = useState<UserData>(user);
    const [passwordData, setPasswordData] = useState<PasswordData>({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [isLoading, setIsLoading] = useState(false);
    const [uploadingImage, setUploadingImage] = useState(false);
    const [message, setMessage] = useState<{text: string; type: 'success' | 'error'} | null>(null);
    const [errors, setErrors] = useState<FormErrors>({});
    const [activeTab, setActiveTab] = useState<number>(0);
    const [userId, setUserId] = useState<number | null>(null); // Added userId state

    useEffect(() => {
        const id = getCurrentUserId();
        setUserId(id);
        if (!id) {
            // Optional: Redirect to login if no user ID found
            // navigate('/login'); // Or show a persistent error message
            setMessage({ text: 'User not authenticated. Please log in.', type: 'error' });
        }
    }, [navigate]);

    // Load user profile on component mount, if userId is available
    useEffect(() => {
        if (userId) {
            fetchUserProfile();
        }
    }, [userId]); // Depends on userId now

    useEffect(() => {
        setFormData(user);
    }, [user]);

    // Mesajların otomatik temizlenmesi
    useEffect(() => {
        if (message) {
            const timer = setTimeout(() => setMessage(null), 4000);
            return () => clearTimeout(timer);
        }
    }, [message]);

    // API call to fetch user profile
    const fetchUserProfile = async () => {
        if (!userId) {
            // This check might be redundant if useEffect dependency handles it, but good for safety
            // setMessage({ text: 'User ID not found. Cannot fetch profile.', type: 'error' });
            return;
        }
        setIsLoading(true);
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                setMessage({ text: 'Authentication token not found. Please log in.', type: 'error' });
                setIsLoading(false);
                navigate('/login');
                return;
            }

            const response = await fetch(`${API_BASE_URL}/${userId}/profile`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                if (response.status === 401) {
                    setMessage({ text: 'Authentication failed. Please log in again.', type: 'error' });
                    navigate('/login'); // Redirect to login
                    return;
                }
                if (response.status === 403) {
                    setMessage({ text: 'You are not authorized to view this profile.', type: 'error' });
                    return;
                }
                if (response.status === 404) {
                    setMessage({ text: 'User profile not found.', type: 'error' });
                    return;
                }
                try {
                    const errorResult = await response.json();
                    setMessage({ text: errorResult.message || `Error fetching profile: ${response.status}`, type: 'error' });
                } catch (e) {
                    setMessage({ text: `Error fetching profile: ${response.status}`, type: 'error' });
                }
                return;
            }

            const result = await response.json();

            if (result.success && result.data) {
                const userData = {
                    firstName: result.data.firstName || '',
                    lastName: result.data.lastName || '',
                    email: result.data.email || '',
                    phone: result.data.phone || '',
                    profileImageUrl: result.data.profileImageUrl || ''
                };
                setUser(userData);
            } else {
                setMessage({ text: 'Profil bilgileri yüklenemedi', type: 'error' });
            }
        } catch (error) {
            setMessage({ text: 'Profil bilgileri yüklenirken hata oluştu', type: 'error' });
        } finally {
            setIsLoading(false);
        }
    };

    const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
        setActiveTab(newValue);
        setMessage(null);
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        // Form hatasını temizle
        setErrors(prev => ({ ...prev, [name]: undefined }));

        if (name === 'firstName' || name === 'lastName') {
            const lettersOnly = /^[a-zA-ZğüşıöçĞÜŞİÖÇ\s]*$/;
            if (value === '' || lettersOnly.test(value)) {
                setFormData(prev => ({ ...prev, [name]: value }));
            }
        } else if (name === 'phone') {
            const numbersOnly = /^[0-9+]*$/;
            if (value === '' || numbersOnly.test(value)) {
                setFormData(prev => ({ ...prev, [name]: value }));
            }
        } else if (name === 'currentPassword' || name === 'newPassword' || name === 'confirmPassword') {
            setPasswordData(prev => ({ ...prev, [name]: value }));
        } else {
            setFormData(prev => ({ ...prev, [name]: value }));
        }
    };

    const validateProfileForm = (): boolean => {
        const newErrors: FormErrors = {};

        if (!formData.firstName) {
            newErrors.firstName = 'Ad alanı zorunludur';
        } else if (formData.firstName.length < 2) {
            newErrors.firstName = 'Ad en az 2 karakter olmalıdır';
        }

        if (!formData.lastName) {
            newErrors.lastName = 'Soyad alanı zorunludur';
        } else if (formData.lastName.length < 2) {
            newErrors.lastName = 'Soyad en az 2 karakter olmalıdır';
        }

        if (formData.phone && formData.phone.length < 10) {
            newErrors.phone = 'Telefon numarası en az 10 haneli olmalıdır';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const validatePasswordForm = (): boolean => {
        const newErrors: FormErrors = {};

        if (!passwordData.currentPassword) {
            newErrors.currentPassword = 'Mevcut şifre zorunludur';
        }

        if (!passwordData.newPassword) {
            newErrors.newPassword = 'Yeni şifre zorunludur';
        } else if (passwordData.newPassword.length < 6) {
            newErrors.newPassword = 'Yeni şifre en az 6 karakter olmalıdır';
        }

        if (!passwordData.confirmPassword) {
            newErrors.confirmPassword = 'Yeni şifrenizi tekrar giriniz';
        } else if (passwordData.newPassword !== passwordData.confirmPassword) {
            newErrors.confirmPassword = 'Şifreler eşleşmiyor';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!validateProfileForm()) return;

        if (!userId) {
            setMessage({ text: 'User ID not found. Please log in to update profile.', type: 'error' });
            return;
        }

        setIsLoading(true);
        setMessage(null);

        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                setMessage({ text: 'Authentication token not found. Please log in.', type: 'error' });
                setIsLoading(false);
                navigate('/login');
                return;
            }

            const response = await fetch(`${API_BASE_URL}/${userId}/profile`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(formData),
            });

            if (!response.ok) {
                if (response.status === 401) {
                    setMessage({ text: 'Authentication failed. Please log in again.', type: 'error' });
                    navigate('/login');
                    return;
                }
                if (response.status === 403) {
                    setMessage({ text: 'You are not authorized to update this profile.', type: 'error' });
                    return;
                }
                // 404 might not be typical here unless user was deleted mid-session
                try {
                    const errorResult = await response.json();
                    setMessage({ text: errorResult.message || `Error updating profile: ${response.status}`, type: 'error' });
                } catch (e) {
                    setMessage({ text: `Error updating profile: ${response.status}`, type: 'error' });
                }
                return;
            }

            const result = await response.json();

            if (result.success && result.data) {
                const userData = {
                    firstName: result.data.firstName || '',
                    lastName: result.data.lastName || '',
                    email: result.data.email || '',
                    phone: result.data.phone || '',
                    profileImageUrl: result.data.profileImageUrl || ''
                };
                setUser(userData);
                setMessage({ text: 'Profil bilgileriniz başarıyla güncellendi!', type: 'success' });
            } else {
                // This case handles backend's "success: false" responses
                setMessage({ text: result.message || 'Profil güncellenemedi (backend error).', type: 'error' });
            }
        } catch (error) { // Network errors or JSON parsing errors
            console.error("handleSubmit error:", error);
            setMessage({ text: 'Profil güncellenirken bir ağ hatası veya beklenmedik bir hata oluştu.', type: 'error' });
        } finally {
            setIsLoading(false);
        }
    };

    const handlePasswordSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setIsLoading(true);
        setMessage(null);

        if (!validatePasswordForm()) { 
            setIsLoading(false);
            return; 
        }

        const token = localStorage.getItem('authToken');
        if (!token) {
            setMessage({ type: 'error', text: 'Authentication token not found. Please login again.' });
            setIsLoading(false);
            navigate('/login');
            return;
        }
        const currentUserId = getCurrentUserId();
        if (!currentUserId) {
            setMessage({ type: 'error', text: 'User ID not found. Please login again.' });
            setIsLoading(false);
            return;
        }

        const payload = { 
            currentPassword: passwordData.currentPassword,
            newPassword: passwordData.newPassword,
            confirmPassword: passwordData.confirmPassword
        };

        try {
            const response = await fetch(`${API_BASE_URL}/${currentUserId}/password`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(payload),
            });

            if (!response.ok) {
                if (response.status === 401) {
                    setMessage({ type: 'error', text: 'Authentication failed. Please login again.' });
                    setIsLoading(false);
                    navigate('/login');
                    return;
                }
                if (response.status === 403) {
                    setMessage({ type: 'error', text: 'You are not authorized to change this password.' });
                    setIsLoading(false);
                    return;
                }
                try {
                    const errorResult = await response.json();
                    setMessage({ type: 'error', text: errorResult.message || `Error changing password: ${response.status}` });
                } catch (e) {
                    setMessage({ type: 'error', text: `Error changing password: ${response.status}` });
                }
                return;
            }

            const result = await response.json();

            if (result.success) {
                setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
                setMessage({ type: 'success', text: 'Şifreniz başarıyla değiştirildi!' });
            } else {
                setMessage({ type: 'error', text: result.message || 'Şifre değiştirilemedi (backend error).' });
            }
        } catch (error) {
            console.error("handlePasswordSubmit error:", error);
            setMessage({ type: 'error', text: 'Şifre değiştirilirken bir ağ hatası veya beklenmedik bir hata oluştu.' });
        } finally {
            setIsLoading(false);
        }
    };

    // Profil fotoğrafı yükleme
    const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            const imageFile = event.target.files[0];
            const formData = new FormData();
            formData.append('file', imageFile);

            setIsLoading(true);
            setMessage(null);

            const token = localStorage.getItem('authToken');
            if (!token) {
                setMessage({ type: 'error', text: 'Authentication token not found. Please login again.' });
                setIsLoading(false);
                navigate('/login');
                return;
            }
            const userId = getCurrentUserId();
            if (!userId) {
                setMessage({ type: 'error', text: 'User ID not found. Please login again.' });
                setIsLoading(false);
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/${userId}/profile-image`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                    body: formData,
                });

                if (!response.ok) {
                    if (response.status === 401) {
                        setMessage({ type: 'error', text: 'Authentication failed. Please login again.' });
                        setIsLoading(false);
                        navigate('/login');
                        return;
                    }
                    if (response.status === 403) {
                        setMessage({ type: 'error', text: 'You are not authorized to upload an image here.' });
                        setIsLoading(false);
                        return;
                    }
                    try {
                        const errorResult = await response.json();
                        setMessage({ type: 'error', text: errorResult.message || `Error uploading image: ${response.status}` });
                    } catch (e) {
                        setMessage({ type: 'error', text: `Error uploading image: ${response.status}` });
                    }
                    return;
                }

                const result = await response.json();

                if (result.success) {
                    setUser(prev => ({ ...prev, profileImageUrl: result.data }));
                    setMessage({ type: 'success', text: 'Profil fotoğrafı başarıyla yüklendi!' });
                } else {
                    setMessage({ type: 'error', text: result.message || 'Profil fotoğrafı yüklenemedi (backend error).' });
                }
            } catch (error) {
                console.error("handleImageUpload error:", error);
                setMessage({ type: 'error', text: 'Profil fotoğrafı yüklenirken bir ağ hatası veya beklenmedik bir hata oluştu.' });
            } finally {
                setIsLoading(false);
            }
        }
    };

    // Profil fotoğrafını silme
    const handleImageDelete = async () => {
        setIsLoading(true);
        setMessage(null);

        const token = localStorage.getItem('authToken');
        if (!token) {
            setMessage({ type: 'error', text: 'Authentication token not found. Please login again.' });
            setIsLoading(false);
            navigate('/login');
            return;
        }
        const userId = getCurrentUserId();
        if (!userId) {
            setMessage({ type: 'error', text: 'User ID not found. Please login again.' });
            setIsLoading(false);
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/${userId}/profile-image`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                if (response.status === 401) {
                    setMessage({ type: 'error', text: 'Authentication failed. Please login again.' });
                    setIsLoading(false);
                    navigate('/login');
                    return;
                }
                if (response.status === 403) {
                    setMessage({ type: 'error', text: 'You are not authorized to delete this image.' });
                    setIsLoading(false);
                    return;
                }
                try {
                    const errorResult = await response.json();
                    setMessage({ type: 'error', text: errorResult.message || `Error deleting image: ${response.status}` });
                } catch (e) {
                    setMessage({ type: 'error', text: `Error deleting image: ${response.status}` });
                }
                return;
            }

            const result = await response.json();

            if (result.success) {
                setUser(prev => ({ ...prev, profileImageUrl: '' }));
                setMessage({ type: 'success', text: 'Profil fotoğrafı başarıyla silindi!' });
            } else {
                setMessage({ type: 'error', text: result.message || 'Profil fotoğrafı silinemedi (backend error).' });
            }
        } catch (error) {
            console.error("handleImageDelete error:", error);
            setMessage({ type: 'error', text: 'Profil fotoğrafı silinirken bir ağ hatası veya beklenmedik bir hata oluştu.' });
        } finally {
            setIsLoading(false);
        }
    };

    if (isLoading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <div className="settings-background">
            {/* Şirket Logosu */}

            {/* Ana Sayfa Butonu */}
            <Box sx={{ position: 'absolute', top: 20, right: 20, zIndex: 10 }}>
                <IconButton
                    onClick={() => navigate('/')}
                    sx={{
                        backgroundColor: 'white',
                        '&:hover': {
                            backgroundColor: 'rgba(255, 255, 255, 0.9)',
                            transform: 'translateY(-2px)',
                            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'
                        },
                        padding: '10px',
                        borderRadius: '50%',
                        transition: 'all 0.3s ease',
                    }}
                    aria-label="Back to Homepage"
                >
                    <HomeIcon sx={{ color: '#00796B', fontSize: 28 }} />
                </IconButton>
            </Box>

            <div className="settings-page-content">
                <Container maxWidth="lg">
                    <Box className="settings-container">
                        <Box className="settings-header">
                            <Box sx={{ position: 'relative', display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 3 }}>
                                <Badge
                                    overlap="circular"
                                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                                    badgeContent={
                                        <Box sx={{ display: 'flex', gap: 0.5 }}>
                                            <input
                                                accept="image/*"
                                                style={{ display: 'none' }}
                                                id="profile-image-upload"
                                                type="file"
                                                onChange={handleImageUpload}
                                                disabled={isLoading}
                                            />
                                            <label htmlFor="profile-image-upload">
                                                <IconButton
                                                    component="span"
                                                    size="small"
                                                    disabled={isLoading}
                                                    sx={{
                                                        backgroundColor: '#00796B',
                                                        color: 'white',
                                                        '&:hover': { backgroundColor: '#00695C' },
                                                        width: 32,
                                                        height: 32
                                                    }}
                                                >
                                                    <PhotoCameraIcon fontSize="small" />
                                                </IconButton>
                                            </label>
                                            {user.profileImageUrl && (
                                                <IconButton
                                                    size="small"
                                                    onClick={handleImageDelete}
                                                    disabled={isLoading}
                                                    sx={{
                                                        backgroundColor: '#f44336',
                                                        color: 'white',
                                                        '&:hover': { backgroundColor: '#d32f2f' },
                                                        width: 32,
                                                        height: 32
                                                    }}
                                                >
                                                    <DeleteIcon fontSize="small" />
                                                </IconButton>
                                            )}
                                        </Box>
                                    }
                                >
                                    <Avatar
                                        src={user.profileImageUrl ? `http://localhost:9090${user.profileImageUrl}` : undefined}
                                        sx={{
                                            width: 120,
                                            height: 120,
                                            fontSize: '2.5rem',
                                            backgroundColor: '#00796B',
                                            border: '4px solid white',
                                            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'
                                        }}
                                    >
                                        {!user.profileImageUrl && (
                                            `${formData.firstName?.charAt(0).toUpperCase()}${formData.lastName?.charAt(0).toUpperCase()}`
                                        )}
                                    </Avatar>
                                </Badge>
                                {isLoading && (
                                    <CircularProgress
                                        size={24}
                                        sx={{
                                            position: 'absolute',
                                            top: '50%',
                                            left: '50%',
                                            marginTop: '-12px',
                                            marginLeft: '-12px',
                                        }}
                                    />
                                )}
                                <Typography variant="caption" color="textSecondary" sx={{ mt: 1, textAlign: 'center' }}>
                                    Profil fotoğrafınızı değiştirmek için kamera simgesine tıklayın
                                </Typography>
                            </Box>
                            <Typography variant="h4" component="h1" className="settings-title">
                                Hesap Ayarları
                            </Typography>
                        </Box>
                        {message && (
                            <Alert
                                severity={message.type}
                                sx={{ mb: 3, borderRadius: 2 }}
                            >
                                {message.text}
                            </Alert>
                        )}

                        {/* Ana Container - Sol tab, sağ içerik */}
                        <Box sx={{ display: 'flex', gap: 4, minHeight: '500px' }}>
                            {/* Sol taraf - Vertical Tabs */}
                            <Box sx={{ minWidth: 200 }}>
                                <Tabs
                                    orientation="vertical"
                                    value={activeTab}
                                    onChange={handleTabChange}
                                    aria-label="settings tabs"
                                    sx={{
                                        borderRight: 1,
                                        borderColor: 'divider',
                                        '& .MuiTabs-indicator': {
                                            backgroundColor: '#00796B',
                                            width: 3,
                                            left: 0
                                        },
                                        '& .MuiTab-root': {
                                            color: 'rgba(0, 0, 0, 0.6)',
                                            alignItems: 'flex-start',
                                            textAlign: 'left',
                                            minHeight: 60,
                                            fontSize: '1rem',
                                            fontWeight: 500,
                                            padding: '12px 20px',
                                            textTransform: 'none',
                                            '&:hover': {
                                                backgroundColor: 'rgba(0, 121, 107, 0.04)',
                                                color: '#00796B'
                                            }
                                        },
                                        '& .Mui-selected': {
                                            color: '#00796B',
                                            backgroundColor: 'rgba(0, 121, 107, 0.08)',
                                            fontWeight: 600
                                        },
                                        '& .MuiTab-iconWrapper': {
                                            marginBottom: '4px !important',
                                            marginRight: '8px !important'
                                        }
                                    }}
                                >
                                    <Tab
                                        icon={<PersonIcon />}
                                        label="Profil Bilgileri"
                                        iconPosition="start"
                                        sx={{ justifyContent: 'flex-start' }}
                                    />
                                    <Tab
                                        icon={<LockIcon />}
                                        label="Şifre Değiştir"
                                        iconPosition="start"
                                        sx={{ justifyContent: 'flex-start' }}
                                    />
                                </Tabs>
                            </Box>

                            {/* Sağ taraf - Tab İçeriği */}
                            <Box sx={{ flex: 1 }}>
                                {activeTab === 0 && (
                                    <Box
                                        component="form"
                                        onSubmit={handleSubmit}
                                        className="settings-form"
                                        sx={{ width: '100%' }}
                                    >
                                        <Typography variant="h5" sx={{ mb: 3, color: '#00796B', fontWeight: 600 }}>
                                            Profil Bilgileri
                                        </Typography>
                                        <Box sx={{ display: 'flex', gap: 3, mb: 3, flexWrap: 'wrap' }}>
                                            <Box sx={{ flex: '1 1 300px' }}>
                                                <TextField
                                                    fullWidth
                                                    label="Ad"
                                                    name="firstName"
                                                    value={formData.firstName}
                                                    onChange={handleChange}
                                                    variant="outlined"
                                                    required
                                                    error={!!errors.firstName}
                                                    helperText={errors.firstName}
                                                    className="custom-textfield"
                                                />
                                            </Box>
                                            <Box sx={{ flex: '1 1 300px' }}>
                                                <TextField
                                                    fullWidth
                                                    label="Soyad"
                                                    name="lastName"
                                                    value={formData.lastName}
                                                    onChange={handleChange}
                                                    variant="outlined"
                                                    required
                                                    error={!!errors.lastName}
                                                    helperText={errors.lastName}
                                                    className="custom-textfield"
                                                />
                                            </Box>
                                        </Box>
                                        <Box sx={{ display: 'flex', gap: 3, mb: 3, flexWrap: 'wrap' }}>
                                            <Box sx={{ flex: '1 1 300px' }}>
                                                <TextField
                                                    fullWidth
                                                    label="E-posta"
                                                    name="email"
                                                    type="email"
                                                    value={formData.email}
                                                    onChange={handleChange}
                                                    variant="outlined"
                                                    required
                                                    error={!!errors.email}
                                                    helperText={errors.email}
                                                    className="custom-textfield"
                                                />
                                            </Box>
                                            <Box sx={{ flex: '1 1 300px' }}>
                                                <TextField
                                                    fullWidth
                                                    label="Telefon"
                                                    name="phone"
                                                    value={formData.phone}
                                                    onChange={handleChange}
                                                    variant="outlined"
                                                    error={!!errors.phone}
                                                    helperText={errors.phone}
                                                    className="custom-textfield"
                                                    placeholder="+905551234567"
                                                />
                                            </Box>
                                        </Box>
                                        <Box sx={{ textAlign: 'right', mt: 4 }}>
                                            <Button
                                                type="submit"
                                                variant="contained"
                                                disabled={isLoading}
                                                className="submit-button"
                                                sx={{
                                                    minWidth: 200,
                                                    height: 50,
                                                    fontSize: '1.1rem',
                                                    fontWeight: 'bold',
                                                    borderRadius: 3,
                                                    backgroundColor: '#00796B',
                                                    textTransform: 'none',
                                                    '&:hover': {
                                                        backgroundColor: '#00695C',
                                                        transform: 'translateY(-2px)',
                                                        boxShadow: '0 6px 12px rgba(0, 0, 0, 0.15)'
                                                    },
                                                    transition: 'all 0.3s ease'
                                                }}
                                            >
                                                {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Profili Güncelle'}
                                            </Button>
                                        </Box>
                                    </Box>
                                )}

                                {activeTab === 1 && (
                                    <Box
                                        component="form"
                                        onSubmit={handlePasswordSubmit}
                                        className="settings-form"
                                        sx={{ width: '100%' }}
                                    >
                                        <Typography variant="h5" sx={{ mb: 3, color: '#00796B', fontWeight: 600 }}>
                                            Şifre Değiştir
                                        </Typography>
                                        <Box sx={{ maxWidth: 400 }}>
                                            <Box sx={{ mb: 3 }}>
                                                <TextField
                                                    fullWidth
                                                    label="Mevcut Şifre"
                                                    name="currentPassword"
                                                    type="password"
                                                    value={passwordData.currentPassword}
                                                    onChange={handleChange}
                                                    variant="outlined"
                                                    required
                                                    error={!!errors.currentPassword}
                                                    helperText={errors.currentPassword}
                                                    className="custom-textfield"
                                                />
                                            </Box>
                                            <Box sx={{ mb: 3 }}>
                                                <TextField
                                                    fullWidth
                                                    label="Yeni Şifre"
                                                    name="newPassword"
                                                    type="password"
                                                    value={passwordData.newPassword}
                                                    onChange={handleChange}
                                                    variant="outlined"
                                                    required
                                                    error={!!errors.newPassword}
                                                    helperText={errors.newPassword}
                                                    className="custom-textfield"
                                                />
                                            </Box>
                                            <Box sx={{ mb: 3 }}>
                                                <TextField
                                                    fullWidth
                                                    label="Yeni Şifre (Tekrar)"
                                                    name="confirmPassword"
                                                    type="password"
                                                    value={passwordData.confirmPassword}
                                                    onChange={handleChange}
                                                    variant="outlined"
                                                    required
                                                    error={!!errors.confirmPassword}
                                                    helperText={errors.confirmPassword}
                                                    className="custom-textfield"
                                                />
                                            </Box>
                                        </Box>
                                        <Box sx={{ textAlign: 'right', mt: 4 }}>
                                            <Button
                                                type="submit"
                                                variant="contained"
                                                disabled={isLoading}
                                                className="submit-button"
                                                sx={{
                                                    minWidth: 200,
                                                    height: 50,
                                                    fontSize: '1.1rem',
                                                    fontWeight: 'bold',
                                                    borderRadius: 3,
                                                    backgroundColor: '#00796B',
                                                    textTransform: 'none',
                                                    '&:hover': {
                                                        backgroundColor: '#00695C',
                                                        transform: 'translateY(-2px)',
                                                        boxShadow: '0 6px 12px rgba(0, 0, 0, 0.15)'
                                                    },
                                                    transition: 'all 0.3s ease'
                                                }}
                                            >
                                                {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Şifreyi Değiştir'}
                                            </Button>
                                        </Box>
                                    </Box>
                                )}
                            </Box>
                        </Box>
                    </Box>
                </Container>
            </div>
        </div>
    );
};

export default Settings;





