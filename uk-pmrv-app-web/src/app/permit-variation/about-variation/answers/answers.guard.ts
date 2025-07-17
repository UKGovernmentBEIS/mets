import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitVariationStore } from '../../store/permit-variation.store';

@Injectable({
  providedIn: 'root',
})
export class AnswersGuard {
  constructor(
    private readonly router: Router,
    private readonly store: PermitVariationStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const taskId = route.paramMap.get('taskId');
          const wizardBaseUrl = `/permit-variation/${taskId}/about`;

          const permitVariationDetails = permitState?.permitVariationDetails;

          return (
            (permitState.permitVariationDetailsCompleted && this.router.parseUrl(wizardBaseUrl.concat('/summary'))) ||
            !!permitVariationDetails ||
            this.router.parseUrl(wizardBaseUrl)
          );
        }),
      )
    );
  }
}
