import { map, OperatorFunction, pipe } from 'rxjs';

import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/corsia/aer-verify/util/aer-verify-corsia.util';
import {
  AerVerifyCorsiaTaskKey,
  AerVerifyCorsiaTaskPayload,
  requestTaskQuery,
  RequestTaskState,
} from '@aviation/request-task/store';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  AviationAerCorsia,
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload,
  AviationAerCorsiaEmissionsReductionClaimVerification,
  AviationAerCorsiaGeneralInformation,
  AviationAerCorsiaIndependentReview,
  AviationAerCorsiaOpinionStatement,
  AviationAerCorsiaProcessAnalysis,
  AviationAerCorsiaTimeAllocationScope,
  AviationAerCorsiaUncorrectedNonConformities,
  AviationAerCorsiaVerificationReport,
  AviationAerCorsiaVerifierDetails,
  AviationAerCorsiaVerifiersConclusions,
  VerificationBodyDetails,
} from 'pmrv-api';

const selectPayload: OperatorFunction<
  RequestTaskState,
  AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload
> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as AerVerifyCorsiaTaskPayload),
);

const selectAer: OperatorFunction<RequestTaskState, AviationAerCorsia> = pipe(
  selectPayload,
  map((payload) => payload.aer),
);

const selectAerAttachments: OperatorFunction<RequestTaskState, { [p: string]: string }> = pipe(
  selectPayload,
  map((payload) => payload.aerAttachments),
);

const selectVerificationReport: OperatorFunction<RequestTaskState, AviationAerCorsiaVerificationReport> = pipe(
  selectPayload,
  map((payload) => payload.verificationReport),
);

const selectVerificationBodyDetails: OperatorFunction<RequestTaskState, VerificationBodyDetails> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.verificationBodyDetails),
);

const selectVerifierDetails: OperatorFunction<RequestTaskState, AviationAerCorsiaVerifierDetails> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.verifierDetails),
);

const selectEmissionsReductionClaimVerification: OperatorFunction<
  RequestTaskState,
  AviationAerCorsiaEmissionsReductionClaimVerification
> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.emissionsReductionClaimVerification),
);

const selectIndependentReview: OperatorFunction<RequestTaskState, AviationAerCorsiaIndependentReview> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.independentReview),
);

const selectVerifiersConclusions: OperatorFunction<RequestTaskState, AviationAerCorsiaVerifiersConclusions> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.verifiersConclusions),
);

const selectTimeAllocationScope: OperatorFunction<RequestTaskState, AviationAerCorsiaTimeAllocationScope> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.timeAllocationScope),
);

const selectGeneralInformation: OperatorFunction<RequestTaskState, AviationAerCorsiaGeneralInformation> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.generalInformation),
);

const selectOpinionStatement: OperatorFunction<RequestTaskState, AviationAerCorsiaOpinionStatement> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.opinionStatement),
);

const selectProcessAnalysis: OperatorFunction<RequestTaskState, AviationAerCorsiaProcessAnalysis> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.processAnalysis),
);

const selectUncorrectedNonConformities: OperatorFunction<
  RequestTaskState,
  AviationAerCorsiaUncorrectedNonConformities
> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.uncorrectedNonConformities),
);

function selectStatusForTask(task: AerVerifyCorsiaTaskKey): OperatorFunction<RequestTaskState, TaskItemStatus> {
  return pipe(
    selectPayload,
    map((payload) => {
      return getTaskStatusByTaskCompletionState(task, payload);
    }),
  );
}

export const aerVerifyCorsiaQuery = {
  selectPayload,
  selectAer,
  selectAerAttachments,
  selectVerificationReport,
  selectVerificationBodyDetails,
  selectVerifierDetails,
  selectEmissionsReductionClaimVerification,
  selectIndependentReview,
  selectVerifiersConclusions,
  selectTimeAllocationScope,
  selectGeneralInformation,
  selectOpinionStatement,
  selectProcessAnalysis,
  selectUncorrectedNonConformities,
  selectStatusForTask,
};
