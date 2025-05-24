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
  Divider,
  Select,
  MenuItem,
  FormControl,
  InputLabel
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import HomeIcon from '@mui/icons-material/Home';
import LockIcon from '@mui/icons-material/Lock';
import PersonIcon from '@mui/icons-material/Person';
import WorkIcon from '@mui/icons-material/Work';
import PhotoCamera from '@mui/icons-material/PhotoCamera';
import './UserSettingsPage.css';
import * as React from "react";
import { 
  Gender, 
  BloodType, 
  EducationLevel, 
  MaritalStatus,
  GenderLabels,
  BloodTypeLabels,
  EducationLevelLabels,
  MaritalStatusLabels 
} from '../../constants/enums';

// API Configuration - Correct port 9090
const API_BASE_URL = 'http://localhost:9090/api/users';
const PERSONAL_FILE_API = 'http://localhost:9090/api/personel-file';
const CURRENT_USER_ID = 1; // This should come from authentication context

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
  // Personal File errors
  gender?: string;
  birthdate?: string;
  personalPhone?: string;
  personalEmail?: string;
  nationalId?: string;
  educationLevel?: string;
  maritalStatus?: string;
  bloodType?: string;
  address?: string;
  city?: string;
  iban?: string;
};

type PersonalFileData = {
  gender: string;
  birthdate: string;
  personalPhone: string;
  personalEmail: string;
  nationalId: string;
  educationLevel: string;
  maritalStatus: string;
  bloodType: string;
  numberOfChildren: string;
  address: string;
  city: string;
  iban: string;
  bankName: string;
  bankAccountNumber: string;
  bankAccountType: string;
};

