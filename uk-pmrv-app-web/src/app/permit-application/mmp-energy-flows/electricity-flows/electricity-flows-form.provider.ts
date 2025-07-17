import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { ElectricityFlows } from 'pmrv-api';

export const electricityFlowsAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const electricityFlows: ElectricityFlows =
      store.permit.monitoringMethodologyPlans?.digitizedPlan?.energyFlows?.electricityFlows;

    const formGroup = fb.group(
      {
        electricityProduced: [
          electricityFlows?.electricityProduced ?? null,
          {
            validators: GovukValidators.required('Select yes if electricity is produced within the installation'),
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
        electricityFlows?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        electricityFlows?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        electricityFlows?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    formGroup?.controls?.electricityProduced?.valueChanges?.subscribe((val) => {
      if (val) {
        if (!formGroup.contains('electricityFlowsDataSources')) {
          const dataSources = electricityFlows?.electricityFlowsDataSources;

          formGroup.addControl(
            'electricityFlowsDataSources',
            fb.array(
              electricityFlows?.electricityFlowsDataSources?.length
                ? electricityFlows?.electricityFlowsDataSources?.map((dataSource) => {
                    return fb.group({
                      quantification: [
                        dataSource?.quantification ?? '',
                        [GovukValidators.required('Select the data source for quantification of energy flows')],
                      ],
                    });
                  })
                : [
                    fb.group({
                      quantification: [
                        dataSources?.[0]?.quantification ?? '',
                        [GovukValidators.required('Select the data source for quantification of energy flows')],
                      ],
                    }),
                  ],
              [GovukValidators.required('Select the data sources used')],
            ),
          );

          formGroup.addControl(
            'methodologyAppliedDescription',
            fb.control(electricityFlows?.methodologyAppliedDescription ?? null, [
              GovukValidators.required('Enter a description of the applied methodology'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]),
          );

          formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);

          formGroup.addControl(
            'supportingFiles',
            requestTaskFileService.buildFormControl(
              store.getState().requestTaskId,
              electricityFlows?.supportingFiles ?? [],
              store.getState().permitAttachments,
              store.getFileUploadSectionAttachmentActionContext(),
              false,
              !state.isEditable,
            ),
          );
        }
      } else {
        formGroup.removeControl('methodologyAppliedDescription');
        formGroup.removeControl('electricityFlowsDataSources');
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

    formGroup.controls.electricityProduced.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
