import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { isProductBenchmark } from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

import { SynthesisGasSP } from 'pmrv-api';

export const calculationSynthesisGasAddFormFactory = {
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
        productBenchmark?.specialProduct?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        productBenchmark?.specialProduct?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ?? null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        productBenchmark?.specialProduct?.hierarchicalOrder?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    const dataSources = (productBenchmark?.specialProduct as SynthesisGasSP)?.dataSources;
    const formGroup = fb.group(
      {
        dataSources: fb.array(
          dataSources?.length
            ? (productBenchmark?.specialProduct as any)?.dataSources?.map((dataSource, index) => {
                return fb.group({
                  details: fb.group(
                    {
                      SYNTHESIS_GAS_TOTAL_PRODUCTION: [
                        dataSource?.details?.SYNTHESIS_GAS_TOTAL_PRODUCTION ?? '',
                        index === 0
                          ? [
                              GovukValidators.required(
                                'Select the calculation method used for total synthesis gas production',
                              ),
                            ]
                          : [],
                      ],
                      SYNTHESIS_GAS_COMPOSITION_DATA: [
                        dataSource?.details?.SYNTHESIS_GAS_COMPOSITION_DATA ?? '',
                        index === 0
                          ? [GovukValidators.required('Select the calculation method used for the volume fraction')]
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
                  ),
                });
              })
            : [
                fb.group({
                  details: fb.group({
                    SYNTHESIS_GAS_TOTAL_PRODUCTION: [
                      dataSources?.[0]?.details.SYNTHESIS_GAS_TOTAL_PRODUCTION ?? '',
                      [
                        GovukValidators.required(
                          'Select the calculation method used for total synthesis gas production',
                        ),
                      ],
                    ],
                    SYNTHESIS_GAS_COMPOSITION_DATA: [
                      dataSources?.[0]?.details.SYNTHESIS_GAS_COMPOSITION_DATA ?? '',
                      [GovukValidators.required('Select the calculation method used for the volume fraction')],
                    ],
                  }),
                }),
              ],
          [GovukValidators.required('Select the data sources used')],
        ),

        methodologyAppliedDescription: [
          productBenchmark?.specialProduct?.methodologyAppliedDescription ?? null,
          [
            GovukValidators.required('Enter a description of the applied methodology'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        hierarchicalOrder: hierarchicalOrderFormGroup,

        supportingFiles: requestTaskFileService.buildFormControl(
          store.getState().requestTaskId,
          productBenchmark?.specialProduct?.supportingFiles ?? [],
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
