import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerNotVerifiedDecision, AviationAerNotVerifiedDecisionReason } from 'pmrv-api';

import { OverallDecisionFormProvider } from '../overall-decision-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  form: FormGroup;
}

type NotVerifiedReasonType =
  | 'UNCORRECTED_MATERIAL_MISSTATEMENT'
  | 'UNCORRECTED_MATERIAL_NON_CONFORMITY'
  | 'VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS'
  | 'SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY'
  | 'SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN'
  | 'NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR'
  | 'ANOTHER_REASON';

@Component({
  selector: 'app-not-verified',
  templateUrl: './not-verified.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotVerifiedComponent {
  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectIsEditable)]).pipe(
    map(([isEditable]) => ({
      pageHeader: 'Why can you not verify the report?',
      isEditable,
      form: this.formProvider.notVerifiedCtrl,
    })),
  );
  form = this.formProvider.notVerifiedCtrl;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: OverallDecisionFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  getReasonValue(reason: NotVerifiedReasonType): AviationAerNotVerifiedDecisionReason {
    if (reason === 'ANOTHER_REASON') {
      return { type: reason, details: this.form.get('otherDetails').value };
    } else if (reason === 'VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS') {
      return { type: reason, details: this.form.get('verificationDetails').value };
    } else if (reason === 'SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY') {
      return { type: reason, details: this.form.get('clarityDetails').value };
    } else if (reason === 'SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN') {
      return { type: reason, details: this.form.get('empLimitationDetails').value };
    } else if (reason === 'NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR') {
      return { type: reason, details: this.form.get('empNotApprovedDetails').value };
    } else {
      return { type: reason };
    }
  }

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      const decisionValue = {
        type: 'NOT_VERIFIED',
        notVerifiedReasons: this.form
          .get('type')
          .value.map((reason: NotVerifiedReasonType) => this.getReasonValue(reason)),
      } as AviationAerNotVerifiedDecision;

      this.store
        .pipe(
          first(),
          switchMap(() => {
            return this.store.aerVerifyDelegate.saveAerVerify(
              {
                overallDecision: decisionValue,
              },
              'in progress',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.nextUrl());
    }
  }

  private nextUrl() {
    return this.router.navigate(['../summary'], { relativeTo: this.route });
  }
}
