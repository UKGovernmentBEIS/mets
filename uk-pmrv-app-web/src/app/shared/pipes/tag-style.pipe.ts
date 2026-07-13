import { Pipe, PipeTransform } from '@angular/core';

import {
  ReviewDeterminationStatus,
  ReviewGroupDecisionStatus,
  ReviewGroupTasksAggregatorStatus,
} from '@permit-application/review/types/review.permit.type';

import { TaskItemStatus } from '../task-list/task-list.interface';

export type TagStyle = 'fill' | 'tinted' | 'none';

@Pipe({
  name: 'tagStyle',
  standalone: false,
})
export class TagStylePipe implements PipeTransform {
  transform(
    status:
      | TaskItemStatus
      | ReviewGroupDecisionStatus
      | ReviewGroupTasksAggregatorStatus
      | ReviewDeterminationStatus
      | string,
  ): TagStyle {
    switch (status) {
      case 'complete':
      case 'accepted':
      case 'approved':
      case 'granted':
        return 'none';
      case 'cannot start yet':
        return 'tinted';
      default:
        return 'fill';
    }
  }
}
