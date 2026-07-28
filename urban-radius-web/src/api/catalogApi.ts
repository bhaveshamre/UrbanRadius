import { Listing, SearchFilters } from '../types/listing';

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

  const response = await fetch(url);

  if (!response.ok) {
    let message = `Search failed (${response.status})`;
    try {
      const body = (await response.json()) as { message?: string };
      if (body.message) {
        message = body.message;
      }
    } catch {
      // use default message
    }
    throw new Error(message);
  }

  const data = (await response.json()) as Listing[];
  return data.map((listing) => ({
    ...listing,
    priceAmount: Number(listing.priceAmount),
  }));
}
