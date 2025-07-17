import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, take } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { activationDateFormProvider } from './activation-date-form.provider';

@Component({
  selector: 'app-reason',
  template: `
    <app-permit-task [breadcrumb]="true">
      <app-wizard-step
        [showBackLink]="true"
        (formSubmit)="onContinue()"
        [formGroup]="form"
        submitText="Continue"
        [hideSubmit]="(store.isEditable$ | async) === false">
        <span class="govuk-caption-l">{{ determination$ | async }}</span>
        <app-page-heading>Set a date for the {{ permitType | permitRequestType }} to become active</app-page-heading>
        <div class="govuk-hint">For example 27.3.2022</div>
        <div formControlName="activationDate" govuk-date-input [isRequired]="true"></div>
      </app-wizard-step>
      <a govukLink routerLink="../..">Return to: {{ determinationHeader }}</a>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [activationDateFormProvider],
})
export class ActivationDateComponent implements PendingRequest {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  permitType = this.store.getValue().requestType;
  determination$ = this.store.getDeterminationType$();
  determinationHeader = this.store.getDeterminationHeader();

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.store
        .pipe(
          first(),
          map((store) => this.getNextStepUrl(store)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    } else {
      const activationDate = this.form.value.activationDate;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postDetermination(
              {
                ...state.determination,
                activationDate,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
          switchMap(() => this.store),
          take(1),
          map((store) => this.getNextStepUrl(store)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    }
  }
  private getNextStepUrl(state: PermitApplicationState): string {
    return state.permitType === 'HSE'
      ? 'emissions'
      : state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_REVIEW'
        ? 'log-changes'
        : state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'
          ? 'reason-template'
          : 'answers';
  }
}
