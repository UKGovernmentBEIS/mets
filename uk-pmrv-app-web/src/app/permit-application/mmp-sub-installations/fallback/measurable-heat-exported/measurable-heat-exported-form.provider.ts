import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

import { MeasurableHeatExported } from 'pmrv-api';

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

    const formGroup = fb.group(
      {
        measurableHeatExported: [
          (fallbackApproach?.measurableHeat?.measurableHeatExported as MeasurableHeatExported)
            ?.measurableHeatExported ?? null,
          {
            validators: GovukValidators.required('Select yes if fuel input is relevant for this sub-installation'),
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
        fallbackApproach?.measurableHeat?.measurableHeatExported?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        fallbackApproach?.measurableHeat?.measurableHeatExported?.hierarchicalOrder
          ?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        fallbackApproach?.measurableHeat?.measurableHeatExported?.hierarchicalOrder
          ?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    formGroup?.controls?.measurableHeatExported?.valueChanges?.subscribe((val) => {
      if (val) {
        if (!formGroup.contains('dataSources')) {
          const dataSources = (fallbackApproach?.measurableHeat?.measurableHeatExported as MeasurableHeatExported)
            ?.dataSources;

          formGroup.addControl(
            'dataSources',
            fb.array(
              (fallbackApproach?.measurableHeat?.measurableHeatExported as MeasurableHeatExported)?.dataSources?.length
                ? (
                    fallbackApproach?.measurableHeat?.measurableHeatExported as MeasurableHeatExported
                  )?.dataSources?.map((dataSource, index) => {
                    return fb.group(
                      {
                        heatExported: [dataSource?.heatExported ?? ''],
                        netMeasurableHeatFlows: [
                          dataSource?.netMeasurableHeatFlows ?? '',
                          ...(index === 0
                            ? [GovukValidators.required('Select the net measurable heat flows exported')]
                            : []),
                        ],
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
                      heatExported: [dataSources?.[0]?.heatExported ?? ''],
                      netMeasurableHeatFlows: [
                        dataSources?.[0]?.netMeasurableHeatFlows ?? '',
                        [GovukValidators.required('Select the net measurable heat flows exported')],
                      ],
                    }),
                  ],
              [GovukValidators.required('Select the data sources used')],
            ),
          );

          formGroup.addControl(
            'methodologyAppliedDescription',
            fb.control(
              fallbackApproach?.measurableHeat?.measurableHeatExported?.methodologyAppliedDescription ?? null,
              [
                GovukValidators.required('Enter a description of the applied methodology'),
                GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
              ],
            ),
          );

          formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);

          formGroup.addControl(
            'methodologyDeterminationEmissionDescription',
            fb.control(
              fallbackApproach?.measurableHeat?.measurableHeatExported?.methodologyDeterminationEmissionDescription ??
                null,
              [
                GovukValidators.required(
                  'Enter a description of how the relevant attributable emission factors were determined',
                ),
                GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
              ],
            ),
          );

          formGroup.addControl(
            'supportingFiles',
            requestTaskFileService.buildFormControl(
              store.getState().requestTaskId,
              fallbackApproach?.measurableHeat?.measurableHeatExported?.supportingFiles ?? [],
              store.getState().permitAttachments,
              store.getFileUploadSectionAttachmentActionContext(),
              false,
              !state.isEditable,
            ),
          );
        }
      } else {
        formGroup.removeControl('methodologyAppliedDescription');
        formGroup.removeControl('methodologyDeterminationEmissionDescription');
        formGroup.removeControl('dataSources');
        formGroup.removeControl('hierarchicalOrder');
        formGroup.removeControl('supportingFiles');
      }
      formGroup.updateValueAndValidity();
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

    formGroup.controls.measurableHeatExported.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
