import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

import { FuelInputAndRelevantEmissionFactorPB } from 'pmrv-api';

import { isProductBenchmark } from '../mmp-sub-installations-status';

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
    const productBenchmark = store.permit.monitoringMethodologyPlans?.digitizedPlan?.subInstallations
      ?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
      ?.find((product) => product.subInstallationNo === route.snapshot.paramMap.get('subInstallationNo'));

    const formGroup = fb.group(
      {
        exist: [
          (productBenchmark?.fuelInputAndRelevantEmissionFactor as FuelInputAndRelevantEmissionFactorPB)?.exist ?? null,
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
        productBenchmark?.fuelInputAndRelevantEmissionFactor?.hierarchicalOrder?.followed ?? null,
        {
          validators: GovukValidators.required('Select yes if the hierarchical order has been followed'),
          updateOn: 'change',
        },
      ],
      notFollowingHierarchicalOrderReason: [
        productBenchmark?.fuelInputAndRelevantEmissionFactor?.hierarchicalOrder?.notFollowingHierarchicalOrderReason ??
          null,
        [GovukValidators.required('Select a reason for not following the hierarchy')],
      ],
      notFollowingHierarchicalOrderDescription: [
        productBenchmark?.fuelInputAndRelevantEmissionFactor?.hierarchicalOrder
          ?.notFollowingHierarchicalOrderDescription ?? null,
        [
          GovukValidators.required('Explain why the hierarchy was not followed'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });

    formGroup?.controls?.exist?.valueChanges?.subscribe((val) => {
      if (val) {
        if (!formGroup.contains('dataSources')) {
          const dataSources = (
            productBenchmark?.fuelInputAndRelevantEmissionFactor as FuelInputAndRelevantEmissionFactorPB
          )?.dataSources;

          formGroup.addControl(
            'dataSources',
            fb.array(
              (productBenchmark?.fuelInputAndRelevantEmissionFactor as FuelInputAndRelevantEmissionFactorPB)
                ?.dataSources?.length
                ? (
                    productBenchmark?.fuelInputAndRelevantEmissionFactor as FuelInputAndRelevantEmissionFactorPB
                  )?.dataSources?.map((dataSource, index) => {
                    return fb.group(
                      {
                        fuelInput: [
                          dataSource?.fuelInput ?? '',
                          index === 0
                            ? [GovukValidators.required('Select fuel input and relevant emission factor')]
                            : [],
                        ],
                        weightedEmissionFactor: [
                          dataSource?.weightedEmissionFactor ?? '',
                          index === 0 ? [GovukValidators.required('Select weighted emission factor')] : [],
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
                        dataSources?.[0]?.fuelInput ?? '',
                        [GovukValidators.required('Select fuel input and relevant emission factor')],
                      ],
                      weightedEmissionFactor: [
                        dataSources?.[0]?.weightedEmissionFactor ?? '',
                        [GovukValidators.required('Select weighted emission factor')],
                      ],
                    }),
                  ],
              [GovukValidators.required('Select the data sources used')],
            ),
          );

          formGroup.addControl(
            'methodologyAppliedDescription',
            fb.control(productBenchmark?.fuelInputAndRelevantEmissionFactor?.methodologyAppliedDescription ?? null, [
              GovukValidators.required('Enter a description of the applied methodology'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]),
          );

          formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);

          formGroup.addControl(
            'supportingFiles',
            requestTaskFileService.buildFormControl(
              store.getState().requestTaskId,
              productBenchmark?.fuelInputAndRelevantEmissionFactor?.supportingFiles ?? [],
              store.getState().permitAttachments,
              store.getFileUploadSectionAttachmentActionContext(),
              false,
              !state.isEditable,
            ),
          );
        }
      } else {
        formGroup.removeControl('methodologyAppliedDescription');
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

    formGroup.controls.exist.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
