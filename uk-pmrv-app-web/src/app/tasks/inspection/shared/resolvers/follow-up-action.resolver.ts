import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, Observable } from 'rxjs';

import { InspectionService } from '@tasks/inspection/core/inspection.service';

import { FollowUpAction } from 'pmrv-api';

import { InspectionSubmitRequestTaskPayload } from '../inspection';

@Injectable()
export class InspectionItemResolver {
  constructor(private readonly inspectionService: InspectionService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<FollowUpAction> {
    return this.inspectionService.payload$.pipe(
      map((payload) => {
        const followUpAction = (payload as InspectionSubmitRequestTaskPayload)?.installationInspection
          ?.followUpActions?.[route.paramMap.get('id')];

        return followUpAction || null;
      }),
    );
  }
}
