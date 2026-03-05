import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-not-success',
  template: `
    <app-page-heading>{{ message$ | async }}</app-page-heading>
    <app-return-link [requestType]="(store | async).requestType" [home]="true"></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotSuccessComponent implements OnInit {
  message$ = this.route.queryParams.pipe(map((params) => params?.message));

  constructor(
    readonly store: PaymentStore,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.breadcrumbService.showDashboardBreadcrumb(this.router.url);
  }
}
