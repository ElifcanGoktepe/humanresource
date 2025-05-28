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
import './UserSettingsPage.css';
import * as React from "react";

// API Configuration - Correct port 9090
const API_BASE_URL = 'http://localhost:9090/api/users';

const getCurrentUserId = () => {
  // TODO: Replace with real authentication context or JWT decode
  // Example: return authContext.user?.id;
  // const token = localStorage.getItem('authToken');
  // if (token) {
  //   const decoded = jwtDecode(token);
  //   return decoded.userId;
  // }
  return 1;
};

const CURRENT_USER_ID = getCurrentUserId();

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

const UserSettingsPage = () => {
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

  // Load user profile on component mount
  useEffect(() => {
    fetchUserProfile();
  }, []);

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
    setIsLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/${CURRENT_USER_ID}/profile`);
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
    
    setIsLoading(true);
    setMessage(null);
    
    try {
      const response = await fetch(`${API_BASE_URL}/${CURRENT_USER_ID}/profile`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });
      
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
        setMessage({ text: result.message || 'Profil güncellenemedi', type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Profil güncellenirken hata oluştu', type: 'error' });
    } finally {
      setIsLoading(false);
    }
  };
  
  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validatePasswordForm()) return;
    
    setIsLoading(true);
    setMessage(null);
    
    try {
      const response = await fetch(`${API_BASE_URL}/${CURRENT_USER_ID}/password`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          currentPassword: passwordData.currentPassword,
          newPassword: passwordData.newPassword
        }),
      });
      
      const result = await response.json();
      
      if (result.success) {
        setPasswordData({
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        });
        setMessage({ text: 'Şifreniz başarıyla değiştirildi!', type: 'success' });
      } else {
        setMessage({ text: result.message || 'Şifre değiştirilemedi', type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Şifre değiştirilirken hata oluştu', type: 'error' });
    } finally {
      setIsLoading(false);
    }
  };

  // Profil fotoğrafı yükleme
  const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Dosya boyutu kontrolü (5MB)
    if (file.size > 5 * 1024 * 1024) {
      setMessage({ text: 'Dosya boyutu 5MB\'dan büyük olamaz', type: 'error' });
      return;
    }

    // Dosya tipi kontrolü
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    if (!allowedTypes.includes(file.type)) {
      setMessage({ text: 'Sadece JPG, JPEG, PNG ve GIF dosyaları yüklenebilir', type: 'error' });
      return;
    }

    setUploadingImage(true);
    setMessage(null);

    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await fetch(`${API_BASE_URL}/${CURRENT_USER_ID}/profile-image`, {
        method: 'POST',
        body: formData,
      });

      const result = await response.json();

      if (result.success) {
        setUser(prev => ({ ...prev, profileImageUrl: result.data }));
        setMessage({ text: 'Profil fotoğrafı başarıyla yüklendi!', type: 'success' });
      } else {
        setMessage({ text: result.message || 'Profil fotoğrafı yüklenemedi', type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Profil fotoğrafı yüklenirken hata oluştu', type: 'error' });
    } finally {
      setUploadingImage(false);
    }
  };

  // Profil fotoğrafını silme
  const handleImageDelete = async () => {
    setUploadingImage(true);
    setMessage(null);

    try {
      const response = await fetch(`${API_BASE_URL}/${CURRENT_USER_ID}/profile-image`, {
        method: 'DELETE',
      });

      const result = await response.json();

      if (result.success) {
        setUser(prev => ({ ...prev, profileImageUrl: '' }));
        setMessage({ text: 'Profil fotoğrafı başarıyla silindi!', type: 'success' });
      } else {
        setMessage({ text: result.message || 'Profil fotoğrafı silinemedi', type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Profil fotoğrafı silinirken hata oluştu', type: 'error' });
    } finally {
      setUploadingImage(false);
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
      <Box 
        component="img"
        src="/img/logo1.png" 
        alt="Company Logo"
        sx={{
          position: 'absolute',
          top: 20,
          left: 20,
          zIndex: 10,
          height: '40px',
          cursor: 'pointer',
          '&:hover': {
            opacity: 0.9,
            transform: 'scale(1.03)',
            transition: 'all 0.3s ease'
          }
        }}
        onClick={() => navigate('/')}
      />
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
      <img src="/img/logo4.png" className="logo-on-background" alt="Logo" />
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
                        disabled={uploadingImage}
                      />
                      <label htmlFor="profile-image-upload">
                        <IconButton
                          component="span"
                          size="small"
                          disabled={uploadingImage}
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
                          disabled={uploadingImage}
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
                {uploadingImage && (
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

export default UserSettingsPage;

//TODO: Backend ile entegre edilecek


