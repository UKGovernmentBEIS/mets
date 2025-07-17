import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

import { FuelInputAndRelevantEmissionFactorFA, FuelInputAndRelevantEmissionFactorHeatFA } from 'pmrv-api';

import { isFallbackApproach } from '../../mmp-sub-installations-status';

export const fuelInputRelevantEmissionFactorAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const fallbackApproach = store.permit.monitoringMethodologyPlans?.digitizedPlan?.subInstallations
      ?.filter((subInstallation) => isFallbackApproach(subInstallation.subInstallationType))
      ?.find((product) => product.subInstallationNo === route.snapshot.paramMap.get('subInstallationNo'));

    const faFactor: FuelInputAndRelevantEmissionFactorFA = fallbackApproach?.fuelInputAndRelevantEmissionFactor;
    const validHeatTypes = new Set(['HEAT_BENCHMARK_CL', 'HEAT_BENCHMARK_NON_CL', 'DISTRICT_HEATING_NON_CL']);
    const isHeatFA = validHeatTypes.has(fallbackApproach?.subInstallationType);
    const heatFAExists = (faFactor as FuelInputAndRelevantEmissionFactorHeatFA)?.exists;
    const isHeatFAExists = heatFAExists === true;

    const formGroup = fb.group(
      {
        ...(isHeatFA
          ? {
              exists: [
                heatFAExists ?? null,
                {
                  validators: GovukValidators.required(
                    'Select yes if fuel input is relevant for this sub-installation',
                  ),
                  updateOn: 'change',
                },
              ],
            }
          : {}),
        ...(isHeatFAExists || !isHeatFA
          ? {
              wasteGasesInput: [
                faFactor?.wasteGasesInput ?? null,
                {
                  validators: GovukValidators.required('Select yes if there is input from waste gases'),
                  updateOn: 'change',
                },
              ],
            }
          : { wasteGasesInput: null }),
      },
      {
        updateOn: 'change',
      },
    );

    const hierarchicalOrderFormGroup = fb.group({
      followed: [
        faFactor?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        faFactor?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        faFactor?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    const dataSourcesFormGroup = () => {
      const dataSources = faFactor?.dataSources;

      formGroup.addControl(
        'dataSources',
        fb.array(
          dataSources?.length
            ? dataSources?.map((dataSource, index) => {
                return fb.group(
                  {
                    fuelInput: [
                      dataSource?.fuelInput ?? '',
                      ...(index === 0 ? [GovukValidators.required('Select the data source used for fuel input')] : []),
                    ],
                    netCalorificValue: [
                      dataSource?.netCalorificValue ?? '',
                      ...(index === 0
                        ? [GovukValidators.required('Select the data source used for net calorific value')]
                        : []),
                    ],
                    weightedEmissionFactor: [
                      dataSource?.weightedEmissionFactor ?? '',
                      ...(index === 0
                        ? [GovukValidators.required('Select the data source used for weighted emission factor')]
                        : []),
                    ],
                    ...(faFactor?.wasteGasesInput
                      ? {
                          wasteGasFuelInput: [
                            dataSource?.wasteGasFuelInput ?? '',
                            ...(index === 0
                              ? [
                                  GovukValidators.required(
                                    'Select the data source used for fuel input from waste gases',
                                  ),
                                ]
                              : []),
                          ],
                          wasteGasNetCalorificValue: [
                            dataSource?.wasteGasNetCalorificValue ?? '',
                            ...(index === 0
                              ? [
                                  GovukValidators.required(
                                    'Select the data source used for net calorific value for waste gas',
                                  ),
                                ]
                              : []),
                          ],
                          emissionFactor: [
                            dataSource?.emissionFactor ?? '',
                            ...(index === 0
                              ? [GovukValidators.required('Select the data source used for the emission factor')]
                              : []),
                          ],
                        }
                      : {}),
                  },
                  index !== 0
                    ? {
                        validators: [
                          atLeastOneRequiredValidator(
                            'Select at least one option in the data source group or remove the group',
                          ),
                        ],
                      }
                    : {},
                );
              })
            : [
                fb.group({
                  fuelInput: [
                    dataSources?.[0]?.fuelInput ?? '',
                    [GovukValidators.required('Select the data source used for fuel input')],
                  ],
                  netCalorificValue: [
                    dataSources?.[0]?.netCalorificValue ?? '',
                    [GovukValidators.required('Select the data source used for net calorific value')],
                  ],
                  weightedEmissionFactor: [
                    dataSources?.[0]?.weightedEmissionFactor ?? '',
                    [GovukValidators.required('Select the data source used for weighted emission factor')],
                  ],
                  ...(faFactor?.wasteGasesInput
                    ? {
                        wasteGasFuelInput: [
                          dataSources?.[0]?.wasteGasFuelInput ?? '',
                          [GovukValidators.required('Select the data source used for fuel input from waste gases')],
                        ],
                        wasteGasNetCalorificValue: [
                          dataSources?.[0]?.wasteGasNetCalorificValue ?? '',
                          [
                            GovukValidators.required(
                              'Select the data source used for net calorific value for waste gas',
                            ),
                          ],
                        ],
                        emissionFactor: [
                          dataSources?.[0]?.emissionFactor ?? '',
                          [GovukValidators.required('Select the data source used for the emission factor')],
                        ],
                      }
                    : {}),
                }),
              ],
          [GovukValidators.required('Select the data sources used')],
        ),
      );
    };

    const methodologyAppliedDescriptionForm = () => {
      formGroup.addControl(
        'methodologyAppliedDescription',
        fb.control(faFactor?.methodologyAppliedDescription ?? null, [
          GovukValidators.required('Enter a description of the applied methodology'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]),
      );
    };

    const supportingFilesForm = () => {
      formGroup.addControl(
        'supportingFiles',
        requestTaskFileService.buildFormControl(
          store.getState().requestTaskId,
          faFactor?.supportingFiles ?? [],
          store.getState().permitAttachments,
          store.getFileUploadSectionAttachmentActionContext(),
          false,
          !state.isEditable,
        ),
      );
    };

    if (!formGroup.contains('dataSources') || !isHeatFA || isHeatFAExists) {
      dataSourcesFormGroup();
    }

    if (!isHeatFA || isHeatFAExists) {
      formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);
      formGroup.updateValueAndValidity();
    } else {
      formGroup.removeControl('hierarchicalOrder');
      formGroup.updateValueAndValidity();
    }

    formGroup?.controls?.exists?.valueChanges?.subscribe((val) => {
      if (val) {
        dataSourcesFormGroup();
        methodologyAppliedDescriptionForm();
        formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);
        supportingFilesForm();
      } else {
        formGroup.removeControl('dataSources');
        formGroup.removeControl('methodologyAppliedDescription');
        formGroup.removeControl('hierarchicalOrder');
        formGroup.removeControl('supportingFiles');
      }
      formGroup.updateValueAndValidity();
    });

    formGroup?.controls?.wasteGasesInput?.valueChanges?.subscribe((val) => {
      if (val) {
        ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control, index) => {
          control.addControl(
            'wasteGasFuelInput',
            fb.control(
              '',
              ...(index === 0
                ? [GovukValidators.required('Select the data source used for fuel input from waste gases')]
                : []),
            ),
          );
          control.addControl(
            'wasteGasNetCalorificValue',
            fb.control(
              '',
              ...(index === 0
                ? [GovukValidators.required('Select the data source used for net calorific value for waste gas')]
                : []),
            ),
          );
          control.addControl(
            'emissionFactor',
            fb.control(
              '',
              ...(index === 0 ? [GovukValidators.required('Select the data source used for the emission factor')] : []),
            ),
          );
          control.updateValueAndValidity();
        });
      } else {
        if (formGroup.contains('dataSources')) {
          ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
            control.removeControl('wasteGasFuelInput');
            control.removeControl('wasteGasNetCalorificValue');
            control.removeControl('emissionFactor');
            control.updateValueAndValidity();
          });
        }
      }
    });

    (formGroup.controls.hierarchicalOrder as FormGroup)?.controls?.followed?.valueChanges?.subscribe((val) => {
      if (val) {
        hierarchicalOrderFormGroup?.controls?.notFollowingHierarchicalOrderReason?.clearValidators();
        hierarchicalOrderFormGroup?.controls?.notFollowingHierarchicalOrderReason?.setValue(null);
        hierarchicalOrderFormGroup?.controls?.notFollowingHierarchicalOrderDescription?.clearValidators();
        hierarchicalOrderFormGroup?.controls?.notFollowingHierarchicalOrderDescription?.setValue(null);
      } else {
        hierarchicalOrderFormGroup?.controls?.notFollowingHierarchicalOrderReason?.setValidators([
          GovukValidators.required('Select a reason for not following the hierarchy'),
        ]);
        hierarchicalOrderFormGroup?.controls?.notFollowingHierarchicalOrderDescription?.setValidators([
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]);
      }

      (
        formGroup.controls.hierarchicalOrder as FormGroup
      ).controls.notFollowingHierarchicalOrderDescription.updateValueAndValidity();
      (
        formGroup.controls.hierarchicalOrder as FormGroup
      ).controls.notFollowingHierarchicalOrderReason.updateValueAndValidity();
      formGroup.controls.hierarchicalOrder.updateValueAndValidity();
    });

    // Set initial values for the form controls
    methodologyAppliedDescriptionForm();
    supportingFilesForm();

    if (isHeatFA) {
      formGroup.controls.exists.updateValueAndValidity({ emitEvent: true });
    }
    formGroup.controls.wasteGasesInput.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
