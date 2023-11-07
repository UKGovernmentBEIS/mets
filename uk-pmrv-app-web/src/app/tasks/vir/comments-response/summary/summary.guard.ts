import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { commentsResponseWizardComplete } from '@tasks/vir/comments-response/comments-response.wizard';
import { VirService } from '@tasks/vir/core/vir.service';

import { VirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class SummaryGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly virService: VirService) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return combineLatest([
      this.virService.payload$ as Observable<VirApplicationRespondToRegulatorCommentsRequestTaskPayload>,
      this.virService.isEditable$,
    ]).pipe(
      map(([payload, isEditable]) => {
        return (
          !isEditable ||
          commentsResponseWizardComplete(route.paramMap.get('id'), payload) ||
          this.router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/vir/comments-response/${route.paramMap.get('id')}/operator-followup`,
          )
        );
      }),
    );
  }
}
