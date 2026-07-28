import { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { restoreSession } from '../store/slices/authSlice';
import { fetchListings, setCategory, setCity } from '../store/slices/searchSlice';
import { Layout } from './Layout';
import { ListingList } from './ListingList';
import { SearchBar } from './SearchBar';

export function SearchPage() {
  const dispatch = useAppDispatch();
  const { city, category, results, providerMap, loading, error, hasSearched } = useAppSelector(
    (state) => state.search,
  );
  const { initialized } = useAppSelector((state) => state.auth);

  useEffect(() => {
    if (!initialized) {
      dispatch(restoreSession());
    }
  }, [dispatch, initialized]);

  return (
    <Layout>
      <div className="space-y-8">
        <SearchBar
          city={city}
          category={category}
          loading={loading}
          onCityChange={(value) => dispatch(setCity(value))}
          onCategoryChange={(value) => dispatch(setCategory(value))}
          onSearch={() => dispatch(fetchListings())}
        />
        <ListingList
          listings={results}
          providerMap={providerMap}
          hasSearched={hasSearched}
          loading={loading}
          error={error}
        />
      </div>
    </Layout>
  );
}
