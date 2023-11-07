import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { DoalAuthorityResponseRequestTaskPayload } from 'pmrv-api';

import { DoalService } from '../core/doal.service';
import { DoalAuthorityTaskSectionKey } from '../core/doal-task.type';
import { canNotifyOperator, resolveSectionStatus } from './section-status';

@Component({
  selector: 'app-authority-response-container',
  templateUrl: './authority-response-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthorityResponseContainerComponent {
  isEditable$ = this.doalService.isEditable$;

  allowNotifyOperator$ = this.doalService.requestTaskItem$.pipe(
    map((requestTaskItem) =>
      canNotifyOperator(
        requestTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload,
        requestTaskItem.allowedRequestTaskActions,
      ),
    ),
  );

  getSectionStatus$(section: DoalAuthorityTaskSectionKey): Observable<TaskItemStatus> {
    return this.doalService.authorityPayload$.pipe(
      first(),
      map((payload) => resolveSectionStatus(payload, section)),
    );
  }

  canViewSectionDetails$(section: DoalAuthorityTaskSectionKey): Observable<boolean> {
    return combineLatest([this.doalService.isEditable$, this.getSectionStatus$(section)]).pipe(
      map(([isEditable, sectionStatus]) => isEditable || !['not started', 'cannot start yet'].includes(sectionStatus)),
    );
  }

  constructor(
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }
}
