import { useState } from 'react';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { logout } from '../store/slices/authSlice';
import { LoginModal } from './LoginModal';

export function AuthButton() {
  const dispatch = useAppDispatch();
  const { user, token } = useAppSelector((state) => state.auth);
  const [modalOpen, setModalOpen] = useState(false);

  if (token && user) {
    return (
      <div className="flex items-center gap-3">
        <div className="text-right">
          <p className="text-sm font-medium text-slate-800">{user.fullName}</p>
          <p className="text-xs text-slate-500 capitalize">{user.role.toLowerCase()}</p>
        </div>
        <button
          type="button"
          onClick={() => dispatch(logout())}
          className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
        >
          Logout
        </button>
      </div>
    );
  }

  return (
    <>
      <button
        type="button"
        onClick={() => setModalOpen(true)}
        className="rounded-lg bg-brand-600 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-700"
      >
        Login
      </button>
      <LoginModal open={modalOpen} onClose={() => setModalOpen(false)} />
    </>
  );
}
