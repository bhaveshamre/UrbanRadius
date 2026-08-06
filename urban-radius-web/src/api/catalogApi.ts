import { Listing, SearchFilters } from '../types/listing';
import { apiFetch, parseApiError } from './httpClient';

export async function searchListings(filters: SearchFilters): Promise<Listing[]> {
  const params = new URLSearchParams();

  if (filters.city.trim()) {
    params.set('city', filters.city.trim());
  }
  if (filters.category) {
    params.set('category', filters.category);
  }
  if (filters.subcategory.trim()) {
    params.set('subcategory', filters.subcategory.trim());
  }

  const query = params.toString();
  const url = query ? `/api/listings?${query}` : '/api/listings';

  const response = await apiFetch(url);

  if (!response.ok) {
    throw new Error(await parseApiError(response, `Search failed (${response.status})`));
  }

  const data = (await response.json()) as Listing[];
  return data.map((listing) => ({
    ...listing,
    priceAmount: Number(listing.priceAmount),
  }));
}

export async function getListingById(listingId: string): Promise<Listing> {
  const response = await apiFetch(`/api/listings/${listingId}`);

  if (!response.ok) {
    throw new Error(await parseApiError(response, `Listing not found (${response.status})`));
  }

  const listing = (await response.json()) as Listing;
  return { ...listing, priceAmount: Number(listing.priceAmount) };
}
