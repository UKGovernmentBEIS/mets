import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable, switchMap } from 'rxjs';

import { ProcedureForm } from 'pmrv-api';

import { DestroySubject } from '../../../../../core/services/destroy-subject.service';
import { TaskKey } from '../../../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { heading } from '../heading';

@Component({
  selector: 'app-summary',
  standalone: false,
  templateUrl: './summary.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  taskKey$: Observable<TaskKey> = this.route.data.pipe(map((x) => x?.taskKey));
  procedure$ = this.taskKey$.pipe(switchMap((taskKey) => this.store.findTask<ProcedureForm>(taskKey)));
  heading = heading;

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
