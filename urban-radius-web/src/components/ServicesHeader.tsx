import { UserProfile } from '../types/user';

interface ServicesHeaderProps {
  user: UserProfile | null;
  loading: boolean;
  resultCount: number;
  hasSearched: boolean;
  error: string | null;
}

export function ServicesHeader({
  user,
  loading,
  resultCount,
  hasSearched,
  error,
}: ServicesHeaderProps) {
  let statusMessage = 'Loading services near you…';

  if (hasSearched && !loading && !error) {
    statusMessage =
      resultCount > 0
        ? `${resultCount} service${resultCount === 1 ? '' : 's'} available`
        : 'No services match your filters yet';
  }

  if (error) {
    statusMessage = 'Could not reach the service catalog';
  }

  return (
    <div className="rounded-xl border border-brand-100 bg-gradient-to-r from-brand-50 to-white p-6 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">
            {user ? `Welcome, ${user.fullName.split(' ')[0]}` : 'Browse local services'}
          </h2>
          <p className="mt-1 text-sm text-slate-600">
            {user
              ? user.role === 'SEEKER'
                ? 'Pick a service below and book with one click.'
                : user.role === 'PROVIDER'
                  ? 'You are logged in as a provider. Switch to My bookings to manage requests.'
                  : 'Explore listings available in your area.'
              : 'Services load automatically. Log in as a seeker to book.'}
          </p>
        </div>
        <div className="rounded-lg bg-white px-3 py-2 text-sm shadow-sm ring-1 ring-slate-200">
          <span className="text-slate-500">Status: </span>
          <span className={error ? 'font-medium text-red-600' : 'font-medium text-brand-700'}>
            {statusMessage}
          </span>
        </div>
      </div>
    </div>
  );
}
