import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-emission-summaries-summary',
  templateUrl: './emission-summaries-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSummariesSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>, private readonly router: Router) {}
}
