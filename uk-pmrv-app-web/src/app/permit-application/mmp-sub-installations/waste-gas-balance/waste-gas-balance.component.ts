import { ChangeDetectionStrategy, Component, Inject, ViewChild } from '@angular/core';
import { FormArray, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';
import { atLeastOneRequiredNestedValidator } from '@shared-user/utils/validators';

import { SubInstallation, WasteGasBalance, WasteGasBalanceEnergyFlowDataSourceDetails } from 'pmrv-api';

import { isProductBenchmark } from '../mmp-sub-installations-status';
import { wasteGasBalanceAddFormFactory } from './waste-gas-balance.component-form.provider';

@Component({
  selector: 'app-waste-gas-balance',
  templateUrl: './waste-gas-balance.component.html',
  styleUrl: './waste-gas-balance.component.scss',
  providers: [wasteGasBalanceAddFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteGasBalanceComponent extends ProductBenchmarkComponent implements PendingRequest {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;

  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles')?.valueChanges?.pipe(
    startWith(this.form.get('supportingFiles')?.value),
    map((value) => value?.length > 0),
  );

  wasteGasActivities: WasteGasBalance['wasteGasActivities'] = [
    'WASTE_GAS_PRODUCED',
    'WASTE_GAS_CONSUMED',
    'WASTE_GAS_FLARED',
    'WASTE_GAS_IMPORTED',
    'WASTE_GAS_EXPORTED',
  ];

  dataSource44Types: WasteGasBalanceEnergyFlowDataSourceDetails['entity'][] = [
    'METHOD_MONITORING_PLAN',
    'LEGAL_METROLOGICAL_CONTROL',
    'OPERATOR_CONTROL_NOT_POINT_B',
    'NOT_OPERATOR_CONTROL_NOT_POINT_B',
    'INDIRECT_DETERMINATION',
    'OTHER_METHODS',
  ];

  dataSource46Types: WasteGasBalanceEnergyFlowDataSourceDetails['energyContent'][] = [
    'CALCULATION_METHOD_MONITORING_PLAN',
    'LABORATORY_ANALYSES_SECTION_61',
    'SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62',
    'CONSTANT_VALUES_STANDARD_SUPPLIER',
    'CONSTANT_VALUES_SCIENTIFIC_EVIDENCE',
  ];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {
    super(store, router, route);
  }

  get dataSourcesFormArray(): FormArray {
    return this.form.get('dataSources') as FormArray;
  }

  addDataSource() {
    this.dataSourcesFormArray.push(
      new UntypedFormGroup(
        {
          ...this.wasteGasActivities.reduce((accum, activity) => {
            return this.form.value?.wasteGasActivities?.includes(activity)
              ? {
                  ...accum,
                  ...{
                    [activity]: new UntypedFormGroup({
                      entity: new UntypedFormControl(''),
                      energyContent: new UntypedFormControl(''),
                      emissionFactor: new UntypedFormControl(''),
                    }),
                  },
                }
              : accum;
          }, {}),
        },
        {
          validators: [
            atLeastOneRequiredNestedValidator(
              'Select at least one option in the data source group or remove the group',
            ),
          ],
        },
      ),
    );
  }

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }

  onSubmit(): void {
    combineLatest([this.permitTask$, this.store, this.route.paramMap])
      .pipe(
        first(),
        switchMap(([permitTask, state, paramMap]) => {
          const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
          const subInstallations = digitizedPlan && digitizedPlan?.subInstallations;
          const productBenchmarkIndex = subInstallations
            ?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
            .findIndex((x) => x.subInstallationNo == paramMap.get('subInstallationNo'));
          return this.store
            .patchTask(
              permitTask,
              {
                ...state.permit.monitoringMethodologyPlans,
                digitizedPlan: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                  subInstallations: subInstallations?.map((item) =>
                    item.subInstallationNo === paramMap.get('subInstallationNo')
                      ? {
                          ...item,
                          wasteGasBalance: {
                            ...this.form.value,
                            dataSources: this.form.value?.dataSources
                              ? [
                                  ...(this.form.value.dataSources as WasteGasBalance['dataSources']).reduce(
                                    (dataSourcesFormValuesAccum, dataSource, dataSourceIndex) => {
                                      dataSourcesFormValuesAccum.push({
                                        energyFlowDataSourceNo: String(dataSourceIndex),
                                        wasteGasActivityDetails: Object.keys(dataSource).reduce((accum, activity) => {
                                          const entity = dataSource?.[activity]?.entity || undefined;
                                          const energyContent = dataSource?.[activity]?.energyContent || undefined;
                                          const emissionFactor = dataSource?.[activity]?.emissionFactor || undefined;
                                          if (entity || energyContent || emissionFactor) {
                                            return {
                                              ...accum,
                                              [activity]: {
                                                entity,
                                                energyContent,
                                                emissionFactor,
                                              },
                                            };
                                          }
                                          return accum;
                                        }, {}),
                                      });
                                      return dataSourcesFormValuesAccum;
                                    },
                                    [],
                                  ),
                                ]
                              : null,
                            supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                          },
                        }
                      : {
                          ...item,
                        },
                  ),
                },
              },
              productBenchmarkIndex !== -1 &&
                state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark']?.length
                ? state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Product_Benchmark']?.map((item, idx) =>
                    productBenchmarkIndex === idx ? false : item,
                  )
                : state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark']
                  ? [...state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Product_Benchmark'], false]
                  : [false],
              'MMP_SUB_INSTALLATION_Product_Benchmark',
            )
            .pipe(this.pendingRequest.trackRequest());
        }),
        first(),
        switchMap(() => this.productBenchmarkType$),
      )
      .subscribe((productBenchmarkType) => {
        const nextStepMap: { [key in SubInstallation['subInstallationType']]?: string } = {
          REFINERY_PRODUCTS: 'calculation-refinery-products',
          LIME: 'calculation-lime',
          DOLIME: 'calculation-dolime',
          STEAM_CRACKING: 'calculation-steam-cracking',
          AROMATICS: 'calculation-aromatics',
          HYDROGEN: 'calculation-hydrogen',
          SYNTHESIS_GAS: 'calculation-synthesis-gas',
          ETHYLENE_OXIDE_ETHYLENE_GLYCOLS: 'calculation-ethylene-oxide-ethylene-glycols',
          VINYL_CHLORIDE_MONOMER: 'calculation-vinyl-chloride-monomer',
        };

        const nextStep = nextStepMap[productBenchmarkType] ?? 'summary';

        this.store.setState({
          ...this.store.getState(),
          permitAttachments: {
            ...this.store.getState().permitAttachments,
            ...this.form.value?.supportingFiles?.reduce(
              (result, item) => ({ ...result, [item.uuid]: item.file.name }),
              {},
            ),
          },
        });
        this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
      });
  }
}
