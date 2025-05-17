import { useState, useEffect } from 'react';
import { 
  Typography, 
  TextField, 
  Button, 
  Container, 
  Alert, 
  Box, 
  IconButton,
  CircularProgress
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import HomeIcon from '@mui/icons-material/Home';
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

const UserSettingsPage = () => {
  // Gerçek projede context veya API'den alınmalı
  const [user, setUser] = useState<UserData>(mockUser);
  const navigate = useNavigate();
  const [formData, setFormData] = useState<UserData>(user);
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState<{text: string; type: 'success' | 'error'} | null>(null);

  useEffect(() => {
    setFormData(user);
  }, [user]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
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
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage(null);
    // API çağrısı burada yapılacak
    setTimeout(() => {
      setUser(formData);
      setMessage({ text: 'Your profile information has been updated successfully!', type: 'success' });
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
              <Box className="user-avatar">
                {formData.firstName?.charAt(0).toUpperCase()}{formData.lastName?.charAt(0).toUpperCase()}
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
                  disabled={isLoading}
                  inputProps={{
                    inputMode: 'tel',
                    pattern: '[0-9+]*',
                    title: 'Please enter only numbers and the + sign'
                  }}
                />
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                <Button
                  type="button"
                  variant="outlined"
                  color="primary"
                  onClick={() => navigate('/')}
                  disabled={isLoading}
                  sx={{ minWidth: '120px' }}
                >
                  Cancel
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  disabled={isLoading}
                  startIcon={isLoading ? <CircularProgress size={20} color="inherit" /> : null}
                  sx={{ minWidth: '120px' }}
                >
                  {isLoading ? 'Saving...' : 'Save Changes'}
                </Button>
              </Box>
            </Box>
          </Box>
        </Container>
      </div>
    </div>
  );
};

export default UserSettingsPage; 