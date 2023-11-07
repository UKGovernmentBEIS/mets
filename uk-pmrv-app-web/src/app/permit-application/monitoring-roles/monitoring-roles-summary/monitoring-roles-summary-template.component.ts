import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { filter, map, Observable } from 'rxjs';

import { SummaryItem } from 'govuk-components';

import { MonitoringReporting } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-monitoring-roles-summary-template',
  templateUrl: './monitoring-roles-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringRolesSummaryTemplateComponent implements OnInit {
  @Input() showOriginal = false;
  @Input() hasBottomBorder = true;

  task$: Observable<MonitoringReporting>;
  roles$: Observable<SummaryItem[][]>;

  ngOnInit(): void {
    this.task$ = this.showOriginal
      ? this.store.pipe(map((state) => (state as any).originalPermitContainer?.permit?.monitoringReporting))
      : this.store.getTask('monitoringReporting');

    this.roles$ = this.task$.pipe(
      filter((task) => !!task),
      map((task) =>
        task.monitoringRoles.map((permit) => [
          { key: 'Job title', value: permit.jobTitle },
          { key: 'Main duties', value: permit.mainDuties },
        ]),
      ),
    );
  }

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
