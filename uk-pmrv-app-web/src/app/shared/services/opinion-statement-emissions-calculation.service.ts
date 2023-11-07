import { Injectable } from '@angular/core';

import BigNumber from 'bignumber.js';
import { fromPairs, indexOf, sortBy } from 'lodash-es';

import { MonitoringApproachTypeEmissions } from 'pmrv-api';

import { format } from '../utils/bignumber.utils';

@Injectable()
export class OpinionStatementEmissionsCalculationService {
  calculateTotalEmissions(monitoringApproachTypeEmissions?: MonitoringApproachTypeEmissions): string {
    if (!monitoringApproachTypeEmissions) {
      return '0';
    }
    const total = Object.keys(monitoringApproachTypeEmissions).reduce((accumulator, key) => {
      const currentValue = monitoringApproachTypeEmissions?.[key]?.reportableEmissions
        ? monitoringApproachTypeEmissions[key].reportableEmissions
        : '0';

      return accumulator.plus(currentValue);
    }, new BigNumber('0'));

    return format(total);
  }

  calculateTotalBiomass(monitoringApproachTypeEmissions?: MonitoringApproachTypeEmissions): string {
    if (!monitoringApproachTypeEmissions) {
      return '0';
    }
    const total = Object.keys(monitoringApproachTypeEmissions).reduce((accumulator, key) => {
      const currentValue = monitoringApproachTypeEmissions?.[key]?.sustainableBiomass
        ? monitoringApproachTypeEmissions[key].sustainableBiomass
        : '0';

      return accumulator.plus(currentValue);
    }, new BigNumber('0'));

    return format(total);
  }

  sortMonitoringApproachTypeEmissions(
    monitoringApproachTypeEmissions?: MonitoringApproachTypeEmissions,
  ): MonitoringApproachTypeEmissions | null {
    if (!monitoringApproachTypeEmissions) {
      return null;
    }
    const order = this.monitoringApproachTypeEmissionsOrder();
    const sortedPairs = sortBy(Object.entries(monitoringApproachTypeEmissions), (obj) => {
      return indexOf(order, obj[0]);
    });
    return <MonitoringApproachTypeEmissions>fromPairs(sortedPairs);
  }

  private monitoringApproachTypeEmissionsOrder(): (keyof MonitoringApproachTypeEmissions)[] {
    return [
      'calculationCombustionEmissions',
      'calculationProcessEmissions',
      'calculationMassBalanceEmissions',
      'calculationTransferredCO2Emissions',
      'measurementCO2Emissions',
      'measurementTransferredCO2Emissions',
      'measurementN2OEmissions',
      'measurementTransferredN2OEmissions',
      'calculationPFCEmissions',
      'inherentCO2Emissions',
      'fallbackEmissions',
    ];
  }

  mapMonitoringApproachTypeCategoryToLabel(category: keyof MonitoringApproachTypeEmissions) {
    switch (category) {
      case 'calculationCombustionEmissions':
        return 'Calculation of combustion emissions';
      case 'calculationProcessEmissions':
        return 'Calculation of process emissions';
      case 'calculationMassBalanceEmissions':
        return 'Calculation of mass balance emissions';
      case 'calculationTransferredCO2Emissions':
        return 'Calculation of transferred CO2 emissions';
      case 'measurementCO2Emissions':
        return 'Measurement of CO2 emissions';
      case 'measurementTransferredCO2Emissions':
        return 'Measurement of transferred CO2 emissions';
      case 'measurementN2OEmissions':
        return 'Measurement of N2O emissions';
      case 'measurementTransferredN2OEmissions':
        return 'Measurement of transferred N2O emissions';
      case 'calculationPFCEmissions':
        return 'Calculation of PFC emissions';
      case 'inherentCO2Emissions':
        return 'Inherent CO2 emissions';
      case 'fallbackEmissions':
        return 'Fallback emissions';
    }
  }
}
