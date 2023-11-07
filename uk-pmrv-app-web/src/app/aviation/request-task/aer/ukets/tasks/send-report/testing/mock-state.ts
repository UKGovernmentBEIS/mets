import { RequestTaskState } from '@aviation/request-task/store';

export const mockState = {
  requestTaskItem: {
    requestTask: {
      id: 278,
      type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
      payload: {
        payloadType: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD',
        reportingYear: '2022',
        serviceContactDetails: {
          name: 'operator10 aviation',
          roleCode: 'operator_admin',
          email: 'operator10@aviation.com',
        },
        aerMonitoringPlanVersions: [],
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
        aerAttachments: {
          '40178fef-857b-4ae3-8d5d-ed80126f6ecc': 'signatureTest (1) (1) (3) (3).bmp',
          'bf96752f-bcde-461f-a972-785bef083a0e': 'signatureTest (1) (1) (3) (3).bmp',
          'fa2bc6ea-2dda-4fda-ac1d-5461e5afdeeb': 'signatureTest (1) (1) (3) (3).bmp',
        },
        reportingRequired: true,
        aer: {
          additionalDocuments: {
            exist: false,
          },
          operatorDetails: {
            operatorName: '123',
            crcoCode: '444',
            flightIdentification: {
              flightIdentificationType: 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION',
              icaoDesignators: '111',
            },
            airOperatingCertificate: {
              certificateExist: true,
              certificateNumber: '123',
              issuingAuthority: 'Algeria - Établissement Nationale de la Navigation Aérienne (ENNA)',
              certificateFiles: ['40178fef-857b-4ae3-8d5d-ed80126f6ecc'],
            },
            operatingLicense: {
              licenseExist: true,
              licenseNumber: '555',
              issuingAuthority: 'Brunei Darussalam - Department of Civil Aviation',
            },
            organisationStructure: {
              legalStatusType: 'LIMITED_COMPANY',
              organisationLocation: {
                type: 'ONSHORE_STATE',
                line1: '123',
                city: '11',
                state: '1',
                country: 'BF',
              },
              registrationNumber: '1233123123',
              differentContactLocationExist: false,
            },
          },
          aerMonitoringPlanChanges: {
            notCoveredChangesExist: false,
          },
          monitoringApproach: {
            monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY',
            totalEmissionsType: 'FULL_SCOPE_FLIGHTS',
            fullScopeTotalEmissions: '1',
          },
          aggregatedEmissionsData: {
            aggregatedEmissionDataDetails: [
              {
                airportFrom: {
                  icao: 'EGWU',
                  name: 'NORTHOLT',
                  country: 'United Kingdom',
                  countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                },
                airportTo: {
                  icao: 'EIDW',
                  name: 'DUBLIN INTERNATIONAL',
                  country: 'Ireland',
                  countryType: 'EEA_COUNTRY',
                },
                fuelType: 'JET_KEROSENE',
                fuelConsumption: '1.938',
                flightsNumber: 2,
              },
              {
                airportFrom: {
                  icao: 'EGKB',
                  name: 'BIGGIN HILL',
                  country: 'United Kingdom',
                  countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                },
                airportTo: {
                  icao: 'EGTK',
                  name: 'OXFORD/KIDLINGTON',
                  country: 'United Kingdom',
                  countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                },
                fuelType: 'JET_KEROSENE',
                fuelConsumption: '0.52',
                flightsNumber: 1,
              },
              {
                airportFrom: {
                  icao: 'EGKB',
                  name: 'BIGGIN HILL',
                  country: 'United Kingdom',
                  countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                },
                airportTo: {
                  icao: 'EDDF',
                  name: 'FRANKFURT MAIN',
                  country: 'Germany',
                  countryType: 'EEA_COUNTRY',
                },
                fuelType: 'JET_KEROSENE',
                fuelConsumption: '1.096',
                flightsNumber: 1,
              },
              {
                airportFrom: {
                  icao: 'EGBB',
                  name: 'BIRMINGHAM',
                  country: 'United Kingdom',
                  countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                },
                airportTo: {
                  icao: 'EPWA',
                  name: 'WARSAW CHOPIN AIRPORT',
                  country: 'Poland',
                  countryType: 'EEA_COUNTRY',
                },
                fuelType: 'JET_KEROSENE',
                fuelConsumption: '2.013',
                flightsNumber: 1,
              },
              {
                airportFrom: {
                  icao: 'EGTE',
                  name: 'EXETER',
                  country: 'United Kingdom',
                  countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                },
                airportTo: {
                  icao: 'EGGW',
                  name: 'LONDON LUTON',
                  country: 'United Kingdom',
                  countryType: 'UKETS_FLIGHTS_TO_EEA_REPORTED',
                },
                fuelType: 'JET_KEROSENE',
                fuelConsumption: '1.317',
                flightsNumber: 1,
              },
            ],
          },
          saf: {
            exist: false,
          },
          aviationAerTotalEmissionsConfidentiality: {
            confidential: false,
          },
          aviationAerAircraftData: {
            aviationAerAircraftDataDetails: [
              {
                aircraftTypeDesignator: 'A320',
                subType: '',
                registrationNumber: '9HIHD',
                ownerOrLessor: 'Aviation Operator 10',
                startDate: '2022-01-01',
                endDate: '2022-12-31',
              },
            ],
          },
        },
        verificationPerformed: false,
      },
      assignable: true,
      assigneeUserId: '6ed9a60b-af4e-46fc-a296-3b2ea7bb43f8',
      assigneeFullName: 'operator11 aviation',
      daysRemaining: -112,
      startDate: '2023-07-20T15:57:57.668981Z',
    },
    allowedRequestTaskActions: [
      'AVIATION_AER_UKETS_SAVE_APPLICATION',
      'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT',
      'AVIATION_AER_UKETS_SUBMIT_APPLICATION',
      'AVIATION_AER_UKETS_REQUEST_VERIFICATION',
    ],
    userAssignCapable: true,
    requestInfo: {
      id: 'AEM00016-2022',
      type: 'AVIATION_AER_UKETS',
      competentAuthority: 'ENGLAND',
      accountId: 16,
      requestMetadata: {
        type: 'AVIATION_AER',
        year: '2022',
        initiatorRequest: {
          type: 'AVIATION_AER_UKETS',
        },
      },
    },
  },
  relatedTasks: [
    {
      creationDate: '2023-07-20T15:57:57.668981Z',
      requestId: 'AEM00016-2022',
      requestType: 'AVIATION_AER_UKETS',
      taskId: 278,
      taskAssignee: {
        firstName: 'operator11',
        lastName: 'aviation',
      },
      taskAssigneeType: 'OPERATOR',
      taskType: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
      daysRemaining: -112,
      accountId: 16,
      accountName: 'Aviation Operator 11',
      competentAuthority: 'ENGLAND',
      isNew: false,
    },
  ],
  timeline: [],
  isTaskReassigned: false,
  taskReassignedTo: null,
  isEditable: true,
  tasksState: {
    abbreviations: {
      status: 'not started',
    },
  },
} as RequestTaskState;

export const mockStateCannotSubmit = {
  ...mockState,
  requestTaskItem: {
    ...mockState.requestTaskItem,
    requestTask: {
      ...mockState.requestTaskItem.requestTask,
      payload: {
        ...mockState.requestTaskItem.requestTask.payload,
        aerSectionsCompleted: {
          ...mockState.requestTaskItem.requestTask.payload['aerSectionsCompleted'],
          saf: [false],
        },
      },
    },
  },
};
