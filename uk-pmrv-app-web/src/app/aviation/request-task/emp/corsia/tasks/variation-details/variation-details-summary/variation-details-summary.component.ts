import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { EmpVariationReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showVariationReviewDecisionComponent } from '@aviation/request-task/util';
import { VariationDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp-corsia/variation-details-summary-template/variation-details-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpVariationCorsiaDetails } from 'pmrv-api';

import { empQuery } from '../../../../shared/emp.selectors';
import { VariationDetailsFormProvider } from '../variation-details-form.provider';

interface ViewModel {
  variationDetails: EmpVariationCorsiaDetails;
  variationRegulatorLedReason: string;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showVariationDecision: boolean;
}

@Component({
  selector: 'app-variation-details-summary',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgIf,
    ReturnToLinkComponent,
    VariationDetailsSummaryTemplateComponent,
    EmpVariationReviewDecisionGroupComponent,
  ],
  templateUrl: './variation-details-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsSummaryComponent implements OnInit, OnDestroy {
  private form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('empVariationDetails')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        variationDetails: this.formProvider.getVariationDetailsFormValue(),
        variationRegulatorLedReason: this.formProvider.getVariationRegulatorLedReasonFormValue(),
        pageHeader: getSummaryHeaderForTaskType(type, 'empVariationDetails'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: VariationDetailsFormProvider,
    private store: RequestTaskStore,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    if (this.form?.valid) {
      this.store.empCorsiaDelegate
        .saveEmp({ empVariationDetails: this.formProvider.getVariationDetailsFormValue() }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../../../'], { relativeTo: this.route, queryParams: { change: null } });
        });
    }
  }
}
