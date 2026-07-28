import { FormEvent } from 'react';
import { LISTING_CATEGORIES, ListingCategory } from '../types/listing';

interface SearchBarProps {
  city: string;
  category: ListingCategory | '';
  loading: boolean;
  onCityChange: (city: string) => void;
  onCategoryChange: (category: ListingCategory | '') => void;
  onSearch: () => void;
}

export function SearchBar({
  city,
  category,
  loading,
  onCityChange,
  onCategoryChange,
  onSearch,
}: SearchBarProps) {
  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    onSearch();
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm"
    >
      <h2 className="text-lg font-semibold text-slate-800">Find skills near you</h2>
      <p className="mt-1 text-sm text-slate-500">
        Live search via Catalog Service + provider ratings from User Service.
      </p>

      <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <label className="block">
          <span className="text-sm font-medium text-slate-700">City</span>
          <input
            type="text"
            value={city}
            onChange={(e) => onCityChange(e.target.value)}
            placeholder="e.g. Bangalore"
            disabled={loading}
            className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-100 disabled:bg-slate-50"
          />
        </label>

        <label className="block">
          <span className="text-sm font-medium text-slate-700">Category</span>
          <select
            value={category}
            onChange={(e) => onCategoryChange(e.target.value as ListingCategory | '')}
            disabled={loading}
            className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-100 disabled:bg-slate-50"
          >
            {LISTING_CATEGORIES.map((option) => (
              <option key={option.label} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <div className="flex items-end">
          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-brand-600 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-700 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:bg-brand-400"
          >
            {loading ? 'Searching…' : 'Search'}
          </button>
        </div>
      </div>
    </form>
  );
}
