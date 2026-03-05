import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { reviewRequestTaskTypes } from '../../../shared/utils/permit';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { heading } from './heading';
import { procedureFormProvider } from './procedure-form.provider';

@Component({
  selector: 'app-procedure',
  templateUrl: './procedure.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [procedureFormProvider],
})
export class ProcedureComponent {
  taskKey$: Observable<string> = this.route.data.pipe(map((x) => x?.taskKey));
  heading = heading;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postTask(data.taskKey, this.form.value, true, data.statusKey)),
        this.pendingRequest.trackRequest(),
        switchMap(() => this.store),
        first(),
      )
      .subscribe((state) =>
        this.router.navigate(
          [reviewRequestTaskTypes.includes(state.requestTaskType) ? '../../review/nitrous-oxide' : 'summary'],
          { relativeTo: this.route, state: { notification: true } },
        ),
      );
  }
}
