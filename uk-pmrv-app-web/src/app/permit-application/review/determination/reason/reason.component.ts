import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, take, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { reasonFormProvider } from './reason-form.provider';

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

        <app-page-heading>{{ header$ | async }}</app-page-heading>
        <div class="govuk-hint">This cannot be viewed by the operator</div>

        <div formControlName="reason" govuk-textarea [maxLength]="10000"></div>
      </app-wizard-step>
      <a govukLink routerLink="../..">Return to: {{ determinationHeader }}</a>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reasonFormProvider],
})
export class ReasonComponent implements PendingRequest, OnInit {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  determination$ = this.store.getDeterminationType$();
  determinationHeader = this.store.getDeterminationHeader();

  header$ = this.store.pipe(
    map((state) =>
      !this.store.isDeterminationTypeApplicable() || state.determination?.type === 'GRANTED'
        ? 'Provide a reason to support your decision'
        : state.determination?.type === 'REJECTED'
          ? 'Provide a reason to support the rejection decision'
          : 'Provide a reason for the application withdrawal',
    ),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        map((state) =>
          !this.store.isDeterminationTypeApplicable() || state.determination?.type !== 'DEEMED_WITHDRAWN'
            ? 'Enter a reason to support your decision.'
            : 'Enter a reason for the application withdrawal.',
        ),
        takeUntil(this.destroy$),
      )
      .subscribe((requiredErrorMessage) => {
        this.form
          .get('reason')
          .setValidators([
            GovukValidators.required(requiredErrorMessage),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ]);
        this.form.get('reason').updateValueAndValidity();
      });
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.store
        .pipe(
          first(),
          map((state) => this.getNextStepUrl(state)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    } else {
      const reason = this.form.value.reason;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postDetermination(
              {
                ...state.determination,
                reason,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
          switchMap(() => this.store),
          take(1),
          map((state) => this.getNextStepUrl(state)),
        )
        .subscribe((url) => this.router.navigate([`../${url}`], { relativeTo: this.route }));
    }
  }

  private getNextStepUrl(state: PermitApplicationState): string {
    return !this.store.isDeterminationTypeApplicable() || state?.determination?.type === 'GRANTED'
      ? 'activation-date'
      : state?.determination?.type === 'REJECTED'
        ? 'official-notice'
        : 'answers';
  }
}
