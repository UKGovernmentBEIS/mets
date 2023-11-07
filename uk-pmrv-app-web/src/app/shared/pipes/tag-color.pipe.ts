import { Pipe, PipeTransform } from '@angular/core';

import {
  ReviewDeterminationStatus,
  ReviewGroupDecisionStatus,
  ReviewGroupTasksAggregatorStatus,
} from '@permit-application/review/types/review.permit.type';

import { TagColor } from 'govuk-components';

import { RequestDetailsDTO } from 'pmrv-api';

import { TaskItemStatus } from '../task-list/task-list.interface';

@Pipe({ name: 'tagColor' })
export class TagColorPipe implements PipeTransform {
  transform(
    status:
      | TaskItemStatus
      | ReviewGroupDecisionStatus
      | ReviewGroupTasksAggregatorStatus
      | ReviewDeterminationStatus
      | RequestDetailsDTO['requestStatus']
      | 'withdrawn',
  ): TagColor {
    switch (status) {
      case 'not started':
      case 'undecided':
      case 'cannot start yet':
      case 'CANCELLED':
      case 'CLOSED':
        return 'grey';
      case 'granted':
      case 'accepted':
      case 'COMPLETED':
      case 'approved':
      case 'APPROVED':
        return 'green';
      case 'operator to amend':
      case 'in progress':
      case 'IN_PROGRESS':
        return 'blue';
      case 'rejected':
      case 'deemed withdrawn':
      case 'withdrawn':
      case 'incomplete':
      case 'WITHDRAWN':
      case 'REJECTED':
        return 'red';
      case 'needs review':
        return 'yellow';
      case 'complete':
        return null;
      default:
        return null;
    }
  }
}
