export type ListingCategory =
  | 'HOME_REPAIR'
  | 'TUTORING'
  | 'COOKING'
  | 'MOVING'
  | 'OTHER';

export type PriceUnit = 'PER_HOUR' | 'PER_VISIT' | 'PER_SESSION' | 'FIXED';

/** Matches Catalog Service ListingResponse JSON */
export interface Listing {
  id: string;
  providerId: string;
  title: string;
  description: string;
  category: ListingCategory;
  subcategory: string;
  priceAmount: number;
  priceUnit: PriceUnit;
  city: string;
  active: boolean;
  attributes: Record<string, unknown>;
  createdAt: string;
  updatedAt: string;
}

export interface SearchFilters {
  city: string;
  category: ListingCategory | '';
  subcategory: string;
}

export const LISTING_CATEGORIES: { value: ListingCategory | ''; label: string }[] = [
  { value: '', label: 'All categories' },
  { value: 'HOME_REPAIR', label: 'Home repair' },
  { value: 'TUTORING', label: 'Tutoring' },
  { value: 'COOKING', label: 'Cooking' },
  { value: 'MOVING', label: 'Moving help' },
  { value: 'OTHER', label: 'Other' },
];

export function formatPrice(amount: number, unit: PriceUnit): string {
  const unitLabel: Record<PriceUnit, string> = {
    PER_HOUR: '/ hour',
    PER_VISIT: '/ visit',
    PER_SESSION: '/ session',
    FIXED: ' fixed',
  };
  return `₹${amount}${unitLabel[unit]}`;
}

export function formatCategory(category: ListingCategory): string {
  return category.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase());
}

export function formatProviderId(providerId: string): string {
  return `Provider ${providerId.slice(0, 8)}…`;
}
