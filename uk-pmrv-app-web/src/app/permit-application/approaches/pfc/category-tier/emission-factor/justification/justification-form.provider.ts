import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { RequestTaskFileService } from '../../../../../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';

export const justificationFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
    route: ActivatedRoute,
  ) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();
    const justification =
      (state.permit.monitoringApproaches?.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
        ?.sourceStreamCategoryAppliedTiers?.[index]?.emissionFactor?.noHighestRequiredTierJustification ?? null;
    const disabled = !state.isEditable;

    return fb.group({
      justification: [
        {
          value: [
            justification?.isCostUnreasonable ? 'isCostUnreasonable' : null,
            justification?.isTechnicallyInfeasible ? 'isTechnicallyInfeasible' : null,
          ].filter(Boolean),
          disabled,
        },
        GovukValidators.required('Select a justification'),
      ],
      technicalInfeasibilityExplanation: [
        { value: justification?.technicalInfeasibilityExplanation ?? null, disabled },
        [
          GovukValidators.required(
            'Explain why it is not technically feasible to meet the highest tier, as set out in Article 17 of the Monitoring and Reporting Regulations (MRR)',
          ),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      files: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        justification?.files ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        disabled,
      ),
    });
  },
};
