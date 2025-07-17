import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { deleteReturnUrl } from '../../../approaches';

@Component({
  selector: 'app-category-tier-delete',
  templateUrl: './delete.component.html',
  styles: `
    .nowrap {
      white-space: nowrap;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => Number(paramMap.get('index'))),
        withLatestFrom(this.store, this.route.data),
        switchMap(([index, state, data]) =>
          this.store.postCategoryTask(data.taskKey, {
            ...state,
            permit: {
              ...state.permit,
              monitoringApproaches: {
                ...state.permit.monitoringApproaches,
                CALCULATION_PFC: {
                  ...state.permit.monitoringApproaches.CALCULATION_PFC,
                  sourceStreamCategoryAppliedTiers:
                    (state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers.length > 1
                      ? (
                          state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach
                        ).sourceStreamCategoryAppliedTiers.filter((_, i) => i !== index)
                      : null,
                } as CalculationOfPFCMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              CALCULATION_PFC_Category: state.permitSectionsCompleted.CALCULATION_PFC_Category?.filter(
                (_, i) => i !== index,
              ),
              CALCULATION_PFC_Activity_Data: state.permitSectionsCompleted.CALCULATION_PFC_Activity_Data?.filter(
                (_, i) => i !== index,
              ),
              CALCULATION_PFC_Emission_Factor: state.permitSectionsCompleted.CALCULATION_PFC_Emission_Factor?.filter(
                (_, i) => i !== index,
              ),
            },
          }),
        ),
        switchMap(() => this.store),
        first(),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((state) => this.router.navigate([deleteReturnUrl(state, 'pfc')], { relativeTo: this.route }));
  }
}
