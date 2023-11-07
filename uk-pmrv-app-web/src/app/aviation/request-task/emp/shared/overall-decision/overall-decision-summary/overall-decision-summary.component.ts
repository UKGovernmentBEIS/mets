import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSubtaskSummaryValues, overallDecisionConfirmUrlMapper } from '@aviation/request-task/util';
import { OverallDecisionSummaryTemplateComponent } from '@aviation/shared/components/emp/overall-decision-summary-template/overall-decision-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmpIssuanceDetermination, ItemDTO } from 'pmrv-api';

import { getVariationScheduleItems } from '../../util/emp.util';
import { OverallDecisionFormProvider } from '../overall-decision-form.provider';

interface ViewModel {
  data: EmpIssuanceDetermination;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  variationScheduleItems: string[];
}

@Component({
  selector: 'app-overall-decision-summary-page',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, OverallDecisionSummaryTemplateComponent],
  templateUrl: './overall-decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionSummaryComponent {
  form = this.formProvider.form;
  requestInfoType: ItemDTO['requestType'];

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectReviewSectionsCompleted),
    this.store.pipe(empQuery.selectVariationReviewDecisions),
    this.store.pipe(empQuery.selectVariationDetailsReviewDecisions),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
  ]).pipe(
    map(([isEditable, sectionsCompleted, reviewDecision, varDetailsReviewDecision, requestInfo]) => {
      this.requestInfoType = requestInfo.type;

      return {
        data: getSubtaskSummaryValues(this.formProvider.form),
        pageHeader: 'Check your answers',
        isEditable,
        hideSubmit: !isEditable || (sectionsCompleted['decision'] && this.form.valid),
        variationScheduleItems: getVariationScheduleItems(reviewDecision, varDetailsReviewDecision),
      } as ViewModel;
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OverallDecisionFormProvider,
    private readonly store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form?.valid) {
      this.store.empDelegate
        .saveEmpOverallDecision(this.form.value as EmpIssuanceDetermination, true)
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() =>
          this.router.navigate(overallDecisionConfirmUrlMapper[this.requestInfoType], { relativeTo: this.route }),
        );
    }
  }
}
