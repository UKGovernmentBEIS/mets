import { TestBed } from '@angular/core/testing';

import { MonitoringApproachTypeEmissions } from 'pmrv-api';

import { OpinionStatementEmissionsCalculationService } from './opinion-statement-emissions-calculation.service';

describe('OpinionStatementEmissionsCalculationService', () => {
  let service: OpinionStatementEmissionsCalculationService;
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [OpinionStatementEmissionsCalculationService] });
    service = TestBed.inject(OpinionStatementEmissionsCalculationService);
  });

  describe('for calculation and sorting methods', () => {
    let mockReportableEmissions: MonitoringApproachTypeEmissions = {
      calculationCombustionEmissions: {
        reportableEmissions: '111.99999',
        sustainableBiomass: '111.99999',
      },
      calculationProcessEmissions: {
        reportableEmissions: '222.00001',
        sustainableBiomass: '222.00001',
      },
      calculationMassBalanceEmissions: {
        reportableEmissions: '333.33333',
        sustainableBiomass: '333.33333',
      },
      calculationTransferredCO2Emissions: {
        reportableEmissions: '444.44444',
        sustainableBiomass: '444.44444',
      },
      measurementCO2Emissions: {
        reportableEmissions: '555.55555',
        sustainableBiomass: '555.55555',
      },
      measurementTransferredCO2Emissions: {
        reportableEmissions: '666.66666',
        sustainableBiomass: '666.66666',
      },
      measurementN2OEmissions: {
        reportableEmissions: '777.77777',
        sustainableBiomass: '777.77777',
      },
      measurementTransferredN2OEmissions: {
        reportableEmissions: '888.88888',
        sustainableBiomass: '888.88888',
      },
      calculationPFCEmissions: {
        reportableEmissions: '999.99999',
      },
      inherentCO2Emissions: {
        reportableEmissions: '1010.10101',
      },
      fallbackEmissions: {
        reportableEmissions: '1111.11111',
        sustainableBiomass: '1111.11111',
      },
    };

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should calculate total emissions correctly', () => {
      expect(service.calculateTotalEmissions(mockReportableEmissions)).toEqual('7121.87874');
      expect(service.calculateTotalEmissions(null)).toEqual('0');
    });

    it('should calculate total biomass correctly', () => {
      expect(service.calculateTotalBiomass(mockReportableEmissions)).toEqual('5111.77774');
      expect(service.calculateTotalBiomass(null)).toEqual('0');
    });

    it('should sort reportable emissions by enum', () => {
      mockReportableEmissions = {
        ...mockReportableEmissions,
        calculationMassBalanceEmissions: {
          reportableEmissions: '0.99999',
          sustainableBiomass: '0.00001',
        },
      };
      const sortedEmissionsKeys = Object.keys(service.sortMonitoringApproachTypeEmissions(mockReportableEmissions));
      expect(sortedEmissionsKeys[0]).toEqual('calculationCombustionEmissions');
      expect(sortedEmissionsKeys[1]).toEqual('calculationProcessEmissions');
      expect(sortedEmissionsKeys[2]).toEqual('calculationMassBalanceEmissions');
    });

    it('should map MonitoringApproachTypeEmissionCategory to label', () => {
      expect(service.mapMonitoringApproachTypeCategoryToLabel('calculationCombustionEmissions')).toEqual(
        'Calculation of combustion emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('calculationProcessEmissions')).toEqual(
        'Calculation of process emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('calculationMassBalanceEmissions')).toEqual(
        'Calculation of mass balance emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('calculationTransferredCO2Emissions')).toEqual(
        'Calculation of transferred CO2 emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('measurementCO2Emissions')).toEqual(
        'Measurement of CO2 emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('measurementTransferredCO2Emissions')).toEqual(
        'Measurement of transferred CO2 emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('measurementN2OEmissions')).toEqual(
        'Measurement of N2O emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('measurementTransferredN2OEmissions')).toEqual(
        'Measurement of transferred N2O emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('calculationPFCEmissions')).toEqual(
        'Calculation of PFC emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('inherentCO2Emissions')).toEqual(
        'Inherent CO2 emissions',
      );
      expect(service.mapMonitoringApproachTypeCategoryToLabel('fallbackEmissions')).toEqual('Fallback emissions');
    });
  });

  describe('for trimming method', () => {
    let mockReportableEmissions: MonitoringApproachTypeEmissions = {
      calculationCombustionEmissions: {
        reportableEmissions: '0',
        sustainableBiomass: '0',
      },
      calculationProcessEmissions: {
        reportableEmissions: '0',
        sustainableBiomass: '0',
      },
      calculationMassBalanceEmissions: {
        reportableEmissions: '0',
        sustainableBiomass: '0',
      },
      calculationTransferredCO2Emissions: {
        reportableEmissions: '0',
        sustainableBiomass: '0',
      },
      measurementCO2Emissions: {
        reportableEmissions: '0',
        sustainableBiomass: '0',
      },
      measurementTransferredCO2Emissions: {
        reportableEmissions: '0',
        sustainableBiomass: '0',
      },
      measurementN2OEmissions: {
        reportableEmissions: '0',
        sustainableBiomass: '0',
      },
      measurementTransferredN2OEmissions: {
        reportableEmissions: '0',
        sustainableBiomass: '0',
      },
      calculationPFCEmissions: {
        reportableEmissions: '0',
      },
      inherentCO2Emissions: {
        reportableEmissions: '0',
      },
      fallbackEmissions: {
        reportableEmissions: '0',
        sustainableBiomass: '0',
      },
    };

    it('should round up positive with 5 decimals', () => {
      mockReportableEmissions = {
        ...mockReportableEmissions,
        inherentCO2Emissions: {
          reportableEmissions: '1.123455',
        },
      };
      expect(service.calculateTotalEmissions(mockReportableEmissions)).toEqual('1.12346');
    });

    it('should round up negative with 5 decimals', () => {
      mockReportableEmissions = {
        ...mockReportableEmissions,
        inherentCO2Emissions: {
          reportableEmissions: '-1.123455',
        },
      };
      expect(service.calculateTotalEmissions(mockReportableEmissions)).toEqual('-1.12346');
    });

    it('should round down positive with 5 decimals', () => {
      mockReportableEmissions = {
        ...mockReportableEmissions,
        inherentCO2Emissions: {
          reportableEmissions: '1.123454',
        },
      };
      expect(service.calculateTotalEmissions(mockReportableEmissions)).toEqual('1.12345');
    });

    it('should round down negative with 5 decimals', () => {
      mockReportableEmissions = {
        ...mockReportableEmissions,
        inherentCO2Emissions: {
          reportableEmissions: '-1.123454',
        },
      };
      expect(service.calculateTotalEmissions(mockReportableEmissions)).toEqual('-1.12345');
    });

    it('should trim multiple unnecessary decimals', () => {
      mockReportableEmissions = {
        ...mockReportableEmissions,
        inherentCO2Emissions: {
          reportableEmissions: '1.00000',
        },
      };
      expect(service.calculateTotalEmissions(mockReportableEmissions)).toEqual('1');
    });
  });
});
