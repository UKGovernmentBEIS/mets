import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { areEmissionSummariesUsingEverything, areEmissionSummariesValid } from '../emission-summaries-status';

@Injectable()
export class EmissionSummariesSummaryGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return combineLatest([this.store.getTask('emissionSummaries'), this.store]).pipe(
      first(),
      map(
        ([emissionSummaries, state]) =>
          (emissionSummaries.length > 0 &&
            areEmissionSummariesValid(state) &&
            areEmissionSummariesUsingEverything(state) &&
            state.permitSectionsCompleted?.emissionSummaries?.[0]) ||
          this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/emission-summaries`),
      ),
    );
  }
}
