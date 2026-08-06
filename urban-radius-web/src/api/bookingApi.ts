import { Booking, CreateBookingPayload } from '../types/booking';
import { apiFetch, parseApiError } from './httpClient';

export async function createBooking(
  token: string,
  payload: CreateBookingPayload,
): Promise<Booking> {
  const response = await apiFetch('/api/bookings', {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(await parseApiError(response, `Booking failed (${response.status})`));
  }

  return response.json() as Promise<Booking>;
}

export async function fetchMyBookings(token: string): Promise<Booking[]> {
  const response = await apiFetch('/api/bookings/my', {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!response.ok) {
    throw new Error(await parseApiError(response, `Failed to load bookings (${response.status})`));
  }

  return response.json() as Promise<Booking[]>;
}

async function transitionBooking(url: string, token: string, fallback: string): Promise<Booking> {
  const response = await apiFetch(url, {
    method: 'PATCH',
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!response.ok) {
    throw new Error(await parseApiError(response, fallback));
  }

  return response.json() as Promise<Booking>;
}

export async function acceptBooking(token: string, bookingId: string): Promise<Booking> {
  return transitionBooking(`/api/bookings/${bookingId}/accept`, token, 'Accept failed');
}

export async function startBooking(token: string, bookingId: string): Promise<Booking> {
  return transitionBooking(`/api/bookings/${bookingId}/start`, token, 'Start failed');
}

export async function completeBooking(token: string, bookingId: string): Promise<Booking> {
  return transitionBooking(`/api/bookings/${bookingId}/complete`, token, 'Complete failed');
}

export async function cancelBooking(token: string, bookingId: string): Promise<Booking> {
  return transitionBooking(`/api/bookings/${bookingId}/cancel`, token, 'Cancel failed');
}
