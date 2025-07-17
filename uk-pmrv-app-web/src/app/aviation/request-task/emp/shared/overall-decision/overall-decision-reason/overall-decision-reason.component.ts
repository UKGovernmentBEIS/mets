import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { EmpDetermination } from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { EmpReviewDeterminationTypePipe } from '@aviation/shared/pipes/review-determination-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { OverallDecisionFormProvider } from '../overall-decision-form.provider';

@Component({
  selector: 'app-overall-decision-reason-page',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    EmpReviewDeterminationTypePipe,
  ],
  template: `
    <app-wizard-step
      [showBackLink]="true"
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(isEditable$ | async) === false">
      <span class="govuk-caption-l">{{ form.value.type | empReviewDeterminationType }}</span>

      <app-page-heading>
        Provide a reason for your decision
        <span *ngIf="form.value.type === 'APPROVED'">(optional)</span>
      </app-page-heading>
      <div class="govuk-hint">This cannot be viewed by the operator</div>

      <div formControlName="reason" govuk-textarea [maxLength]="10000"></div>
    </app-wizard-step>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionReasonComponent {
  form = this.formProvider.form;
  isEditable$ = this.store.pipe(requestTaskQuery.selectIsEditable);

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OverallDecisionFormProvider,
    public store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onContinue() {
    this.store.empDelegate
      .saveEmpOverallDecision(this.form.value as EmpDetermination, false)
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['../', 'summary'], { relativeTo: this.route, state: { force: true } }));
  }
}
