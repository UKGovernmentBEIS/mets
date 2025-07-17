import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { commentsResponseWizardComplete } from '@tasks/air/comments-response/comments-response.wizard';
import { AirService } from '@tasks/air/shared/services/air.service';

import { AirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

@Injectable()
export class SummaryGuard {
  constructor(
    private readonly router: Router,
    private readonly airService: AirService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return combineLatest([
      this.airService.payload$ as Observable<AirApplicationRespondToRegulatorCommentsRequestTaskPayload>,
      this.airService.isEditable$,
    ]).pipe(
      map(([payload, isEditable]) => {
        return (
          !isEditable ||
          commentsResponseWizardComplete(route.paramMap.get('id'), payload) ||
          this.router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/air/comments-response/${route.paramMap.get('id')}/operator-followup`,
          )
        );
      }),
    );
  }
}
