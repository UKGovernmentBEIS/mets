import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-management-procedures',
  templateUrl: './management-procedures.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresComponent {
  showDiff$ = this.store.showDiff$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((x) => x?.groupKey));

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
