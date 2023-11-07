import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PermitIssuanceGrantDetermination } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-determination',
  templateUrl: './determination.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeterminationComponent implements PendingRequest {
  isGrantDisplayed$ = this.store.isDeterminationGrantButtonDisplayed$();
  isRejectDisplayed$ = this.store.isDeterminationRejectButtonDisplayed$();

  header = this.store.getDeterminationHeader();
  headerHint = this.store.getDeterminationHeaderHint();
  approveText = this.store.getDeterminationGrantText();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(type: PermitIssuanceGrantDetermination['type']): void {
    if (!this.determinationChanged(type)) {
      this.router.navigate(['reason'], { relativeTo: this.route });
    } else {
      this.store
        .pipe(
          first(),
          switchMap(() => this.store.postDetermination({ type }, false)),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['reason'], { relativeTo: this.route }));
    }
  }

  determinationChanged(type: PermitIssuanceGrantDetermination['type']): boolean {
    return this.store.getState().determination?.type !== type;
  }
}
