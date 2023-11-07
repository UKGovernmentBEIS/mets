import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, pluck, switchMap, switchMapTo } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { reviewRequestTaskTypes } from '../../../shared/utils/permit';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { headingMap } from '../heading';
import { optionalFormProvider } from './optional-form.provider';

@Component({
  selector: 'app-optional',
  templateUrl: './optional.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [optionalFormProvider],
})
export class OptionalComponent {
  taskKey$ = this.route.data.pipe(pluck('taskKey'));
  headingMap = headingMap;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postTask(data.taskKey, this.form.value, true, data.statusKey)),
        this.pendingRequest.trackRequest(),
        switchMapTo(this.store),
        first(),
      )
      .subscribe((state) =>
        this.router.navigate(
          [reviewRequestTaskTypes.includes(state.requestTaskType) ? '../../review/measurement' : 'summary'],
          { relativeTo: this.route, state: { notification: true } },
        ),
      );
  }
}
