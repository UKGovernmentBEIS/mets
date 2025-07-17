import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map } from 'rxjs';

import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  constructor(
    readonly nonComplianceService: NonComplianceService,
    private readonly router: Router,
  ) {}

  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';
  readonly requestInfoId$ = this.nonComplianceService.requestTaskItem$.pipe(map((data) => data.requestInfo.id));
}
