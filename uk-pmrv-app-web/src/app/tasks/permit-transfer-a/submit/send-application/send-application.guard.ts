import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitTransferAService } from '../../core/permit-transfer-a.service';
import { getSectionStatus } from '../../core/permit-transfer-a-task-statuses';

@Injectable({
  providedIn: 'root',
})
export class SendApplicationGuard {
  constructor(
    private readonly permitTransferAService: PermitTransferAService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.permitTransferAService.getPayload().pipe(
      first(),
      map((payload) => {
        const sectionStatus = getSectionStatus(payload);
        const baseUrl = `tasks/${route.paramMap.get('taskId')}/permit-transfer-a/submit`;

        return sectionStatus === 'complete' || this.router.parseUrl(baseUrl);
      }),
    );
  }
}
