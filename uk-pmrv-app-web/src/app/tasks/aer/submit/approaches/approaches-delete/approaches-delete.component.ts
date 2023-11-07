import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { Aer, AerApplicationSubmitRequestTaskPayload, AerMonitoringApproachEmissions } from 'pmrv-api';

@Component({
  selector: 'app-approaches-delete',
  template: `
    <app-approaches-delete-template (delete)="delete()" [monitoringApproach]="monitoringApproach$ | async">
    </app-approaches-delete-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesDeleteComponent {
  monitoringApproach$ = combineLatest([
    this.aerService.getTask('monitoringApproachEmissions'),
    this.route.paramMap,
  ]).pipe(
    first(),
    map(([monitoringApproaches, paramMap]) => {
      const monitoringApproachSection: AerMonitoringApproachEmissions =
        monitoringApproaches[paramMap.get('monitoringApproach')];

      return monitoringApproachSection === null
        ? (paramMap.get('monitoringApproach') as AerMonitoringApproachEmissions['type'])
        : monitoringApproachSection.type;
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly aerService: AerService,
    private readonly store: CommonTasksStore,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('monitoringApproach')),
        withLatestFrom(this.store),
        switchMap(([deletedMonitoringApproach, state]) => {
          const measurementAndN2OApproaches = ['MEASUREMENT_CO2', 'MEASUREMENT_N2O'];
          const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
          const aer = payload.aer;

          const { [deletedMonitoringApproach]: deletedApproach, ...remainingMonitoringApproachEmissions } =
            aer.monitoringApproachEmissions;

          const areMeasurementAndN2OApproachesDeleted =
            measurementAndN2OApproaches.includes(deletedMonitoringApproach) &&
            !Object.keys(remainingMonitoringApproachEmissions).some((item) =>
              measurementAndN2OApproaches.includes(item),
            );

          const aerUpdatedWithRemainingMonitoringApproachEmissions = {
            ...aer,
            monitoringApproachEmissions: remainingMonitoringApproachEmissions,
          } as Aer;

          const aerSectionsCompletedUpdatedWithMonitoringApproachEmissionsStatus = {
            ...payload.aerSectionsCompleted,
            monitoringApproachEmissions: [false],
          };

          return this.aerService.postAer(
            {
              ...state,
              requestTaskItem: {
                ...state.requestTaskItem,
                requestTask: {
                  ...state.requestTaskItem.requestTask,
                  payload: {
                    ...payload,
                    ...(areMeasurementAndN2OApproachesDeleted
                      ? {
                          aer: {
                            ...aerUpdatedWithRemainingMonitoringApproachEmissions,
                            emissionPoints: [],
                          },
                          aerSectionsCompleted: {
                            ...Object.keys(aerSectionsCompletedUpdatedWithMonitoringApproachEmissionsStatus)
                              .filter((key) => key !== 'emissionPoints')
                              .reduce(
                                (res, key) => ({
                                  ...res,
                                  [key]: aerSectionsCompletedUpdatedWithMonitoringApproachEmissionsStatus[key],
                                }),
                                {},
                              ),
                          },
                        }
                      : {
                          aer: aerUpdatedWithRemainingMonitoringApproachEmissions,
                          aerSectionsCompleted: {
                            ...Object.keys(aerSectionsCompletedUpdatedWithMonitoringApproachEmissionsStatus)
                              .filter((key) => key !== deletedMonitoringApproach)
                              .reduce(
                                (res, key) => ({
                                  ...res,
                                  [key]: aerSectionsCompletedUpdatedWithMonitoringApproachEmissionsStatus[key],
                                }),
                                {},
                              ),
                          },
                        }),
                  } as AerApplicationSubmitRequestTaskPayload,
                },
              },
            },
            'AER_SAVE_APPLICATION',
          );
        }),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
