import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-permit-type',
  templateUrl: './permit-type.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitTypeComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((data) => data.groupKey));
  originalPermitType$ = this.store.originalPermitContainer$.pipe(map((permitContainer) => permitContainer.permitType));
  permitType$ = this.store.pipe(map((state) => state.permitType));
  showDiff$ = this.store.showDiff$;

  isPermitTypeEditable$ = this.store.isPermitTypeEditable();

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
