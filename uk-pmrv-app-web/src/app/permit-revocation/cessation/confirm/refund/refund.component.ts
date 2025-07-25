import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import {
  PERMIT_REVOCATION_CESSATION_TASK_FORM,
  permitRevocationCessationFormProvider,
} from '@permit-revocation/cessation/confirm/core/factory/cessation-form-provider';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PermitRevocationStore } from '../../../store/permit-revocation-store';

@Component({
  selector: 'app-revocation-cessation-refund',
  template: `
    <app-wizard-step
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(store.isEditable$ | async) === false">
      <span class="govuk-caption-l">Confirm cessation of regulated activities</span>

      <app-page-heading>Should the installations subsistence fee be refunded?</app-page-heading>

      <div
        govuk-radio
        formControlName="subsistenceFeeRefunded"
        hint="The refund will take place outside of the UK ETS reporting service">
        <govuk-radio-option label="Yes" [value]="true"></govuk-radio-option>
        <govuk-radio-option label="No" [value]="false"></govuk-radio-option>
      </div>
    </app-wizard-step>
    <a govukLink routerLink="../..">Return to: Cessation</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [permitRevocationCessationFormProvider],
})
export class RefundComponent implements PendingRequest, OnInit {
  constructor(
    @Inject(PERMIT_REVOCATION_CESSATION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitRevocationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  onContinue(): void {
    const navigateToNextStep = () => this.router.navigate(['../notice'], { relativeTo: this.route });
    if (!this.form.dirty) {
      navigateToNextStep();
    } else {
      const subsistenceFeeRefunded = this.form.value.subsistenceFeeRefunded;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postSaveCessation(
              {
                ...state.cessation,
                subsistenceFeeRefunded,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => navigateToNextStep());
    }
  }

  ngOnInit(): void {
    this.breadcrumbService.addToLastBreadcrumbAndShow('cessation');
  }
}
