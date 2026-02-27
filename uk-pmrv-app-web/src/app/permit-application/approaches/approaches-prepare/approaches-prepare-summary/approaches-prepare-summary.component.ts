import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { DestroySubject } from '../../../../core/services/destroy-subject.service';

@Component({
  selector: 'app-approaches-prepare-summary',
  standalone: false,
  templateUrl: './approaches-prepare-summary.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesPrepareSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(private readonly router: Router) {}
}
