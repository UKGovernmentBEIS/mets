import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { VirService } from '@tasks/vir/core/vir.service';

import { VirApplicationReviewRequestTaskPayload, VirRequestMetadata } from 'pmrv-api';

@Component({
  selector: 'app-review-container',
  templateUrl: './review-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewContainerComponent {
  virTitle$ = this.virService.requestMetadata$.pipe(
    map((metadata) => 'Review ' + (metadata as VirRequestMetadata).year + ' verifier improvement report'),
  );
  virPayload$ = this.virService.payload$ as Observable<VirApplicationReviewRequestTaskPayload>;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(private readonly virService: VirService, private readonly router: Router) {}
}
