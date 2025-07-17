import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerAmendGroup, AerAmendGroupStatusKey } from '../../../core/aer.amend.types';
import { AerService } from '../../../core/aer.service';
import { TaskStatusPipe } from '../../../shared/pipes/task-status.pipe';

@Injectable({
  providedIn: 'root',
})
export class AmendSummaryGuard {
  constructor(
    private readonly router: Router,
    private readonly aerService: AerService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<UrlTree | boolean> {
    const amendGroup = route.paramMap.get('section') as AerAmendGroup;
    const taskStatusPipe = new TaskStatusPipe(this.aerService);

    return taskStatusPipe.transform(('AMEND_' + amendGroup) as AerAmendGroupStatusKey).pipe(
      first(),
      map((taskStatus) => {
        return (
          taskStatus === 'complete' ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/aer/submit/amend/${amendGroup}`)
        );
      }),
    );
  }
}
