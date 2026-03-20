import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { AuthStore, selectCurrentDomain } from '@core/store';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { NonComplianceApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-non-compliance-summary',
  standalone: false,
  templateUrl: './non-compliance-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceSummaryComponent implements OnInit {
  nonCompliance$ = (
    this.nonComplianceService.getPayload() as Observable<NonComplianceApplicationSubmittedRequestActionPayload>
  ).pipe(map((payload) => payload));

  isAviation$ = this.authStore.pipe(
    selectCurrentDomain,
    map((v) => v === 'AVIATION'),
  );

  requestTaskType = this.nonComplianceService.requestActionType();
  isAmended = signal(this.requestTaskType === 'NON_COMPLIANCE_DETAILS_AMENDED');

  breadcrumbs: BreadcrumbItem[];

  ngOnInit() {
    const isAmended = this.isAmended();

    this.breadcrumbs = [
      {
        link: this.router.url.startsWith('/aviation') ? ['/aviation/dashboard'] : ['/dashboard'],
        text: 'Dashboard',
      },
    ];

    if (isAmended) {
      this.breadcrumbService.show(this.breadcrumbs);
    }
  }

  constructor(
    readonly nonComplianceService: NonComplianceService,
    readonly store: CommonActionsStore,
    public readonly authStore: AuthStore,
    private readonly router: Router,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}
}
