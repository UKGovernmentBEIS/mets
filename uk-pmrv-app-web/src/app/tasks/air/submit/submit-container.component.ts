import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { originalOrder } from '@shared/keyvalue-order';
import { AirService } from '@tasks/air/shared/services/air.service';

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  title$ = this.airService.title$;
  airPayload$ = this.airService.payload$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  readonly daysRemaining$ = this.airService.daysRemaining$;
  readonly originalOrder = originalOrder;

  constructor(private readonly airService: AirService, private readonly router: Router) {}
}
