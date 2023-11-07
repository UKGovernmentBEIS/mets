import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { DoalService } from '@tasks/doal/core/doal.service';

@Component({
  selector: 'app-peer-review-wait',
  template: `
    <app-base-task-container-component
      [header]="title"
      [customContentTemplate]="customContentTemplate"
      expectedTaskType="DOAL_WAIT_FOR_PEER_REVIEW"
    >
    </app-base-task-container-component>

    <ng-template #customContentTemplate>
      <govuk-warning-text>Waiting for peer review, you cannot make any changes</govuk-warning-text>
      <app-submit-section-list></app-submit-section-list>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewWaitComponent {
  title = this.itemNamePipe.transform('DOAL_WAIT_FOR_PEER_REVIEW');

  constructor(private readonly doalService: DoalService, private readonly itemNamePipe: ItemNamePipe) {}
}
