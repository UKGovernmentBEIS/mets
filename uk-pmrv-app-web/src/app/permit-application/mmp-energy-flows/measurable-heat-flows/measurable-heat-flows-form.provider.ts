import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

export const measurableHeatFlowsAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const measurableHeatFlows =
      store.permit.monitoringMethodologyPlans?.digitizedPlan?.energyFlows?.measurableHeatFlows;

    const formGroup = fb.group(
      {
        measurableHeatFlowsRelevant: [
          measurableHeatFlows?.measurableHeatFlowsRelevant ?? null,
          {
            validators: GovukValidators.required(
              'Select yes if measurable heat flows are relevant for the installation',
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
        measurableHeatFlows?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        measurableHeatFlows?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        measurableHeatFlows?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    formGroup?.controls?.measurableHeatFlowsRelevant?.valueChanges?.subscribe((val) => {
      if (val) {
        if (!formGroup.contains('measurableHeatFlowsDataSources')) {
          const dataSources = measurableHeatFlows?.measurableHeatFlowsDataSources;

          formGroup.addControl(
            'measurableHeatFlowsDataSources',
            fb.array(
              measurableHeatFlows?.measurableHeatFlowsDataSources?.length
                ? measurableHeatFlows?.measurableHeatFlowsDataSources?.map((dataSource, index) => {
                    return fb.group(
                      {
                        quantification: [
                          dataSource?.quantification ?? '',
                          index === 0
                            ? [
                                GovukValidators.required(
                                  'Select the data source for quantification of measurable heat flows',
                                ),
                              ]
                            : [],
                        ],
                        net: [
                          dataSource?.net ?? '',
                          index === 0
                            ? [GovukValidators.required('Select the data source for net measurable heat flows')]
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
                        [
                          GovukValidators.required(
                            'Select the data source for quantification of measurable heat flows',
                          ),
                        ],
                      ],
                      net: [
                        dataSources?.[0]?.net ?? '',
                        [GovukValidators.required('Select the data source for net measurable heat flows')],
                      ],
                    }),
                  ],
              [GovukValidators.required('Select the data sources used')],
            ),
          );

          formGroup.addControl(
            'methodologyAppliedDescription',
            fb.control(measurableHeatFlows?.methodologyAppliedDescription ?? null, [
              GovukValidators.required('Enter a description of the applied methodology'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]),
          );

          formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);

          formGroup.addControl(
            'supportingFiles',
            requestTaskFileService.buildFormControl(
              store.getState().requestTaskId,
              measurableHeatFlows?.supportingFiles ?? [],
              store.getState().permitAttachments,
              store.getFileUploadSectionAttachmentActionContext(),
              false,
              !state.isEditable,
            ),
          );
        }
      } else {
        formGroup.removeControl('methodologyAppliedDescription');
        formGroup.removeControl('measurableHeatFlowsDataSources');
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

    formGroup.controls.measurableHeatFlowsRelevant.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
