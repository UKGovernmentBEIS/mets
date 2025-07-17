import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable, switchMap } from 'rxjs';

import { TransportCO2AndN2OPipelineSystems } from 'pmrv-api';

import { TaskKey } from '../../../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { headingMap } from '../../heading';

@Component({
  selector: 'app-transferred-co2-pipeline-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  @Input() isPreview: boolean;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  taskKey$: Observable<TaskKey> = this.route.data.pipe(map((x) => x?.taskKey));
  transportCO2AndN2OPipelineSystems$ = this.taskKey$.pipe(
    switchMap((taskKey) => this.store.findTask<TransportCO2AndN2OPipelineSystems>(taskKey)),
  );
  headingMap = headingMap;

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(): void {
    this.router.navigate(['../'], { relativeTo: this.route, state: { changing: true } });
  }
}
