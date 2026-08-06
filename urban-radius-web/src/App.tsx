import { useState } from 'react';
import { MyBookingsPage } from './components/MyBookingsPage';
import { SearchPage } from './components/SearchPage';
import { AppPage } from './components/Layout';

function App() {
  const [page, setPage] = useState<AppPage>('search');

  if (page === 'bookings') {
    return <MyBookingsPage activePage={page} onNavigate={setPage} />;
  }

  return <SearchPage activePage={page} onNavigate={setPage} />;
}

export default App;
