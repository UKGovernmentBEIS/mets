import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { requestTaskQuery } from '@aviation/request-task/store';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { MonitoringApproachSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-approach-summary-template';
import { EmissionSmallEmittersSupportFacilityFormValues } from '@aviation/shared/components/aer/monitoring-approach-summary-template/monitoring-approach.interfaces';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AerReviewDecisionGroupComponent } from '../../../aer-review-decision-group/aer-review-decision-group.component';
import { BaseMonitoringApproachComponent } from '../base-monitoring-approach.component';

export interface ViewModel {
  data: EmissionSmallEmittersSupportFacilityFormValues | null;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}
@Component({
  selector: 'app-monitoring-approach-summary',
  templateUrl: './monitoring-approach-summary.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    MonitoringApproachSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class MonitoringApproachSummaryComponent extends BaseMonitoringApproachComponent implements OnInit, OnDestroy {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('monitoringApproach')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        data: this.form.valid ? this.formProvider.getFormValue() : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'monitoringApproach'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    if (this.form?.valid) {
      this.saveEmpAndNavigate(this.formProvider.getFormValue(), 'complete', '../../../');
    }
  }
}
