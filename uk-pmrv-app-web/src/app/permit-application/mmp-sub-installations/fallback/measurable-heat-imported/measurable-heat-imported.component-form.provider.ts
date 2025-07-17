import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { pairwise } from 'rxjs';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { MeasurableHeatLabelPipe } from '@permit-application/shared/pipes/measurable-heat-label.pipe';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredNestedValidator } from '@shared-user/utils/validators';

import { GovukValidators } from 'govuk-components';

import { MeasurableHeatImported } from 'pmrv-api';

import { isFallbackApproach } from '../../mmp-sub-installations-status';

export const measurableHeatImportedAddFormFactory = {
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

    const measurableHeatImportedActivities: MeasurableHeatImported['measurableHeatImportedActivities'] = [
      'MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES',
      'MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK',
      'MEASURABLE_HEAT_IMPORTED_PULP',
      'MEASURABLE_HEAT_IMPORTED_FUEL_BENCHMARK',
      'MEASURABLE_HEAT_IMPORTED_WASTE_GAS',
      'MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED',
    ];

    const measurableHeatPipe = new MeasurableHeatLabelPipe();

    const createError = (activity, param) =>
      `${param !== 'net' ? 'Select the measurable heat' : 'Select the'} ${measurableHeatPipe.transform(activity, param).toLowerCase()}`;

    const formGroup = fb.group(
      {
        measurableHeatImportedActivities: [
          fallbackApproach?.measurableHeat?.measurableHeatImported?.measurableHeatImportedActivities,
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
        fallbackApproach?.measurableHeat?.measurableHeatImported?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        fallbackApproach?.measurableHeat?.measurableHeatImported?.hierarchicalOrder
          ?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        fallbackApproach?.measurableHeat?.measurableHeatImported?.hierarchicalOrder
          ?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    formGroup?.controls?.measurableHeatImportedActivities?.valueChanges?.subscribe((val) => {
      if (val !== null && val?.length > 0 && !val?.includes('MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED')) {
        const dataSources = fallbackApproach?.measurableHeat?.measurableHeatImported?.dataSources;

        const formArray = fb.array(
          dataSources?.length
            ? dataSources?.map((dataSource, index) => {
                return fb.group(
                  {
                    ...measurableHeatImportedActivities.reduce(
                      (accum, activity) =>
                        val?.includes(activity)
                          ? {
                              ...accum,
                              ...{
                                [activity]: fb.group({
                                  entity: [
                                    dataSource?.measurableHeatImportedActivityDetails?.[activity]?.entity ?? '',
                                    ...(index === 0
                                      ? [GovukValidators.required(createError(activity, 'imported'))]
                                      : []),
                                  ],
                                  netContent: [
                                    dataSource?.measurableHeatImportedActivityDetails?.[activity]?.netContent ?? '',
                                    ...(index === 0 ? [GovukValidators.required(createError(activity, 'net'))] : []),
                                  ],
                                }),
                              },
                            }
                          : accum,
                      {},
                    ),
                  },
                  index > 0
                    ? {
                        validators: [
                          atLeastOneRequiredNestedValidator(
                            'Select at least one option in the data source group or remove the group',
                          ),
                        ],
                      }
                    : {},
                );
              })
            : [
                fb.group({
                  ...measurableHeatImportedActivities.reduce(
                    (accum, activity) =>
                      val?.includes(activity)
                        ? {
                            [activity]: fb.group({
                              entity: [
                                dataSources?.[0]?.measurableHeatImportedActivityDetails?.[activity]?.entity ?? '',
                                [GovukValidators.required(createError(activity, 'imported'))],
                              ],
                              netContent: [
                                dataSources?.[0]?.measurableHeatImportedActivityDetails?.[activity]?.netContent ?? '',
                                [GovukValidators.required(createError(activity, 'net'))],
                              ],
                            }),
                          }
                        : accum,
                    {},
                  ),
                }),
              ],
          [GovukValidators.required('Select the data sources used')],
        );

        if (!formGroup.contains('dataSources')) {
          formGroup.addControl('dataSources', formArray);
        } else {
          measurableHeatImportedActivities.forEach((activity) => {
            if (val?.includes(activity)) {
              ((formGroup.get('dataSources') as FormArray).controls as FormGroup[])?.forEach((control) => {
                control.addControl(
                  activity,
                  fb.group({
                    entity: ['', [GovukValidators.required(createError(activity, 'imported'))]],
                    netContent: ['', [GovukValidators.required(createError(activity, 'net'))]],
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

        if (!formGroup.contains('methodologyAppliedDescription')) {
          formGroup.addControl(
            'methodologyAppliedDescription',
            fb.control(
              fallbackApproach?.measurableHeat?.measurableHeatImported?.methodologyAppliedDescription ?? null,
              [
                GovukValidators.required('Enter a description of the applied methodology'),
                GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
              ],
            ),
          );
        }

        if (!formGroup.contains('methodologyDeterminationEmissionDescription')) {
          formGroup.addControl(
            'methodologyDeterminationEmissionDescription',
            fb.control(
              fallbackApproach?.measurableHeat?.measurableHeatImported?.methodologyDeterminationEmissionDescription ??
                null,
              [
                GovukValidators.required(
                  'Enter a description of how the relevant attributable emission factors are determined',
                ),
                GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
              ],
            ),
          );
        }

        formGroup.addControl(
          'supportingFiles',
          requestTaskFileService.buildFormControl(
            store.getState().requestTaskId,
            fallbackApproach?.measurableHeat?.measurableHeatImported?.supportingFiles ?? [],
            store.getState().permitAttachments,
            store.getFileUploadSectionAttachmentActionContext(),
            false,
            !state.isEditable,
          ),
        );
      } else {
        formGroup.removeControl('dataSources');
        formGroup.removeControl('methodologyAppliedDescription');
        formGroup.removeControl('methodologyDeterminationEmissionDescription');
        formGroup.removeControl('hierarchicalOrder');
        formGroup.removeControl('supportingFiles');
      }
    });

    formGroup?.controls?.measurableHeatImportedActivities?.valueChanges
      ?.pipe(pairwise())
      .subscribe(([previousVal, val]) => {
        if (
          !previousVal?.includes('MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED') &&
          val.includes('MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED')
        ) {
          formGroup
            .get('measurableHeatImportedActivities')
            .patchValue(val?.filter((x) => x === 'MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED'));
        }
        if (previousVal?.includes('MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED') && val?.length > 1) {
          formGroup
            .get('measurableHeatImportedActivities')
            .patchValue(val?.filter((x) => x !== 'MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED'));
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

    formGroup.controls?.measurableHeatImportedActivities?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.dataSources?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.methodologyAppliedDescription?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.methodologyDeterminationEmissionDescription?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
