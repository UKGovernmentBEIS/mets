import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { PermitTransferAService } from '../core/permit-transfer-a.service';
import { getSectionStatus } from '../core/permit-transfer-a-task-statuses';

@Component({
  selector: 'app-transfer-a-submit',
  templateUrl: './submit-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  transferDetailsStatus$ = this.permitTransferAService.getPayload().pipe(map((payload) => getSectionStatus(payload)));

  canViewSectionDetails$ = combineLatest([this.permitTransferAService.isEditable$, this.transferDetailsStatus$]).pipe(
    map(([isEditable, transferDetailsStatus]) => isEditable || transferDetailsStatus !== 'not started'),
  );

  constructor(
    readonly permitTransferAService: PermitTransferAService,
    private readonly router: Router,
  ) {}
}
