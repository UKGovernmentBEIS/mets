import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { ReturnOfAllowancesService } from '../../core/return-of-allowances.service';

@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  constructor(readonly returnOfAllowancesService: ReturnOfAllowancesService) {}

  readonly requestInfoId$ = this.returnOfAllowancesService.requestTaskItem$.pipe(map((data) => data.requestInfo.id));
}
