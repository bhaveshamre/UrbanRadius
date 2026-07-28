import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { searchListings } from '../../api/catalogApi';
import { fetchProviderProfiles } from '../../api/userApi';
import { Listing, ListingCategory } from '../../types/listing';
import { UserProfile } from '../../types/user';

export interface SearchState {
  city: string;
  category: ListingCategory | '';
  subcategory: string;
  results: Listing[];
  providerMap: Record<string, UserProfile>;
  loading: boolean;
  error: string | null;
  hasSearched: boolean;
}

const initialState: SearchState = {
  city: 'Bangalore',
  category: '',
  subcategory: '',
  results: [],
  providerMap: {},
  loading: false,
  error: null,
  hasSearched: false,
};

export const fetchListings = createAsyncThunk(
  'search/fetchListings',
  async (_, { getState, rejectWithValue }) => {
    try {
      const state = getState() as { search: SearchState };
      const listings = await searchListings({
        city: state.search.city,
        category: state.search.category,
        subcategory: state.search.subcategory,
      });

      const providerMap = await fetchProviderProfiles(listings.map((listing) => listing.providerId));

      return { listings, providerMap };
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Search failed';
      return rejectWithValue(message);
    }
  },
);

const searchSlice = createSlice({
  name: 'search',
  initialState,
  reducers: {
    setCity(state, action: PayloadAction<string>) {
      state.city = action.payload;
    },
    setCategory(state, action: PayloadAction<ListingCategory | ''>) {
      state.category = action.payload;
    },
    setSubcategory(state, action: PayloadAction<string>) {
      state.subcategory = action.payload;
    },
    clearError(state) {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchListings.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.hasSearched = true;
      })
      .addCase(fetchListings.fulfilled, (state, action) => {
        state.loading = false;
        state.results = action.payload.listings;
        state.providerMap = action.payload.providerMap;
      })
      .addCase(fetchListings.rejected, (state, action) => {
        state.loading = false;
        state.results = [];
        state.providerMap = {};
        state.error = (action.payload as string) ?? 'Search failed';
      });
  },
});

export const { setCity, setCategory, setSubcategory, clearError } = searchSlice.actions;
export default searchSlice.reducer;
