import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BdrService } from '@tasks/bdr/shared/services/bdr.service';

import { baselineComplete } from '../submit.wizard';

@Injectable({ providedIn: 'root' })
export class BaselineStepGuard {
  constructor(
    private readonly bdrService: BdrService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      (this.bdrService.payload$ as Observable<any>).pipe(
        map((payload) => {
          return (
            !baselineComplete(payload) ||
            this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/bdr/submit/baseline/summary`)
          );
        }),
      )
    );
  }
}
