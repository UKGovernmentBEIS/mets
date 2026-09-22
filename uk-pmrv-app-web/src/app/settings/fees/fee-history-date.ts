/**
 * Formats a fee history record's timestamp as "DD Mon YYYY, h:mmam/pm" (e.g. "21 Jun 2026, 12:20pm"),
 * matching the Figma mockup rendering rather than METS-2965's AC text (short month, no space before
 * am/pm). Not built on GovukDatePipe: its combined Intl.DateTimeFormat changes the en-GB date/time
 * separator depending on month length, so date and time are formatted independently here instead.
 */
export function formatFeeHistoryDateTime(createdAt: string): string {
  const date = new Date(createdAt);
  if (isNaN(date.getTime())) {
    return '';
  }

  const datePart = Intl.DateTimeFormat('en-GB', {
    timeZone: 'Europe/London',
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(date);

  const timePart = Intl.DateTimeFormat('en-GB-u-hc-h12', {
    timeZone: 'Europe/London',
    hour: 'numeric',
    minute: '2-digit',
  })
    .formatToParts(date)
    .map((part) => (part.type === 'literal' && part.value.trim() === '' ? '' : part.value))
    .join('');

  return `${datePart}, ${timePart}`;
}
