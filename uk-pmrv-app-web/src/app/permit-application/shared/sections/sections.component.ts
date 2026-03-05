import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, first, map, Observable } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload, UIConfigurationService } from 'pmrv-api';

import { findAmendedGroupsByReviewGroups } from '../../amend/amend';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { PermitAmendGroup } from '../types/amend.permit.type';
import { amendRequestTaskTypes, permitTypeMap } from '../utils/permit';

@Component({
  selector: 'app-sections',
  templateUrl: './sections.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionsComponent {
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
