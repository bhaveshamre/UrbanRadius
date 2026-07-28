import { formatCategory, formatPrice, formatProviderId, Listing } from '../types/listing';
import { UserProfile } from '../types/user';

interface ListingCardProps {
  listing: Listing;
  provider?: UserProfile;
}

export function ListingCard({ listing, provider }: ListingCardProps) {
  return (
    <article className="flex flex-col rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition hover:border-brand-200 hover:shadow-md">
      <div className="flex items-start justify-between gap-3">
        <h3 className="text-lg font-semibold text-slate-900">{listing.title}</h3>
        <span className="shrink-0 rounded-full bg-brand-50 px-2.5 py-0.5 text-xs font-medium text-brand-700">
          {formatCategory(listing.category)}
        </span>
      </div>

      <p className="mt-2 line-clamp-2 flex-1 text-sm text-slate-600">{listing.description}</p>

      <div className="mt-4 space-y-2 border-t border-slate-100 pt-4 text-sm">
        <p className="font-semibold text-brand-700">
          {formatPrice(listing.priceAmount, listing.priceUnit)}
        </p>
        <p className="text-slate-500">{listing.city}</p>
        {provider ? (
          <p className="text-slate-600">
            <span className="text-amber-500">★</span> {provider.averageRating.toFixed(1)}{' '}
            <span className="text-slate-500">
              ({provider.ratingCount} review{provider.ratingCount === 1 ? '' : 's'}) —{' '}
              {provider.fullName}
            </span>
          </p>
        ) : (
          <p className="text-slate-500">{formatProviderId(listing.providerId)}</p>
        )}
      </div>
    </article>
  );
}
