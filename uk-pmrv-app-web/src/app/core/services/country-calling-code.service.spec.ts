import { TestBed } from '@angular/core/testing';

import { CountryCallingCodeService } from '@core/services/country-calling-code.service';

describe('CountryCallingCodeService', () => {
  let service: CountryCallingCodeService;

  beforeEach(() => {
    service = TestBed.inject(CountryCallingCodeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should handle missing codes', () => {
    expect(service.getCountryCallingCode('GB-ENG')).toEqual(44);
  });
});
