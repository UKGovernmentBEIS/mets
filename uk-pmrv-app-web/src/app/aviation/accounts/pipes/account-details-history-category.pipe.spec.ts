import { AviationAccoundDetailsHistoryCategoryPipe } from './account-details-history-category.pipe';

describe('AviationAccoundDetailsHistoryCategoryPipe', () => {
  it('create an instance', () => {
    const pipe = new AviationAccoundDetailsHistoryCategoryPipe();
    expect(pipe).toBeTruthy();
  });

  it('should return correct value for valid reporting status', () => {
    const pipe = new AviationAccoundDetailsHistoryCategoryPipe();
    expect(pipe.transform('FIRST_YEAR_OF_REPORTING_OBLIGATION')).toEqual('First year of reporting obligation');
  });
});
