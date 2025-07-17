import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { ServiceContactDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp/service-contact-details-summary-template/service-contact-details-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { ServiceContactDetails } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: ServiceContactDetails;
  showDecision: boolean;
}

@Component({
  selector: 'app-service-contact-details',
  standalone: true,
  imports: [
    ReturnToLinkComponent,
    SharedModule,
    ServiceContactDetailsSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-service-contact-details-summary-template [data]="vm.data"></app-service-contact-details-summary-template>
      <app-aviation-aer-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="serviceContactDetails"></app-aviation-aer-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServiceContactDetailsComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectPayload),
  ]).pipe(
    map(([type, payload]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.serviceContactDetails,
        data: payload.serviceContactDetails,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
