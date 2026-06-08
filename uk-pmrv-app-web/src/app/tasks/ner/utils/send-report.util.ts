import { CompetentAuthorityPipe } from '@shared/pipes/competent-authority.pipe';

import { RequestTaskDTO, RequestTaskItemDTO } from 'pmrv-api';

export const getNerSendReportHeader = (
  requestTaskType: RequestTaskDTO['type'],
  verificationPerformed: boolean,
  regulatorVerificationRequired: boolean,
): string => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_SUBMIT':
      return verificationPerformed ? 'Send report to regulator' : 'Send application for verification';
    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'Send report';
    case 'NER_APPLICATION_AMENDS_SUBMIT':
      return regulatorVerificationRequired && !verificationPerformed
        ? 'Send application for verification'
        : 'Send report to regulator';
    default:
      return null;
  }
};

export const getNerSendReportConfirmationTitle = (
  requestTaskType: RequestTaskDTO['type'],
  verificationPerformed: boolean,
  regulatorVerificationRequired: boolean,
): string => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_SUBMIT':
      return verificationPerformed ? 'Sent application to regulator for review' : 'Sent to verifier for review';
    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'Verification report sent to operator';
    case 'NER_APPLICATION_AMENDS_SUBMIT':
      return regulatorVerificationRequired && !verificationPerformed
        ? 'Sent to verifier for review'
        : 'Sent application to regulator for review';

    default:
      return null;
  }
};

export const getNerBobyContent = (
  requestItem: RequestTaskItemDTO,
  verificationPerformed: boolean,
  regulatorVerificationRequired: boolean,
): string => {
  const competentAuthorityPipe = new CompetentAuthorityPipe();
  const { requestTask: { type: requestTaskType } = {}, requestInfo: { competentAuthority } = {} } = requestItem;

  switch (requestTaskType) {
    case 'NER_APPLICATION_SUBMIT':
      return verificationPerformed
        ? `
        <div class="govuk-body">Your application will be sent directly to your regulator (${competentAuthorityPipe.transform(competentAuthority)}).</div>
        <div class="govuk-body">By selecting ‘Confirm and send’ you confirm that the information is correct to the best of your knowledge.</div>`
        : 'By selecting ‘Confirm and send’ you confirm that the information in your application is correct to the best of your knowledge.';
    case 'NER_APPLICATION_AMENDS_SUBMIT':
      return regulatorVerificationRequired && !verificationPerformed
        ? 'By selecting ‘Confirm and send’ you confirm that the information in your application is correct to the best of your knowledge.'
        : `
        <div class="govuk-body">Your application will be sent directly to your regulator (${competentAuthorityPipe.transform(competentAuthority)}).</div>
        <div class="govuk-body">By selecting ‘Confirm and send’ you confirm that the information is correct to the best of your knowledge.</div>`;

    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'By selecting ‘Confirm and send’ you confirm that the information you have provided in this report is correct to the best of your knowledge.';

    default:
      return null;
  }
};

export const getNerWhatHappensNextContent = (
  requestTaskType: RequestTaskDTO['type'],
  verificationPerformed: boolean,
): string => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_SUBMIT':
      return verificationPerformed
        ? 'Any fees due to OPRED or NRW will be managed outside of the Manage your UK ETS (METS) reporting service.'
        : 'Your application has been submitted and will be reviewed by your Verifier.';

    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return `
        The operator can either: <br/>
        <ul class="govuk-list govuk-list--bullet">
          <li>submit the application to the regulator</li>
          <li>make changes and repeat the submission process</li>
        </ul>
      `;

    default:
      return null;
  }
};

export const nerShowCurrentVerifierTypes: Array<RequestTaskDTO['type']> = ['NER_APPLICATION_SUBMIT'];