const UserSettingsPage = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<UserData>({
    firstName: '',
    lastName: '',
    email: '',
    phone: ''
  });
  const [formData, setFormData] = useState<UserData>(user);
  const [passwordData, setPasswordData] = useState<PasswordData>({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [personalFileData, setPersonalFileData] = useState<PersonalFileData>({
    gender: '',
    birthdate: '',
    personalPhone: '',
    personalEmail: '',
    nationalId: '',
    educationLevel: '',
    maritalStatus: '',
    bloodType: '',
    numberOfChildren: '',
    address: '',
    city: '',
    iban: '',
    bankName: '',
    bankAccountNumber: '',
    bankAccountType: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState<{text: string; type: 'success' | 'error'} | null>(null);
  const [errors, setErrors] = useState<FormErrors>({});
  const [activeTab, setActiveTab] = useState<number>(0);
  const [profileImage, setProfileImage] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  // Load user profile on component mount
  useEffect(() => {
    fetchUserProfile();
    fetchPersonalFile();
  }, []);

  useEffect(() => {
    setFormData(user);
  }, [user]);

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
          phone: result.data.phone || ''
        };
        setUser(userData);
      } else {
        setMessage({ text: 'Failed to load profile data', type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Error loading profile data', type: 'error' });
    } finally {
      setIsLoading(false);
    }
  };

  // API call to fetch personal file
  const fetchPersonalFile = async () => {
    try {
      const response = await fetch(`${PERSONAL_FILE_API}/me`);
      const result = await response.json();
      
      if (result.code === 200 && result.data) {
        setPersonalFileData({
          gender: result.data.gender || '',
          birthdate: result.data.birthdate || '',
          personalPhone: result.data.personalPhone || '',
          personalEmail: result.data.personalEmail || '',
          nationalId: result.data.nationalId || '',
          educationLevel: result.data.educationLevel || '',
          maritalStatus: result.data.maritalStatus || '',
          bloodType: result.data.bloodType || '',
          numberOfChildren: result.data.numberOfChildren || '',
          address: result.data.address || '',
          city: result.data.city || '',
          iban: result.data.iban || '',
          bankName: result.data.bankName || '',
          bankAccountNumber: result.data.bankAccountNumber || '',
          bankAccountType: result.data.bankAccountType || ''
        });
      }
    } catch (error) {
      console.error('Error loading personal file:', error);
    }
  };

  const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
    setMessage(null);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    
    // Clear form error
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
    } else if (name === 'personalPhone' || name === 'personalEmail' || 
               name === 'nationalId' || name === 'numberOfChildren' || name === 'address' || name === 'city' || 
               name === 'iban' || name === 'bankName' || name === 'bankAccountNumber' || name === 'bankAccountType') {
      setPersonalFileData(prev => ({ ...prev, [name]: value }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSelectChange = (e: any) => {
    const { name, value } = e.target;
    
    // Clear form error
    setErrors(prev => ({ ...prev, [name]: undefined }));
    
    if (name === 'gender' || name === 'birthdate' || name === 'educationLevel' || name === 'maritalStatus' || name === 'bloodType') {
      setPersonalFileData(prev => ({ ...prev, [name]: value }));
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
          phone: result.data.phone || ''
        };
        setUser(userData);
        setMessage({ text: 'Your profile information has been updated successfully!', type: 'success' });
      } else {
        setMessage({ text: result.message || 'Failed to update profile', type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Error updating profile', type: 'error' });
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
        body: JSON.stringify(passwordData),
      });
      
      const result = await response.json();
      
      if (result.success) {
        setPasswordData({
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        });
        setMessage({ text: 'Your password has been changed successfully!', type: 'success' });
      } else {
        setMessage({ text: result.message || 'Failed to change password', type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Error changing password', type: 'error' });
    } finally {
      setIsLoading(false);
    }
  };

  const handlePersonalFileSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    setIsLoading(true);
    setMessage(null);
    
    try {
      const response = await fetch(`${PERSONAL_FILE_API}/save`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(personalFileData),
      });
      
      const result = await response.json();
      
      if (result.code === 200) {
        setMessage({ text: 'Personal file updated successfully!', type: 'success' });
      } else {
        setMessage({ text: result.message || 'Failed to update personal file', type: 'error' });
      }
    } catch (error) {
      setMessage({ text: 'Error updating personal file', type: 'error' });
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
      {/* Company Logo */}
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
      {/* Home Button */}
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
              <Tab icon={<WorkIcon />} label="Personal File" />
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
            
            {activeTab === 2 && (
              <Box 
                component="form" 
                onSubmit={handlePersonalFileSubmit} 
                className="settings-form" 
                sx={{ width: '100%' }}
              >
                <Typography variant="h6" sx={{ mb: 3, color: '#00796B' }}>
                  Personal Information
                </Typography>
                <Box sx={{ display: 'flex', gap: 3, mb: 3, flexWrap: 'wrap' }}>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <FormControl fullWidth>
                      <InputLabel id="gender-label">Gender</InputLabel>
                      <Select
                        labelId="gender-label"
                        id="gender"
                        name="gender"
                        value={personalFileData.gender}
                        onChange={handleSelectChange}
                        label="Gender"
                        disabled={isLoading}
                        error={!!errors.gender}
                      >
                        {GenderLabels.map((gender) => (
                          <MenuItem key={gender.value} value={gender.value}>
                            {gender.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="Birth Date"
                      name="birthdate"
                      type="date"
                      value={personalFileData.birthdate}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                      error={!!errors.birthdate}
                      helperText={errors.birthdate}
                      InputLabelProps={{ shrink: true }}
                    />
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', gap: 3, mb: 3, flexWrap: 'wrap' }}>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="Personal Phone"
                      name="personalPhone"
                      value={personalFileData.personalPhone}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                      error={!!errors.personalPhone}
                      helperText={errors.personalPhone}
                    />
                  </Box>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="Personal Email"
                      name="personalEmail"
                      type="email"
                      value={personalFileData.personalEmail}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                      error={!!errors.personalEmail}
                      helperText={errors.personalEmail}
                    />
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', gap: 3, mb: 3, flexWrap: 'wrap' }}>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="National ID"
                      name="nationalId"
                      value={personalFileData.nationalId}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                      error={!!errors.nationalId}
                      helperText={errors.nationalId}
                    />
                  </Box>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <FormControl fullWidth>
                      <InputLabel id="educationLevel-label">Education Level</InputLabel>
                      <Select
                        labelId="educationLevel-label"
                        id="educationLevel"
                        name="educationLevel"
                        value={personalFileData.educationLevel}
                        onChange={handleSelectChange}
                        label="Education Level"
                        disabled={isLoading}
                        error={!!errors.educationLevel}
                      >
                        {EducationLevelLabels.map((level) => (
                          <MenuItem key={level.value} value={level.value}>
                            {level.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Box>
                
                <Typography variant="h6" sx={{ mb: 3, color: '#00796B' }}>
                  Family & Health Information
                </Typography>
                <Box sx={{ display: 'flex', gap: 3, mb: 3, flexWrap: 'wrap' }}>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <FormControl fullWidth>
                      <InputLabel id="maritalStatus-label">Marital Status</InputLabel>
                      <Select
                        labelId="maritalStatus-label"
                        id="maritalStatus"
                        name="maritalStatus"
                        value={personalFileData.maritalStatus}
                        onChange={handleSelectChange}
                        label="Marital Status"
                        disabled={isLoading}
                        error={!!errors.maritalStatus}
                      >
                        {MaritalStatusLabels.map((status) => (
                          <MenuItem key={status.value} value={status.value}>
                            {status.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <FormControl fullWidth>
                      <InputLabel id="bloodType-label">Blood Type</InputLabel>
                      <Select
                        labelId="bloodType-label"
                        id="bloodType"
                        name="bloodType"
                        value={personalFileData.bloodType}
                        onChange={handleSelectChange}
                        label="Blood Type"
                        disabled={isLoading}
                        error={!!errors.bloodType}
                      >
                        {BloodTypeLabels.map((type) => (
                          <MenuItem key={type.value} value={type.value}>
                            {type.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', gap: 3, mb: 3, flexWrap: 'wrap' }}>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="Number of Children"
                      name="numberOfChildren"
                      type="number"
                      value={personalFileData.numberOfChildren}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                    />
                  </Box>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="City"
                      name="city"
                      value={personalFileData.city}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                      error={!!errors.city}
                      helperText={errors.city}
                    />
                  </Box>
                </Box>
                <Box sx={{ mb: 3 }}>
                  <TextField
                    fullWidth
                    label="Address"
                    name="address"
                    value={personalFileData.address}
                    onChange={handleSelectChange}
                    variant="outlined"
                    disabled={isLoading}
                    error={!!errors.address}
                    helperText={errors.address}
                    multiline
                    rows={3}
                  />
                </Box>
                
                <Typography variant="h6" sx={{ mb: 3, color: '#00796B' }}>
                  Banking Information
                </Typography>
                <Box sx={{ display: 'flex', gap: 3, mb: 3, flexWrap: 'wrap' }}>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="IBAN"
                      name="iban"
                      value={personalFileData.iban}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                      error={!!errors.iban}
                      helperText={errors.iban}
                    />
                  </Box>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="Bank Name"
                      name="bankName"
                      value={personalFileData.bankName}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                    />
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', gap: 3, mb: 4, flexWrap: 'wrap' }}>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="Bank Account Number"
                      name="bankAccountNumber"
                      value={personalFileData.bankAccountNumber}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                    />
                  </Box>
                  <Box sx={{ flex: '1 1 300px' }}>
                    <TextField
                      fullWidth
                      label="Bank Account Type"
                      name="bankAccountType"
                      value={personalFileData.bankAccountType}
                      onChange={handleSelectChange}
                      variant="outlined"
                      disabled={isLoading}
                    />
                  </Box>
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
                    Save Personal File
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