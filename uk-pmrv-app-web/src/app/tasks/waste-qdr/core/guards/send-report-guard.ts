import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { wasteQdrSubmitWizardComplete } from '@tasks/waste-qdr/utils';

import { WasteQDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { WasteQdrService } from '../waste-qdr.service';

@Injectable({
  providedIn: 'root',
})
export class SendReportGuard {
  constructor(
    private readonly wasteQdrService: WasteQdrService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.wasteQdrService.getPayload().pipe(
      map((payload) => payload as WasteQDRApplicationSubmitRequestTaskPayload),
      map((payload) => {
        return (
          wasteQdrSubmitWizardComplete(payload) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/waste-qdr/submit/send-report`)
        );
      }),
    );
  }
}
