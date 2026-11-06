import { useState } from 'react';
import axios from 'axios';
import { CalendarDays, ChefHat, LogOut, Package } from 'lucide-react';
import CateringBookingsAdmin from './CateringBookingsAdmin';
import FoodManagementAdmin from './FoodManagementAdmin';
import ThaliManagementAdmin from './ThaliManagementAdmin';
import './admin.css';

type AdminTab = 'catering' | 'food' | 'thali';
const api = axios.create();

export default function AdminApp() {
  const [token, setToken] = useState(() => localStorage.getItem('kc-admin-token') ?? '');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [tab, setTab] = useState<AdminTab>('catering');
  const [loading, setLoading] = useState(false);

  const login = async (event: React.FormEvent) => {
    event.preventDefault(); setLoading(true); setError('');
    try {
      const { data } = await api.post('/api/auth/login', { email, password });
      localStorage.setItem('kc-admin-token', data.token); setToken(data.token);
    } catch { setError('Sign in failed. Check your email and password, then try again.'); }
    finally { setLoading(false); }
  };
  const signOut = () => { localStorage.removeItem('kc-admin-token'); setToken(''); setTab('catering'); };

  if (!token) return <div className="admin-login"><div className="admin-login-card"><a href="/" className="brand"><span className="brand-mark"><ChefHat size={24} /></span><span className="brand-copy"><strong>Kanchale</strong><small>CATERING · PUNE</small></span></a><p className="eyebrow eyebrow-dark">Staff access</p><h1>Welcome <em>back.</em></h1><p className="login-subtitle">Sign in to manage catering bookings and your menu.</p>{error && <div className="admin-error">{error}</div>}<form onSubmit={login}><label>Email address<input type="email" required value={email} onChange={event => setEmail(event.target.value)} placeholder="admin@example.com" /></label><label>Password<input type="password" required value={password} onChange={event => setPassword(event.target.value)} placeholder="Your password" /></label><button className="button button-primary" disabled={loading}>{loading ? 'Signing in…' : 'Sign in to dashboard'}</button></form><a className="admin-back" href="/">← Back to the website</a></div></div>;

  return <div className="admin-layout"><aside className="admin-sidebar"><a href="/" className="brand admin-brand"><span className="brand-mark"><ChefHat size={22} /></span><span className="brand-copy"><strong>Kanchale</strong><small>ADMIN STUDIO</small></span></a><div className="admin-nav-label">CATERING MANAGEMENT</div><button className={tab === 'catering' ? 'admin-nav active' : 'admin-nav'} onClick={() => setTab('catering')}><CalendarDays size={17} />Catering bookings</button><button className={tab === 'food' ? 'admin-nav active' : 'admin-nav'} onClick={() => setTab('food')}><Package size={17} />Food & menu</button><button className={tab === 'thali' ? 'admin-nav active' : 'admin-nav'} onClick={() => setTab('thali')}><Package size={17} />Thali menu</button><div className="sidebar-bottom"><a href="/" className="admin-site-link">View customer website ↗</a><button onClick={signOut} className="admin-nav"><LogOut size={17} />Sign out</button></div></aside><main className="admin-main"><header className="admin-topbar"><div><span className="admin-date">KANCHale CATERING · PUNE</span><h1>{tab === 'catering' ? 'Catering bookings' : tab === 'food' ? 'Food & menu' : 'Thali menu'}</h1></div><div className="admin-user"><span className="online-dot" />{tab === 'catering' ? 'Booking enquiries' : tab === 'food' ? 'Menu management' : 'Thali management'}<span className="admin-initial">K</span></div></header>{tab === 'catering' ? <CateringBookingsAdmin token={token} /> : tab === 'food' ? <FoodManagementAdmin token={token} /> : <ThaliManagementAdmin token={token} />}</main></div>;
}
