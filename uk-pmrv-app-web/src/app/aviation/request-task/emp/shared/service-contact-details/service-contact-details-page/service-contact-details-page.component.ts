import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import {
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { ServiceContactDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp/service-contact-details-summary-template/service-contact-details-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { ServiceContactDetails } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../../../store';
import { EmpVariationReviewDecisionGroupComponent } from '../../../shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { empQuery } from '../../emp.selectors';
import { EmpReviewDecisionGroupComponent } from '../../emp-review-decision-group/emp-review-decision-group.component';
import { empHeaderTaskMap, variationSubmitRegulatorLedRequestTaskTypes } from '../../util/emp.util';

interface ViewModel {
  accountId: number;
  contactDetails: ServiceContactDetails;
  hideSubmit: boolean;
  showDecision: boolean;
  showVariationDecision: boolean;
  showVariationRegLedDecision: boolean;
}

@Component({
  selector: 'app-service-contact-details-page',
  templateUrl: './service-contact-details-page.component.html',
  imports: [
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationReviewDecisionGroupComponent,
    ServiceContactDetailsSummaryTemplateComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ServiceContactDetailsPageComponent {
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly empHeaderTaskMap = empHeaderTaskMap;

  readonly requestTaskType$ = this.store.pipe(requestTaskQuery.selectRequestTaskType);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(empQuery.selectServiceContactDetails),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(empQuery.selectStatusForTask('serviceContactDetails')),
  ]).pipe(
    map(([type, requestInfo, contactDetails, isEditable, taskStatus]) => ({
      accountId: requestInfo.accountId,
      contactDetails,
      hideSubmit:
        !isEditable ||
        ['complete', 'cannot start yet'].includes(taskStatus) ||
        variationSubmitRegulatorLedRequestTaskTypes.includes(type),
      showDecision: showReviewDecisionComponent.includes(type),
      showVariationDecision: showVariationReviewDecisionComponent.includes(type),
      showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
    })),
  );

  onSubmit() {
    const serviceContactDetails = this.store.empDelegate.payload.serviceContactDetails;

    this.store.empDelegate
      .saveEmp({ serviceContactDetails }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route }));
  }
}
