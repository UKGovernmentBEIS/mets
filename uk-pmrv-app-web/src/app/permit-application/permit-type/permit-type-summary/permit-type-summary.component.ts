import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-permit-type-summary',
  templateUrl: './permit-type-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitTypeSummaryComponent {
  permitType = this.store.permitType;
  isEditable$ = this.store.isEditable$;
  requestTaskType$ = this.store.pipe(map((state) => state.requestTaskType));
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>, private readonly router: Router) {}
}
