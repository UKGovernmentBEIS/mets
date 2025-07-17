import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

export const fuelInputFlowsAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const energyFlows = store.permit.monitoringMethodologyPlans?.digitizedPlan?.energyFlows;

    const hierarchicalOrderFormGroup = fb.group({
      followed: [
        energyFlows?.fuelInputFlows?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        energyFlows?.fuelInputFlows?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        energyFlows?.fuelInputFlows?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    const fuelInputDataSources = energyFlows?.fuelInputFlows?.fuelInputDataSources;
    const formGroup = fb.group(
      {
        fuelInputDataSources: fb.array(
          fuelInputDataSources?.length
            ? fuelInputDataSources?.map((dataSource, index) => {
                return fb.group(
                  {
                    fuelInput: [
                      dataSource?.fuelInput ?? '',
                      index === 0 ? [GovukValidators.required('Select the data source for fuel input')] : [],
                    ],
                    energyContent: [
                      dataSource?.energyContent ?? '',
                      index === 0 ? [GovukValidators.required('Select the data source for energy content')] : [],
                    ],
                  },
                  {
                    validators:
                      index > 0
                        ? [
                            atLeastOneRequiredValidator(
                              'Select at least one option in the data source group or remove the group',
                            ),
                          ]
                        : [],
                  },
                );
              })
            : [
                fb.group({
                  fuelInput: [
                    fuelInputDataSources?.[0]?.fuelInput ?? '',
                    [GovukValidators.required('Select the data source for fuel input')],
                  ],
                  energyContent: [
                    fuelInputDataSources?.[0]?.energyContent ?? '',
                    [GovukValidators.required('Select the data source for energy content')],
                  ],
                }),
              ],
          [GovukValidators.required('Select the data sources used')],
        ),

        methodologyAppliedDescription: [
          energyFlows?.fuelInputFlows?.methodologyAppliedDescription ?? null,
          [
            GovukValidators.required('Enter a description of the methodology applied for each data source'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        hierarchicalOrder: hierarchicalOrderFormGroup,

        supportingFiles: requestTaskFileService.buildFormControl(
          store.getState().requestTaskId,
          energyFlows?.fuelInputFlows?.supportingFiles ?? [],
          store.getState().permitAttachments,
          store.getFileUploadSectionAttachmentActionContext(),
          false,
          !state.isEditable,
        ),
      },
      {
        updateOn: 'change',
      },
    );

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
