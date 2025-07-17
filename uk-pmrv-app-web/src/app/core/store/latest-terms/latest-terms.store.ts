import { Injectable } from '@angular/core';

import { Store } from '@core/store';

import { TermsDTO } from 'pmrv-api';

import { initialState } from './latest-terms.state';

@Injectable({ providedIn: 'root' })
export class LatestTermsStore extends Store<TermsDTO> {
  constructor() {
    super(initialState);
  }

  setLatestTerms(latestTerms: TermsDTO) {
    this.setState(latestTerms);
  }
}
