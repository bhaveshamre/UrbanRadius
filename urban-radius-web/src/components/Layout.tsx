import { AuthButton } from './AuthButton';

export type AppPage = 'search' | 'bookings';

interface LayoutProps {
  children: React.ReactNode;
  activePage: AppPage;
  onNavigate: (page: AppPage) => void;
}

export function Layout({ children, activePage, onNavigate }: LayoutProps) {
  return (
    <div className="min-h-screen flex flex-col">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-4 px-4 py-4 sm:px-6">
          <div>
            <h1 className="text-xl font-bold text-brand-700">Urban Radius</h1>
            <p className="text-sm text-slate-500">Hyperlocal skills & services</p>
          </div>

          <nav className="flex items-center gap-1 rounded-lg bg-slate-100 p-1">
            <NavTab
              label="Search"
              active={activePage === 'search'}
              onClick={() => onNavigate('search')}
            />
            <NavTab
              label="My bookings"
              active={activePage === 'bookings'}
              onClick={() => onNavigate('bookings')}
            />
          </nav>

          <AuthButton />
        </div>
      </header>

      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-8 sm:px-6">{children}</main>

      <footer className="border-t border-slate-200 bg-white py-4 text-center text-sm text-slate-500">
        Urban Radius — search, book, and manage local services
      </footer>
    </div>
  );
}

function NavTab({
  label,
  active,
  onClick,
}: {
  label: string;
  active: boolean;
  onClick: () => void;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`rounded-md px-3 py-1.5 text-sm font-medium transition ${
        active
          ? 'bg-white text-brand-700 shadow-sm'
          : 'text-slate-600 hover:text-slate-900'
      }`}
    >
      {label}
    </button>
  );
}
