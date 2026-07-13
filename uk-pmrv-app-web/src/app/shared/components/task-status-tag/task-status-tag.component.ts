import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import {
  ReviewDeterminationStatus,
  ReviewGroupDecisionStatus,
  ReviewGroupTasksAggregatorStatus,
} from '@permit-application/review/types/review.permit.type';

import { DecisionStatus, FollowUpDecisionStatus } from '../../../tasks/permit-notification/core/section-status';
import { statusMap } from '../../task-list/task-item/status.map';
import { TaskItemStatus } from '../../task-list/task-list.interface';

@Component({
  selector: 'app-task-status-tag',
  standalone: false,
  template: `
    @if ((status | tagStyle) === 'fill') {
      <govuk-tag [color]="status | tagColor" class="app-task-list__tag">
        {{ status | i18nSelect: statusMap }}
      </govuk-tag>
    } @else {
      <div
        class="govuk-task-list__status app-task-list__tag"
        [class.govuk-task-list__status--cannot-start-yet]="(status | tagStyle) === 'tinted'">
        {{ status | i18nSelect: statusMap }}
      </div>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskStatusTagComponent {
  @Input() status:
    | TaskItemStatus
    | ReviewGroupDecisionStatus
    | ReviewDeterminationStatus
    | ReviewGroupTasksAggregatorStatus
    | DecisionStatus
    | FollowUpDecisionStatus;

  statusMap = statusMap;
}
