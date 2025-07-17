import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { isFallbackApproach } from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

import { AnnualActivityFuelLevel } from 'pmrv-api';

export const annualActivityFuelAddFormFactory = {
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

    const hierarchicalOrderFormGroup = fb.group({
      followed: [
        fallbackApproach?.annualLevel?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        fallbackApproach?.annualLevel?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        fallbackApproach?.annualLevel?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    const fuelDataSources = (fallbackApproach?.annualLevel as AnnualActivityFuelLevel)?.fuelDataSources;
    const formGroup = fb.group(
      {
        fuelDataSources: fb.array(
          fuelDataSources?.length
            ? (fallbackApproach?.annualLevel as AnnualActivityFuelLevel)?.fuelDataSources?.map((dataSource, index) => {
                return fb.group(
                  {
                    fuelInput: [
                      dataSource?.fuelInput ?? '',
                      ...(index === 0 ? [GovukValidators.required('Select the data source used for fuel input')] : []),
                    ],
                    energyContent: [dataSource?.energyContent ?? ''],
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
                    fuelDataSources?.[0]?.fuelInput ?? '',
                    [GovukValidators.required('Select the data source used for fuel input')],
                  ],
                  energyContent: [fuelDataSources?.[0]?.energyContent ?? ''],
                }),
              ],
          [GovukValidators.required('Select the data sources used')],
        ),

        methodologyAppliedDescription: [
          fallbackApproach?.annualLevel?.methodologyAppliedDescription ?? null,
          [
            GovukValidators.required('Enter a description of the methodology used'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        hierarchicalOrder: hierarchicalOrderFormGroup,
        trackingMethodologyDescription: [
          fallbackApproach?.annualLevel?.trackingMethodologyDescription ?? null,
          [
            GovukValidators.required('Enter a description of the methodology used to track the products produced'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        supportingFiles: requestTaskFileService.buildFormControl(
          store.getState().requestTaskId,
          fallbackApproach?.annualLevel?.supportingFiles ?? [],
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
