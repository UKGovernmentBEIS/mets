import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { WithholdingAllowancesService } from '../../core/withholding-allowances.service';
import { isWizardComplete } from '../submit';

@Injectable({
  providedIn: 'root',
})
export class SummaryGuard {
  constructor(
    private readonly router: Router,
    private readonly withholdingAllowancesService: WithholdingAllowancesService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.withholdingAllowancesService.payload$.pipe(
      map((payload) => {
        return (
          isWizardComplete(payload) ||
          this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/withholding-allowances/submit/recovery-details`)
        );
      }),
    );
  }
}
