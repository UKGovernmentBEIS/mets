import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { reviewRequestTaskTypes } from '../../../shared/utils/permit';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { descriptionFormProvider } from './description-form.provider';

@Component({
  selector: 'app-description',
  templateUrl: './description.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [descriptionFormProvider],
})
export class DescriptionComponent {
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
        switchMap((data) => this.store.patchTask(data.taskKey, this.form.value, true, data.statusKey)),
        this.pendingRequest.trackRequest(),
        switchMap(() => this.store),
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
