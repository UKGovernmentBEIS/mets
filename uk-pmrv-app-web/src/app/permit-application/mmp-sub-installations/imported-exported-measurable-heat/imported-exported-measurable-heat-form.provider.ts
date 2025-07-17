import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { pairwise } from 'rxjs';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

import { isProductBenchmark } from '../mmp-sub-installations-status';

export const importedExportedMeasurableHeatAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const productBenchmark = store.permit.monitoringMethodologyPlans?.digitizedPlan?.subInstallations
      ?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
      ?.find((product) => product.subInstallationNo === route.snapshot.paramMap.get('subInstallationNo'));

    const formGroup = fb.group(
      {
        fuelBurnCalculationTypes: [
          productBenchmark?.importedExportedMeasurableHeat?.fuelBurnCalculationTypes ?? null,
          {
            validators: GovukValidators.required(
              'Select whether measurable heat is imported to or exported from this sub-installation',
            ),
            updateOn: 'change',
          },
        ],
      },
      {
        updateOn: 'change',
      },
    );

    const hierarchicalOrderFormGroup = fb.group({
      followed: [
        productBenchmark?.importedExportedMeasurableHeat?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        productBenchmark?.importedExportedMeasurableHeat?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ??
          null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        productBenchmark?.importedExportedMeasurableHeat?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ??
          null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    formGroup?.controls?.fuelBurnCalculationTypes?.valueChanges?.subscribe((val) => {
      if (val !== null && val?.length > 0 && !val?.includes('NO_MEASURABLE_HEAT')) {
        const dataSources = productBenchmark?.importedExportedMeasurableHeat?.dataSources;
        const formArray = fb.array(
          productBenchmark?.importedExportedMeasurableHeat?.dataSources?.length
            ? productBenchmark?.importedExportedMeasurableHeat?.dataSources?.map((dataSource, index) => {
                return fb.group(
                  {
                    ...(val?.includes('MEASURABLE_HEAT_IMPORTED')
                      ? {
                          measurableHeatImported: [dataSource?.measurableHeatImported ?? ''],
                        }
                      : {}),
                    ...(val?.includes('MEASURABLE_HEAT_FROM_PULP')
                      ? {
                          measurableHeatPulp: [dataSource?.measurableHeatPulp ?? ''],
                        }
                      : {}),

                    ...(val?.includes('MEASURABLE_HEAT_FROM_NITRIC_ACID')
                      ? {
                          measurableHeatNitricAcid: [dataSource?.measurableHeatNitricAcid ?? ''],
                        }
                      : {}),
                    ...(val?.includes('MEASURABLE_HEAT_EXPORTED')
                      ? {
                          measurableHeatExported: [dataSource?.measurableHeatExported ?? ''],
                        }
                      : {}),
                    netMeasurableHeatFlows: [
                      dataSource?.netMeasurableHeatFlows ?? '',
                      index === 0 ? [GovukValidators.required('Select the net measurable heat flows')] : [],
                    ],
                  },
                  {
                    validators:
                      index !== 0
                        ? atLeastOneRequiredValidator(
                            'Select at least one option in the data source group or remove the group',
                          )
                        : null,
                  },
                );
              })
            : [
                fb.group({
                  ...(val?.includes('MEASURABLE_HEAT_IMPORTED')
                    ? {
                        measurableHeatImported: [dataSources?.[0]?.measurableHeatImported ?? ''],
                      }
                    : {}),
                  ...(val?.includes('MEASURABLE_HEAT_FROM_PULP')
                    ? {
                        measurableHeatPulp: [dataSources?.[0]?.measurableHeatPulp ?? ''],
                      }
                    : {}),
                  ...(val?.includes('MEASURABLE_HEAT_FROM_NITRIC_ACID')
                    ? {
                        measurableHeatNitricAcid: [dataSources?.[0]?.measurableHeatNitricAcid ?? ''],
                      }
                    : {}),
                  ...(val?.includes('MEASURABLE_HEAT_EXPORTED')
                    ? {
                        measurableHeatExported: [dataSources?.[0]?.measurableHeatExported ?? ''],
                      }
                    : {}),
                  netMeasurableHeatFlows: [
                    dataSources?.[0]?.netMeasurableHeatFlows ?? '',
                    [GovukValidators.required('Select the net measurable heat flows')],
                  ],
                }),
              ],
          [GovukValidators.required('Select the data sources used')],
        );

        if (!formGroup.contains('dataSources')) {
          formGroup.addControl('dataSources', formArray);
        } else {
          if (val?.includes('MEASURABLE_HEAT_IMPORTED')) {
            ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
              control.addControl('measurableHeatImported', fb.control(''));
              control.updateValueAndValidity();
            });
          } else {
            ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
              control.removeControl('measurableHeatImported');
              control.updateValueAndValidity();
            });
          }

          if (val?.includes('MEASURABLE_HEAT_FROM_PULP')) {
            ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
              control.addControl('measurableHeatPulp', fb.control(''));
              control.updateValueAndValidity();
            });
          } else {
            ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
              control.removeControl('measurableHeatPulp');
              control.updateValueAndValidity();
            });
          }

          if (val?.includes('MEASURABLE_HEAT_FROM_NITRIC_ACID')) {
            ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
              control.addControl('measurableHeatNitricAcid', fb.control(''));
              control.updateValueAndValidity();
            });
          } else {
            ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
              control.removeControl('measurableHeatNitricAcid');
              control.updateValueAndValidity();
            });
          }

          if (val?.includes('MEASURABLE_HEAT_EXPORTED')) {
            ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
              control.addControl('measurableHeatExported', fb.control(''));
              control.updateValueAndValidity();
            });
          } else {
            ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
              control.removeControl('measurableHeatExported');
              control.updateValueAndValidity();
            });
          }
        }

        if (!formGroup.contains('dataSourcesMethodologyAppliedDescription')) {
          formGroup.addControl(
            'dataSourcesMethodologyAppliedDescription',
            fb.control(
              productBenchmark?.importedExportedMeasurableHeat?.dataSourcesMethodologyAppliedDescription ?? null,
              [
                GovukValidators.required('Enter a description of the applied methodology'),
                GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
              ],
            ),
          );
        }
        if (!formGroup.contains('hierarchicalOrder')) {
          formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);
        }
        if (!formGroup.contains('methodologyDeterminationDescription')) {
          formGroup.addControl(
            'methodologyDeterminationDescription',
            fb.control(productBenchmark?.importedExportedMeasurableHeat?.methodologyDeterminationDescription ?? null, [
              GovukValidators.required('Enter a description of how relevant emission factors were determined'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]),
          );
        }

        if (!formGroup.contains('measurableHeatImportedFromPulp')) {
          formGroup.addControl(
            'measurableHeatImportedFromPulp',
            fb.control(productBenchmark?.importedExportedMeasurableHeat?.measurableHeatImportedFromPulp ?? null, {
              validators: [
                GovukValidators.required(
                  'Select yes if measurable heat flows imported from sub-installations producing pulp are relevant',
                ),
              ],
              updateOn: 'change',
            }),
          );

          formGroup.addControl(
            'pulpMethodologyDeterminationDescription',
            fb.control(
              productBenchmark?.importedExportedMeasurableHeat?.pulpMethodologyDeterminationDescription ?? null,
              [GovukValidators.required('Enter a description of the applied methodology')],
            ),
          );
        }

        formGroup.addControl(
          'supportingFiles',
          requestTaskFileService.buildFormControl(
            store.getState().requestTaskId,
            productBenchmark?.importedExportedMeasurableHeat?.supportingFiles ?? [],
            store.getState().permitAttachments,
            store.getFileUploadSectionAttachmentActionContext(),
            false,
            !state.isEditable,
          ),
        );
      } else {
        formGroup.removeControl('dataSources');
        formGroup.removeControl('dataSourcesMethodologyAppliedDescription');
        formGroup.removeControl('hierarchicalOrder');
        formGroup.removeControl('methodologyDeterminationDescription');
        formGroup.removeControl('measurableHeatImportedFromPulp');
        formGroup.removeControl('supportingFiles');
      }
    });

    formGroup?.controls?.fuelBurnCalculationTypes?.valueChanges?.pipe(pairwise()).subscribe(([previousVal, val]) => {
      if (!previousVal?.includes('NO_MEASURABLE_HEAT') && val.includes('NO_MEASURABLE_HEAT')) {
        formGroup.get('fuelBurnCalculationTypes').patchValue(val?.filter((x) => x === 'NO_MEASURABLE_HEAT'));
      }
      if (previousVal?.includes('NO_MEASURABLE_HEAT') && val?.length > 1) {
        formGroup.get('fuelBurnCalculationTypes').patchValue(val?.filter((x) => x !== 'NO_MEASURABLE_HEAT'));
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

    formGroup?.controls?.measurableHeatImportedFromPulp?.valueChanges?.subscribe((val) => {
      if (val) {
        hierarchicalOrderFormGroup?.controls?.notFollowingHierarchicalOrderDescription?.setValidators([
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]);
      } else {
        hierarchicalOrderFormGroup?.controls?.notFollowingHierarchicalOrderDescription?.clearValidators();
        hierarchicalOrderFormGroup?.controls?.notFollowingHierarchicalOrderDescription?.setValue(null);
      }
    });

    formGroup.controls?.fuelBurnCalculationTypes.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.measurableHeatImportedFromPulp?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.dataSourcesMethodologyAppliedDescription?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
