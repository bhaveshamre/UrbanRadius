import { Listing } from '../types/listing';

/** @deprecated Phase 5a mock data — use Catalog API via Redux in Phase 5b+ */
export const mockListings: Listing[] = [
  {
    id: '11111111-1111-1111-1111-111111111111',
    providerId: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    title: 'AC Repair & Servicing',
    description: 'All brands. Same-day emergency visits across Bangalore.',
    category: 'HOME_REPAIR',
    subcategory: 'AC_REPAIR',
    priceAmount: 500,
    priceUnit: 'PER_VISIT',
    city: 'Bangalore',
    active: true,
    attributes: { brands: ['LG', 'Samsung', 'Daikin'], emergencyAvailable: true },
    createdAt: '2026-07-27T10:00:00Z',
    updatedAt: '2026-07-27T10:00:00Z',
  },
];
