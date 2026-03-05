import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map } from 'rxjs';

import { VirService } from '@tasks/vir/core/vir.service';

import { VirRequestMetadata } from 'pmrv-api';

@Component({
  selector: 'app-submit-container',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  virTitle$ = this.virService.requestMetadata$.pipe(
    map((metadata) => (metadata as VirRequestMetadata).year + ' verifier improvement report'),
  );
  virPayload$ = this.virService.payload$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  readonly daysRemaining$ = this.virService.daysRemaining$;

  constructor(
    private readonly virService: VirService,
    private readonly router: Router,
  ) {}
}
