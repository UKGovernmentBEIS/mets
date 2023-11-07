import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { pluck } from 'rxjs';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { PermitApplicationState } from '../../store/permit-application.state';

@Component({
  selector: 'app-monitoring-methodology-plan',
  templateUrl: './monitoring-methodology-plan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringMethodologyPlanComponent {
  showDiff$ = this.store.showDiff$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
