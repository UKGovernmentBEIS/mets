import { ChangeDetectionStrategy, Component } from '@angular/core';

import { CommonTasksStore } from '../../store/common-tasks.store';

@Component({
  selector: 'app-peer-review-wait',
  templateUrl: './peer-review-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewWaitComponent {
  constructor(readonly store: CommonTasksStore) {}
}
