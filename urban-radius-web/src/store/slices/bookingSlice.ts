import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import {
  acceptBooking as acceptBookingRequest,
  cancelBooking as cancelBookingRequest,
  completeBooking as completeBookingRequest,
  createBooking as createBookingRequest,
  fetchMyBookings as fetchMyBookingsRequest,
  startBooking as startBookingRequest,
} from '../../api/bookingApi';
import { getListingById } from '../../api/catalogApi';
import { fetchPaymentByBookingId } from '../../api/paymentApi';
import { getUserProfile } from '../../api/userApi';
import { Booking, BookingEnrichment, CreateBookingPayload } from '../../types/booking';
import type { RootState } from '../store';

export interface BookingState {
  bookings: Booking[];
  enrichment: Record<string, BookingEnrichment>;
  loading: boolean;
  actionLoadingId: string | null;
  error: string | null;
  createSuccess: boolean;
}

const initialState: BookingState = {
  bookings: [],
  enrichment: {},
  loading: false,
  actionLoadingId: null,
  error: null,
  createSuccess: false,
};

async function enrichBookings(
  token: string,
  bookings: Booking[],
): Promise<Record<string, BookingEnrichment>> {
  const enrichment: Record<string, BookingEnrichment> = {};

  await Promise.all(
    bookings.map(async (booking) => {
      const [listing, seeker, provider, payment] = await Promise.all([
        getListingById(booking.listingId).catch(() => null),
        getUserProfile(booking.seekerId).catch(() => null),
        getUserProfile(booking.providerId).catch(() => null),
        fetchPaymentByBookingId(token, booking.id).catch(() => null),
      ]);

      enrichment[booking.id] = {
        listingTitle: listing?.title ?? `Listing ${booking.listingId.slice(0, 8)}…`,
        seekerName: seeker?.fullName ?? 'Seeker',
        providerName: provider?.fullName ?? 'Provider',
        paymentStatus: payment?.status,
      };
    }),
  );

  return enrichment;
}

function requireToken(getState: () => unknown): string {
  const token = (getState() as RootState).auth.token;
  if (!token) {
    throw new Error('Please log in to manage bookings');
  }
  return token;
}

export const fetchMyBookings = createAsyncThunk(
  'booking/fetchMy',
  async (_, { getState, rejectWithValue }) => {
    try {
      const token = requireToken(getState);
      const bookings = await fetchMyBookingsRequest(token);
      const enrichment = await enrichBookings(token, bookings);
      return { bookings, enrichment };
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to load bookings';
      return rejectWithValue(message);
    }
  },
);

export const createBooking = createAsyncThunk(
  'booking/create',
  async (payload: CreateBookingPayload, { getState, rejectWithValue }) => {
    try {
      const token = requireToken(getState);
      const booking = await createBookingRequest(token, payload);
      return booking;
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Booking failed';
      return rejectWithValue(message);
    }
  },
);

function transitionThunk(
  name: string,
  action: (token: string, bookingId: string) => Promise<Booking>,
) {
  return createAsyncThunk(
    name,
    async (bookingId: string, { getState, rejectWithValue }) => {
      try {
        const token = requireToken(getState);
        return await action(token, bookingId);
      } catch (error) {
        const message = error instanceof Error ? error.message : 'Action failed';
        return rejectWithValue(message);
      }
    },
  );
}

export const acceptBooking = transitionThunk('booking/accept', acceptBookingRequest);
export const startBooking = transitionThunk('booking/start', startBookingRequest);
export const completeBooking = transitionThunk('booking/complete', completeBookingRequest);
export const cancelBooking = transitionThunk('booking/cancel', cancelBookingRequest);

const bookingSlice = createSlice({
  name: 'booking',
  initialState,
  reducers: {
    clearBookingError(state) {
      state.error = null;
    },
    clearCreateSuccess(state) {
      state.createSuccess = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMyBookings.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyBookings.fulfilled, (state, action) => {
        state.loading = false;
        state.bookings = action.payload.bookings;
        state.enrichment = action.payload.enrichment;
      })
      .addCase(fetchMyBookings.rejected, (state, action) => {
        state.loading = false;
        state.error = (action.payload as string) ?? 'Failed to load bookings';
      })
      .addCase(createBooking.pending, (state) => {
        state.actionLoadingId = 'create';
        state.error = null;
        state.createSuccess = false;
      })
      .addCase(createBooking.fulfilled, (state) => {
        state.actionLoadingId = null;
        state.createSuccess = true;
      })
      .addCase(createBooking.rejected, (state, action) => {
        state.actionLoadingId = null;
        state.error = (action.payload as string) ?? 'Booking failed';
      });

    const transitionCases = [acceptBooking, startBooking, completeBooking, cancelBooking];
    transitionCases.forEach((thunk) => {
      builder
        .addCase(thunk.pending, (state, action) => {
          state.actionLoadingId = action.meta.arg;
          state.error = null;
        })
        .addCase(thunk.fulfilled, (state, action) => {
          state.actionLoadingId = null;
          const index = state.bookings.findIndex((b) => b.id === action.payload.id);
          if (index >= 0) {
            state.bookings[index] = action.payload;
          }
        })
        .addCase(thunk.rejected, (state, action) => {
          state.actionLoadingId = null;
          state.error = (action.payload as string) ?? 'Action failed';
        });
    });
  },
});

export const { clearBookingError, clearCreateSuccess } = bookingSlice.actions;
export default bookingSlice.reducer;
