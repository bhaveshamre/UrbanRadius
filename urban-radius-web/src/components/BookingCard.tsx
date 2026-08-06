import {
  acceptBooking,
  cancelBooking,
  completeBooking,
  fetchMyBookings,
  startBooking,
} from '../store/slices/bookingSlice';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import {
  BOOKING_STATUS_LABELS,
  BOOKING_STATUS_STYLES,
  Booking,
  BookingEnrichment,
  formatBookingDate,
} from '../types/booking';

interface BookingCardProps {
  booking: Booking;
  enrichment?: BookingEnrichment;
}

export function BookingCard({ booking, enrichment }: BookingCardProps) {
  const dispatch = useAppDispatch();
  const { user } = useAppSelector((state) => state.auth);
  const actionLoadingId = useAppSelector((state) => state.booking.actionLoadingId);

  if (!user) {
    return null;
  }

  const isProvider = user.id === booking.providerId;
  const isSeeker = user.id === booking.seekerId;
  const isLoading = actionLoadingId === booking.id;

  async function runAction(action: ReturnType<typeof acceptBooking>) {
    try {
      await dispatch(action).unwrap();
      dispatch(fetchMyBookings());
    } catch {
      // error stored in slice
    }
  }

  const canAccept = isProvider && booking.status === 'REQUESTED';
  const canStart = isProvider && booking.status === 'ACCEPTED';
  const canComplete = isProvider && booking.status === 'IN_PROGRESS';
  const canCancel =
    (isSeeker || isProvider) &&
    booking.status !== 'COMPLETED' &&
    booking.status !== 'CANCELLED';

  return (
    <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <h3 className="font-semibold text-slate-900">
            {enrichment?.listingTitle ?? `Booking ${booking.id.slice(0, 8)}…`}
          </h3>
          <p className="mt-1 text-sm text-slate-500">
            Scheduled: {formatBookingDate(booking.scheduledAt)}
          </p>
        </div>
        <span
          className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${BOOKING_STATUS_STYLES[booking.status]}`}
        >
          {BOOKING_STATUS_LABELS[booking.status]}
        </span>
      </div>

      <dl className="mt-4 grid gap-2 text-sm sm:grid-cols-2">
        <div>
          <dt className="text-slate-500">Seeker</dt>
          <dd className="font-medium text-slate-800">{enrichment?.seekerName ?? '—'}</dd>
        </div>
        <div>
          <dt className="text-slate-500">Provider</dt>
          <dd className="font-medium text-slate-800">{enrichment?.providerName ?? '—'}</dd>
        </div>
        {enrichment?.paymentStatus && (
          <div>
            <dt className="text-slate-500">Payment</dt>
            <dd className="font-medium capitalize text-slate-800">
              {enrichment.paymentStatus.toLowerCase()}
            </dd>
          </div>
        )}
      </dl>

      {booking.notes && (
        <p className="mt-3 rounded-lg bg-slate-50 px-3 py-2 text-sm text-slate-600">
          <span className="font-medium text-slate-700">Notes:</span> {booking.notes}
        </p>
      )}

      {(canAccept || canStart || canComplete || canCancel) && (
        <div className="mt-4 flex flex-wrap gap-2 border-t border-slate-100 pt-4">
          {canAccept && (
            <ActionButton
              label="Accept"
              variant="primary"
              loading={isLoading}
              onClick={() => runAction(acceptBooking(booking.id))}
            />
          )}
          {canStart && (
            <ActionButton
              label="Start work"
              variant="primary"
              loading={isLoading}
              onClick={() => runAction(startBooking(booking.id))}
            />
          )}
          {canComplete && (
            <ActionButton
              label="Mark complete"
              variant="primary"
              loading={isLoading}
              onClick={() => runAction(completeBooking(booking.id))}
            />
          )}
          {canCancel && (
            <ActionButton
              label="Cancel"
              variant="secondary"
              loading={isLoading}
              onClick={() => runAction(cancelBooking(booking.id))}
            />
          )}
        </div>
      )}
    </article>
  );
}

function ActionButton({
  label,
  variant,
  loading,
  onClick,
}: {
  label: string;
  variant: 'primary' | 'secondary';
  loading: boolean;
  onClick: () => void;
}) {
  const base = 'rounded-lg px-3 py-1.5 text-sm font-medium disabled:opacity-50';
  const styles =
    variant === 'primary'
      ? `${base} bg-brand-600 text-white hover:bg-brand-700`
      : `${base} border border-slate-300 text-slate-700 hover:bg-slate-50`;

  return (
    <button type="button" disabled={loading} onClick={onClick} className={styles}>
      {loading ? '…' : label}
    </button>
  );
}
