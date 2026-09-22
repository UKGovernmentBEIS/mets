import { formatFeeHistoryDateTime } from './fee-history-date';

describe('formatFeeHistoryDateTime', () => {
  it('formats the date/time per the Figma mockup (short month, no space before lowercase am/pm)', () => {
    expect(formatFeeHistoryDateTime('2026-06-21T11:20:00.000Z')).toEqual('21 Jun 2026, 12:20pm');
  });

  it('pads single-digit minutes', () => {
    expect(formatFeeHistoryDateTime('2026-06-21T11:05:00.000Z')).toEqual('21 Jun 2026, 12:05pm');
  });

  it('handles the am/pm boundary correctly', () => {
    expect(formatFeeHistoryDateTime('2026-06-21T11:00:00.000Z')).toEqual('21 Jun 2026, 12:00pm');
    expect(formatFeeHistoryDateTime('2026-06-20T23:00:00.000Z')).toEqual('21 Jun 2026, 12:00am');
  });

  it('returns an empty string for an invalid date', () => {
    expect(formatFeeHistoryDateTime('not-a-date')).toEqual('');
  });
});
