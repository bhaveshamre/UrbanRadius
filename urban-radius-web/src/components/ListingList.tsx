import { ListingCard } from './ListingCard';
import { Listing } from '../types/listing';
import { UserProfile } from '../types/user';

interface ListingListProps {
  listings: Listing[];
  providerMap: Record<string, UserProfile>;
  hasSearched: boolean;
  loading: boolean;
  error: string | null;
}

export function ListingList({
  listings,
  providerMap,
  hasSearched,
  loading,
  error,
}: ListingListProps) {
  if (!hasSearched) {
    return (
      <div className="rounded-xl border border-dashed border-slate-300 bg-white p-12 text-center">
        <p className="text-slate-600">
          Enter a city and click Search to load listings from Catalog Service.
        </p>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="rounded-xl border border-slate-200 bg-white p-12 text-center">
        <p className="text-slate-600">Searching listings and loading provider profiles…</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-12 text-center">
        <p className="font-medium text-red-800">Could not load listings</p>
        <p className="mt-1 text-sm text-red-600">{error}</p>
        <p className="mt-3 text-xs text-red-500">
          Ensure Catalog Service (8082) and User Service (8081) are running.
        </p>
      </div>
    );
  }

  if (listings.length === 0) {
    return (
      <div className="rounded-xl border border-slate-200 bg-white p-12 text-center">
        <p className="font-medium text-slate-800">No listings found</p>
        <p className="mt-1 text-sm text-slate-500">Try a different city or category.</p>
      </div>
    );
  }

  return (
    <div>
      <p className="mb-4 text-sm text-slate-500">
        {listings.length} listing{listings.length === 1 ? '' : 's'} found
      </p>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {listings.map((listing) => (
          <ListingCard
            key={listing.id}
            listing={listing}
            provider={providerMap[listing.providerId]}
          />
        ))}
      </div>
    </div>
  );
}
