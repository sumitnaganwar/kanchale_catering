import { useEffect, useState } from 'react';
import axios from 'axios';
import { ArrowRight, Check, ChefHat, MapPin, Users, X } from 'lucide-react';
import './catering.css';
import './thali-public.css';

type PublicThali = { id: number; name: string; description: string; pricePerPlate: number; image: string; minimumPlates: number; foods: { id: number; name: string; description: string; vegetarian: boolean }[] };
type CateringForm = {
  customerName: string; mobileNumber: string; emailAddress: string; eventType: string;
  eventDate: string; eventLocation: string; numberOfPlates: string; foodPreference: string;
  cateringPackage: string; budgetPerPlate: string; additionalRequirements: string;
};
const emptyForm: CateringForm = {
  customerName: '', mobileNumber: '', emailAddress: '', eventType: 'Birthday Catering', eventDate: '',
  eventLocation: '', numberOfPlates: '200', foodPreference: 'Veg', cateringPackage: '', budgetPerPlate: '', additionalRequirements: '',
};
const serviceCards = [
  { name: 'Birthday Catering', description: 'Bring everyone together over the dishes they love most.', image: 'photo-1464366400600-7168b8af9bc3' },
  { name: 'Corporate Catering', description: 'A polished, punctual spread for meetings, teams and company milestones.', image: 'photo-1517248135467-4c7edcad34c4' },
  { name: 'Family Functions', description: 'Comforting favourites, served generously for your favourite people.', image: 'photo-1547592180-85f173990554' },
  { name: 'Engagement Ceremony', description: 'Celebrate your new beginning with a beautiful table and warm hospitality.', image: 'photo-1519741497674-611481863552' },
  { name: 'Private Parties', description: 'Easy, generous catering for anniversaries, reunions and special get-togethers.', image: 'photo-1530103862676-de8c9debad1d' },
  { name: 'Festival Catering', description: 'Make festive gatherings extra special with a feast made for sharing.', image: 'photo-1601050690597-df0568f70950' },
];
const packages = ['Classic Celebration', 'Family Feast', 'Signature Maharashtrian', 'Maharashtrian Thali (200+ Plates)', 'Corporate Table'];
const photo = (id: string, width = 800) => `https://images.unsplash.com/${id}?auto=format&fit=crop&w=${width}&q=85`;
const thaliPhoto = (image: string) => !image ? photo('photo-1546833999-b9f581a1996d') : image.startsWith('/') || image.startsWith('http') ? image : photo(image);
const tomorrow = () => { const date = new Date(); date.setDate(date.getDate() + 1); return date.toISOString().slice(0, 10); };

