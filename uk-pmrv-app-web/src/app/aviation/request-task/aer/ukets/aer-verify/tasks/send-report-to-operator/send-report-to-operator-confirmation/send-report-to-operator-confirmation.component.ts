import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-send-report-to-operator-confirmation',
  templateUrl: './send-report-to-operator-confirmation.component.html',
  standalone: true,
  imports: [GovukComponentsModule, AsyncPipe, RouterLink],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class SendReportToOperatorConfirmationComponent implements OnInit {
  private store = inject(RequestTaskStore);
  private breadcrumbs = inject(BreadcrumbService);
  private router = inject(Router);

  protected requestId$ = this.store.pipe(requestTaskQuery.selectRequestInfo);

  ngOnInit(): void {
    this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
  }
}
