import React, { useMemo, useState } from "react";
import { api } from "./api.js";
import { clearAuth, loadAuth, saveAuth } from "./auth.js";

const emptyAuth = { username: "", password: "", roles: [] };

export default function App() {
  const [auth, setAuth] = useState(() => loadAuth() || null);
  const [message, setMessage] = useState(null);

  const isAdmin = useMemo(() => auth?.roles?.includes("ADMINISTRATOR"), [auth]);

  const handleLogout = () => {
    clearAuth();
    setAuth(null);
  };

  return (
    <div className="app">
      <header className="topbar">
        <div>
          <h1>Bank Service UI</h1>
          <p>Simple React dashboard for users and admins</p>
        </div>
        {auth && (
          <div className="user-chip">
            <div>
              <div className="user-name">{auth.username}</div>
              <div className="user-roles">{auth.roles?.join(", ") || "USER"}</div>
            </div>
            <button className="ghost" onClick={handleLogout}>Log out</button>
          </div>
        )}
      </header>

      <main>
        {!auth && <AuthPanel onAuth={setAuth} setMessage={setMessage} />}
        {auth && (
          <div className="grid">
            <UserPanel auth={auth} setMessage={setMessage} />
            {isAdmin && <AdminPanel auth={auth} setMessage={setMessage} />}
          </div>
        )}
      </main>

      {message && (
        <div className={`toast ${message.type}`} onClick={() => setMessage(null)}>
          <strong>{message.title}</strong>
          <div>{message.body}</div>
          {message.details?.length > 0 && (
            <ul>
              {message.details.map((d) => (
                <li key={d}>{d}</li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}

function AuthPanel({ onAuth, setMessage }) {
  const [login, setLogin] = useState("");
  const [password, setPassword] = useState("");
  const [reg, setReg] = useState({ login: "", password: "", firstName: "", lastName: "" });
  const [loading, setLoading] = useState(false);

  const doLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const auth = { username: login, password };
      const user = await api.login(auth);
      const roles = user.roles || [];
      const stored = { username: login, password, roles };
      saveAuth(stored);
      onAuth(stored);
      setMessage({ type: "success", title: "Logged in", body: `Welcome, ${login}` });
    } catch (err) {
      setMessage({ type: "error", title: "Login failed", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  const doRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.register(reg);
      setMessage({ type: "success", title: "Registered", body: "You can log in now." });
      setReg({ login: "", password: "", firstName: "", lastName: "" });
    } catch (err) {
      setMessage({ type: "error", title: "Registration failed", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="panel auth">
      <div className="panel-body">
        <section>
          <h2>Login</h2>
          <form onSubmit={doLogin} className="form">
            <label>
              Login
              <input value={login} onChange={(e) => setLogin(e.target.value)} required />
            </label>
            <label>
              Password
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            </label>
            <button className="primary" disabled={loading}>Sign in</button>
          </form>
        </section>
        <section>
          <h2>Register</h2>
          <form onSubmit={doRegister} className="form">
            <label>
              Login
              <input value={reg.login} onChange={(e) => setReg({ ...reg, login: e.target.value })} required />
            </label>
            <label>
              Password
              <input type="password" value={reg.password} onChange={(e) => setReg({ ...reg, password: e.target.value })} required />
            </label>
            <label>
              First name
              <input value={reg.firstName} onChange={(e) => setReg({ ...reg, firstName: e.target.value })} />
            </label>
            <label>
              Last name
              <input value={reg.lastName} onChange={(e) => setReg({ ...reg, lastName: e.target.value })} />
            </label>
            <button className="primary" disabled={loading}>Create account</button>
          </form>
        </section>
      </div>
    </div>
  );
}

function UserPanel({ auth, setMessage }) {
  const [cards, setCards] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [transfer, setTransfer] = useState({ fromCardId: "", toCardId: "", amount: "" });
  const [loading, setLoading] = useState(false);

  const loadCards = async () => {
    setLoading(true);
    try {
      const data = await api.getUserCards(auth, page, size);
      setCards(data.content || []);
    } catch (err) {
      setMessage({ type: "error", title: "Failed to load cards", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  const doTransfer = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.transfer(auth, { ...transfer, amount: Number(transfer.amount) });
      setMessage({ type: "success", title: "Transfer complete", body: "Funds moved successfully." });
      setTransfer({ fromCardId: "", toCardId: "", amount: "" });
      await loadCards();
    } catch (err) {
      setMessage({ type: "error", title: "Transfer failed", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  const requestBlock = async (last4) => {
    setLoading(true);
    try {
      await api.requestBlock(auth, last4);
      setMessage({ type: "success", title: "Request sent", body: `Block request for ${last4} sent.` });
      await loadCards();
    } catch (err) {
      setMessage({ type: "error", title: "Request failed", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="panel">
      <div className="panel-header">
        <h2>User dashboard</h2>
        <button className="ghost" onClick={loadCards} disabled={loading}>Refresh</button>
      </div>
      <div className="panel-body">
        <section>
          <h3>My cards</h3>
          <div className="card-list">
            {cards.map((card) => (
              <div className="card-item" key={card.cardNumberLast4}>
                <div>
                  <div className="card-title">**** {card.cardNumberLast4}</div>
                  <div className="card-sub">Status: {card.cardStatus}</div>
                  <div className="card-sub">Balance: {card.balance ?? 0}</div>
                  <div className="card-sub">Expiry: {card.expiryDate}</div>
                </div>
                <button className="secondary" onClick={() => requestBlock(card.cardNumberLast4)} disabled={loading}>
                  Request block
                </button>
              </div>
            ))}
            {cards.length === 0 && <div className="muted">No cards found. Click refresh.</div>}
          </div>
          <div className="pager">
            <button className="ghost" disabled={page === 0} onClick={() => setPage((p) => Math.max(0, p - 1))}>Prev</button>
            <span>Page {page + 1}</span>
            <button className="ghost" onClick={() => setPage((p) => p + 1)}>Next</button>
          </div>
        </section>

        <section>
          <h3>Transfer between my cards</h3>
          <form onSubmit={doTransfer} className="form form-row">
            <label>
              From (last 4)
              <input value={transfer.fromCardId} onChange={(e) => setTransfer({ ...transfer, fromCardId: e.target.value })} required />
            </label>
            <label>
              To (last 4)
              <input value={transfer.toCardId} onChange={(e) => setTransfer({ ...transfer, toCardId: e.target.value })} required />
            </label>
            <label>
              Amount
              <input type="number" step="0.01" value={transfer.amount} onChange={(e) => setTransfer({ ...transfer, amount: e.target.value })} required />
            </label>
            <button className="primary" disabled={loading}>Transfer</button>
          </form>
        </section>
      </div>
    </div>
  );
}

function AdminPanel({ auth, setMessage }) {
  const [cards, setCards] = useState([]);
  const [ownerQuery, setOwnerQuery] = useState("");
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const defaultExpiry = useMemo(() => {
    const now = new Date();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    const year = String((now.getFullYear() + 2) % 100).padStart(2, "0");
    return `${month}/${year}`;
  }, []);
  const [createCard, setCreateCard] = useState({ ownerName: "", expiryDate: defaultExpiry, balance: "0" });
  const [activateCard, setActivateCard] = useState({ ownerName: "", cardNumberLast4: "" });
  const [deleteCard, setDeleteCard] = useState({ ownerName: "", cardNumberLast4: "" });
  const [loading, setLoading] = useState(false);

  const loadAll = async () => {
    setLoading(true);
    try {
      const data = await api.adminGetAllCards(auth, page, size);
      setCards(data.content || []);
    } catch (err) {
      setMessage({ type: "error", title: "Failed to load cards", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  const findByOwner = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await api.adminFindCardsByOwner(auth, ownerQuery, 0, size);
      setCards(data.content || []);
      setPage(0);
    } catch (err) {
      setMessage({ type: "error", title: "Search failed", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  const doCreate = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.adminCreateCard(auth, { ...createCard, balance: Number(createCard.balance) });
      setMessage({ type: "success", title: "Card created", body: `Card issued for ${createCard.ownerName}` });
      setCreateCard({ ownerName: "", expiryDate: defaultExpiry, balance: "0" });
      await loadAll();
    } catch (err) {
      setMessage({ type: "error", title: "Create failed", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  const doActivate = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.adminActivateCard(auth, activateCard);
      setMessage({ type: "success", title: "Card activated", body: `${activateCard.cardNumberLast4} activated` });
      setActivateCard({ ownerName: "", cardNumberLast4: "" });
      await loadAll();
    } catch (err) {
      setMessage({ type: "error", title: "Activate failed", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  const doDelete = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.adminDeleteCard(auth, deleteCard.ownerName, deleteCard.cardNumberLast4);
      setMessage({ type: "success", title: "Card deleted", body: `${deleteCard.cardNumberLast4} removed` });
      setDeleteCard({ ownerName: "", cardNumberLast4: "" });
      await loadAll();
    } catch (err) {
      setMessage({ type: "error", title: "Delete failed", body: err.message, details: err.details });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="panel">
      <div className="panel-header">
        <h2>Admin dashboard</h2>
        <button className="ghost" onClick={loadAll} disabled={loading}>Refresh</button>
      </div>
      <div className="panel-body">
        <section>
          <h3>Cards</h3>
          <form onSubmit={findByOwner} className="form form-row">
            <label>
              Owner
              <input value={ownerQuery} onChange={(e) => setOwnerQuery(e.target.value)} placeholder="Owner name" />
            </label>
            <button className="secondary" disabled={loading}>Find</button>
          </form>
          <div className="card-list">
            {cards.map((card) => (
              <div className="card-item" key={`${card.ownerName}-${card.cardNumberLast4}`}>
                <div>
                  <div className="card-title">**** {card.cardNumberLast4}</div>
                  <div className="card-sub">Owner: {card.ownerName}</div>
                  <div className="card-sub">Status: {card.cardStatus}</div>
                  <div className="card-sub">Balance: {card.balance ?? 0}</div>
                </div>
              </div>
            ))}
            {cards.length === 0 && <div className="muted">No cards found. Click refresh.</div>}
          </div>
          <div className="pager">
            <button className="ghost" disabled={page === 0} onClick={() => setPage((p) => Math.max(0, p - 1))}>Prev</button>
            <span>Page {page + 1}</span>
            <button className="ghost" onClick={() => setPage((p) => p + 1)}>Next</button>
          </div>
        </section>

        <section className="split">
          <div>
            <h3>Create card</h3>
            <form onSubmit={doCreate} className="form">
              <label>
                Owner name
                <input value={createCard.ownerName} onChange={(e) => setCreateCard({ ...createCard, ownerName: e.target.value })} required />
              </label>
              <label>
                Expiry (MM/YY)
                <input value={createCard.expiryDate} onChange={(e) => setCreateCard({ ...createCard, expiryDate: e.target.value })} required />
              </label>
              <label>
                Balance
                <input type="number" step="0.01" value={createCard.balance} onChange={(e) => setCreateCard({ ...createCard, balance: e.target.value })} required />
              </label>
              <button className="primary" disabled={loading}>Create</button>
            </form>
          </div>
          <div>
            <h3>Activate card</h3>
            <form onSubmit={doActivate} className="form">
              <label>
                Owner name
                <input value={activateCard.ownerName} onChange={(e) => setActivateCard({ ...activateCard, ownerName: e.target.value })} required />
              </label>
              <label>
                Card last 4
                <input value={activateCard.cardNumberLast4} onChange={(e) => setActivateCard({ ...activateCard, cardNumberLast4: e.target.value })} required />
              </label>
              <button className="secondary" disabled={loading}>Activate</button>
            </form>
            <h3>Delete card</h3>
            <form onSubmit={doDelete} className="form">
              <label>
                Owner name
                <input value={deleteCard.ownerName} onChange={(e) => setDeleteCard({ ...deleteCard, ownerName: e.target.value })} required />
              </label>
              <label>
                Card last 4
                <input value={deleteCard.cardNumberLast4} onChange={(e) => setDeleteCard({ ...deleteCard, cardNumberLast4: e.target.value })} required />
              </label>
              <button className="danger" disabled={loading}>Delete</button>
            </form>
          </div>
        </section>
      </div>
    </div>
  );
}
