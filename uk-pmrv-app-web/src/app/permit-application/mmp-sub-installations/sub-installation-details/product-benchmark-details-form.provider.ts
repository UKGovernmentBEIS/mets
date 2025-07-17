import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { InstallationConnection } from 'pmrv-api';

import { isProductBenchmark } from '../mmp-sub-installations-status';

export const productBenchmarkAddFormFactory = {
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

    const formGroup = fb.group({
      subInstallationType: [
        productBenchmark?.subInstallationType ?? '',
        [GovukValidators.required('Select a product benchmark')],
      ],
      description: [
        productBenchmark?.description ?? null,
        [
          GovukValidators.required('Enter a description of the system boundaries'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      supportingFiles: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        productBenchmark?.supportingFiles ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
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
