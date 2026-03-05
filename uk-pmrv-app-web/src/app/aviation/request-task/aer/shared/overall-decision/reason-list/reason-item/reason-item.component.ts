import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { overallDecisionQuery } from '@aviation/request-task/aer/shared/overall-decision/overall-decision.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerVerifiedSatisfactoryWithCommentsDecision } from 'pmrv-api';

import { OverallDecisionFormProvider } from '../../overall-decision-form.provider';
import { AER_VERIFY_TASK_FORM, reasonItemFormProvider } from './reason-item-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-reason-item',
  templateUrl: './reason-item.component.html',
  providers: [DestroySubject, reasonItemFormProvider],
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReasonItemComponent {
  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectIsEditable)]).pipe(
    map(([isEditable]) => ({
      pageHeader: 'What is your assessment of this report?',
      isEditable,
    })),
  );

  constructor(
    @Inject(AER_VERIFY_TASK_FORM) readonly form: UntypedFormGroup,
    @Inject(TASK_FORM_PROVIDER) readonly parentFormProvider: OverallDecisionFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      let decisionValue: AviationAerVerifiedSatisfactoryWithCommentsDecision;
      combineLatest([this.store.pipe(first(), overallDecisionQuery.selectOverallDecision), this.route.paramMap])
        .pipe(
          first(),
          switchMap(([overallDecision, paramMap]) => {
            const index = Number(paramMap.get('index'));
            const overallAssessmentInfo = overallDecision as AviationAerVerifiedSatisfactoryWithCommentsDecision;

            decisionValue = {
              ...overallAssessmentInfo,
              reasons:
                index >= (overallAssessmentInfo?.reasons?.length ?? 0)
                  ? [...(overallAssessmentInfo?.reasons ?? []), this.form.get('reason').value]
                  : overallAssessmentInfo.reasons.map((item, idx) =>
                      idx === index ? this.form.get('reason').value : item,
                    ),
            };

            return this.store.aerVerifyDelegate.saveAerVerify(
              {
                overallDecision: decisionValue,
              },
              'in progress',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.parentFormProvider.setFormValue({
            ...decisionValue,
          });
          this.nextUrl();
        });
    }
  }

  private nextUrl() {
    return this.router.navigate(['..'], { relativeTo: this.route, queryParams: { change: true } });
  }
}
