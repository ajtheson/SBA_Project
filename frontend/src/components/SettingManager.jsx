import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import './SettingManager.css';

/**
 * Generic component for managing system settings (Roles, Categories, etc.)
 */
const SettingManager = ({ title, service }) => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({ name: '', status: true });

  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      setLoading(true);
      const response = await service.getAll();
      setItems(response.data.data || []);
    } catch (error) {
      toast.error('Failed to fetch items');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await service.update(editingItem.id, formData);
        toast.success('Updated successfully');
      } else {
        await service.create(formData);
        toast.success('Created successfully');
      }
      closeModal();
      fetchItems();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Operation failed');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this item?')) return;
    
    try {
      await service.delete(id);
      toast.success('Deleted successfully');
      fetchItems();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Delete failed');
    }
  };

  const handleToggleStatus = async (id) => {
    try {
      await service.toggleStatus(id);
      toast.success('Status updated');
      fetchItems();
    } catch (error) {
      toast.error('Failed to update status');
    }
  };

  const openModal = (item = null) => {
    setEditingItem(item);
    setFormData(item ? { name: item.name, status: item.status } : { name: '', status: true });
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingItem(null);
    setFormData({ name: '', status: true });
  };

  return (
    <div className="setting-manager">
      <div className="header">
        <h2>{title}</h2>
        <button className="btn btn-primary" onClick={() => openModal()}>
          Add New
        </button>
      </div>

      {loading ? (
        <div className="loading">Loading...</div>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {items.length === 0 ? (
              <tr>
                <td colSpan="4" className="text-center">No items found</td>
              </tr>
            ) : (
              items.map((item) => (
                <tr key={item.id}>
                  <td>{item.id}</td>
                  <td>{item.name}</td>
                  <td>
                    <span className={`badge ${item.status ? 'badge-active' : 'badge-inactive'}`}>
                      {item.status ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td>
                    <button className="btn btn-sm btn-info" onClick={() => openModal(item)}>
                      Edit
                    </button>
                    <button 
                      className="btn btn-sm btn-warning" 
                      onClick={() => handleToggleStatus(item.id)}
                    >
                      Toggle
                    </button>
                    <button 
                      className="btn btn-sm btn-danger" 
                      onClick={() => handleDelete(item.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>{editingItem ? 'Edit' : 'Add'} {title}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Name *</label>
                <input
                  type="text"
                  className="form-control"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>
                  <input
                    type="checkbox"
                    checked={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.checked })}
                  />
                  {' '}Active
                </label>
              </div>
              <div className="form-actions">
                <button type="submit" className="btn btn-primary">
                  {editingItem ? 'Update' : 'Create'}
                </button>
                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default SettingManager;
