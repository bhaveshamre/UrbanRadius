import { Payment } from '../types/booking';
import { apiFetch } from './httpClient';

export async function fetchPaymentByBookingId(
  token: string,
  bookingId: string,
): Promise<Payment | null> {
  const response = await apiFetch(`/api/payments/booking/${bookingId}`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    return null;
  }

  const data = (await response.json()) as Payment;
  return { ...data, amount: Number(data.amount) };
}
