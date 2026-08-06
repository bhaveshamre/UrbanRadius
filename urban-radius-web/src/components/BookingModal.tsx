import { FormEvent, useState } from 'react';
import { Listing } from '../types/listing';

interface BookingModalProps {
  listing: Listing;
  loading: boolean;
  error: string | null;
  onClose: () => void;
  onSubmit: (scheduledAt: string, notes: string) => void;
}

export function BookingModal({
  listing,
  loading,
  error,
  onClose,
  onSubmit,
}: BookingModalProps) {
  const defaultDate = new Date(Date.now() + 24 * 60 * 60 * 1000);
  const defaultLocal = defaultDate.toISOString().slice(0, 16);

  const [scheduledAt, setScheduledAt] = useState(defaultLocal);
  const [notes, setNotes] = useState('');

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    const iso = new Date(scheduledAt).toISOString();
    onSubmit(iso, notes.trim());
  }

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="booking-modal-title"
    >
      <div className="w-full max-w-md rounded-xl bg-white p-6 shadow-xl">
        <div className="flex items-start justify-between gap-3">
          <div>
            <h2 id="booking-modal-title" className="text-lg font-semibold text-slate-900">
              Book service
            </h2>
            <p className="mt-1 text-sm text-slate-600">{listing.title}</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg px-2 py-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600"
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <div>
            <label htmlFor="scheduledAt" className="block text-sm font-medium text-slate-700">
              Preferred date & time
            </label>
            <input
              id="scheduledAt"
              type="datetime-local"
              required
              value={scheduledAt}
              onChange={(e) => setScheduledAt(e.target.value)}
              className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 text-sm"
            />
          </div>

          <div>
            <label htmlFor="notes" className="block text-sm font-medium text-slate-700">
              Notes for provider (optional)
            </label>
            <textarea
              id="notes"
              rows={3}
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="Describe what you need…"
              className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 text-sm"
            />
          </div>

          {error && (
            <p className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>
          )}

          <p className="text-xs text-slate-500">
            Payment will be held when you submit. The provider must accept before work begins.
          </p>

          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="flex-1 rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 rounded-lg bg-brand-600 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700 disabled:opacity-50"
            >
              {loading ? 'Booking…' : 'Confirm booking'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
