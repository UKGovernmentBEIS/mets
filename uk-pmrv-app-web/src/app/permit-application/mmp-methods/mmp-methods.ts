import { inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import {
  isFallbackApproach,
  isProductBenchmark,
} from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { SubInstallation } from 'pmrv-api';

import {
  noPhysicalPartsValidator,
  subInstallationsExistsValidator,
  subInstallationsNotExists,
} from './physical-parts-list/physical-parts-list';

export function mmpMethodsStatus(state: PermitApplicationState): TaskItemStatus {
  const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
  const mmpMethods = digitizedPlan?.methodTask;
  const hasError = mmpMethods?.connections
    ?.map((connection) => subInstallationsNotExists(connection.itemId, state))
    .find((error) => error);

  return state.permitSectionsCompleted?.mmpMethods?.[0] && isMethodsWizardCompleted(state) && !hasError
    ? 'complete'
    : mmpMethods !== undefined
      ? hasError
        ? 'needs review'
        : 'in progress'
      : 'not started';
}

export const getCompletedSubinstallationTypes = (
  state: PermitApplicationState,
): Array<SubInstallation['subInstallationType']> => {
  const subInstallations = state.permit.monitoringMethodologyPlans?.digitizedPlan?.subInstallations || [];
  const mmpProductBenchmark = state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark'] ?? [];
  const mmpFallbackApproach = state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Fallback_Approach'] ?? [];

  const productBenchmarkSubInstallations = subInstallations
    .filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
    .filter((_, index) => (mmpProductBenchmark.length > 0 ? mmpProductBenchmark[index] : false))
    .map((subInstallation) => subInstallation.subInstallationType);

  const fallbackApproachSubInstallations = subInstallations
    .filter((subInstallation) => isFallbackApproach(subInstallation.subInstallationType))
    .filter((_, index) => (mmpFallbackApproach.length > 0 ? mmpFallbackApproach[index] : false))
    .map((subInstallation) => subInstallation.subInstallationType);

  return [...productBenchmarkSubInstallations, ...fallbackApproachSubInstallations];
};

export const hasTwoOrMoreSubInstallationsCompleted = (state: PermitApplicationState) => {
  return getCompletedSubinstallationTypes(state).length > 1;
};

export const methodsBacklinkResolver = () => {
  const store: PermitApplicationStore<PermitApplicationState> = inject(PermitApplicationStore);
  const physicalPartsAndUnitsAnswer =
    store.permit.monitoringMethodologyPlans.digitizedPlan?.methodTask?.physicalPartsAndUnitsAnswer;

  return physicalPartsAndUnitsAnswer
    ? '../assign-parts'
    : hasTwoOrMoreSubInstallationsCompleted(store.getState())
      ? '../'
      : '../../';
};

export const isMethodsWizardCompleted = (state: PermitApplicationState) => {
  const { physicalPartsAndUnitsAnswer, connections, assignParts, avoidDoubleCountToggle } =
    state?.permit?.monitoringMethodologyPlans?.digitizedPlan?.methodTask || {};

  if (hasTwoOrMoreSubInstallationsCompleted(state)) {
    if (physicalPartsAndUnitsAnswer == undefined) {
      return false;
    } else if (physicalPartsAndUnitsAnswer) {
      return connections?.length > 0 && !!assignParts && !!avoidDoubleCountToggle;
    } else {
      return !!avoidDoubleCountToggle;
    }
  } else {
    return !!avoidDoubleCountToggle;
  }
};

export const createPhysicalPartsListForm = (state: PermitApplicationState) => {
  const subInstallationsCompleted = hasTwoOrMoreSubInstallationsCompleted(state);
  const digitizedPlan = state.permit.monitoringMethodologyPlans?.digitizedPlan;

  const form = new FormGroup(
    {},
    {
      validators: subInstallationsCompleted ? [noPhysicalPartsValidator(digitizedPlan?.methodTask?.connections)] : [],
    },
  );

  digitizedPlan.methodTask?.connections?.forEach((physicalPart) => {
    form.addControl(
      `item${physicalPart.itemId}`,
      new FormControl(
        null,
        subInstallationsCompleted ? subInstallationsExistsValidator(physicalPart.itemId, state) : null,
      ),
    );
  });

  return form;
};
