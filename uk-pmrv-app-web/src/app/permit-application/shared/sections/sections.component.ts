import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { combineLatest, first, map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload, UIConfigurationService } from 'pmrv-api';

import { findAmendedGroupsByReviewGroups } from '../../amend/amend';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { PermitAmendGroup } from '../types/amend.permit.type';
import { amendRequestTaskTypes, permitTypeMap } from '../utils/permit';

@Component({
  selector: 'app-sections',
  standalone: false,
  templateUrl: './sections.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionsComponent {
  @Input() hideSubmit: boolean;
  @Input() submitLabel = 'Submit';
  @Input() submitButtonLabel = 'Check & submit';
  @Input() isSubmitDisabled: boolean;
  @Input() submitLink: string;
  @Input() submitLinkText: string;
  @Input() submitLinkStatus: TaskItemStatus;

  @Output() readonly submitApplication = new EventEmitter();

  isTaskTypeAmendsSubmit$ = this.store.pipe(
    map((state) => state.requestTaskType),
    map((requestTaskType) => amendRequestTaskTypes.includes(requestTaskType)),
  );

  permitType$ = this.store.pipe(
    map((state) => state.permitType),
    map((permitType) => permitTypeMap[permitType]),
  );

  amendedReviewGroups$: Observable<PermitAmendGroup[]> = this.store.pipe(
    first(),
    map((state) =>
      findAmendedGroupsByReviewGroups(
        Object.keys(
          state.reviewGroupDecisions,
        ) as PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'][],
      ),
    ),
  );

  showMMPTasks$ = combineLatest([this.store, this.uiConfigurationService.getUIConfiguration()]).pipe(
    map(([state, uiConfiguration]) => {
      const digitizedMMP = uiConfiguration.features?.['digitized-mmp'];
      return (
        state.permit?.monitoringMethodologyPlans?.exist &&
        digitizedMMP &&
        !!state?.permitSectionsCompleted?.monitoringMethodologyPlans?.[0]
      );
    }),
  );

  isTask$: Observable<boolean> = this.store.pipe(map((state) => state.isRequestTask));

  digitizedPlans$ = this.store.pipe(map((state) => state.permit?.monitoringMethodologyPlans?.digitizedPlan));

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly uiConfigurationService: UIConfigurationService,
  ) {}
}
