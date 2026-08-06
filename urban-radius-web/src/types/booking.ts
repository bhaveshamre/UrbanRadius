export type BookingStatus =
  | 'REQUESTED'
  | 'ACCEPTED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED';

export type PaymentStatus = 'HELD' | 'RELEASED' | 'REFUNDED' | 'FAILED';

/** Matches Order Service BookingResponse JSON */
export interface Booking {
  id: string;
  listingId: string;
  seekerId: string;
  providerId: string;
  status: BookingStatus;
  scheduledAt: string;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBookingPayload {
  listingId: string;
  scheduledAt: string;
  notes?: string;
}

export interface Payment {
  id: string;
  bookingId: string;
  seekerId: string;
  providerId: string;
  amount: number;
  currency: string;
  status: PaymentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface BookingEnrichment {
  listingTitle: string;
  seekerName: string;
  providerName: string;
  paymentStatus?: PaymentStatus;
}

export const BOOKING_STATUS_LABELS: Record<BookingStatus, string> = {
  REQUESTED: 'Requested',
  ACCEPTED: 'Accepted',
  IN_PROGRESS: 'In progress',
  COMPLETED: 'Completed',
  CANCELLED: 'Cancelled',
};

export const BOOKING_STATUS_STYLES: Record<BookingStatus, string> = {
  REQUESTED: 'bg-amber-50 text-amber-800',
  ACCEPTED: 'bg-blue-50 text-blue-800',
  IN_PROGRESS: 'bg-violet-50 text-violet-800',
  COMPLETED: 'bg-green-50 text-green-800',
  CANCELLED: 'bg-slate-100 text-slate-600',
};

export function formatBookingDate(iso: string): string {
  return new Date(iso).toLocaleString(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  });
}
