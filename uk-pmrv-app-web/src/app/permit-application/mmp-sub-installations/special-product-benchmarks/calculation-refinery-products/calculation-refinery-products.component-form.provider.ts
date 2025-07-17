import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { CalculationRefineryProductsRelevantCWTFunctionsPipe } from '@permit-application/shared/pipes/calculation-refinery-products-relevant-cwt-functions.pipe';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared-user/utils/validators';

import { GovukValidators } from 'govuk-components';

import { RefineryProductsSP } from 'pmrv-api';

import { isProductBenchmark } from '../../mmp-sub-installations-status';

export const refineryProductsRelevantCWTFunctions: RefineryProductsSP['refineryProductsRelevantCWTFunctions'] = [
  'ATMOSPHERIC_CRUDE_DISTILLATION',
  'VACUUM_DISTILLATION',
  'SOLVENT_DEASPHALTING',
  'VISBREAKING',
  'THERMAL_CRACKING',
  'DELAYED_COKING',
  'FLUID_COKING',
  'FLEXICOKING',
  'COKE_CALCINING',
  'FLUID_CATALYTIC_CRACKING',
  'OTHER_CATALYTIC_CRACKING',
  'DISTILLATE_GASOIL_HYDROCRACKING',
  'RESIDUAL_HYDROCRACKING',
  'NAPHTHA_GASOLINE_HYDROTREATING',
  'KEROSENE_DIESEL_HYDROTREATING',
  'RESIDUAL_HYDROTREATING',
  'VGO_HYDROTREATING',
  'HYDROGEN_PRODUCTION',
  'CATALYTIC_REFORMING',
  'ALKYLATION',
  'C4_ISOMERISATION',
  'C5_C6_ISOMERISATION',
  'OXYGENATE_PRODUCTION',
  'PROPYLENE_PRODUCTION',
  'ASPHALT_MANUFACTURE',
  'POLYMER_MODIFIED_ASPHALT_BLENDING',
  'SULPHUR_RECOVERY',
  'AROMATIC_SOLVENT_EXTRACTION',
  'HYDRODEALKYLATION',
  'TDP_TDA',
  'CYCLOHEXANE_PRODUCTION',
  'XYLENE_ISOMERISATION',
  'PARAXYLENE_PRODUCTION',
  'METAXYLENE_PRODUCTION',
  'PHTHALIC_ANHYDRIDE_PRODUCTION',
  'MALEIC_ANHYDRIDE_PRODUCTION',
  'ETHYLBENZENE_PRODUCTION',
  'CUMENE_PRODUCTION',
  'PHENOL_PRODUCTION',
  'LUBE_SOLVENT_EXTRACTION',
  'LUBE_SOLVENT_DEWAXING',
  'CATALYTIC_WAX_ISOMERISATION',
  'LUBE_HYDROCRACKER',
  'WAX_DEOILING',
  'LUBE_WAX_HYDROTREATING',
  'SOLVENT_HYDROTREATING',
  'SOLVENT_FRACTIONATION',
  'MOL_SIEVE_C10_PLUS_PARAFFINS',
  'PARTIAL_OXIDATION_RESIDUAL_FEEDS_POX_FUEL',
  'PARTIAL_OXIDATION_RESIDUAL_FEEDS_POX_HYDROGEN_METHANOL',
  'METHANOL_FROM_SYNGAS',
  'AIR_SEPARATION',
  'FRACTIONATION_PURCHASED_NGL',
  'FLUE_GAS_TREATMENT',
  'TREATMENT_COMPRESSION_FUEL_GAS_SALES',
  'SEAWATER_DESALINATION',
];

export const refineryProductsFormFactory = {
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

    const functionsPipe = new CalculationRefineryProductsRelevantCWTFunctionsPipe();
    const calculationMethodErrorMsg = (detailsKey) =>
      `Select the calculation method used for ${functionsPipe.transform([detailsKey])[0].description}`;

    const formGroup = fb.group(
      {
        refineryProductsRelevantCWTFunctions: [
          (productBenchmark?.specialProduct as RefineryProductsSP)?.refineryProductsRelevantCWTFunctions,
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

    (formGroup as FormGroup)?.controls?.refineryProductsRelevantCWTFunctions?.valueChanges?.subscribe((val) => {
      if (val !== null && val?.length > 0) {
        const refineryProductsDataSources = (productBenchmark?.specialProduct as RefineryProductsSP)
          ?.refineryProductsDataSources;

        const formArray = fb.array(
          refineryProductsDataSources?.length
            ? refineryProductsDataSources?.map((dataSource, index) => {
                const refineDetails = (
                  formGroup as FormGroup
                )?.controls?.refineryProductsRelevantCWTFunctions.value.reduce((accum, dataSourceDetailKey) => {
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
                }, {});

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
                  ...refineryProductsRelevantCWTFunctions.reduce((accum, CWTFunction) => {
                    const refineDetails = (
                      formGroup as FormGroup
                    )?.controls?.refineryProductsRelevantCWTFunctions.value.reduce((accumFunc, dataSourceDetailKey) => {
                      return {
                        ...accumFunc,
                        [dataSourceDetailKey]: [
                          refineryProductsDataSources?.[0]?.details[dataSourceDetailKey] ?? '',
                          [GovukValidators.required(calculationMethodErrorMsg(dataSourceDetailKey))],
                        ],
                      };
                    }, {});

                    return val?.includes(CWTFunction) ? refineDetails : accum;
                  }, {}),
                }),
              ],
          [GovukValidators.required('Select the data sources used')],
        );

        if (!formGroup.contains('refineryProductsDataSources')) {
          formGroup.addControl('refineryProductsDataSources', formArray);
        } else {
          refineryProductsRelevantCWTFunctions.forEach((CWTFunction) => {
            const refineryProductsDataSourcesFormGroup = (formGroup.get('refineryProductsDataSources') as FormArray)
              .controls as FormGroup[];

            if (val?.includes(CWTFunction)) {
              refineryProductsDataSourcesFormGroup?.forEach((control, index) => {
                control.addControl(
                  CWTFunction,
                  index === 0
                    ? fb.control('', [GovukValidators.required(calculationMethodErrorMsg(CWTFunction))])
                    : fb.control(''),
                );
                control.updateValueAndValidity();
              });
            } else {
              refineryProductsDataSourcesFormGroup?.forEach((control) => {
                control.removeControl(CWTFunction);
                control.updateValueAndValidity();
              });
            }
          });
        }
      } else {
        // When an error is raised conserning the refineryProductsRelevantCWTFunctions control
        // and you try to uncheck the functions, at the last one it comes here and the control
        // 'refineryProductsDataSources' remains on the form where as a result you can see the error msg
        // without the 'refineryProductsDataSources' selects/
        if (formGroup.get('refineryProductsDataSources')) {
          formGroup.removeControl('refineryProductsDataSources');
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

    formGroup.controls?.refineryProductsDataSources?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.refineryProductsRelevantCWTFunctions?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.hierarchicalOrder?.updateValueAndValidity({ emitEvent: true });
    formGroup.controls?.methodologyAppliedDescription?.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
