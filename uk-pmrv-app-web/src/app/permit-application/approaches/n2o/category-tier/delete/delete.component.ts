import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

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
                MEASUREMENT_N2O: {
                  ...state.permit.monitoringApproaches.MEASUREMENT_N2O,
                  emissionPointCategoryAppliedTiers:
                    (state.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
                      .emissionPointCategoryAppliedTiers.length > 1
                      ? (
                          state.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach
                        ).emissionPointCategoryAppliedTiers.filter((_, i) => i !== index)
                      : null,
                } as MeasurementOfN2OMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              MEASUREMENT_N2O_Category: state.permitSectionsCompleted.MEASUREMENT_N2O_Category?.filter(
                (_, i) => i !== index,
              ),
              MEASUREMENT_N2O_Measured_Emissions:
                state.permitSectionsCompleted.MEASUREMENT_N2O_Measured_Emissions?.filter((_, i) => i !== index),
              MEASUREMENT_N2O_Applied_Standard: state.permitSectionsCompleted.MEASUREMENT_N2O_Applied_Standard?.filter(
                (_, i) => i !== index,
              ),
            },
          }),
        ),
        switchMap(() => this.store),
        first(),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((state) =>
        this.router.navigate([deleteReturnUrl(state, 'nitrous-oxide')], { relativeTo: this.route }),
      );
  }
}
