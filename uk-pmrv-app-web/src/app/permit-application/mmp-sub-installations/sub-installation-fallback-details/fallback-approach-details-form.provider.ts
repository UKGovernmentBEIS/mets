import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { InstallationConnection } from 'pmrv-api';

import { isFallbackApproach } from '../mmp-sub-installations-status';

export const fallbackApproachAddFormFactory = {
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

    const selectType = (type) => {
      const types = {
        HEAT_BENCHMARK_CL: 'HEAT_BENCHMARK',
        HEAT_BENCHMARK_NON_CL: 'HEAT_BENCHMARK',
        FUEL_BENCHMARK_CL: 'FUEL_BENCHMARK',
        FUEL_BENCHMARK_NON_CL: 'FUEL_BENCHMARK',
        PROCESS_EMISSIONS_CL: 'PROCESS_EMISSIONS',
        PROCESS_EMISSIONS_NON_CL: 'PROCESS_EMISSIONS',
        DISTRICT_HEATING_NON_CL: 'DISTRICT_HEATING_NON_CL',
      };
      return types[type] ?? null;
    };

    const formGroup = fb.group({
      subInstallationTypeOptions: fb.group(
        {
          parentOption: [selectType(fallbackApproach?.subInstallationType)],
          HEAT_BENCHMARK: fb.group({
            selectedValue: [fallbackApproach?.subInstallationType ?? null, { updateOn: 'change' }],
            subOptions: fb.array([fb.control(null), fb.control(null)]),
          }),
          FUEL_BENCHMARK: fb.group({
            selectedValue: [fallbackApproach?.subInstallationType ?? null, { updateOn: 'change' }],
            subOptions: fb.array([fb.control(null), fb.control(null)]),
          }),
          PROCESS_EMISSIONS: fb.group({
            selectedValue: [fallbackApproach?.subInstallationType ?? null, { updateOn: 'change' }],
            subOptions: fb.array([fb.control(null), fb.control(null)]),
          }),
        },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select a fallback approach')],
        },
      ),
      subInstallationType: [
        fallbackApproach?.subInstallationType ?? null,
        [GovukValidators.required('Select a fallback approach')],
      ],

      description: [
        fallbackApproach?.description ?? null,
        [
          GovukValidators.required('Enter a description of the system boundaries'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      supportingFiles: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        fallbackApproach?.supportingFiles ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
    });

    (formGroup?.controls?.subInstallationTypeOptions as FormGroup)?.controls?.parentOption?.valueChanges.subscribe(
      (val) => {
        if (val === 'DISTRICT_HEATING_NON_CL') {
          formGroup?.controls?.subInstallationType?.setValue(val);
        } else {
          formGroup?.controls?.subInstallationType?.reset();

          const formArray = (
            (formGroup?.controls?.subInstallationTypeOptions as FormGroup)?.controls?.[val] as FormGroup
          )?.controls?.subOptions as FormArray;

          formArray?.reset();
        }
      },
    );

    ['HEAT_BENCHMARK', 'FUEL_BENCHMARK', 'PROCESS_EMISSIONS'].forEach((approach) => {
      (
        (formGroup?.controls?.subInstallationTypeOptions as FormGroup)?.controls?.[approach] as FormGroup
      )?.controls?.selectedValue?.valueChanges.subscribe((val) => {
        formGroup?.controls?.subInstallationType?.setValue(val);
      });
    });

    return formGroup;
  },
};

export const entityTypeOptions: InstallationConnection['entityType'][] = [
  'ETS_INSTALLATION',
  'NON_ETS_INSTALLATION',
  'NITRIC_ACID_PRODUCTION',
  'HEAT_DISTRIBUTION_NETWORK',
];
export const connectionTypeOptions: InstallationConnection['connectionType'][] = [
  'MEASURABLE_HEAT',
  'WASTE_GAS',
  'TRANSFERRED_CO2_FOR_USE_IN_YOUR_INSTALLATION_CCU',
  'TRANSFERRED_CO2_FOR_STORAGE',
  'INTERMEDIATE_PRODUCTS_COVERED_BY_PRODUCT_BENCHMARKS',
];
export const flowDirectionOptions: InstallationConnection['flowDirection'][] = ['IMPORT', 'EXPORT'];
