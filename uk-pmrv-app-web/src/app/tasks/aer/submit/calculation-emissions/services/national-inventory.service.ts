import { Injectable } from '@angular/core';

import { first, Observable, shareReplay, switchMap } from 'rxjs';

import { NationalInventoryDataDTO, ReportingDataService } from 'pmrv-api';

import { AerService } from '../../../core/aer.service';

@Injectable({
  providedIn: 'root',
})
export class NationalInventoryService {
  cachedNationalInventoryData$: Observable<NationalInventoryDataDTO>;

  get nationalInventoryData$() {
    if (!this.cachedNationalInventoryData$) {
      this.cachedNationalInventoryData$ = this.getNationalInventoryData().pipe(shareReplay(1));
    }
    return this.cachedNationalInventoryData$;
  }

  constructor(private readonly reportingDataService: ReportingDataService, private readonly aerService: AerService) {}

  private getNationalInventoryData(): Observable<NationalInventoryDataDTO> {
    return this.aerService.getPayload().pipe(
      first(),
      switchMap((payload) => this.reportingDataService.getNationalInventoryData(payload?.reportingYear)),
    );
  }
}
