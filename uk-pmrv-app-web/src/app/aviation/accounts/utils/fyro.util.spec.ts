import { FYRO_MIN_YEAR, getFyroLabel, getFyroRangeErrorMessage } from './fyro.util';

describe('fyro.util', () => {
  describe('FYRO_MIN_YEAR', () => {
    it('should have the CORSIA and UK ETS minimum years', () => {
      expect(FYRO_MIN_YEAR.CORSIA).toBe(2019);
      expect(FYRO_MIN_YEAR.UK_ETS_AVIATION).toBe(2021);
    });
  });

  describe('getFyroLabel', () => {
    it('should return the CORSIA label', () => {
      expect(getFyroLabel('CORSIA')).toEqual('First year within the scope of applicability');
    });

    it('should return the UK ETS label for UK ETS and unset schemes', () => {
      expect(getFyroLabel('UK_ETS_AVIATION')).toEqual('First year of reporting obligation');
      expect(getFyroLabel(null)).toEqual('First year of reporting obligation');
      expect(getFyroLabel(undefined)).toEqual('First year of reporting obligation');
    });
  });

  describe('getFyroRangeErrorMessage', () => {
    it('should return the CORSIA account-open message', () => {
      expect(getFyroRangeErrorMessage('CORSIA', false)).toEqual(
        'The year must be the same as or after 2019 and it cannot be later than the current year',
      );
    });

    it('should return the UK ETS account-open message', () => {
      expect(getFyroRangeErrorMessage('UK_ETS_AVIATION', false)).toEqual(
        'The year must be the same as or after 2021 and it cannot be later than the current year',
      );
    });

    it('should return the CORSIA account-details message', () => {
      expect(getFyroRangeErrorMessage('CORSIA', true)).toEqual(
        'The year must be the same as or after 2019 and it cannot be later than previously set',
      );
    });

    it('should return the UK ETS account-details message', () => {
      expect(getFyroRangeErrorMessage('UK_ETS_AVIATION', true)).toEqual(
        'The year must be the same as or after 2021 and it cannot be later than previously set',
      );
    });
  });
});
