import { Injectable } from '@angular/core';

import { combineLatest, map, Observable, of, switchMap, tap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthStore } from '@core/store';
import { LatestTermsStore } from '@core/store/latest-terms/latest-terms.store';

import { TermsAndConditionsService, TermsDTO } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class LatestTermsService {
  constructor(
    private readonly latestTermsStore: LatestTermsStore,
    private readonly configStore: ConfigStore,
    private readonly authStore: AuthStore,
    private readonly termsAndConditionsService: TermsAndConditionsService,
  ) {}

  initLatestTerms(): Observable<TermsDTO> {
    return combineLatest([this.configStore.pipe(selectIsFeatureEnabled('terms')), this.authStore]).pipe(
      switchMap(([termsEnabled, authState]) =>
        termsEnabled && authState.isLoggedIn ? this.termsAndConditionsService.getLatestTerms() : of(null),
      ),
      tap((latestTerms) => this.latestTermsStore.setLatestTerms(latestTerms)),
      map(() => this.latestTermsStore.getState()),
    );
  }
}