export default function CateringSection() {
  const [open, setOpen] = useState(false);
  const [thalis, setThalis] = useState<PublicThali[]>([]);
  const [expandedThali, setExpandedThali] = useState<number | null>(null);
  const [form, setForm] = useState<CateringForm>(emptyForm);
  const [error, setError] = useState('');
  const [sending, setSending] = useState(false);
  const [bookingId, setBookingId] = useState<number | string | null>(null);
  useEffect(() => { axios.get<PublicThali[]>('/api/thalis').then(({ data }) => setThalis(Array.isArray(data) ? data : [])).catch(() => setThalis([])); }, []);
  const openForm = (eventType = 'Birthday Catering', thali?: PublicThali) => { setForm({ ...emptyForm, eventType, cateringPackage: thali?.name ?? '', budgetPerPlate: thali ? String(thali.pricePerPlate) : '' }); setError(''); setBookingId(null); setOpen(true); };
  const close = () => { setOpen(false); setForm(emptyForm); setBookingId(null); setError(''); };
  const submit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (Number(form.numberOfPlates) < 200) { setError('Bulk catering bookings require at least 200 plates.'); return; }
    setSending(true); setError('');
    try {
      const { data } = await axios.post('/api/catering-bookings', { ...form, numberOfPlates: Number(form.numberOfPlates), budgetPerPlate: Number(form.budgetPerPlate) });
      setBookingId(data.id);
    } catch (err) {
      setError(axios.isAxiosError(err) ? (err.response?.data?.detail ?? err.response?.data?.message ?? 'We could not send your enquiry. Please try again.') : 'We could not send your enquiry. Please try again.');
    } finally { setSending(false); }
  };

  return <>
    <section className="bulk-catering" id="bulk-catering">
      <div className="bulk-hero">
        <img src={photo('photo-1547592180-85f173990554', 1800)} alt="An abundant buffet prepared for a special celebration" />
        <div className="bulk-hero-shade" />
        <div className="bulk-hero-content">
          <p className="eyebrow"><span /> Gather beautifully</p>
          <h2>Making Every<br />Celebration <em>Delicious!</em></h2>
          <p>Premium catering for birthdays, corporate parties, family celebrations and special occasions.</p>
          <div className="bulk-promise"><Users size={17} /><strong>Bulk catering from 200 plates</strong><span>Thoughtful menus · Warm service · Pune</span></div>
          <div className="bulk-actions"><button className="button button-primary" onClick={() => openForm()}>Book Catering Service <ArrowRight size={17} /></button><a className="button button-glass" href="#catering-packages">Explore Catering Packages</a></div>
        </div>
        <div className="bulk-hero-note"><ChefHat size={16} /> Made with care, served with joy</div>
      </div>
      <div className="bulk-intro section-wrap" id="catering-packages"><div><p className="eyebrow eyebrow-dark">Gather around our table</p><h2>There’s a feast<br />for <em>every occasion.</em></h2></div><p>From the first welcome to the last sweet, we make every plate feel personal. Choose your celebration and tell us what you’re planning — our team will take it from there. Every thali is offered for a minimum bulk booking of 200 plates.</p></div>
      {thalis.length > 0 && <section className="public-thalis section-wrap" id="thalis"><div className="public-thali-heading"><div><p className="eyebrow eyebrow-dark">A whole meal, in one plate</p><h3>Our <em>thalis.</em></h3></div><span>One complete plate · One per-plate price · 200 plate minimum</span></div><div className="public-thali-grid">{thalis.map(thali => <article className={expandedThali === thali.id ? 'public-thali-card expanded' : 'public-thali-card'} key={thali.id}><button className="public-thali-toggle" onClick={() => setExpandedThali(current => current === thali.id ? null : thali.id)} aria-expanded={expandedThali === thali.id}><img src={thaliPhoto(thali.image)} alt={thali.name} loading="lazy" /><span className="public-thali-card-title"><strong>{thali.name}</strong><small>₹{Number(thali.pricePerPlate).toLocaleString('en-IN')} per plate · min. {thali.minimumPlates} plates</small></span><span className="thali-expand-icon">{expandedThali === thali.id ? '−' : '+'}</span></button>{expandedThali === thali.id && <div className="public-thali-details"><p>{thali.description}</p><small>THIS PLATE INCLUDES</small><ul>{thali.foods.map(food => <li key={food.id}>{food.name}</li>)}</ul><button className="button button-primary" onClick={() => openForm('Family Functions', thali)}>Book this thali for 200+ plates <ArrowRight size={15} /></button></div>}</article>)}</div></section>}
      <div className="catering-category-grid section-wrap">{serviceCards.map((card, index) => <article className="catering-category-card" key={card.name}><div className="catering-category-image"><img src={photo(card.image, 700)} alt={`${card.name} buffet and celebration`} loading="lazy" /><span>0{index + 1}</span></div><div className="catering-category-copy"><h3>{card.name}</h3><p>{card.description}</p><button onClick={() => openForm(card.name)}>Enquire for this event <ArrowRight size={15} /></button></div></article>)}</div>
      <div className="bulk-footnote"><span><MapPin size={15} /> Serving Aundh, Pune and surrounding areas</span><span>Minimum event booking: 200 plates</span></div>
    </section>

    {open && <div className="catering-modal-backdrop" onClick={close}><section className="catering-modal" role="dialog" aria-modal="true" aria-labelledby="catering-form-title" onClick={event => event.stopPropagation()}>
      <header className="catering-modal-heading"><div><p className="eyebrow eyebrow-dark">Let’s plan a lovely day</p><h2 id="catering-form-title">Your celebration,<br /><em>beautifully catered.</em></h2><p>Share a few details and our team will get in touch to plan your menu.</p></div><button className="icon-button" onClick={close} aria-label="Close booking form"><X /></button></header>
      {bookingId !== null ? <div className="catering-success"><span><Check size={27} /></span><p className="eyebrow eyebrow-dark">Enquiry received</p><h3>Let’s make it <em>delicious.</em></h3><p>Thank you, {form.customerName}. Our catering team will contact you soon.</p><div className="catering-ref">BOOKING REFERENCE <strong>KC-CAT-{bookingId}</strong></div><button className="button button-primary" onClick={close}>Done <ArrowRight size={16} /></button></div> : <form className="catering-form" onSubmit={submit}>
        <div className="catering-form-grid">
          <label>Customer name<input required maxLength={120} value={form.customerName} onChange={event => setForm({ ...form, customerName: event.target.value })} placeholder="Your full name" /></label>
          <label>Mobile number<input required type="tel" pattern="[+]?[0-9\s-]{10,15}" value={form.mobileNumber} onChange={event => setForm({ ...form, mobileNumber: event.target.value })} placeholder="10-digit mobile number" /></label>
          <label>Email address<input required type="email" maxLength={180} value={form.emailAddress} onChange={event => setForm({ ...form, emailAddress: event.target.value })} placeholder="you@example.com" /></label>
          <label>Event type<select required value={form.eventType} onChange={event => setForm({ ...form, eventType: event.target.value })}>{serviceCards.map(card => <option key={card.name}>{card.name}</option>)}</select></label>
          <label>Event date<input required type="date" min={tomorrow()} value={form.eventDate} onChange={event => setForm({ ...form, eventDate: event.target.value })} /></label>
          <label>Number of plates<input required type="number" min="200" step="1" value={form.numberOfPlates} onChange={event => setForm({ ...form, numberOfPlates: event.target.value })} /><small>Minimum booking is 200 plates.</small></label>
          <label className="catering-form-wide">Event location<input required maxLength={400} value={form.eventLocation} onChange={event => setForm({ ...form, eventLocation: event.target.value })} placeholder="Venue name, area and city" /></label>
          <label>Food preference<select required value={form.foodPreference} onChange={event => setForm({ ...form, foodPreference: event.target.value })}><option>Veg</option><option>Non-Veg</option><option>Both</option></select></label>
          <label>Catering package<select required value={form.cateringPackage} onChange={event => setForm({ ...form, cateringPackage: event.target.value })}><option value="">Choose a package</option>{packages.map(item => <option key={item}>{item}</option>)}{thalis.map(thali => <option key={`thali-${thali.id}`}>{thali.name}</option>)}</select></label>
          <label>Budget per plate (₹)<input required type="number" min="1" step="1" value={form.budgetPerPlate} onChange={event => setForm({ ...form, budgetPerPlate: event.target.value })} placeholder="Your budget per plate" /></label>
          <label className="catering-form-wide">Additional requirements<textarea rows={3} maxLength={2000} value={form.additionalRequirements} onChange={event => setForm({ ...form, additionalRequirements: event.target.value })} placeholder="Menu preferences, dietary needs, service details…" /></label>
        </div>
        {error && <p className="catering-form-error" role="alert">{error}</p>}
        <div className="catering-form-submit"><p>We’ll use your details only to follow up about this event.</p><button className="button button-primary" type="submit" disabled={sending}>{sending ? 'Sending enquiry…' : 'Send Catering Enquiry'} <ArrowRight size={16} /></button></div>
      </form>}
    </section></div>}
  </>;
}
