import { ChangeDetectionStrategy, Component, HostBinding, Input } from '@angular/core';

import {
  ReviewDeterminationStatus,
  ReviewGroupDecisionStatus,
  ReviewGroupTasksAggregatorStatus,
} from '@permit-application/review/types/review.permit.type';

import { DecisionStatus, FollowUpDecisionStatus } from '../../../tasks/permit-notification/core/section-status';
import { TaskItemStatus } from '../task-list.interface';

@Component({
  selector: 'li[app-task-item]',
  standalone: false,
  templateUrl: './task-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskItemComponent {
  @Input() link: string;
  @Input() linkText: string;
  @Input() status:
    | TaskItemStatus
    | ReviewGroupDecisionStatus
    | ReviewDeterminationStatus
    | ReviewGroupTasksAggregatorStatus
    | DecisionStatus
    | FollowUpDecisionStatus;
  @Input() hasContent: boolean;

  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostBinding('class.app-task-list__item') readonly taskListItem = true;
}
