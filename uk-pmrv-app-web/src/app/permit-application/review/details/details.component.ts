import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, pluck, withLatestFrom } from 'rxjs';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { PermitApplicationState } from '../../store/permit-application.state';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));
  showDiff$ = this.store.showDiff$;

  originalInstallationOperatorDetails$ = (this.store as any).select('originalPermitContainer').pipe(
    withLatestFrom(this.showDiff$),
    map(([opc, showDiff]) => (showDiff ? opc.installationOperatorDetails : null)),
  );
  installationOperatorDetails$ = this.store.select('installationOperatorDetails');

  environmentalPermitsAndLicences$ = this.store.getTask('environmentalPermitsAndLicences');
  origEnvironmentalPermitsAndLicences$ = (this.store as any)
    .select('originalPermitContainer')
    .pipe(map((opc) => (opc as any).permit.environmentalPermitsAndLicences));

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
