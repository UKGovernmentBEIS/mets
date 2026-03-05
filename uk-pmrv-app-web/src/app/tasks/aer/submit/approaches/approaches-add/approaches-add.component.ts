import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  AerApplicationSubmitRequestTaskPayload,
  AerMonitoringApproachEmissions,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { approachesAddFormProvider } from './approaches-add-form.provider';

@Component({
  selector: 'app-approaches-add',
  template: `
    <app-aer-task [breadcrumb]="[{ text: 'Define monitoring approaches', link: ['monitoring-approaches'] }]">
      <app-approaches-add-template
        (formSubmit)="onSubmit()"
        [monitoringApproaches]="monitoringApproaches$ | async"
        [form]="form">
        <ng-container heading>
          <p class="govuk-body">Select the monitoring approaches relevant to your installation.</p>
          <p class="govuk-body">
            Get help with
            <a govukLink routerLink="../help-with-monitoring-approaches" target="_blank">monitoring approaches</a>
            .
          </p>
        </ng-container>
        <ng-container returnTo>
          <a govukLink routerLink="..">Return to: Define monitoring approaches</a>
        </ng-container>
      </app-approaches-add-template>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [approachesAddFormProvider],
})
export class ApproachesAddComponent {
  monitoringApproaches$ = this.aerService
    .getTask('monitoringApproachEmissions')
    .pipe(map((monitoringApproaches) => monitoringApproaches ?? {}));

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly store: CommonTasksStore,
  ) {}

  onSubmit(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) => {
          const requestTaskType = state.requestTaskItem.requestTask.type;
          const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
          const aer = payload.aer;
          const monitoringApproachEmissions = aer.monitoringApproachEmissions;
          const monitoringApproachesFormValues = this.form.value.monitoringApproaches;

          const calculationApproach = monitoringApproachesFormValues.includes('CALCULATION_CO2')
            ? this.buildApproach(
                monitoringApproachEmissions,
                'CALCULATION_CO2',
                this.form.value?.hasTransferCalculationCO2,
              )
            : {};
          const measurementCO2Approach = monitoringApproachesFormValues.includes('MEASUREMENT_CO2')
            ? this.buildApproach(
                monitoringApproachEmissions,
                'MEASUREMENT_CO2',
                this.form.value?.hasTransferMeasurementCO2,
              )
            : {};
          const measurementN2OApproach = monitoringApproachesFormValues.includes('MEASUREMENT_N2O')
            ? this.buildApproach(
                monitoringApproachEmissions,
                'MEASUREMENT_N2O',
                this.form.value?.hasTransferMeasurementN2O,
              )
            : {};
          const inherentCo2Approach = monitoringApproachesFormValues.includes('INHERENT_CO2')
            ? this.buildApproach(monitoringApproachEmissions, 'INHERENT_CO2', undefined)
            : {};
          const pfcApproach = monitoringApproachesFormValues.includes('CALCULATION_PFC')
            ? this.buildApproach(monitoringApproachEmissions, 'CALCULATION_PFC', undefined)
            : {};
          const fallbackApproach = monitoringApproachesFormValues.includes('FALLBACK')
            ? this.buildApproach(monitoringApproachEmissions, 'FALLBACK', undefined)
            : {};

          const removeEmissionPoints =
            !monitoringApproachesFormValues.includes('MEASUREMENT_CO2') &&
            !monitoringApproachesFormValues.includes('MEASUREMENT_N2O');

          let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

          switch (requestTaskType) {
            case 'AER_APPLICATION_SUBMIT':
              actionType = 'AER_SAVE_APPLICATION';
              break;
            case 'AER_APPLICATION_AMENDS_SUBMIT':
              actionType = 'AER_SAVE_APPLICATION_AMEND';
              break;
          }

          return this.aerService.postAer(
            {
              ...state,
              requestTaskItem: {
                ...state.requestTaskItem,
                requestTask: {
                  ...state.requestTaskItem.requestTask,
                  payload: {
                    ...payload,
                    aer: {
                      ...aer,
                      monitoringApproachEmissions: {
                        ...calculationApproach,
                        ...measurementCO2Approach,
                        ...measurementN2OApproach,
                        ...inherentCo2Approach,
                        ...pfcApproach,
                        ...fallbackApproach,
                      },
                      ...(removeEmissionPoints ? { emissionPoints: null } : null),
                    },
                    aerSectionsCompleted: {
                      ...payload.aerSectionsCompleted,
                      monitoringApproachEmissions: [false],
                      ...(removeEmissionPoints ? { emissionPoints: [false] } : null),
                    },
                  } as AerApplicationSubmitRequestTaskPayload,
                },
              },
            },
            actionType,
          );
        }),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }

  buildApproach(
    monitoringApproachEmissions: AerMonitoringApproachEmissions,
    monitorApproach: string,
    hasTransferForm: boolean,
  ) {
    return {
      [monitorApproach]: {
        ...monitoringApproachEmissions?.[monitorApproach],
        type: monitorApproach,
        ...(hasTransferForm !== undefined ? { hasTransfer: hasTransferForm } : {}),
        ...(['CALCULATION_CO2', 'MEASUREMENT_CO2', 'MEASUREMENT_N2O'].includes(monitorApproach) &&
          this.updateTransferredDataOnEmissions(monitoringApproachEmissions, monitorApproach, hasTransferForm)),
      },
    };
  }

  private updateTransferredDataOnEmissions(
    monitoringApproachEmissions: AerMonitoringApproachEmissions,
    monitorApproach: string,
    newHasTransferValue: boolean,
  ) {
    let dataTypeKey;
    switch (monitorApproach) {
      case 'CALCULATION_CO2':
        dataTypeKey = 'sourceStreamEmissions';
        break;
      case 'MEASUREMENT_CO2':
      case 'MEASUREMENT_N2O':
        dataTypeKey = 'emissionPointEmissions';
        break;
      default:
        break;
    }

    const emissionsData = monitoringApproachEmissions?.[monitorApproach]?.[dataTypeKey];

    const currentHasTransferValue = monitoringApproachEmissions?.[monitorApproach]?.hasTransfer;

    let result = emissionsData;
    if (currentHasTransferValue === true && newHasTransferValue === false) {
      result = this.removeTransferredFromSourceStreamEmissions(emissionsData);
    } else if (currentHasTransferValue === false && newHasTransferValue === true) {
      result = this.addTransferredFromSourceStreamEmissions(emissionsData, monitorApproach);
    }

    return { [dataTypeKey]: result };
  }

  private removeTransferredFromSourceStreamEmissions(emissionsData): any {
    const res = emissionsData?.reduce((acc, emission) => {
      const { transfer, ...rest } = emission;
      acc.push(rest);
      return acc;
    }, []);

    return res;
  }

  private addTransferredFromSourceStreamEmissions(emissionsData, monitorApproach): any {
    const res = emissionsData?.map((emission) => {
      return {
        ...emission,
        transfer: {
          entryAccountingForTransfer: false,
          transferType: monitorApproach === 'MEASUREMENT_N2O' ? 'TRANSFER_N2O' : 'TRANSFER_CO2',
        },
      };
    });

    return res;
  }
}
