import { ValidationErrors, ValidatorFn } from '@angular/forms';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';

import { MethodTask } from 'pmrv-api';

import { getCompletedSubinstallationTypes } from '../mmp-methods';

export const subInstallationsNotExists = (id: string, state: PermitApplicationState) => {
  const digitizedPlan = state.permit.monitoringMethodologyPlans.digitizedPlan;
  const physicalPart = digitizedPlan.methodTask.connections.find((physicalPart) => physicalPart.itemId === id);
  const completedSubInstallations = getCompletedSubinstallationTypes(state);

  return (
    physicalPart.subInstallations.some((element) => !completedSubInstallations.includes(element)) ||
    completedSubInstallations.length < 2
  );
};

export const subInstallationsExistsValidator = (id: string, state: PermitApplicationState): ValidatorFn => {
  const notExists = subInstallationsNotExists(id, state);

  return (): ValidationErrors | null => {
    return notExists ? { subInstallationNotExists: 'Select at least two sub-installations' } : null;
  };
};

export const noPhysicalPartsValidator = (connections: MethodTask['connections'] = []): ValidatorFn => {
  const noPhysicalParts = connections.length < 1;

  return (): ValidationErrors | null => {
    return noPhysicalParts ? { noPhysicalParts: 'Select at least one physical part' } : null;
  };
};
