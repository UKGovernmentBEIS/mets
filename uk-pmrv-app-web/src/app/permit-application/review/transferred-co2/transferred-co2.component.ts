import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { PermitApplicationState } from '../../store/permit-application.state';

@Component({
  selector: 'app-transferred-co2',
  templateUrl: './transferred-co2.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferredCO2Component {
  showDiff$ = this.store.showDiff$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((x) => x?.groupKey));

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
