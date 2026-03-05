import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { map } from 'rxjs';

import { isVariationRegulatorLedRequest } from '@permit-application/shared/utils/permit';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-review-group-decision-container',
  templateUrl: './decision-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DecisionContainerComponent {
  @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
  @Input() canEdit = true;
  @Output() readonly notification = new EventEmitter<boolean>();

  // TODO: the permit application should not be aware of variation details
  isVariation$ = this.store.pipe(map((state) => state.requestType === 'PERMIT_VARIATION'));
  isVariationRegulatorLed$ = this.store.pipe(map((state) => isVariationRegulatorLedRequest(state)));

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}

  notifyEvent(): void {
    this.notification.emit(true);
  }
}
