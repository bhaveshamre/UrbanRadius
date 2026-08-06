import { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { clearBookingError, fetchMyBookings } from '../store/slices/bookingSlice';
import { restoreSession } from '../store/slices/authSlice';
import { BookingCard } from './BookingCard';
import { Layout, AppPage } from './Layout';

interface MyBookingsPageProps {
  activePage: AppPage;
  onNavigate: (page: AppPage) => void;
}

export function MyBookingsPage({ activePage, onNavigate }: MyBookingsPageProps) {
  const dispatch = useAppDispatch();
  const { token, user, initialized } = useAppSelector((state) => state.auth);
  const { bookings, enrichment, loading, error } = useAppSelector((state) => state.booking);

  useEffect(() => {
    if (!initialized) {
      dispatch(restoreSession());
    }
  }, [dispatch, initialized]);

  useEffect(() => {
    if (token) {
      dispatch(fetchMyBookings());
    }
  }, [dispatch, token]);

  useEffect(() => {
    return () => {
      dispatch(clearBookingError());
    };
  }, [dispatch]);

  const sortedBookings = [...bookings].sort(
    (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
  );

  return (
    <Layout activePage={activePage} onNavigate={onNavigate}>
      <div className="space-y-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">My bookings</h2>
          <p className="mt-1 text-sm text-slate-600">
            {user?.role === 'PROVIDER'
              ? 'Manage incoming requests and update job status.'
              : user?.role === 'SEEKER'
                ? 'Track your service requests and payment holds.'
                : 'Log in to view and manage bookings.'}
          </p>
        </div>

        {!token && (
          <div className="rounded-xl border border-dashed border-slate-300 bg-white p-12 text-center">
            <p className="text-slate-600">Log in to see bookings you are part of.</p>
          </div>
        )}

        {token && loading && (
          <div className="rounded-xl border border-slate-200 bg-white p-12 text-center">
            <p className="text-slate-600">Loading your bookings…</p>
          </div>
        )}

        {token && error && !loading && (
          <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center">
            <p className="font-medium text-red-800">{error}</p>
            <button
              type="button"
              onClick={() => dispatch(fetchMyBookings())}
              className="mt-3 text-sm font-medium text-red-700 underline"
            >
              Try again
            </button>
          </div>
        )}

        {token && !loading && !error && sortedBookings.length === 0 && (
          <div className="rounded-xl border border-slate-200 bg-white p-12 text-center">
            <p className="font-medium text-slate-800">No bookings yet</p>
            <p className="mt-1 text-sm text-slate-500">
              {user?.role === 'SEEKER'
                ? 'Search for a listing and click Book now to request a service.'
                : 'When seekers book your listings, they will appear here.'}
            </p>
            {user?.role === 'SEEKER' && (
              <button
                type="button"
                onClick={() => onNavigate('search')}
                className="mt-4 rounded-lg bg-brand-600 px-4 py-2 text-sm font-medium text-white hover:bg-brand-700"
              >
                Browse listings
              </button>
            )}
          </div>
        )}

        {token && !loading && sortedBookings.length > 0 && (
          <div className="space-y-4">
            {sortedBookings.map((booking) => (
              <BookingCard
                key={booking.id}
                booking={booking}
                enrichment={enrichment[booking.id]}
              />
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
}
