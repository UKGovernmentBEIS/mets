import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const verifierDetailsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const verificationReport = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport;

    return fb.group({
      name: [
        { value: verificationReport.verifierContact?.name ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required(`Enter the verifier's name`),
            GovukValidators.maxLength(10000, `The verifier's name should not be more than 10000 characters`),
          ],
        },
      ],
      email: [
        { value: verificationReport.verifierContact?.email ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required(`Enter the verifier's email address`),
            GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
            GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
          ],
        },
      ],
      phoneNumber: [
        { value: verificationReport.verifierContact?.phoneNumber ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required(`Enter the verifier's phone number`),
            GovukValidators.maxLength(255, `Verifier's phone number should not be more than 255 characters`),
          ],
        },
      ],
      leadEtsAuditor: [
        { value: verificationReport.verificationTeamDetails?.leadEtsAuditor ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Enter the name of the lead ETS auditor'),
            GovukValidators.maxLength(10000, 'The lead ETS auditor name should not be more than 10000 characters'),
          ],
        },
      ],
      etsAuditors: [
        { value: verificationReport.verificationTeamDetails?.etsAuditors ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Enter the name of the ETS auditor'),
            GovukValidators.maxLength(10000, 'The ETS auditor name should not be more than 10000 characters'),
          ],
        },
      ],
      etsTechnicalExperts: [
        { value: verificationReport.verificationTeamDetails?.etsTechnicalExperts ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Enter the name of the technical experts (ETS auditor)'),
            GovukValidators.maxLength(
              10000,
              'The technical experts (ETS auditor) name should not be more than 10000 characters',
            ),
          ],
        },
      ],
      independentReviewer: [
        { value: verificationReport.verificationTeamDetails?.independentReviewer ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Enter the name of the Independent Reviewer'),
            GovukValidators.maxLength(10000, 'The Independent Reviewer name should not be more than 10000 characters'),
          ],
        },
      ],
      technicalExperts: [
        { value: verificationReport.verificationTeamDetails?.technicalExperts ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Enter the names of the Technical Experts (Independent Review)'),
            GovukValidators.maxLength(
              10000,
              'The Technical Experts (Independent Review) names should not be more than 10000 characters',
            ),
          ],
        },
      ],
      authorisedSignatoryName: [
        {
          value: verificationReport.verificationTeamDetails?.authorisedSignatoryName ?? null,
          disabled: !state.isEditable,
        },
        {
          validators: [
            GovukValidators.required('Enter the name of the authorised signatory'),
            GovukValidators.maxLength(10000, 'The authorised signatory name should not be more than 10000 characters'),
          ],
        },
      ],
    });
  },
};
