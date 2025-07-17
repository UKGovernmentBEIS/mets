import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { isProductBenchmark } from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { SteamCrackingSP } from 'pmrv-api';

export const calculationSteamCrackingAddFormFactory = {
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

    const dataSources = (productBenchmark?.specialProduct as SteamCrackingSP)?.dataSources;
    const formGroup = fb.group(
      {
        dataSources: fb.array(
          dataSources?.length
            ? (productBenchmark?.specialProduct as SteamCrackingSP)?.dataSources?.map((dataSource) => {
                return fb.group({
                  detail: [dataSource?.detail ?? '', [GovukValidators.required('Select the calculation method used')]],
                });
              })
            : [
                fb.group({
                  detail: [
                    dataSources?.[0]?.detail ?? '',
                    [GovukValidators.required('Select the calculation method used')],
                  ],
                }),
              ],
          [GovukValidators.required('Select the calculation method used')],
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
        (
          formGroup.controls.hierarchicalOrder as FormGroup
        )?.controls?.notFollowingHierarchicalOrderReason?.clearValidators();
        (formGroup.controls.hierarchicalOrder as FormGroup)?.controls?.notFollowingHierarchicalOrderReason?.setValue(
          null,
        );
        (
          formGroup.controls.hierarchicalOrder as FormGroup
        )?.controls?.notFollowingHierarchicalOrderDescription?.clearValidators();
        (
          formGroup.controls.hierarchicalOrder as FormGroup
        )?.controls?.notFollowingHierarchicalOrderDescription?.setValue(null);
      } else {
        (
          formGroup.controls.hierarchicalOrder as FormGroup
        )?.controls?.notFollowingHierarchicalOrderReason?.setValidators([
          GovukValidators.required('Select a reason for not following the hierarchy'),
        ]);
        (
          formGroup.controls.hierarchicalOrder as FormGroup
        )?.controls?.notFollowingHierarchicalOrderDescription?.setValidators([
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
