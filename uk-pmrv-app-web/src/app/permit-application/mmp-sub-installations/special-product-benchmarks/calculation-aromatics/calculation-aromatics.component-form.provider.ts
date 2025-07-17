import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { CalculationAromaticsRelevantCWTFunctionsPipe } from '@permit-application/shared/pipes/calculation-aromatics-relevant-cwt-functions.pipe';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

import { AromaticsSP } from 'pmrv-api';

import { isProductBenchmark } from '../../mmp-sub-installations-status';

export const relevantCWTFunctions: AromaticsSP['relevantCWTFunctions'] = [
  'NAPHTHA_GASOLINE_HYDROTREATER',
  'AROMATIC_SOLVENT_EXTRACTION',
  'TDP_TDA',
  'HYDRODEALKYLATION',
  'XYLENE_ISOMERISATION',
  'PARAXYLENE_PRODUCTION',
  'CYCLOHEXANE_PRODUCTION',
  'CUMENE_PRODUCTION',
];

export const aromaticsFormFactory = {
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

    const functionsPipe = new CalculationAromaticsRelevantCWTFunctionsPipe();
    const calculationMethodErrorMsg = (detailsKey) =>
      `Select the calculation method used for ${functionsPipe.transform([detailsKey])[0].description}`;

    const formGroup = fb.group(
      {
        relevantCWTFunctions: [
          (productBenchmark?.specialProduct as AromaticsSP)?.relevantCWTFunctions,
          {
            validators: GovukValidators.required('Select the relevant CWT functions'),
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

    if (!formGroup.contains('methodologyAppliedDescription')) {
      formGroup.addControl(
        'methodologyAppliedDescription',
        fb.control(productBenchmark?.specialProduct?.methodologyAppliedDescription ?? null, [
          GovukValidators.required('Enter a description of the applied methodology'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]),
      );
    }

    if (!formGroup.contains('hierarchicalOrder')) {
      formGroup.addControl('hierarchicalOrder', hierarchicalOrderFormGroup);
    }

    (formGroup as FormGroup)?.controls?.relevantCWTFunctions?.valueChanges?.subscribe((val) => {
      if (val !== null && val?.length > 0) {
        const dataSources = (productBenchmark?.specialProduct as AromaticsSP)?.dataSources;

        const formArray = fb.array(
          dataSources?.length
            ? dataSources?.map((dataSource, index) => {
                const refineDetails = (formGroup as FormGroup)?.controls?.relevantCWTFunctions.value.reduce(
                  (accum, dataSourceDetailKey) => {
                    if (index === 0) {
                      return {
                        ...accum,
                        [dataSourceDetailKey]: [
                          dataSource.details[dataSourceDetailKey] ?? '',
                          [GovukValidators.required(calculationMethodErrorMsg(dataSourceDetailKey))],
                        ],
                      };
                    }
                    return {
                      ...accum,
                      [dataSourceDetailKey]: dataSource.details[dataSourceDetailKey] ?? '',
                    };
                  },
                  {},
                );

                return fb.group(
                  {
                    ...refineDetails,
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
                  ...relevantCWTFunctions.reduce((accum, CWTFunction) => {
                    const refineDetails = (formGroup as FormGroup)?.controls?.relevantCWTFunctions.value.reduce(
                      (accumFunc, dataSourceDetailKey) => {
                        return {
                          ...accumFunc,
                          [dataSourceDetailKey]: [
                            dataSources?.[0]?.details[dataSourceDetailKey] ?? '',
                            [GovukValidators.required(calculationMethodErrorMsg(dataSourceDetailKey))],
                          ],
                        };
                      },
                      {},
                    );

                    return val?.includes(CWTFunction) ? refineDetails : accum;
                  }, {}),
                }),
              ],
          [GovukValidators.required('Select the data sources used')],
        );

        if (!formGroup.contains('dataSources')) {
          formGroup.addControl('dataSources', formArray);
        } else {
          relevantCWTFunctions.forEach((CWTFunction) => {
            const dataSourcesFormGroup = (formGroup.get('dataSources') as FormArray).controls as FormGroup[];
            if (val?.includes(CWTFunction)) {
              dataSourcesFormGroup?.forEach((control, index) => {
                control.addControl(
                  CWTFunction,
                  index === 0
                    ? fb.control('', [GovukValidators.required(calculationMethodErrorMsg(CWTFunction))])
                    : fb.control(''),
                );
                control.updateValueAndValidity();
              });
            } else {
              dataSourcesFormGroup?.forEach((control) => {
                control.removeControl(CWTFunction);
                control.updateValueAndValidity();
              });
            }
          });
        }
      } else {
        // When an error is raised conserning the relevantCWTFunctions control
        // and you try to uncheck the functions, at the last one it comes here and the control
        // 'dataSources' remains on the form where as a result you can see the error msg
        // without the 'dataSources' selects/
        if (formGroup.get('dataSources')) {
          formGroup.removeControl('dataSources');
        }
      }
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

    formGroup.addControl(
      'supportingFiles',
      requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        productBenchmark?.specialProduct?.supportingFiles ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
    );

    formGroup.controls?.dataSources?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.relevantCWTFunctions?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.methodologyAppliedDescription?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
