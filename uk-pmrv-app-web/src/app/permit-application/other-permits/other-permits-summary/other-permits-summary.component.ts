import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-other-permits-summary',
  templateUrl: './other-permits-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OtherPermitsSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  environmentalPermitsAndLicences$ = this.store.getTask('environmentalPermitsAndLicences');

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}
}
