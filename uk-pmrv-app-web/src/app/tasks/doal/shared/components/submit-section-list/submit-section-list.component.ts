import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, first, map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { DoalService } from '@tasks/doal/core/doal.service';
import { DoalTaskSectionKey } from '@tasks/doal/core/doal-task.type';
import { resolveSectionStatus } from '@tasks/doal/submit/section-status';

@Component({
  selector: 'app-submit-section-list',
  templateUrl: './submit-section-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitSectionListComponent {
  constructor(private readonly doalService: DoalService) {}

  determinationUrl$ = this.doalService.payload$.pipe(
    first(),
    map((payload) =>
      resolveSectionStatus(payload, 'determination') === 'cannot start yet'
        ? null
        : !payload.doal?.determination?.type
        ? './determination'
        : payload.doal?.determination?.type === 'CLOSED'
        ? './determination/close/summary'
        : './determination/proceed-authority/summary',
    ),
  );

  getSectionStatus$(section: DoalTaskSectionKey): Observable<TaskItemStatus> {
    return this.doalService.payload$.pipe(
      first(),
      map((payload) => resolveSectionStatus(payload, section)),
    );
  }

  canViewSectionDetails$(section: DoalTaskSectionKey): Observable<boolean> {
    return combineLatest([this.doalService.isEditable$, this.getSectionStatus$(section)]).pipe(
      map(([isEditable, sectionStatus]) => isEditable || !['not started', 'cannot start yet'].includes(sectionStatus)),
    );
  }
}
