import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { AssessAndControlRisk, DataFlowActivities, ManagementProceduresDefinition } from 'pmrv-api';

import { RequestTaskFileService } from '../../shared/services/request-task-file-service/request-task-file.service';
import { GroupBuilderConfig } from '../../shared/types';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

export const managementProceduresFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ) =>
    fb.group({
      ...managementProceduresControls(store.permit[route.snapshot.data.permitTask], !store.getValue().isEditable),
      ...(route.snapshot.data.permitTask === 'dataFlowActivities'
        ? dataFlowActivities(store, requestTaskFileService, !store.getValue().isEditable)
        : route.snapshot.data.permitTask === 'assessAndControlRisk'
          ? assessAndControlRisk(store, requestTaskFileService, !store.getValue().isEditable)
          : null),
    }),
};

function managementProceduresControls(
  value: ManagementProceduresDefinition | DataFlowActivities,
  disabled: boolean,
): GroupBuilderConfig<ManagementProceduresDefinition | DataFlowActivities> {
  return {
    appliedStandards: [
      { value: value?.appliedStandards ?? null, disabled },
      GovukValidators.maxLength(2000, 'The applied standards should not be more than 2000 characters'),
    ],
    diagramReference: [
      { value: value?.diagramReference ?? null, disabled },
      GovukValidators.maxLength(500, 'The diagram reference should not be more than 500 characters'),
    ],
    itSystemUsed: [
      { value: value?.itSystemUsed ?? null, disabled },
      GovukValidators.maxLength(500, 'The IT system used should not be more than 500 characters'),
    ],
    locationOfRecords: [
      { value: value?.locationOfRecords ?? null, disabled },
      [
        GovukValidators.required('Enter the location of the records'),
        GovukValidators.maxLength(2000, 'The location of the records should not be more than 2000 characters'),
      ],
    ],
    procedureDescription: [
      { value: value?.procedureDescription ?? null, disabled },
      [
        GovukValidators.required('Enter a brief description of the procedure'),
        GovukValidators.maxLength(10000, 'The procedure description should not be more than 10000 characters'),
      ],
    ],
    procedureDocumentName: [
      { value: value?.procedureDocumentName ?? null, disabled },
      [
        GovukValidators.required('Enter the name of the procedure document'),
        GovukValidators.maxLength(1000, 'The procedure document name should not be more than 1000 characters'),
      ],
    ],
    procedureReference: [
      { value: value?.procedureReference ?? null, disabled },
      [
        GovukValidators.required('Enter a procedure reference'),
        GovukValidators.maxLength(500, 'The procedure reference should not be more than 500 characters'),
      ],
    ],
    responsibleDepartmentOrRole: [
      { value: value?.responsibleDepartmentOrRole ?? null, disabled },
      [
        GovukValidators.required('Enter the name of the department or role responsible'),
        GovukValidators.maxLength(
          1000,
          'The name of the department or role responsible should not be more than 1000 characters',
        ),
      ],
    ],
  };
}

function dataFlowActivities(
  store: PermitApplicationStore<PermitApplicationState>,
  requestTaskFileService: RequestTaskFileService,
  disabled: boolean,
): GroupBuilderConfig<DataFlowActivities> {
  const value = store.permit.dataFlowActivities;
  return {
    primaryDataSources: [
      { value: value?.primaryDataSources ?? null, disabled },
      [
        GovukValidators.required('List the primary data sources'),
        GovukValidators.maxLength(10000, 'The primary data sources should not be more than 10000 characters'),
      ],
    ],
    processingSteps: [
      { value: value?.processingSteps ?? null, disabled },
      [
        GovukValidators.required('Enter a description of the processing steps for each data flow activity'),
        GovukValidators.maxLength(
          10000,
          'The description of the processing steps should not be more than 10000 characters',
        ),
      ],
    ],
    diagramAttachmentId: requestTaskFileService.buildFormControl(
      store.getState().requestTaskId,
      value?.diagramAttachmentId,
      store.getState().permitAttachments,
      store.getFileUploadSectionAttachmentActionContext(),
      false,
      disabled,
    ),
  };
}

function assessAndControlRisk(
  store: PermitApplicationStore<PermitApplicationState>,
  requestTaskFileService: RequestTaskFileService,
  disabled: boolean,
): GroupBuilderConfig<AssessAndControlRisk> {
  const value = store.permit.assessAndControlRisk;
  return {
    riskAssessmentAttachments: requestTaskFileService.buildFormControl(
      store.getState().requestTaskId,
      value?.riskAssessmentAttachments ?? [],
      store.getState().permitAttachments,
      store.getFileUploadSectionAttachmentActionContext(),
      false,
      disabled,
    ),
  };
}
