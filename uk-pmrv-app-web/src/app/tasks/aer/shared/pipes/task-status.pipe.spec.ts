import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { mockAerDataReviewDecisionAmends, mockReview } from '@tasks/aer/review/testing/mock-review';
import { mockAerApplyPayload } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import {
  AerApplicationReviewRequestTaskPayload,
  AerApplicationSubmitRequestTaskPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
} from 'pmrv-api';

import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  let pipe: TaskStatusPipe;
  let store: CommonTasksStore;
  let aerService: AerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    });
    store = TestBed.inject(CommonTasksStore);
    aerService = TestBed.inject(AerService);
    pipe = new TaskStatusPipe(aerService);
  });

  it('should be created', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve statuses', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'AER_APPLICATION_SUBMIT_PAYLOAD',
            aer: {},
            aerSectionsCompleted: {},
          } as AerApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('aerMonitoringPlanDeviation'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('abbreviations'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('additionalDocuments'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('confidentialityStatement'))).resolves.toEqual('not started');

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'AER_APPLICATION_SUBMIT_PAYLOAD',
            aer: mockAerApplyPayload.aer,
            aerSectionsCompleted: {
              ...mockAerApplyPayload.aerSectionsCompleted,
              sourceStreams: [true],
              emissionSources: [true],
              regulatedActivities: [true],
            },
          } as AerApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('aerMonitoringPlanDeviation'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('abbreviations'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('additionalDocuments'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('confidentialityStatement'))).resolves.toEqual('complete');

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('emissionSources'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('monitoringApproachEmissions'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('regulatedActivities'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('pollutantRegisterActivities'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('naceCodes'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('CALCULATION_CO2'))).resolves.toEqual('complete');

    await expect(firstValueFrom(pipe.transform('sendReport'))).resolves.toEqual('cannot start yet');

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'AER_APPLICATION_SUBMIT_PAYLOAD',
            aer: mockAerApplyPayload.aer,
            aerSectionsCompleted: {},
          } as AerApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('aerMonitoringPlanDeviation'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('abbreviations'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('additionalDocuments'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('confidentialityStatement'))).resolves.toEqual('in progress');

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('emissionSources'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('monitoringApproachEmissions'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('regulatedActivities'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('pollutantRegisterActivities'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('naceCodes'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('CALCULATION_CO2'))).resolves.toEqual('cannot start yet');

    await expect(firstValueFrom(pipe.transform('sendReport'))).resolves.toEqual('cannot start yet');

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            verificationReport: {},
            verificationSectionsCompleted: {},
          } as AerApplicationVerificationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('verificationTeamDetails'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('methodologiesToCloseDataGaps'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('materialityLevel'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('complianceMonitoringReporting'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('overallAssessment'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('uncorrectedMisstatements'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('summaryOfConditions'))).resolves.toEqual('not started');

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            ...mockVerificationApplyPayload,
            verificationSectionsCompleted: {
              verificationTeamDetails: [false],
              methodologiesToCloseDataGaps: [false],
              materialityLevel: [false],
              complianceMonitoringReporting: [false],
              overallAssessment: [false],
              uncorrectedMisstatements: [false],
              summaryOfConditions: [false],
            },
          } as AerApplicationVerificationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('verificationTeamDetails'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('methodologiesToCloseDataGaps'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('materialityLevel'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('complianceMonitoringReporting'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('overallAssessment'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('uncorrectedMisstatements'))).resolves.toEqual('in progress');
    await expect(firstValueFrom(pipe.transform('summaryOfConditions'))).resolves.toEqual('in progress');

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            verificationSectionsCompleted: {
              verificationTeamDetails: [true],
              methodologiesToCloseDataGaps: [true],
              materialityLevel: [true],
              complianceMonitoringReporting: [true],
              uncorrectedMisstatements: [true],
              summaryOfConditions: [true],
              overallAssessment: [true],
            },
          } as AerApplicationVerificationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('verificationTeamDetails'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('methodologiesToCloseDataGaps'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('materialityLevel'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('complianceMonitoringReporting'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('overallAssessment'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('uncorrectedMisstatements'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('summaryOfConditions'))).resolves.toEqual('complete');

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_REVIEW',
          payload: {
            ...mockReview,
            reviewSectionsCompleted: {},
          } as AerApplicationReviewRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('ADDITIONAL_INFORMATION'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('EMISSIONS_SUMMARY'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('CALCULATION_CO2'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_CO2'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('FALLBACK'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('CALCULATION_PFC'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('INHERENT_CO2'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('VERIFIER_DETAILS'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('OPINION_STATEMENT'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('ETS_COMPLIANCE_RULES'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('COMPLIANCE_MONITORING_REPORTING'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('OVERALL_DECISION'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('UNCORRECTED_MISSTATEMENTS'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('UNCORRECTED_NON_CONFORMITIES'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('UNCORRECTED_NON_COMPLIANCES'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('RECOMMENDED_IMPROVEMENTS'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('CLOSE_DATA_GAPS_METHODOLOGIES'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('MATERIALITY_LEVEL'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('SUMMARY_OF_CONDITIONS'))).resolves.toEqual('undecided');

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_REVIEW',
          payload: {
            ...mockReview,
          } as AerApplicationReviewRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('ADDITIONAL_INFORMATION'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('EMISSIONS_SUMMARY'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('CALCULATION_CO2'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_CO2'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('FALLBACK'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('CALCULATION_PFC'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('INHERENT_CO2'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('VERIFIER_DETAILS'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('OPINION_STATEMENT'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('ETS_COMPLIANCE_RULES'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('COMPLIANCE_MONITORING_REPORTING'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('OVERALL_DECISION'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('UNCORRECTED_MISSTATEMENTS'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('UNCORRECTED_NON_CONFORMITIES'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('UNCORRECTED_NON_COMPLIANCES'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('RECOMMENDED_IMPROVEMENTS'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('CLOSE_DATA_GAPS_METHODOLOGIES'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('MATERIALITY_LEVEL'))).resolves.toEqual('accepted');
    await expect(firstValueFrom(pipe.transform('SUMMARY_OF_CONDITIONS'))).resolves.toEqual('accepted');

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_REVIEW',
          payload: {
            ...mockReview,
            reviewGroupDecisions: {
              ...mockReview.reviewGroupDecisions,
              INSTALLATION_DETAILS: mockAerDataReviewDecisionAmends,
              FUELS_AND_EQUIPMENT: mockAerDataReviewDecisionAmends,
              ADDITIONAL_INFORMATION: mockAerDataReviewDecisionAmends,
              EMISSIONS_SUMMARY: mockAerDataReviewDecisionAmends,
              CALCULATION_CO2: mockAerDataReviewDecisionAmends,
              MEASUREMENT_CO2: mockAerDataReviewDecisionAmends,
              FALLBACK: mockAerDataReviewDecisionAmends,
              MEASUREMENT_N2O: mockAerDataReviewDecisionAmends,
              CALCULATION_PFC: mockAerDataReviewDecisionAmends,
              INHERENT_CO2: mockAerDataReviewDecisionAmends,
            },
          } as AerApplicationReviewRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('ADDITIONAL_INFORMATION'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('EMISSIONS_SUMMARY'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('CALCULATION_CO2'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_CO2'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('FALLBACK'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT_N2O'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('CALCULATION_PFC'))).resolves.toEqual('operator to amend');
    await expect(firstValueFrom(pipe.transform('INHERENT_CO2'))).resolves.toEqual('operator to amend');
  });
});
