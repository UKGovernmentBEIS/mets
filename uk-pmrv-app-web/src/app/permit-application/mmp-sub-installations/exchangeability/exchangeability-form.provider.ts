import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { isProductBenchmark } from '../mmp-sub-installations-status';

export const exchangeabilityAddFormFactory = {
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

    const hierarchicalOrderFormGroup = fb.group({
      followed: [
        productBenchmark?.fuelAndElectricityExchangeability?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        productBenchmark?.fuelAndElectricityExchangeability?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ??
          null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        productBenchmark?.fuelAndElectricityExchangeability?.hierarchicalOrder
          ?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    const energyFlowDataSources =
      productBenchmark?.fuelAndElectricityExchangeability?.fuelAndElectricityExchangeabilityEnergyFlowDataSources;
    const formGroup = fb.group(
      {
        fuelAndElectricityExchangeabilityEnergyFlowDataSources: fb.array(
          energyFlowDataSources?.length
            ? productBenchmark?.fuelAndElectricityExchangeability?.fuelAndElectricityExchangeabilityEnergyFlowDataSources?.map(
                (dataSource) => {
                  return fb.group({
                    relevantElectricityConsumption: [
                      dataSource?.relevantElectricityConsumption ?? '',
                      [GovukValidators.required('Select the data sources used')],
                    ],
                  });
                },
              )
            : [
                fb.group({
                  relevantElectricityConsumption: [
                    energyFlowDataSources?.[0]?.relevantElectricityConsumption ?? '',
                    [GovukValidators.required('Select the data sources used')],
                  ],
                }),
              ],
          [GovukValidators.required('Select the data sources used')],
        ),

        methodologyAppliedDescription: [
          productBenchmark?.fuelAndElectricityExchangeability?.methodologyAppliedDescription ?? null,
          [
            GovukValidators.required('Enter a description of the applied methodology'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        hierarchicalOrder: hierarchicalOrderFormGroup,

        supportingFiles: requestTaskFileService.buildFormControl(
          store.getState().requestTaskId,
          productBenchmark?.fuelAndElectricityExchangeability?.supportingFiles ?? [],
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

    return formGroup;
  },
};
