import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { pairwise } from 'rxjs';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredNestedValidator } from '@shared-user/utils/validators';

import { GovukValidators } from 'govuk-components';

import { WasteGasBalance } from 'pmrv-api';

import { isProductBenchmark } from '../mmp-sub-installations-status';

export const wasteGasBalanceAddFormFactory = {
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

    const wasteGasActivities: WasteGasBalance['wasteGasActivities'] = [
      'WASTE_GAS_PRODUCED',
      'WASTE_GAS_CONSUMED',
      'WASTE_GAS_FLARED',
      'WASTE_GAS_IMPORTED',
      'WASTE_GAS_EXPORTED',
    ];

    const formGroup = fb.group(
      {
        wasteGasActivities: [
          productBenchmark?.wasteGasBalance?.wasteGasActivities,
          {
            validators: GovukValidators.required(
              'Select whether you have any waste gas activites at this sub-installation',
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
        productBenchmark?.wasteGasBalance?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        productBenchmark?.wasteGasBalance?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        productBenchmark?.wasteGasBalance?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    if (!formGroup.contains('dataSourcesMethodologyAppliedDescription')) {
      formGroup.addControl(
        'dataSourcesMethodologyAppliedDescription',
        fb.control(productBenchmark?.wasteGasBalance?.dataSourcesMethodologyAppliedDescription ?? null, [
          GovukValidators.required('Enter a description of the applied methodology'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]),
      );
    }

    formGroup?.controls?.wasteGasActivities?.valueChanges?.subscribe((val) => {
      if (val !== null && val?.length > 0 && !val?.includes('NO_WASTE_GAS_ACTIVITIES')) {
        const dataSources = productBenchmark?.wasteGasBalance?.dataSources;

        const formArray = fb.array(
          dataSources?.length
            ? dataSources?.map((dataSource) => {
                return fb.group(
                  {
                    ...wasteGasActivities.reduce(
                      (accum, activity) =>
                        val?.includes(activity)
                          ? {
                              ...accum,
                              ...{
                                [activity]: fb.group({
                                  entity: dataSource?.wasteGasActivityDetails?.[activity]?.entity ?? '',
                                  energyContent: dataSource?.wasteGasActivityDetails?.[activity]?.energyContent ?? '',
                                  emissionFactor: dataSource?.wasteGasActivityDetails?.[activity]?.emissionFactor ?? '',
                                }),
                              },
                            }
                          : accum,
                      {},
                    ),
                  },
                  {
                    validators: [
                      atLeastOneRequiredNestedValidator(
                        'Select at least one option in the data source group or remove the group',
                      ),
                    ],
                  },
                );
              })
            : [
                fb.group(
                  {
                    ...wasteGasActivities.reduce(
                      (accum, activity) =>
                        val?.includes(activity)
                          ? {
                              [activity]: fb.group({
                                entity: dataSources?.[0]?.wasteGasActivityDetails?.[activity]?.entity ?? '',
                                energyContent:
                                  dataSources?.[0]?.wasteGasActivityDetails?.[activity]?.energyContent ?? '',
                                emissionFactor:
                                  dataSources?.[0]?.wasteGasActivityDetails?.[activity]?.emissionFactor ?? '',
                              }),
                            }
                          : accum,
                      {},
                    ),
                  },
                  {
                    validators: [
                      atLeastOneRequiredNestedValidator(
                        'Select at least one option in the data source group or remove the group',
                      ),
                    ],
                  },
                ),
              ],
          [GovukValidators.required('Select the data sources used')],
        );

        if (!formGroup.contains('dataSources')) {
          formGroup.addControl('dataSources', formArray);
        } else {
          wasteGasActivities.forEach((activity) => {
            if (val?.includes(activity)) {
              ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
                control.addControl(
                  activity,
                  fb.group({
                    entity: fb.control(''),
                    energyContent: fb.control(''),
                    emissionFactor: fb.control(''),
                  }),
                );
                control.updateValueAndValidity();
              });
            } else {
              ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
                control.removeControl(activity);
                control.updateValueAndValidity();
              });
            }
          });
        }

        if (!formGroup.contains('hierarchicalOrder')) {
          formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);
        }

        if (!formGroup.contains('dataSourcesMethodologyAppliedDescription')) {
          formGroup.addControl(
            'dataSourcesMethodologyAppliedDescription',
            fb.control(productBenchmark?.wasteGasBalance?.dataSourcesMethodologyAppliedDescription ?? null, [
              GovukValidators.required('Enter a description of the applied methodology'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]),
          );
        }

        formGroup.addControl(
          'supportingFiles',
          requestTaskFileService.buildFormControl(
            store.getState().requestTaskId,
            productBenchmark?.wasteGasBalance?.supportingFiles ?? [],
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
        formGroup.removeControl('supportingFiles');
      }
    });

    formGroup?.controls?.wasteGasActivities?.valueChanges?.pipe(pairwise()).subscribe(([previousVal, val]) => {
      if (!previousVal?.includes('NO_WASTE_GAS_ACTIVITIES') && val.includes('NO_WASTE_GAS_ACTIVITIES')) {
        formGroup.get('wasteGasActivities').patchValue(val?.filter((x) => x === 'NO_WASTE_GAS_ACTIVITIES'));
      }
      if (previousVal?.includes('NO_WASTE_GAS_ACTIVITIES') && val?.length > 1) {
        formGroup.get('wasteGasActivities').patchValue(val?.filter((x) => x !== 'NO_WASTE_GAS_ACTIVITIES'));
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

    formGroup.controls?.wasteGasActivities?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.dataSources?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.dataSourcesMethodologyAppliedDescription?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
