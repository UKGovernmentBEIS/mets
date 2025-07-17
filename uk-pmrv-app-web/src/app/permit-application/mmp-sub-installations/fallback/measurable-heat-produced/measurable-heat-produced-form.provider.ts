import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { isFallbackApproach } from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { MeasurableHeatProduced } from 'pmrv-api';

export const measurableHeatProducedAddFormFactory = {
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

    const faMeasurableHeatProduced: MeasurableHeatProduced = fallbackApproach?.measurableHeat?.measurableHeatProduced;

    const hierarchicalOrderFormGroup = fb.group({
      followed: [
        faMeasurableHeatProduced?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        faMeasurableHeatProduced?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        faMeasurableHeatProduced?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    const heatProducedDataSources = faMeasurableHeatProduced?.dataSources;

    const formGroup = fb.group(
      {
        produced: [
          faMeasurableHeatProduced?.produced ?? null,
          {
            validators: GovukValidators.required('Select yes if measurable heat is produced at this sub-installation'),
            updateOn: 'change',
          },
        ],
        ...(faMeasurableHeatProduced?.produced
          ? {
              dataSources: fb.array(
                heatProducedDataSources?.length
                  ? faMeasurableHeatProduced?.dataSources?.map((dataSource) => {
                      return fb.group({
                        heatProduced: [
                          dataSource?.heatProduced ?? '',
                          [GovukValidators.required('Select the data source used for heat produced')],
                        ],
                      });
                    })
                  : [
                      fb.group({
                        heatProduced: [
                          heatProducedDataSources?.[0]?.heatProduced ?? '',
                          [GovukValidators.required('Select the data source used for heat produced')],
                        ],
                      }),
                    ],
                [GovukValidators.required('Select the data sources used')],
              ),
            }
          : {}),

        ...(faMeasurableHeatProduced?.produced
          ? {
              methodologyAppliedDescription: [
                faMeasurableHeatProduced?.methodologyAppliedDescription ?? null,
                [
                  GovukValidators.required('Enter a description of the methodology used'),
                  GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
                ],
              ],
            }
          : {}),

        ...(faMeasurableHeatProduced?.produced ? { hierarchicalOrder: hierarchicalOrderFormGroup } : {}),

        ...(faMeasurableHeatProduced?.produced
          ? {
              supportingFiles: requestTaskFileService.buildFormControl(
                store.getState().requestTaskId,
                faMeasurableHeatProduced?.supportingFiles ?? [],
                store.getState().permitAttachments,
                store.getFileUploadSectionAttachmentActionContext(),
                false,
                !state.isEditable,
              ),
            }
          : {}),
      },
      {
        updateOn: 'change',
      },
    );

    formGroup?.controls?.produced?.valueChanges?.subscribe((val) => {
      if (val) {
        formGroup.addControl(
          'dataSources',
          fb.array(
            heatProducedDataSources?.length
              ? faMeasurableHeatProduced?.dataSources?.map((dataSource) => {
                  return fb.group({
                    heatProduced: [
                      dataSource?.heatProduced ?? '',
                      [GovukValidators.required('Select the data source used for heat produced')],
                    ],
                  });
                })
              : [
                  fb.group({
                    heatProduced: [
                      heatProducedDataSources?.[0]?.heatProduced ?? '',
                      [GovukValidators.required('Select the data source used for heat produced')],
                    ],
                  }),
                ],
            [GovukValidators.required('Select the data sources used')],
          ),
        );
        formGroup.addControl(
          'methodologyAppliedDescription',
          fb.control(faMeasurableHeatProduced?.methodologyAppliedDescription ?? null, [
            GovukValidators.required('Enter a description of the methodology used'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ]),
        );
        formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);
        formGroup.addControl(
          'supportingFiles',
          requestTaskFileService.buildFormControl(
            store.getState().requestTaskId,
            faMeasurableHeatProduced?.supportingFiles ?? [],
            store.getState().permitAttachments,
            store.getFileUploadSectionAttachmentActionContext(),
            false,
            !state.isEditable,
          ),
        );
      } else {
        formGroup.removeControl('dataSources');
        formGroup.removeControl('methodologyAppliedDescription');
        formGroup.removeControl('hierarchicalOrder');
        formGroup.removeControl('supportingFiles');
      }
      formGroup.updateValueAndValidity();
    });

    (formGroup.controls.hierarchicalOrder as FormGroup)?.controls?.followed?.valueChanges?.subscribe((val) => {
      if (val) {
        hierarchicalOrderFormGroup.controls?.notFollowingHierarchicalOrderReason?.clearValidators();
        hierarchicalOrderFormGroup.controls?.notFollowingHierarchicalOrderReason?.setValue(null);
        hierarchicalOrderFormGroup.controls?.notFollowingHierarchicalOrderDescription?.clearValidators();
        hierarchicalOrderFormGroup.controls?.notFollowingHierarchicalOrderDescription?.setValue(null);
      } else {
        hierarchicalOrderFormGroup.controls?.notFollowingHierarchicalOrderReason?.setValidators([
          GovukValidators.required('Select a reason for not following the hierarchy'),
        ]);
        hierarchicalOrderFormGroup.controls?.notFollowingHierarchicalOrderDescription?.setValidators([
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

    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
