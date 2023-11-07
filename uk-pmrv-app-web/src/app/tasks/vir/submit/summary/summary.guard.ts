import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { VirService } from '@tasks/vir/core/vir.service';
import { operatorImprovementResponseComplete } from '@tasks/vir/submit/submit.wizard';

@Injectable({
  providedIn: 'root',
})
export class SummaryGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly virService: VirService) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.virService.payload$.pipe(
      map((payload) => {
        return (
          operatorImprovementResponseComplete(route.paramMap.get('id'), payload?.operatorImprovementResponses) ||
          this.router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/vir/submit/${route.paramMap.get('id')}/recommendation-response`,
          )
        );
      }),
    );
  }
}
