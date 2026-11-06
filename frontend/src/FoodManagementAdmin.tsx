import { FormEvent, useCallback, useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { ImagePlus, Pencil, Plus, Search, Trash2, X } from 'lucide-react';
import './food-admin.css';

type Food = { id: number; name: string; description: string; price: number; category: string; vegetarian: boolean; image: string; tag?: string; available: boolean };
type FoodForm = Omit<Food, 'id'>;
const categories = ['Maharashtrian Food', 'North Indian Food', 'South Indian Food', 'Chinese Food', 'Snacks & Starters', 'Sweets & Desserts', 'Catering Packages', 'Special Thali'];
const emptyForm: FoodForm = { name: '', description: '', price: 0, category: categories[0], vegetarian: true, image: '', tag: '', available: true };
const api = axios.create();
const picture = (id: string) => !id ? '' : id.startsWith('/') || id.startsWith('http') ? id : `https://images.unsplash.com/${id}?auto=format&fit=crop&w=140&q=75`;

export default function FoodManagementAdmin({ token }: { token: string }) {
  const [foods, setFoods] = useState<Food[]>([]);
  const [form, setForm] = useState<FoodForm>(emptyForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [search, setSearch] = useState('');
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState('');
  const [originalImage, setOriginalImage] = useState('');
  const headers = { Authorization: `Bearer ${token}` };
  const loadFoods = useCallback(async () => {
    try { const { data } = await api.get<Food[]>('/api/admin/foods', { headers }); setFoods(data); setError(''); }
    catch { setError('Could not load menu items. Check your admin session and try again.'); }
  }, [token]);
  useEffect(() => { loadFoods(); }, [loadFoods]);
  useEffect(() => {
    if (!selectedFile) { setPreviewUrl(''); return; }
    const objectUrl = URL.createObjectURL(selectedFile);
    setPreviewUrl(objectUrl);
    return () => URL.revokeObjectURL(objectUrl);
  }, [selectedFile]);
  const filtered = useMemo(() => foods.filter(item => `${item.name} ${item.category}`.toLowerCase().includes(search.toLowerCase())), [foods, search]);
  const startNew = () => { setEditingId(null); setForm({ ...emptyForm }); setOriginalImage(''); setSelectedFile(null); setError(''); setFormOpen(true); };
  const startEdit = (item: Food) => {
    setEditingId(item.id);
    setForm({ name: item.name, description: item.description, price: item.price, category: item.category, vegetarian: item.vegetarian, image: item.image, tag: item.tag ?? '', available: item.available });
    setOriginalImage(item.image); setSelectedFile(null); setError(''); setFormOpen(true);
  };
  const closeForm = () => { setFormOpen(false); setEditingId(null); setSelectedFile(null); setOriginalImage(''); setError(''); };
  const save = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault(); setSaving(true); setError('');
    let uploadedPath = '';
    let image = form.image.trim();
    try {
      if (selectedFile) {
        const fileData = new FormData(); fileData.append('file', selectedFile);
        const uploaded = await api.post<{ url: string }>('/api/admin/foods/images', fileData, { headers });
        image = uploaded.data.url; uploadedPath = image;
      }
      const payload = { ...form, image, price: Number(form.price), tag: form.tag?.trim() || null };
      if (editingId === null) await api.post('/api/admin/foods', payload, { headers });
      else await api.put(`/api/admin/foods/${editingId}`, payload, { headers });
      if (originalImage.startsWith('/uploads/') && originalImage !== image) await api.delete('/api/admin/foods/images', { headers, params: { path: originalImage } }).catch(() => {});
      closeForm(); await loadFoods();
    } catch (err) {
      if (uploadedPath) await api.delete('/api/admin/foods/images', { headers, params: { path: uploadedPath } }).catch(() => {});
      setError(axios.isAxiosError(err) ? err.response?.data?.detail ?? err.response?.data?.message ?? 'Could not save this food item.' : 'Could not save this food item.');
    } finally { setSaving(false); }
  };
  const removePhoto = () => { setForm(current => ({ ...current, image: '' })); setSelectedFile(null); };
  const imagePreview = previewUrl || picture(form.image);
  const toggleAvailability = async (item: Food) => {
    try {
      await api.put(`/api/admin/foods/${item.id}`, { ...item, available: !item.available }, { headers });
      await loadFoods();
    } catch { setError(`Could not update availability for ${item.name}.`); }
  };
  const remove = async (item: Food) => {
    if (!window.confirm(`Delete “${item.name}”? Items already used in orders cannot be deleted; disable availability instead.`)) return;
    try { await api.delete(`/api/admin/foods/${item.id}`, { headers }); await loadFoods(); }
    catch (err) { setError(axios.isAxiosError(err) ? err.response?.data?.detail ?? err.response?.data?.message ?? 'Could not delete this item.' : 'Could not delete this item.'); }
  };

  return <section className="admin-panel food-admin-panel">
    <div className="panel-heading food-admin-heading"><div><h2>Menu items</h2><p>{foods.length} items · Changes appear on the customer website</p></div><button className="food-add-button" onClick={startNew}><Plus size={15} /> Add food item</button></div>
    <div className="food-admin-tools"><label className="admin-search"><Search size={15} /><input value={search} onChange={event => setSearch(event.target.value)} placeholder="Search food or category" /></label><span>{filtered.length} shown</span></div>
    {error && <div className="admin-error inline-error">{error}<button onClick={() => setError('')}>×</button></div>}
    {formOpen && <form className="food-edit-form" onSubmit={save}>
      <div className="food-edit-title"><div><span>{editingId === null ? 'NEW MENU ITEM' : `EDITING ITEM #${editingId}`}</span><h3>{editingId === null ? 'Add something delicious.' : 'Update food details.'}</h3></div><button type="button" className="icon-button" onClick={closeForm} aria-label="Close form"><X size={18} /></button></div>
      <div className="food-edit-grid">
        <label>Food name<input required maxLength={120} value={form.name} onChange={event => setForm({ ...form, name: event.target.value })} placeholder="e.g. Misal Pav" /></label>
        <label>Category<select required value={form.category} onChange={event => setForm({ ...form, category: event.target.value })}>{[...new Set([...categories, ...(form.category && !categories.includes(form.category) ? [form.category] : [])])].map(category => <option key={category}>{category}</option>)}</select></label>
        <label>Price (₹)<input required type="number" min="0.01" step="0.01" value={form.price || ''} onChange={event => setForm({ ...form, price: Number(event.target.value) })} placeholder="250" /></label>
        <label>Food type<select value={form.vegetarian ? 'veg' : 'nonveg'} onChange={event => setForm({ ...form, vegetarian: event.target.value === 'veg' })}><option value="veg">Vegetarian</option><option value="nonveg">Non-vegetarian</option></select></label>
        <label className="food-form-wide">Description<input required maxLength={600} value={form.description} onChange={event => setForm({ ...form, description: event.target.value })} placeholder="A short, appetizing description" /></label>
        <div className="food-form-wide food-photo-field"><label htmlFor="food-photo-url">Food photo <small>Upload a JPG, PNG or WebP (max 8 MB), or enter an Unsplash photo ID.</small></label><div className="food-image-input"><ImagePlus size={16} /><input id="food-photo-url" maxLength={500} value={form.image} onChange={event => setForm({ ...form, image: event.target.value })} placeholder="photo-... or uploaded image path" /><label className="food-upload-button">Choose photo<input type="file" accept="image/jpeg,image/png,image/webp" onChange={event => setSelectedFile(event.target.files?.[0] ?? null)} /></label></div>{(imagePreview || selectedFile) && <div className="food-image-preview"><img src={imagePreview} alt="Selected food photo preview" /><span>{selectedFile?.name ?? 'Current food photo'}</span><button type="button" className="food-remove-photo" onClick={removePhoto}><Trash2 size={13} /> Remove photo</button></div>}</div>
        <label>Card label <small>Optional, e.g. Bestseller</small><input maxLength={60} value={form.tag ?? ''} onChange={event => setForm({ ...form, tag: event.target.value })} placeholder="Optional" /></label>
        <label className="food-availability-check"><input type="checkbox" checked={form.available} onChange={event => setForm({ ...form, available: event.target.checked })} /> Available for ordering</label>
      </div>
      <div className="food-form-actions"><button type="button" className="food-cancel-button" onClick={closeForm}>Cancel</button><button type="submit" className="food-add-button" disabled={saving}>{saving ? 'Saving…' : editingId === null ? 'Add food item' : 'Save changes'}</button></div>
    </form>}
    <div className="admin-table-scroll"><table className="admin-table food-admin-table"><thead><tr><th>ITEM</th><th>CATEGORY</th><th>PRICE</th><th>DIET</th><th>AVAILABILITY</th><th>ACTIONS</th></tr></thead><tbody>{filtered.map(item => <tr key={item.id}><td className="admin-food-cell">{picture(item.image) ? <img src={picture(item.image)} alt={item.name} /> : <span className="food-image-empty"><ImagePlus size={15} /></span>}<span>{item.name}<small>{item.description}</small></span></td><td>{item.category}</td><td>₹{Number(item.price).toLocaleString('en-IN')}</td><td>{item.vegetarian ? 'Vegetarian' : 'Non-veg'}</td><td><button className={item.available ? 'status-pill status-completed food-availability' : 'status-pill status-cancelled food-availability'} onClick={() => toggleAvailability(item)}>{item.available ? 'Available' : 'Unavailable'}</button></td><td><div className="food-row-actions"><button aria-label={`Edit ${item.name}`} onClick={() => startEdit(item)}><Pencil size={14} /> Edit</button><button className="food-delete-button" aria-label={`Delete ${item.name}`} onClick={() => remove(item)}><Trash2 size={14} /> Delete</button></div></td></tr>)}</tbody></table></div>
    {!filtered.length && <div className="admin-empty">No menu items match this search.</div>}
  </section>;
}
