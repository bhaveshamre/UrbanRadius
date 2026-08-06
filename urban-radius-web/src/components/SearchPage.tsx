import { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { restoreSession } from '../store/slices/authSlice';
import {
  clearBookingError,
  clearCreateSuccess,
  createBooking,
  fetchMyBookings,
} from '../store/slices/bookingSlice';
import { fetchListings, setCategory, setCity } from '../store/slices/searchSlice';
import { Listing } from '../types/listing';
import { BookingModal } from './BookingModal';
import { Layout, AppPage } from './Layout';
import { ListingList } from './ListingList';
import { SearchBar } from './SearchBar';
import { ServicesHeader } from './ServicesHeader';

interface SearchPageProps {
  activePage: AppPage;
  onNavigate: (page: AppPage) => void;
}

export function SearchPage({ activePage, onNavigate }: SearchPageProps) {
  const dispatch = useAppDispatch();
  const { city, category, results, providerMap, loading, error, hasSearched } = useAppSelector(
    (state) => state.search,
  );
  const { token, user, initialized } = useAppSelector((state) => state.auth);
  const {
    actionLoadingId,
    error: bookingError,
    createSuccess,
  } = useAppSelector((state) => state.booking);

  const [bookingListing, setBookingListing] = useState<Listing | null>(null);
  const [loginPrompt, setLoginPrompt] = useState(false);

  useEffect(() => {
    if (!initialized) {
      dispatch(restoreSession());
    }
  }, [dispatch, initialized]);

  useEffect(() => {
    if (!initialized) {
      return;
    }

    if (user?.city) {
      dispatch(setCity(user.city));
    }

    dispatch(fetchListings());
  }, [dispatch, initialized, user?.id]);

  useEffect(() => {
    if (createSuccess) {
      setBookingListing(null);
      dispatch(clearCreateSuccess());
      dispatch(fetchMyBookings());
      onNavigate('bookings');
    }
  }, [createSuccess, dispatch, onNavigate]);

  const canBook = Boolean(token && user?.role === 'SEEKER');

  function handleBook(listing: Listing) {
    if (!token) {
      setLoginPrompt(true);
      return;
    }
    if (user?.role !== 'SEEKER') {
      setLoginPrompt(true);
      return;
    }
    setLoginPrompt(false);
    dispatch(clearBookingError());
    setBookingListing(listing);
  }

  function handleBookingSubmit(scheduledAt: string, notes: string) {
    if (!bookingListing) {
      return;
    }
    dispatch(
      createBooking({
        listingId: bookingListing.id,
        scheduledAt,
        notes: notes || undefined,
      }),
    );
  }

  return (
    <Layout activePage={activePage} onNavigate={onNavigate}>
      <div className="space-y-6">
        <ServicesHeader
          user={user}
          loading={loading}
          resultCount={results.length}
          hasSearched={hasSearched}
          error={error}
        />

        <SearchBar
          city={city}
          category={category}
          loading={loading}
          onCityChange={(value) => dispatch(setCity(value))}
          onCategoryChange={(value) => dispatch(setCategory(value))}
          onSearch={() => dispatch(fetchListings())}
        />

        {loginPrompt && (
          <div className="rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900">
            Log in as a <strong>seeker</strong> (e.g. amit@example.com / secret) to book a listing.
          </div>
        )}

        <section aria-labelledby="services-heading">
          <h2 id="services-heading" className="mb-4 text-lg font-semibold text-slate-800">
            Available services
          </h2>
          <ListingList
            listings={results}
            providerMap={providerMap}
            hasSearched={hasSearched}
            loading={loading}
            error={error}
            canBook={canBook}
            onBook={handleBook}
            onRetry={() => dispatch(fetchListings())}
          />
        </section>
      </div>

      {bookingListing && (
        <BookingModal
          listing={bookingListing}
          loading={actionLoadingId === 'create'}
          error={bookingError}
          onClose={() => {
            setBookingListing(null);
            dispatch(clearBookingError());
          }}
          onSubmit={handleBookingSubmit}
        />
      )}
    </Layout>
  );
}
