import { ListingCard } from './ListingCard';
import { Listing } from '../types/listing';
import { UserProfile } from '../types/user';

interface ListingListProps {
  listings: Listing[];
  providerMap: Record<string, UserProfile>;
  hasSearched: boolean;
  loading: boolean;
  error: string | null;
  canBook?: boolean;
  onBook?: (listing: Listing) => void;
  onRetry?: () => void;
}

export function ListingList({
  listings,
  providerMap,
  hasSearched,
  loading,
  error,
  canBook,
  onBook,
  onRetry,
}: ListingListProps) {
  if (!hasSearched || loading) {
    return (
      <div className="rounded-xl border border-slate-200 bg-white p-12 text-center">
        <p className="text-slate-600">Loading services from the catalog…</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-12 text-center">
        <p className="font-medium text-red-800">Could not load services</p>
        <p className="mt-1 text-sm text-red-600">{error}</p>
        <p className="mt-3 text-xs text-red-500">
          Ensure API Gateway (8085), Catalog (8082), and User Service (8081) are running, then retry.
        </p>
        {onRetry && (
          <button
            type="button"
            onClick={onRetry}
            className="mt-4 rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white hover:bg-red-700"
          >
            Retry
          </button>
        )}
      </div>
    );
  }

  if (listings.length === 0) {
    return (
      <div className="rounded-xl border border-slate-200 bg-white p-12 text-center">
        <p className="font-medium text-slate-800">No services found</p>
        <p className="mt-1 text-sm text-slate-500">
          Try another city or category, or ask a provider to publish a listing.
        </p>
      </div>
    );
  }

  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {listings.map((listing) => (
        <ListingCard
          key={listing.id}
          listing={listing}
          provider={providerMap[listing.providerId]}
          canBook={canBook}
          onBook={onBook}
        />
      ))}
    </div>
  );
}
