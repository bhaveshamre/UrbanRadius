import { AuthButton } from './AuthButton';

interface LayoutProps {
  children: React.ReactNode;
}

export function Layout({ children }: LayoutProps) {
  return (
    <div className="min-h-screen flex flex-col">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-4 sm:px-6">
          <div>
            <h1 className="text-xl font-bold text-brand-700">Urban Radius</h1>
            <p className="text-sm text-slate-500">Hyperlocal skills & services</p>
          </div>
          <AuthButton />
        </div>
      </header>

      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-8 sm:px-6">{children}</main>

      <footer className="border-t border-slate-200 bg-white py-4 text-center text-sm text-slate-500">
        Urban Radius — seeker search + Keycloak login
      </footer>
    </div>
  );
}
