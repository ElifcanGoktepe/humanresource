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
  Divider
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import HomeIcon from '@mui/icons-material/Home';
import LockIcon from '@mui/icons-material/Lock';
import PersonIcon from '@mui/icons-material/Person';
import PhotoCamera from '@mui/icons-material/PhotoCamera';
import './UserSettingsPage.css';

// Backend'den çekilecek user datası için örnek type
// Gerçek projede context veya props ile alınabilir
const mockUser = {
  firstName: 'Test',
  lastName: 'User',
  email: 'test@example.com',
  phone: '+905551234567',
};

type UserData = {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
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
  // Gerçek projede context veya API'den alınmalı
  const [user, setUser] = useState<UserData>(mockUser);
  const navigate = useNavigate();
  const [formData, setFormData] = useState<UserData>(user);
  const [passwordData, setPasswordData] = useState<PasswordData>({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState<{text: string; type: 'success' | 'error'} | null>(null);
  const [errors, setErrors] = useState<FormErrors>({});
  const [activeTab, setActiveTab] = useState<number>(0);
  const [profileImage, setProfileImage] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  useEffect(() => {
    setFormData(user);
  }, [user]);

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

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setProfileImage(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  const validateProfileForm = (): boolean => {
    const newErrors: FormErrors = {};
    
    if (!formData.firstName) {
      newErrors.firstName = 'First name is required';
    } else if (formData.firstName.length < 2) {
      newErrors.firstName = 'First name must be at least 2 characters';
    }
    
    if (!formData.lastName) {
      newErrors.lastName = 'Last name is required';
    } else if (formData.lastName.length < 2) {
      newErrors.lastName = 'Last name must be at least 2 characters';
    }
    
    if (formData.phone && formData.phone.length < 10) {
      newErrors.phone = 'Phone number must be at least 10 digits';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  const validatePasswordForm = (): boolean => {
    const newErrors: FormErrors = {};
    
    if (!passwordData.currentPassword) {
      newErrors.currentPassword = 'Current password is required';
    }
    
    if (!passwordData.newPassword) {
      newErrors.newPassword = 'New password is required';
    } else if (passwordData.newPassword.length < 6) {
      newErrors.newPassword = 'New password must be at least 6 characters';
    }
    
    if (!passwordData.confirmPassword) {
      newErrors.confirmPassword = 'Please confirm your new password';
    } else if (passwordData.newPassword !== passwordData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateProfileForm()) return;
    
    setIsLoading(true);
    setMessage(null);
    // API çağrısı burada yapılacak
    setTimeout(() => {
      setUser(formData);
      setMessage({ text: 'Your profile information has been updated successfully!', type: 'success' });
      setIsLoading(false);
    }, 1200);
  };
  
  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validatePasswordForm()) return;
    
    setIsLoading(true);
    setMessage(null);
    // API çağrısı burada yapılacak
    setTimeout(() => {
      setPasswordData({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      });
      setMessage({ text: 'Your password has been changed successfully!', type: 'success' });
      setIsLoading(false);
    }, 1200);
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
              <Box className="user-avatar" sx={{ position: 'relative' }}>
                {previewUrl ? (
                  <img src={previewUrl} alt="Profile Preview" className="profile-img-preview" />
                ) : (
                  <>
                    {formData.firstName?.charAt(0).toUpperCase()}{formData.lastName?.charAt(0).toUpperCase()}
                  </>
                )}
                <input
                  type="file"
                  accept="image/*"
                  style={{ display: 'none' }}
                  id="profile-image-upload"
                  onChange={handleImageChange}
                />
                <label htmlFor="profile-image-upload" className="profile-img-upload-label">
                  <IconButton component="span" sx={{ position: 'absolute', bottom: 0, right: 0, backgroundColor: 'white', boxShadow: 1 }}>
                    <PhotoCamera sx={{ color: '#00796B' }} />
                  </IconButton>
                </label>
              </Box>
              <Typography variant="h4" component="h1" className="settings-title">
                Account Settings
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
            
            <Tabs
              value={activeTab}
              onChange={handleTabChange}
              aria-label="settings tabs"
              sx={{ 
                mb: 3,
                '& .MuiTabs-indicator': { backgroundColor: '#00796B' },
                '& .MuiTab-root': { color: 'rgba(0, 0, 0, 0.6)' },
                '& .Mui-selected': { color: '#00796B' }
              }}
            >
              <Tab icon={<PersonIcon />} label="Profile" />
              <Tab icon={<LockIcon />} label="Password" />
          </Tabs>
          {activeTab === 0 && (
            <Box 
              component="form" 
              onSubmit={handleSubmit} 
              className="settings-form" 
              sx={{ width: '100%' }}
            >
              <Box sx={{ display: 'flex', gap: 3, mb: 3, flexWrap: 'wrap' }}>
                <Box sx={{ flex: '1 1 300px' }}>
                  <TextField
                    fullWidth
                    label="First Name"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    variant="outlined"
                    required
                    disabled={isLoading}
                    error={!!errors.firstName}
                    helperText={errors.firstName}
                    inputProps={{
                      pattern: '[A-Za-zğüşıöçĞÜŞİÖÇ\\s]*',
                      title: 'Please enter only letters and spaces'
                    }}
                  />
                </Box>
                <Box sx={{ flex: '1 1 300px' }}>
                  <TextField
                    fullWidth
                    label="Last Name"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    variant="outlined"
                    required
                    disabled={isLoading}
                    error={!!errors.lastName}
                    helperText={errors.lastName}
                    inputProps={{
                      pattern: '[A-Za-zğüşıöçĞÜŞİÖÇ\\s]*',
                      title: 'Please enter only letters and spaces'
                    }}
                  />
                </Box>
              </Box>
              <Box sx={{ mb: 3 }}>
                <TextField
                  fullWidth
                  label="Email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  variant="outlined"
                  required
                  disabled
                />
              </Box>
              <Box sx={{ mb: 4 }}>
                <TextField
                  fullWidth
                  label="Phone"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  variant="outlined"
                  placeholder="+90 555 123 4567"
                  disabled={isLoading}
                  error={!!errors.phone}
                  helperText={errors.phone}
                  inputProps={{
                    pattern: '[0-9+\\s]*',
                    title: 'Please enter only numbers, plus sign and spaces'
                  }}
                />
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  size="large"
                  disabled={isLoading}
                  sx={{
                    backgroundColor: '#00796B',
                    px: 4,
                    py: 1.5,
                    borderRadius: 2,
                    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      backgroundColor: '#00695C',
                      transform: 'translateY(-2px)',
                      boxShadow: '0 6px 12px rgba(0, 0, 0, 0.15)'
                    }
                  }}
                >
                  Save Changes
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
              <Box sx={{ mb: 3 }}>
                <TextField
                  fullWidth
                  label="Current Password"
                  name="currentPassword"
                  type="password"
                  value={passwordData.currentPassword}
                  onChange={handleChange}
                  variant="outlined"
                  required
                  disabled={isLoading}
                  error={!!errors.currentPassword}
                  helperText={errors.currentPassword}
                />
              </Box>
              <Box sx={{ mb: 3 }}>
                <TextField
                  fullWidth
                  label="New Password"
                  name="newPassword"
                  type="password"
                  value={passwordData.newPassword}
                  onChange={handleChange}
                  variant="outlined"
                  required
                  disabled={isLoading}
                  error={!!errors.newPassword}
                  helperText={errors.newPassword}
                />
              </Box>
              <Box sx={{ mb: 4 }}>
                <TextField
                  fullWidth
                  label="Confirm New Password"
                  name="confirmPassword"
                  type="password"
                  value={passwordData.confirmPassword}
                  onChange={handleChange}
                  variant="outlined"
                  required
                  disabled={isLoading}
                  error={!!errors.confirmPassword}
                  helperText={errors.confirmPassword}
                />
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  size="large"
                  disabled={isLoading}
                  sx={{
                    backgroundColor: '#00796B',
                    px: 4,
                    py: 1.5,
                    borderRadius: 2,
                    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      backgroundColor: '#00695C',
                      transform: 'translateY(-2px)',
                      boxShadow: '0 6px 12px rgba(0, 0, 0, 0.15)'
                    }
                  }}
                >
                  Change Password
                </Button>
              </Box>
            </Box>
          )}
        </Box>
      </Container>
    </div>
  </div>
);
};

export default UserSettingsPage; 

//TODO: Backend ile entegre edilecek


