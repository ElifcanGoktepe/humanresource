import React, { useEffect, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css'; // Bootstrap stilini kullanmak için
import AssignmentCreateForm from './AssignmentCreateForm'; // Yeni oluşturulan formu import et

// TypeScript Arayüzleri
interface ResourceInfo {
  id: number;
  name: string;
}

interface EmployeeInfo {
  id: number;
  firstName: string;
  lastName: string;
}

export interface AssignmentData {
  id: number;
  resource: ResourceInfo;
  assignedTo: EmployeeInfo;
  assignmentDate: string;
  expectedReturnDate: string;
  actualReturnDate?: string | null;
  status: string;
  description?: string;
  category?: string;
}

const API_BASE_URL = 'http://localhost:9090/api/v1'; // Backend API adresimiz

const AssignmentPage: React.FC = () => {
  const [assignments, setAssignments] = useState<AssignmentData[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [showCreateModal, setShowCreateModal] = useState<boolean>(false); // Modal görünürlüğü için state

  const fetchAssignments = async () => { // Fonksiyonu dışarı aldık, tekrar kullanabilmek için
    setIsLoading(true);
    setError(null);
    const token = localStorage.getItem('authToken'); // Settings.tsx'deki gibi authToken kullanıyoruz

    if (!token) {
      setError('Kimlik doğrulama tokenı bulunamadı. Lütfen giriş yapın.');
      setIsLoading(false);
      // İsteğe bağlı: kullanıcıyı login sayfasına yönlendir
      // navigate('/login'); 
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/assignments`,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: 'Bilinmeyen bir hata oluştu.' }));
        throw new Error(errorData.message || `Zimmetler alınamadı: ${response.status}`);
      }

      const data: AssignmentData[] = await response.json();
      setAssignments(data);
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Zimmetler yüklenirken bilinmeyen bir hata oluştu.');
      }
      console.error("Fetch assignments error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAssignments();
  }, []);

  const handleOpenCreateModal = () => {
    setShowCreateModal(true);
  };

  const handleCloseCreateModal = () => {
    setShowCreateModal(false);
  };

  const handleAssignmentCreated = () => {
    setShowCreateModal(false); // Modalı kapat
    fetchAssignments(); // Zimmet listesini yenile
  };

  if (isLoading && assignments.length === 0) { // Sadece ilk yüklemede tam sayfa yükleme göster
    return <div className="container mt-3"><p>Zimmetler yükleniyor...</p></div>;
  }

  if (error && assignments.length === 0) { // Sadece ilk yüklemede tam sayfa hata göster
    return <div className="container mt-3"><div className="alert alert-danger">Hata: {error}</div></div>;
  }

  return (
    <div className="container mt-3">
      <h1>Zimmet Yönetimi</h1>
      {/* Hata mesajını listenin üstünde de gösterebiliriz (eğer ilk yükleme sonrası bir hata olursa) */}
      {error && assignments.length > 0 && (
          <div className="alert alert-danger">Listeyi yenilerken hata oluştu: {error}</div>
      )}
      <button className="btn btn-primary mb-3" onClick={handleOpenCreateModal}>
        Yeni Zimmet Oluştur
      </button>
      
      {isLoading && assignments.length > 0 && <p>Liste güncelleniyor...</p>} 

      {assignments.length === 0 && !isLoading && !error ? (
        <p>Gösterilecek zimmet bulunmamaktadır.</p>
      ) : (
        <table className="table table-striped table-hover">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Kaynak Adı</th>
              <th>Zimmetlenen Kişi</th>
              <th>Zimmet Tarihi</th>
              <th>Bek. İade Tarihi</th>
              <th>Durum</th>
              <th>Aksiyonlar</th>
            </tr>
          </thead>
          <tbody>
            {assignments.map((assignment) => (
              <tr key={assignment.id}>
                <td>{assignment.id}</td>
                <td>{assignment.resource?.name || 'N/A'}</td>
                <td>{`${assignment.assignedTo?.firstName || ''} ${assignment.assignedTo?.lastName || ''}`.trim() || 'N/A'}</td>
                <td>{new Date(assignment.assignmentDate).toLocaleDateString()}</td>
                <td>{new Date(assignment.expectedReturnDate).toLocaleDateString()}</td>
                <td>{assignment.status}</td>
                <td>
                  <button className="btn btn-sm btn-info me-1">Detay</button>
                  <button className="btn btn-sm btn-warning me-1">Düzenle</button>
                  {/* Silme işlemi için ek bir onay gerekebilir */}
                  {/* <button className="btn btn-sm btn-danger">Sil</button> */}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <AssignmentCreateForm 
        show={showCreateModal} 
        onHide={handleCloseCreateModal} 
        onSuccess={handleAssignmentCreated} 
      />
    </div>
  );
};

export default AssignmentPage; 