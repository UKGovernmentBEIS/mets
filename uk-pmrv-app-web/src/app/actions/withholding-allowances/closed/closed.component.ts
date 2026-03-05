import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { WithholdingOfAllowancesApplicationClosedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';
import { WithholdingAllowancesActionService } from '../core/withholding-allowances.service';

@Component({
  selector: 'app-closed',
  templateUrl: './closed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ClosedComponent {
  payload$ =
    this.withholdingAllowancesActionService.getPayload() as Observable<WithholdingOfAllowancesApplicationClosedRequestActionPayload>;

  constructor(
    readonly withholdingAllowancesActionService: WithholdingAllowancesActionService,
    readonly store: CommonActionsStore,
  ) {}

  get supportingDocumentFiles$() {
    return this.payload$.pipe(
      map((payload) => this.withholdingAllowancesActionService.getDownloadUrlFiles(payload?.files)),
    );
  }
}
