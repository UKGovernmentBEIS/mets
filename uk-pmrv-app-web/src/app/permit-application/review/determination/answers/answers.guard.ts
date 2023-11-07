import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Injectable()
export class AnswersGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        const wizardUrl = `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/review/determination`;

        return (
          (storeState.reviewSectionsCompleted?.[route.data.statusKey] &&
            this.router.parseUrl(wizardUrl.concat('/summary'))) ||
          this.store.isDeterminationWizardComplete() ||
          this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/review/determination`)
        );
      }),
    );
  }
}
