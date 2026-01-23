import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BdrS2Service } from '@tasks/bdrs2/core';
import { baselineComplete } from '@tasks/bdrs2/utils';

@Injectable({ providedIn: 'root' })
export class BDRS2BaselineStepGuard {
  constructor(
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      (this.bdrs2Service.payload$ as Observable<any>).pipe(
        map((payload) => {
          return (
            !baselineComplete(payload) ||
            this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/bdrs2/submit/baseline/summary`)
          );
        }),
      )
    );
  }
}
