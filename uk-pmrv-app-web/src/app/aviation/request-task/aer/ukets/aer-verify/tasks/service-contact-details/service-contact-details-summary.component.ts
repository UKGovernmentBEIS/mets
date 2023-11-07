import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from "@aviation/request-task/aer/shared/aer.selectors";
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { requestTaskQuery, RequestTaskStore } from "@aviation/request-task/store";
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { ServiceContactDetails } from 'pmrv-api';


interface ViewModel {
  accountId: number;
  contactDetails: ServiceContactDetails;
}

@Component({
  selector: 'app-service-contact-details-summary',
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ 'serviceContactDetails' | i18nSelect: aerHeaderTaskMap }}</app-page-heading>

      <dl govuk-summary-list *ngIf="vm.contactDetails">
        <div govukSummaryListRow class="full-name">
          <dt govukSummaryListRowKey>Full name</dt>
          <dd govukSummaryListRowValue>{{ vm.contactDetails.name }}</dd>
        </div>

        <div govukSummaryListRow class="role">
          <dt govukSummaryListRowKey>Role</dt>
          <dd govukSummaryListRowValue>{{ vm.contactDetails.roleCode | userRole }}</dd>
        </div>

        <div govukSummaryListRow class="email">
          <dt govukSummaryListRowKey>Email address</dt>
          <dd govukSummaryListRowValue>{{ vm.contactDetails.email }}</dd>
        </div>
      </dl>
    </ng-container>

    <app-return-to-link></app-return-to-link>
  `,
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ServiceContactDetailsSummaryComponent {
  private store = inject(RequestTaskStore);

  readonly aerHeaderTaskMap = aerHeaderTaskMap;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(aerQuery.selectServiceContactDetails),
  ]).pipe(
    map(([requestInfo, contactDetails]) => ({
      accountId: requestInfo.accountId,
      contactDetails,
    })),
  );
}
