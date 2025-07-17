import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

import { WasteGasFlows } from 'pmrv-api';

export const wasteGasFlowsAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const wasteGasFlows: WasteGasFlows =
      store.permit.monitoringMethodologyPlans?.digitizedPlan?.energyFlows?.wasteGasFlows;

    const formGroup = fb.group(
      {
        wasteGasFlowsRelevant: [
          wasteGasFlows?.wasteGasFlowsRelevant ?? null,
          {
            validators: GovukValidators.required('Select yes if waste gas flows are relevant for the installation'),
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
        wasteGasFlows?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        wasteGasFlows?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        wasteGasFlows?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    formGroup?.controls?.wasteGasFlowsRelevant?.valueChanges?.subscribe((val) => {
      if (val) {
        if (!formGroup.contains('wasteGasFlowsDataSources')) {
          const dataSources = wasteGasFlows?.wasteGasFlowsDataSources;

          formGroup.addControl(
            'wasteGasFlowsDataSources',
            fb.array(
              wasteGasFlows?.wasteGasFlowsDataSources?.length
                ? wasteGasFlows?.wasteGasFlowsDataSources?.map((dataSource, index) => {
                    return fb.group(
                      {
                        quantification: [
                          dataSource?.quantification ?? '',
                          index === 0
                            ? [GovukValidators.required('Select the data source for quantification of waste gas flows')]
                            : [],
                        ],
                        energyContent: [
                          dataSource?.energyContent ?? '',
                          index === 0
                            ? [GovukValidators.required('Select the data source for energy content of waste gases')]
                            : [],
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
                      quantification: [
                        dataSources?.[0]?.quantification ?? '',
                        [GovukValidators.required('Select the data source for quantification of waste gas flows')],
                      ],
                      energyContent: [
                        dataSources?.[0]?.energyContent ?? '',
                        [GovukValidators.required('Select the data source for energy content of waste gases')],
                      ],
                    }),
                  ],
              [GovukValidators.required('Select the data sources used')],
            ),
          );

          formGroup.addControl(
            'methodologyAppliedDescription',
            fb.control(wasteGasFlows?.methodologyAppliedDescription ?? null, [
              GovukValidators.required('Enter a description of the applied methodology'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]),
          );

          formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);

          formGroup.addControl(
            'supportingFiles',
            requestTaskFileService.buildFormControl(
              store.getState().requestTaskId,
              wasteGasFlows?.supportingFiles ?? [],
              store.getState().permitAttachments,
              store.getFileUploadSectionAttachmentActionContext(),
              false,
              !state.isEditable,
            ),
          );
        }
      } else {
        formGroup.removeControl('methodologyAppliedDescription');
        formGroup.removeControl('wasteGasFlowsDataSources');
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

    formGroup.controls.wasteGasFlowsRelevant.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
