import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { noSelection } from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationVerificationSubmitRequestTaskPayload,
  MonitoringApproachTypeEmissions,
  OpinionStatement,
  ReportableAndBiomassEmission,
  ReportableEmission,
} from 'pmrv-api';

export const reviewEmissionsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const opinionStatement = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.opinionStatement;
    const isEditable = state.isEditable;
    return fb.group(
      {
        operatorEmissionsAcceptable: [
          {
            value: opinionStatement.operatorEmissionsAcceptable ?? null,
            disabled: !state.isEditable,
          },
          { validators: [GovukValidators.required(noSelection)] },
        ],
        monitoringApproachTypeEmissions: fb.group(
          {
            calculationCombustionEmissions: reportableAndBiomassEmissionFBGroup(
              opinionStatement,
              isEditable,
              'calculationCombustionEmissions',
            ),
            calculationProcessEmissions: reportableAndBiomassEmissionFBGroup(
              opinionStatement,
              isEditable,
              'calculationProcessEmissions',
            ),
            calculationMassBalanceEmissions: reportableAndBiomassEmissionFBGroup(
              opinionStatement,
              isEditable,
              'calculationMassBalanceEmissions',
            ),
            calculationTransferredCO2Emissions: reportableAndBiomassEmissionFBGroup(
              opinionStatement,
              isEditable,
              'calculationTransferredCO2Emissions',
            ),
            measurementCO2Emissions: reportableAndBiomassEmissionFBGroup(
              opinionStatement,
              isEditable,
              'measurementCO2Emissions',
            ),
            measurementTransferredCO2Emissions: reportableAndBiomassEmissionFBGroup(
              opinionStatement,
              isEditable,
              'measurementTransferredCO2Emissions',
            ),
            measurementN2OEmissions: reportableAndBiomassEmissionFBGroup(
              opinionStatement,
              isEditable,
              'measurementN2OEmissions',
            ),
            measurementTransferredN2OEmissions: reportableAndBiomassEmissionFBGroup(
              opinionStatement,
              isEditable,
              'measurementTransferredN2OEmissions',
            ),
            calculationPFCEmissions: reportableEmissionFBGroup(opinionStatement, isEditable, 'calculationPFCEmissions'),
            inherentCO2Emissions: reportableEmissionFBGroup(opinionStatement, isEditable, 'inherentCO2Emissions'),
            fallbackEmissions: reportableAndBiomassEmissionFBGroup(opinionStatement, isEditable, 'fallbackEmissions'),
          },
          {
            updateOn: 'change',
          },
        ),
      },
      {
        updateOn: 'change',
      },
    );
  },
};

const monitoringApproachTypeErrorMessage = (key: keyof MonitoringApproachTypeEmissions): string => {
  switch (key) {
    case 'calculationCombustionEmissions':
      return 'calculation combustion emissions';
    case 'calculationProcessEmissions':
      return 'calculation process emissions';
    case 'calculationMassBalanceEmissions':
      return 'calculation mass balance emissions';
    case 'calculationTransferredCO2Emissions':
      return 'calculation transferred CO2 emissions';
    case 'measurementCO2Emissions':
      return 'measurement CO2 emissions';
    case 'measurementTransferredCO2Emissions':
      return 'measurement transferred CO2 emissions';
    case 'measurementN2OEmissions':
      return 'measurement N2O emissions';
    case 'measurementTransferredN2OEmissions':
      return 'measurement transferred N2O emissions';
    case 'calculationPFCEmissions':
      return 'calculation PFC emissions';
    case 'inherentCO2Emissions':
      return 'inherent CO2 emissions';
    case 'fallbackEmissions':
      return 'fallback emissions';
  }
};

const reportableAndBiomassEmissionFBGroup = (
  opinionStatement: OpinionStatement,
  isEditable: boolean,
  emissionKey: keyof MonitoringApproachTypeEmissions,
) => {
  const fb = new UntypedFormBuilder();
  const errorMessage = monitoringApproachTypeErrorMessage(emissionKey);
  const reportableAndBiomassEmission = opinionStatement?.monitoringApproachTypeEmissions?.[
    emissionKey
  ] as ReportableAndBiomassEmission;
  return fb.group({
    reportableEmissions: [
      {
        value: reportableAndBiomassEmission?.reportableEmissions ?? null,
        disabled: !isEditable,
      },
      {
        validators: [
          GovukValidators.required(`Enter reportable emissions for the ${errorMessage}`),
          GovukValidators.maxDecimalsValidator(5),
        ],
      },
    ],
    sustainableBiomass: [
      {
        value: reportableAndBiomassEmission?.sustainableBiomass ?? null,
        disabled: !isEditable,
      },
      {
        validators: [
          GovukValidators.required(`Enter sustainable biomass for the ${errorMessage}`),
          GovukValidators.maxDecimalsValidator(5),
        ],
      },
    ],
  });
};

const reportableEmissionFBGroup = (
  opinionStatement: OpinionStatement,
  isEditable: boolean,
  emissionKey: keyof MonitoringApproachTypeEmissions,
) => {
  const fb = new UntypedFormBuilder();
  const errorMessage = monitoringApproachTypeErrorMessage(emissionKey);
  const reportableAndBiomassEmission = opinionStatement?.monitoringApproachTypeEmissions?.[
    emissionKey
  ] as ReportableEmission;
  return fb.group({
    reportableEmissions: [
      {
        value: reportableAndBiomassEmission?.reportableEmissions ?? null,
        disabled: !isEditable,
      },
      {
        validators: [
          GovukValidators.required(`Enter reportable emissions for the ${errorMessage}`),
          GovukValidators.maxDecimalsValidator(5),
        ],
      },
    ],
  });
};
