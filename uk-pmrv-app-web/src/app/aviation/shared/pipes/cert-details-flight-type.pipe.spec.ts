import { CertDetailsFlightTypePipe } from '@aviation/shared/pipes/cert-details-flight-type.pipe';

describe('CertDetailsFlightTypePipe', () => {
  const pipe = new CertDetailsFlightTypePipe();

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform cert details flight types', () => {
    expect(pipe.transform('ALL_INTERNATIONAL_FLIGHTS')).toEqual('All international flights');
    expect(pipe.transform('INTERNATIONAL_FLIGHTS_WITH_NO_OFFSETTING_OBLIGATIONS')).toEqual(
      'Only for international flights with no offsetting obligations',
    );
    expect(pipe.transform('INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS')).toEqual(
      'Only for international flights with offsetting obligations',
    );

    expect(pipe.transform(null)).toEqual(null);
    expect(pipe.transform(undefined)).toEqual(null);
  });
});
