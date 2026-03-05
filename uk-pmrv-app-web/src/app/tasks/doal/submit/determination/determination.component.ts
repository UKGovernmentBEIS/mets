import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { DoalDetermination } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DoalService } from '../../core/doal.service';

@Component({
  selector: 'app-determination',
  templateUrl: './determination.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationComponent {
  editable$ = this.doalService.isEditable$;

  constructor(
    private readonly doalService: DoalService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(type: DoalDetermination['type']): void {
    if (!this.determinationChanged(type)) {
      this.router.navigate([this.resolveTypeUrl(type), 'reason'], { relativeTo: this.route });
    } else {
      this.doalService
        .saveDoal(
          {
            determination: {
              type,
            } as any,
          },
          this.route.snapshot.data.sectionKey,
          false,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() =>
          this.router.navigate([this.resolveTypeUrl(type), 'reason'], {
            relativeTo: this.route,
          }),
        );
    }
  }

  private determinationChanged(type: DoalDetermination['type']): boolean {
    return this.doalService.payloadState.doal?.determination?.type !== type;
  }

  private resolveTypeUrl(type: DoalDetermination['type']) {
    return type === 'CLOSED' ? 'close' : 'proceed-authority';
  }
}
