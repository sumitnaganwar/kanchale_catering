import { useCallback, useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { ImagePlus, Pencil, Plus, Trash2, X } from 'lucide-react';
import './thali-admin.css';

type Food = { id: number; name: string; available: boolean };
type Thali = { id: number; name: string; description: string; pricePerPlate: number; image: string; minimumPlates: number; available: boolean; foods: { id: number; name: string }[] };
type ThaliForm = { name: string; description: string; pricePerPlate: string; image: string; foodIds: number[]; available: boolean };
const emptyForm: ThaliForm = { name: '', description: '', pricePerPlate: '', image: '', foodIds: [], available: true };
const api = axios.create();
const imageUrl = (image: string) => !image ? '' : image.startsWith('/') || image.startsWith('http') ? image : `https://images.unsplash.com/${image}?auto=format&fit=crop&w=700&q=80`;

export default function ThaliManagementAdmin({ token }: { token: string }) {
  const [thalis, setThalis] = useState<Thali[]>([]);
  const [foods, setFoods] = useState<Food[]>([]);
  const [form, setForm] = useState<ThaliForm>(emptyForm);
  const [editing, setEditing] = useState<Thali | null>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [preview, setPreview] = useState('');
  const [open, setOpen] = useState(false);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const headers = { Authorization: `Bearer ${token}` };
  const load = useCallback(async () => {
    try { const [thaliResult, foodResult] = await Promise.all([api.get<Thali[]>('/api/admin/thalis', { headers }), api.get<Food[]>('/api/admin/foods', { headers })]); setThalis(thaliResult.data); setFoods(foodResult.data); setError(''); }
    catch { setError('Could not load thalis or menu foods. Check your connection and try again.'); }
  }, [token]);
  useEffect(() => { load(); }, [load]);
  useEffect(() => {
    if (!selectedFile) { setPreview(''); return; }
    const url = URL.createObjectURL(selectedFile); setPreview(url); return () => URL.revokeObjectURL(url);
  }, [selectedFile]);
  const availableFoods = useMemo(() => foods.filter(food => food.available), [foods]);
  const beginCreate = () => { setEditing(null); setForm({ ...emptyForm }); setSelectedFile(null); setError(''); setOpen(true); };
  const beginEdit = (thali: Thali) => { setEditing(thali); setForm({ name: thali.name, description: thali.description, pricePerPlate: String(thali.pricePerPlate), image: thali.image, foodIds: thali.foods.map(food => food.id), available: thali.available }); setSelectedFile(null); setError(''); setOpen(true); };
  const closeForm = () => { setOpen(false); setEditing(null); setSelectedFile(null); setError(''); };
  const toggleFood = (id: number) => setForm(current => ({ ...current, foodIds: current.foodIds.includes(id) ? current.foodIds.filter(foodId => foodId !== id) : [...current.foodIds, id] }));
  const save = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!form.foodIds.length) { setError('Select at least one food item for this thali.'); return; }
    setSaving(true); setError(''); let uploadedPath = '';
    try {
      let image = form.image.trim();
      if (selectedFile) { const body = new FormData(); body.append('file', selectedFile); const result = await api.post<{ url: string }>('/api/admin/foods/images', body, { headers }); image = result.data.url; uploadedPath = image; }
      const payload = { ...form, pricePerPlate: Number(form.pricePerPlate), image };
      if (editing) await api.put(`/api/admin/thalis/${editing.id}`, payload, { headers });
      else await api.post('/api/admin/thalis', payload, { headers });
      if (editing?.image.startsWith('/uploads/') && editing.image !== image) await api.delete('/api/admin/foods/images', { headers, params: { path: editing.image } }).catch(() => {});
      closeForm(); await load();
    } catch (err) {
      if (uploadedPath) await api.delete('/api/admin/foods/images', { headers, params: { path: uploadedPath } }).catch(() => {});
      setError(axios.isAxiosError(err) ? err.response?.data?.detail ?? err.response?.data?.message ?? 'Could not save this thali.' : 'Could not save this thali.');
    } finally { setSaving(false); }
  };
  const remove = async (thali: Thali) => {
    if (!window.confirm(`Delete the ${thali.name} thali?`)) return;
    try { await api.delete(`/api/admin/thalis/${thali.id}`, { headers }); if (thali.image.startsWith('/uploads/')) await api.delete('/api/admin/foods/images', { headers, params: { path: thali.image } }).catch(() => {}); await load(); }
    catch { setError('Could not delete this thali.'); }
  };

  return <section className="admin-panel thali-admin-panel">
    <div className="panel-heading"><div><h2>Thali menu</h2><p>Create a complete plate with its included foods and one per-plate price. Bulk booking minimum: 200 plates.</p></div><button className="food-add-button" onClick={beginCreate}><Plus size={15} /> Create thali</button></div>
    {error && <div className="admin-error inline-error">{error}<button onClick={() => setError('')}>×</button></div>}
    {open && <form className="thali-edit-form" onSubmit={save}>
      <div className="food-edit-title"><div><span>{editing ? `EDIT THALI #${editing.id}` : 'NEW THALI'}</span><h3>{editing ? 'Update thali menu' : 'Create a complete thali'}</h3></div><button type="button" className="icon-button" aria-label="Close form" onClick={closeForm}><X size={18} /></button></div>
      <div className="thali-form-grid"><label>Thali name<input required maxLength={120} value={form.name} onChange={event => setForm({ ...form, name: event.target.value })} placeholder="e.g. Maharashtrian Special Thali" /></label><label>Price per plate (₹)<input required type="number" min="0.01" step="0.01" value={form.pricePerPlate} onChange={event => setForm({ ...form, pricePerPlate: event.target.value })} placeholder="250" /></label><label className="thali-wide">Description<input required maxLength={800} value={form.description} onChange={event => setForm({ ...form, description: event.target.value })} placeholder="Describe this thali" /></label><div className="thali-wide"><label className="thali-photo-label">Thali photo <small>Choose a JPG, PNG, WebP photo, or enter an Unsplash photo ID.</small></label><div className="food-image-input"><ImagePlus size={16} /><input value={form.image} onChange={event => setForm({ ...form, image: event.target.value })} placeholder="photo-... or uploaded image path" /><label className="food-upload-button">Choose photo<input type="file" accept="image/jpeg,image/png,image/webp" onChange={event => setSelectedFile(event.target.files?.[0] ?? null)} /></label></div>{(preview || imageUrl(form.image)) && <div className="food-image-preview"><img src={preview || imageUrl(form.image)} alt="Thali preview" /><span>{selectedFile?.name ?? 'Current thali photo'}</span><button type="button" className="food-remove-photo" onClick={() => { setForm({ ...form, image: '' }); setSelectedFile(null); }}><Trash2 size={13} /> Remove</button></div>}</div>
        <div className="thali-food-picker thali-wide"><div><strong>Foods included in this thali</strong><small>Select the foods customers receive in one plate.</small></div>{availableFoods.length ? <div className="thali-food-options">{availableFoods.map(food => <label key={food.id}><input type="checkbox" checked={form.foodIds.includes(food.id)} onChange={() => toggleFood(food.id)} /><span>{food.name}</span></label>)}</div> : <p className="thali-no-foods">Add available foods from Food & menu first.</p>}</div>
        <label className="thali-available"><input type="checkbox" checked={form.available} onChange={event => setForm({ ...form, available: event.target.checked })} /> Show this thali to customers</label>
      </div><div className="food-form-actions"><button type="button" className="food-cancel-button" onClick={closeForm}>Cancel</button><button type="submit" className="food-add-button" disabled={saving}>{saving ? 'Saving…' : editing ? 'Save thali' : 'Create thali'}</button></div>
    </form>}
    {!thalis.length ? <div className="admin-empty">No thalis yet. Create one by choosing foods from your menu.</div> : <div className="thali-admin-grid">{thalis.map(thali => <article className="thali-admin-card" key={thali.id}><div className="thali-admin-image">{imageUrl(thali.image) ? <img src={imageUrl(thali.image)} alt={thali.name} /> : <div className="thali-no-image"><ImagePlus /></div>}<span className={thali.available ? 'status-pill status-completed' : 'status-pill status-cancelled'}>{thali.available ? 'Available' : 'Hidden'}</span></div><div className="thali-admin-copy"><div className="thali-admin-name-price"><h3>{thali.name}</h3><strong>₹{Number(thali.pricePerPlate).toLocaleString('en-IN')}<small> / plate</small></strong></div><p>{thali.description}</p><div className="thali-food-chip-list">{thali.foods.map(food => <span key={food.id}>{food.name}</span>)}</div><div className="thali-admin-minimum">Minimum bulk booking: {thali.minimumPlates} plates</div><div className="food-row-actions"><button onClick={() => beginEdit(thali)}><Pencil size={14} /> Edit thali</button><button className="food-delete-button" onClick={() => remove(thali)}><Trash2 size={14} /> Delete</button></div></div></article>)}</div>}
  </section>;
}
