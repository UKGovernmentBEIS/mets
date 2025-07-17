import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { AviationAerUkEtsApplicationReviewRequestTaskPayload, RequestTaskItemDTO } from 'pmrv-api';

import { RequestTaskStore } from '../store';
import { CompleteReportGuard } from './complete-report-page.guard';

describe('CompleteReportGuard', () => {
  let guard: CompleteReportGuard;
  let router: Router;
  let store: RequestTaskStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };
  const requestTaskItem = {
    requestTask: {
      type: 'AVIATION_AER_UKETS_APPLICATION_REVIEW',
      payload: {
        aer: {
          saf: {
            exist: false,
          },
          operatorDetails: {
            crcoCode: '44',
            operatorName: 'Aviation Operator 44',
            operatingLicense: {
              licenseExist: false,
            },
            flightIdentification: {
              icaoDesignators: '33',
              flightIdentificationType: 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION',
            },
            organisationStructure: {
              legalStatusType: 'LIMITED_COMPANY',
              registrationNumber: '33',
              organisationLocation: {
                city: '33',
                type: 'ONSHORE_STATE',
                line1: '33',
                country: 'BH',
              },
              differentContactLocationExist: false,
            },
            airOperatingCertificate: {
              certificateExist: false,
            },
          },
          monitoringApproach: {
            totalEmissionsType: 'FULL_SCOPE_FLIGHTS',
            monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY',
            fullScopeTotalEmissions: 222,
          },
          additionalDocuments: {
            exist: false,
          },
          aggregatedEmissionsData: {
            aggregatedEmissionDataDetails: [
              {
                fuelType: 'JET_KEROSENE',
                airportTo: {
                  icao: 'EGGW',
                  name: 'LONDON LUTON',
                  country: 'United Kingdom',
                  countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                },
                airportFrom: {
                  icao: 'EGTE',
                  name: 'EXETER',
                  country: 'United Kingdom',
                  countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                },
                flightsNumber: 1,
                fuelConsumption: 1.317,
              },
            ],
          },
          aviationAerAircraftData: {
            aviationAerAircraftDataDetails: [
              {
                endDate: '2022-12-31',
                startDate: '2022-01-01',
                ownerOrLessor: 'Aviation Operator 44',
                registrationNumber: '9HIHD',
                aircraftTypeDesignator: 'A320',
              },
              {
                endDate: '2022-12-31',
                startDate: '2022-01-02',
                ownerOrLessor: 'Aviation Operator 44',
                registrationNumber: '9HIHD',
                aircraftTypeDesignator: 'B738',
              },
            ],
          },
          aerMonitoringPlanChanges: {
            details: '333',
            notCoveredChangesExist: true,
          },
          aviationAerTotalEmissionsConfidentiality: {
            confidential: false,
          },
          confidentiality: {
            totalEmissionsPublished: false,
            aggregatedStatePairDataPublished: false,
          },
        } as any,

        type: 'AVIATION_AER_UKETS_APPLICATION_REVIEW',
        reportingYear: '2022',
        aerAttachments: {},
        reportingRequired: true,
        reviewAttachments: {},
        aerSectionsCompleted: {
          saf: [true],
          operatorDetails: [true],
          monitoringApproach: [true],
          additionalDocuments: [true],
          reportingObligation: [true],
          serviceContactDetails: [true],
          aggregatedEmissionsData: [true],
          aviationAerAircraftData: [true],
          aerMonitoringPlanChanges: [true],
          aviationAerTotalEmissionsConfidentiality: [true],
        },
        reviewGroupDecisions: {
          AIRCRAFT_DATA: {
            type: 'ACCEPTED',
            details: {},
            reviewDataType: 'AER_DATA',
          },
          TOTAL_EMISSIONS: {
            type: 'ACCEPTED',
            details: {},
            reviewDataType: 'AER_DATA',
          },
          OPERATOR_DETAILS: {
            type: 'ACCEPTED',
            details: {
              notes: '222',
            },
            reviewDataType: 'AER_DATA',
          },
          MONITORING_APPROACH: {
            type: 'ACCEPTED',
            details: {},
            reviewDataType: 'AER_DATA',
          },
          ADDITIONAL_DOCUMENTS: {
            type: 'ACCEPTED',
            details: {},
            reviewDataType: 'AER_DATA',
          },
          MONITORING_PLAN_CHANGES: {
            type: 'ACCEPTED',
            details: {},
            reviewDataType: 'AER_DATA',
          },
          SERVICE_CONTACT_DETAILS: {
            type: 'ACCEPTED',
            details: {
              notes: '111',
            },
            reviewDataType: 'AER_DATA',
          },
          AGGREGATED_EMISSIONS_DATA: {
            type: 'ACCEPTED',
            details: {},
            reviewDataType: 'AER_DATA',
          },
          EMISSIONS_REDUCTION_CLAIM: {
            type: 'ACCEPTED',
            details: {},
            reviewDataType: 'AER_DATA',
          },
          CONFIDENTIALITY: {
            type: 'ACCEPTED',
            details: {},
            reviewDataType: 'AER_DATA',
          },
        },
        serviceContactDetails: {
          name: 'operator44 aviation',
          email: 'operator44@aviation.com',
          roleCode: 'operator_admin',
        },
        reviewSectionsCompleted: {
          AIRCRAFT_DATA: true,
          TOTAL_EMISSIONS: true,
          OPERATOR_DETAILS: true,
          MONITORING_APPROACH: true,
          ADDITIONAL_DOCUMENTS: true,
          MONITORING_PLAN_CHANGES: true,
          SERVICE_CONTACT_DETAILS: true,
          AGGREGATED_EMISSIONS_DATA: true,
          EMISSIONS_REDUCTION_CLAIM: true,
          CONFIDENTIALITY: true,
        },
      } as any,
    },
    allowedRequestTaskActions: [
      'AVIATION_AER_UKETS_COMPLETE_REVIEW',
      'AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION',
      'AVIATION_AER_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
    ],
  } as any;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [HttpClientModule, RouterTestingModule],
    });
    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(CompleteReportGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if decisions are not completed', async () => {
    store.setState({
      ...store.getState(),
      isEditable: false,
      requestTaskItem: {
        ...requestTaskItem,
        requestTask: {
          ...requestTaskItem.requestTask,
          payload: {
            ...requestTaskItem.requestTask.payload,
            reviewGroupDecisions: {
              ...requestTaskItem.requestTask.payload.reviewGroupDecisions,
              AGGREGATED_EMISSIONS_DATA: {},
            },
          } as AviationAerUkEtsApplicationReviewRequestTaskPayload,
        },
      } as RequestTaskItemDTO,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/aviation/tasks/${activatedRouteSnapshot.params.taskId}`));
  });

  it('should activate if all are completed', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: requestTaskItem,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });
});
