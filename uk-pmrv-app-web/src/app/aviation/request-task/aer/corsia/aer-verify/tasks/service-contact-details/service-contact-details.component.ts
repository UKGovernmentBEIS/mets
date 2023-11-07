import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { ServiceContactDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp/service-contact-details-summary-template/service-contact-details-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { ServiceContactDetails } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: ServiceContactDetails;
}

@Component({
  selector: 'app-service-contact-details',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, ServiceContactDetailsSummaryTemplateComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-service-contact-details-summary-template [data]="vm.data"></app-service-contact-details-summary-template>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServiceContactDetailsComponent {
  vm$: Observable<ViewModel> = this.store.pipe(aerVerifyCorsiaQuery.selectPayload).pipe(
    map((payload) => {
      return {
        heading: aerHeaderTaskMap['serviceContactDetails'],
        data: payload.serviceContactDetails,
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
