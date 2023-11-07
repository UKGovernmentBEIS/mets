import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/ukets/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { ServiceContactDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp/service-contact-details-summary-template/service-contact-details-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { ServiceContactDetails } from 'pmrv-api';

import { aerQuery } from '../../aer.selectors';

interface ViewModel {
  accountId: number;
  contactDetails: ServiceContactDetails;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-service-contact-details-page',
  templateUrl: './service-contact-details-page.component.html',
  imports: [
    SharedModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    ServiceContactDetailsSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ServiceContactDetailsPageComponent {
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly aerHeaderTaskMap = aerHeaderTaskMap;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(aerQuery.selectServiceContactDetails),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('serviceContactDetails')),
  ]).pipe(
    map(([type, requestInfo, contactDetails, isEditable, taskStatus]) => ({
      accountId: requestInfo.accountId,
      contactDetails,
      hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
      showDecision: showReviewDecisionComponent.includes(type),
    })),
  );

  onSubmit() {
    const serviceContactDetails = this.store.aerDelegate.payload.serviceContactDetails;

    this.store.aerDelegate
      .saveAer({ serviceContactDetails }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
