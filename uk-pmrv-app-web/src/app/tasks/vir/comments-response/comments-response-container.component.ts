import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { VirService } from '@tasks/vir/core/vir.service';

import { VirApplicationRespondToRegulatorCommentsRequestTaskPayload, VirRequestMetadata } from 'pmrv-api';

@Component({
  selector: 'app-comments-response-container',
  templateUrl: './comments-response-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommentsResponseContainerComponent {
  virPayload$ = this.virService.payload$ as Observable<VirApplicationRespondToRegulatorCommentsRequestTaskPayload>;
  regulatorImprovementResponses$ = this.virPayload$.pipe(map((payload) => payload?.regulatorImprovementResponses));

  isEditable$ = this.virService.isEditable$;
  virTitle$ = this.virService.requestMetadata$.pipe(
    map((metadata) => (metadata as VirRequestMetadata).year + ' verifier improvement report'),
  );

  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  readonly daysRemaining$ = this.virService.daysRemaining$;

  constructor(private readonly virService: VirService, private readonly router: Router) {}
}
