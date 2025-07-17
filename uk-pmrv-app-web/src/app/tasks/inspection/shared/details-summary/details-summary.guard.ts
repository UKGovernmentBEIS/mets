import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { InspectionSubmitRequestTaskPayload } from '@tasks/inspection/shared/inspection';

import { isInstallationInspectionDetailsSubmitCompleted } from '../../submit/submit.wizard';

export function detailsSummaryGuard(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
  const router = inject(Router);
  const inspectionService = inject(InspectionService);

  return inspectionService.payload$.pipe(
    map((payload: InspectionSubmitRequestTaskPayload) => {
      return isInstallationInspectionDetailsSubmitCompleted(payload?.installationInspection)
        ? true
        : router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/inspection/${route.paramMap.get('type')}/submit/details`,
          );
    }),
  );
}
