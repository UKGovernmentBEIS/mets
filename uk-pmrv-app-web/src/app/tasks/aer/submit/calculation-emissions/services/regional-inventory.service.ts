import { Injectable } from '@angular/core';

import { first, Observable, of, shareReplay, switchMap } from 'rxjs';

import { RegionalInventoryEmissionCalculationParamsDTO, ReportingDataService } from 'pmrv-api';

import { AerService } from '../../../core/aer.service';

@Injectable({
  providedIn: 'root',
})
export class RegionalInventoryService {
  constructor(private readonly reportingDataService: ReportingDataService, private readonly aerService: AerService) {}

  getRegionalInventoryData(localZoneCode): Observable<RegionalInventoryEmissionCalculationParamsDTO> {
    return this.aerService.getPayload().pipe(
      first(),
      switchMap((payload) =>
        localZoneCode
          ? this.reportingDataService.getRegionalInventoryEmissionCalculationParams(
              payload?.reportingYear,
              localZoneCode,
            )
          : of({}),
      ),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }
}
