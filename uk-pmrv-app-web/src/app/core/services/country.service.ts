import { Injectable } from '@angular/core';

import { first, from, map, mergeMap, Observable, shareReplay } from 'rxjs';

import { ReferenceDataService } from 'pmrv-api';

import { Country } from '../models/country';

@Injectable({ providedIn: 'root' })
export class CountryService {
  private countries$: Observable<Country[]> = this.referenceDataService.getReferenceData(['COUNTRIES']).pipe(
    map((response) => response.COUNTRIES as Country[]),
    shareReplay({ bufferSize: 1, refCount: false }),
  );

  constructor(private readonly referenceDataService: ReferenceDataService) {}

  getUkCountries(): Observable<Country[]> {
    return this.countries$;
  }

  getCountry(code: string): Observable<Country> {
    return this.countries$.pipe(
      mergeMap((countries) => from(countries)),
      first((country) => country.code === code),
    );
  }

  getCountryCode(name: string): Observable<Country['code']> {
    return this.countries$.pipe(
      mergeMap((countries) => from(countries)),
      first((country) => country.name === name),
      map((country) => country.code),
    );
  }
}
