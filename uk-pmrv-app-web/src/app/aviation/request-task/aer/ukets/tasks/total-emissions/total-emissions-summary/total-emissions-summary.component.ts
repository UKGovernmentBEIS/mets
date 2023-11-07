import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { TotalEmissionsSchemeYearHeaderComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/shared/total-emissions-scheme-year-header';
import { TotalEmissionsStandardFuelsTableComponent } from '@aviation/request-task/aer/ukets/tasks/total-emissions/table/total-emissions-standard-fuels-table';
import { TotalEmissionsFormProvider } from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { TotalEmissionsSummaryTemplateComponent } from '@aviation/shared/components/aer/total-emissions/total-emissions-summary-template/total-emissions-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerTotalEmissionsConfidentiality } from 'pmrv-api';

import { AerReviewDecisionGroupComponent } from '../../../aer-review-decision-group/aer-review-decision-group.component';

interface ViewModel {
  data: AviationAerTotalEmissionsConfidentiality;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-total-emissions-summary',
  templateUrl: './total-emissions-summary.component.html',
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    RouterLink,
    TotalEmissionsStandardFuelsTableComponent,
    TotalEmissionsSchemeYearHeaderComponent,
    TotalEmissionsSchemeYearHeaderComponent,
    TotalEmissionsSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerQuery.selectAerMonitoringPlanVersions),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('aviationAerTotalEmissionsConfidentiality')),
  ]).pipe(
    map(([type, planVersions, isEditable, taskStatus]) => {
      return {
        planVersions,
        data: this.form.valid ? this.form.value : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'aviationAerTotalEmissionsConfidentiality'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: TotalEmissionsFormProvider,
    private store: RequestTaskStore,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form?.valid) {
      this.store.aerDelegate
        .saveAer({ aviationAerTotalEmissionsConfidentiality: this.form.value }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../../'], { relativeTo: this.route, queryParams: { change: null } });
        });
    }
  }
}
