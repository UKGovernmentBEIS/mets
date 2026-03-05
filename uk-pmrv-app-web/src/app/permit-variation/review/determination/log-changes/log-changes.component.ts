import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../permit-application/shared/permit-task-form.token';
import { PermitVariationStore } from '../../../store/permit-variation.store';
import { logChangesFormProvider } from './log-changes-form.provider';

@Component({
  selector: 'app-log-changes',
  template: `
    <app-permit-task [breadcrumb]="true">
      <app-wizard-step
        [showBackLink]="true"
        (formSubmit)="onContinue()"
        [formGroup]="form"
        submitText="Continue"
        [hideSubmit]="(store.isEditable$ | async) === false">
        <span class="govuk-caption-l">{{ determination$ | async }}</span>

        <app-page-heading>Enter a summary of the changes for the permit variation log</app-page-heading>
        <div class="govuk-hint">The operator will see this in the approved permit document.</div>
        <div class="govuk-hint">Enter a summary for all items that were added to the variation schedule.</div>

        <div formControlName="logChanges" govuk-textarea [maxLength]="10000"></div>
      </app-wizard-step>

      <a govukLink routerLink="../..">Return to: Permit variation</a>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [logChangesFormProvider],
})
export class LogChangesComponent implements PendingRequest {
  determination$ = this.store.getDeterminationType$();
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitVariationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      const logChanges = this.form.value.logChanges;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postDetermination(
              {
                ...state.determination,
                logChanges,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }
}
