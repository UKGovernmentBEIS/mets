import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { TransportCO2AndN2OPipelineSystems } from 'pmrv-api';

import { TaskKey } from '../../../../shared/types/permit-task.type';
import { headingMap } from '../../heading';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnswersComponent {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  taskKey$: Observable<TaskKey> = this.route.data.pipe(map((x) => x?.taskKey));
  transportCO2AndN2OPipelineSystems$ = this.taskKey$.pipe(
    switchMap((taskKey) => this.store.findTask<TransportCO2AndN2OPipelineSystems>(taskKey)),
  );
  headingMap = headingMap;
  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  confirm(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postStatus(data.statusKey, true)),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }

  changeClick(): void {
    this.router.navigate(['../'], { relativeTo: this.route, state: { changing: true } });
  }
}
