import { useCallback, useEffect, useState } from 'react';
import axios from 'axios';
import { CalendarDays, MapPin, MessageCircle, RefreshCw, Users } from 'lucide-react';
import './catering-admin.css';

type Booking = {
  id: number; customerName: string; mobileNumber: string; emailAddress: string; eventType: string;
  eventDate: string; eventLocation: string; numberOfPlates: number; foodPreference: string;
  cateringPackage: string; budgetPerPlate: number; additionalRequirements?: string;
  status: string; quotationAmount?: number | null; createdAt: string;
};
const bookingStatuses = [
  ['PENDING', 'Pending'], ['CONTACTED', 'Contacted'], ['QUOTATION_SENT', 'Quotation Sent'],
  ['CONFIRMED', 'Confirmed'], ['COMPLETED', 'Completed'], ['REJECTED', 'Rejected'],
];
const api = axios.create();

export default function CateringBookingsAdmin({ token }: { token: string }) {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [error, setError] = useState('');
  const [quotes, setQuotes] = useState<Record<number, string>>({});
  const [saving, setSaving] = useState<number | null>(null);
  const headers = { Authorization: `Bearer ${token}` };
  const refresh = useCallback(async () => {
    try { const { data } = await api.get<Booking[]>('/api/admin/catering-bookings', { headers }); setBookings(data); setError(''); }
    catch { setError('Could not load catering enquiries. Check your admin session and try refreshing.'); }
  }, [token]);
  useEffect(() => { refresh(); const timer = window.setInterval(refresh, 15000); return () => window.clearInterval(timer); }, [refresh]);
  const updateStatus = async (id: number, status: string) => {
    setSaving(id); setError('');
    try { const { data } = await api.patch<Booking>(`/api/admin/catering-bookings/${id}/status`, { status }, { headers }); setBookings(current => current.map(item => item.id === id ? data : item)); }
    catch { setError('Could not update this booking status.'); }
    finally { setSaving(null); }
  };
  const saveQuote = async (id: number) => {
    const amount = Number(quotes[id]);
    if (!Number.isFinite(amount) || amount <= 0) { setError('Enter a quotation amount greater than zero.'); return; }
    setSaving(id); setError('');
    try { const { data } = await api.patch<Booking>(`/api/admin/catering-bookings/${id}/quotation`, { quotationAmount: amount }, { headers }); setBookings(current => current.map(item => item.id === id ? data : item)); }
    catch { setError('Could not save the quotation.'); }
    finally { setSaving(null); }
  };
  return <section className="admin-panel catering-admin-panel">
    <div className="panel-heading"><div><h2>Bulk catering enquiries</h2><p>{bookings.length} booking requests · automatically refreshed every 15 seconds</p></div><button className="admin-link" onClick={refresh}><RefreshCw size={14} /> Refresh</button></div>
    {error && <div className="admin-error inline-error">{error}<button onClick={() => setError('')}>×</button></div>}
    {!bookings.length ? <div className="admin-empty">No catering enquiries yet. New requests for 200+ plates will appear here.</div> : <div className="catering-admin-list">{bookings.map(booking => {
      const waPhone = booking.mobileNumber.replace(/\D/g, '').replace(/^0+/, '');
      const whatsappNumber = waPhone.length === 10 ? `91${waPhone}` : waPhone;
      const message = encodeURIComponent(`Hello ${booking.customerName}, we’re following up about your ${booking.eventType} catering enquiry for ${booking.numberOfPlates} plates on ${booking.eventDate}.`);
      return <article className="catering-admin-card" key={booking.id}>
        <div className="catering-admin-card-top"><div><span className="catering-admin-ref">BOOKING #KC-CAT-{booking.id}</span><h3>{booking.eventType}</h3></div><select aria-label={`Booking ${booking.id} status`} className={`status-select status-${booking.status.toLowerCase().replace(/_/g, '-')}`} value={booking.status} disabled={saving === booking.id} onChange={event => updateStatus(booking.id, event.target.value)}>{bookingStatuses.map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select></div>
        <div className="catering-admin-info-grid">
          <div><small>CUSTOMER</small><strong>{booking.customerName}</strong><a href={`tel:${booking.mobileNumber}`}>{booking.mobileNumber}</a><a href={`mailto:${booking.emailAddress}`}>{booking.emailAddress}</a><a className="catering-whatsapp" href={`https://wa.me/${whatsappNumber}?text=${message}`} target="_blank" rel="noreferrer"><MessageCircle size={13} /> WhatsApp customer</a></div>
          <div><small>EVENT DETAILS</small><strong><CalendarDays size={14} /> {booking.eventDate}</strong><span><MapPin size={14} /> {booking.eventLocation}</span><span><Users size={14} /> {booking.numberOfPlates} plates</span></div>
          <div><small>MENU & BUDGET</small><strong>{booking.foodPreference}</strong><span>{booking.cateringPackage}</span><span>Budget: ₹{Number(booking.budgetPerPlate).toLocaleString('en-IN')} / plate</span></div>
          <div className="catering-admin-quote"><small>QUOTATION</small>{booking.quotationAmount != null && <strong className="quotation-current">₹{Number(booking.quotationAmount).toLocaleString('en-IN')}</strong>}<div><input aria-label={`Quotation amount for booking ${booking.id}`} type="number" min="1" step="1" placeholder="Amount in ₹" value={quotes[booking.id] ?? (booking.quotationAmount ?? '')} onChange={event => setQuotes(current => ({ ...current, [booking.id]: event.target.value }))} /><button disabled={saving === booking.id} onClick={() => saveQuote(booking.id)}>{saving === booking.id ? 'Saving…' : 'Save quote'}</button></div></div>
        </div>
        {booking.additionalRequirements && <div className="catering-admin-requirements"><small>ADDITIONAL REQUIREMENTS</small><p>{booking.additionalRequirements}</p></div>}
      </article>;
    })}</div>}
  </section>;
}
