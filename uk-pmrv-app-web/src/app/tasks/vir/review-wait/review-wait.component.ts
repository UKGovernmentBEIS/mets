import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { VirService } from '@tasks/vir/core/vir.service';

import { VirRequestMetadata } from 'pmrv-api';

@Component({
  selector: 'app-review-wait',
  templateUrl: './review-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewWaitComponent {
  virTitle$ = this.virService.requestMetadata$.pipe(
    map((metadata) => (metadata as VirRequestMetadata).year + ' verifier improvement report'),
  );

  constructor(private readonly virService: VirService) {}
}
