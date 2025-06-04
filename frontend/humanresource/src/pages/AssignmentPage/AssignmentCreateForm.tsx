import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

// Tipler (ResourceInfo, EmployeeInfo AssignmentPage.tsx'den alınabilir veya ortak bir yere taşınabilir)
interface ResourceInfo {
  id: number;
  name: string;
  resourceIdentifier?: string; 
}

interface EmployeeInfo {
  id: number;
  firstName: string;
  lastName: string;
}

// CreateAssignmentRequestDto'ya karşılık gelecek bir tip
interface CreateAssignmentPayload {
  resourceId: number | null;
  assignedToEmployeeId: number | null;
  assignmentDate: string;
  expectedReturnDate: string;
  description: string;
  category: string; // AssignmentCategory enum string hali (örn: "COMPUTER")
  notes?: string;
}

// Backend'den gelecek Resource ve Employee listeleri için tipler
// AssignmentPage.tsx'de tanımlananlar kullanılabilir veya buraya özel tanımlanabilir.

const API_BASE_URL = 'http://localhost:9090/api/v1';

interface AssignmentCreateFormProps {
  show: boolean;
  onHide: () => void;
  onSuccess: () => void; // Form başarıyla gönderildiğinde çağrılacak fonksiyon
}

const AssignmentCreateForm: React.FC<AssignmentCreateFormProps> = ({ show, onHide, onSuccess }) => {
  const [formData, setFormData] = useState<CreateAssignmentPayload>({
    resourceId: null,
    assignedToEmployeeId: null,
    assignmentDate: '',
    expectedReturnDate: '',
    description: '',
    category: 'OTHER', // Varsayılan bir kategori
    notes: ''
  });
  const [resources, setResources] = useState<ResourceInfo[]>([]);
  const [employees, setEmployees] = useState<EmployeeInfo[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Kaynakları ve çalışanları çekmek için useEffect
  useEffect(() => {
    if (!show) return; // Modal gösterilmiyorsa API çağrısı yapma

    const fetchData = async (endpoint: string, setter: React.Dispatch<React.SetStateAction<any[]>>) => {
      const token = localStorage.getItem('authToken');
      if (!token) {
        setError('Token bulunamadı.');
        return;
      }
      try {
        const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error(`${endpoint} verileri alınamadı`);
        const data = await response.json();
        setter(data);
      } catch (err) {
        if (err instanceof Error) setError(err.message);
        else setError('Bilinmeyen bir hata oluştu');
        console.error(`Error fetching ${endpoint}:`, err);
      }
    };

    fetchData('resources', setResources);
    fetchData('employees', setEmployees); // TODO: Employee için doğru endpoint'i kontrol et (örn: /employees/all-active veya benzeri)
  }, [show]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    const token = localStorage.getItem('authToken');

    if (!token) {
      setError('Token bulunamadı.');
      setIsLoading(false);
      return;
    }
    if (!formData.resourceId || !formData.assignedToEmployeeId) {
        setError('Kaynak ve Çalışan seçimi zorunludur.');
        setIsLoading(false);
        return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/assignments`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: 'Bilinmeyen bir hata oluştu.'}));
        throw new Error(errorData.message || 'Zimmet oluşturulamadı');
      }
      // Başarılı
      onSuccess(); // Ana listeyi yenilemek ve modalı kapatmak için callback
    } catch (err) {
      if (err instanceof Error) setError(err.message);
      else setError('Zimmet oluşturulurken bilinmeyen bir hata oluştu.');
    } finally {
      setIsLoading(false);
    }
  };

  if (!show) return null; // Modal gösterilmiyorsa hiçbir şey render etme

  return (
    // Basit bir modal yapısı (Bootstrap veya react-bootstrap ile daha iyi hale getirilebilir)
    <div className="modal fade show d-block" tabIndex={-1} style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
      <div className="modal-dialog modal-lg">
        <div className="modal-content">
          <form onSubmit={handleSubmit}>
            <div className="modal-header">
              <h5 className="modal-title">Yeni Zimmet Oluştur</h5>
              <button type="button" className="btn-close" onClick={onHide}></button>
            </div>
            <div className="modal-body">
              {error && <div className="alert alert-danger">{error}</div>}
              
              <div className="mb-3">
                <label htmlFor="resourceId" className="form-label">Kaynak</label>
                <select 
                  id="resourceId" 
                  name="resourceId" 
                  className="form-select" 
                  value={formData.resourceId || ''} 
                  onChange={handleChange}
                  required
                >
                  <option value="" disabled>Kaynak Seçin...</option>
                  {resources.map(r => <option key={r.id} value={r.id}>{r.name} ({r.resourceIdentifier || r.id})</option>)}
                </select>
              </div>

              <div className="mb-3">
                <label htmlFor="assignedToEmployeeId" className="form-label">Zimmetlenecek Çalışan</label>
                <select 
                  id="assignedToEmployeeId" 
                  name="assignedToEmployeeId" 
                  className="form-select" 
                  value={formData.assignedToEmployeeId || ''} 
                  onChange={handleChange}
                  required
                >
                  <option value="" disabled>Çalışan Seçin...</option>
                  {employees.map(e => <option key={e.id} value={e.id}>{e.firstName} {e.lastName}</option>)}
                </select>
              </div>

              <div className="row mb-3">
                <div className="col">
                  <label htmlFor="assignmentDate" className="form-label">Zimmet Tarihi</label>
                  <input 
                    type="date" 
                    id="assignmentDate" 
                    name="assignmentDate" 
                    className="form-control" 
                    value={formData.assignmentDate} 
                    onChange={handleChange} 
                    required 
                  />
                </div>
                <div className="col">
                  <label htmlFor="expectedReturnDate" className="form-label">Beklenen İade Tarihi</label>
                  <input 
                    type="date" 
                    id="expectedReturnDate" 
                    name="expectedReturnDate" 
                    className="form-control" 
                    value={formData.expectedReturnDate} 
                    onChange={handleChange} 
                    required 
                  />
                </div>
              </div>

              <div className="mb-3">
                <label htmlFor="description" className="form-label">Açıklama</label>
                <input 
                    type="text" 
                    id="description" 
                    name="description" 
                    className="form-control" 
                    value={formData.description} 
                    onChange={handleChange} 
                    required 
                />
              </div>

              <div className="mb-3">
                  <label htmlFor="category" className="form-label">Kategori</label>
                  <select 
                      id="category" 
                      name="category" 
                      className="form-select" 
                      value={formData.category} 
                      onChange={handleChange}
                      required
                  >
                      {/* Backend'deki AssignmentCategory enum değerleri buraya gelmeli */}
                      <option value="COMPUTER">Bilgisayar</option>
                      <option value="PHONE">Telefon</option>
                      <option value="OTHER">Diğer</option>
                  </select>
              </div>

              <div className="mb-3">
                <label htmlFor="notes" className="form-label">Notlar</label>
                <textarea 
                    id="notes" 
                    name="notes" 
                    className="form-control" 
                    value={formData.notes || ''} 
                    onChange={handleChange} 
                />
              </div>

            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-secondary" onClick={onHide}>Kapat</button>
              <button type="submit" className="btn btn-primary" disabled={isLoading}>
                {isLoading ? 'Oluşturuluyor...' : 'Zimmeti Oluştur'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AssignmentCreateForm; 