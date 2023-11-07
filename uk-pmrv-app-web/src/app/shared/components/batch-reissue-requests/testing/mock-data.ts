import {
  BatchReissuesResponseDTO,
  PermitBatchReissueRequestMetadata,
  RequestDetailsDTO,
  RequestDetailsSearchResults,
} from 'pmrv-api';

export const mockPermitBatchReissues: RequestDetailsSearchResults = {
  canInitiateBatchReissue: true,
  requestDetails: [
    {
      id: 'requestId1',
      requestType: 'PERMIT_BATCH_REISSUE',
      requestStatus: 'COMPLETED',
      creationDate: new Date('2023-05-25').toISOString(),
      requestMetadata: {
        type: 'PERMIT_BATCH_REISSUE',
        submitterId: 'submitterId',
        submitter: 'submitter_full_name1',
        submissionDate: new Date('2023-05-25').toISOString(),
        accountsReports: {
          '1': {
            permitId: 'permitId1',
            installationName: 'install1',
            operatorName: 'operator1',
          },
          '2': {
            permitId: 'permitId2',
            installationName: 'install2',
            operatorName: 'operator2',
          },
        },
      } as PermitBatchReissueRequestMetadata,
    } as RequestDetailsDTO,
    {
      id: 'requestId2',
      requestType: 'PERMIT_BATCH_REISSUE',
      requestStatus: 'IN_PROGRESS',
      creationDate: new Date('2023-05-26').toISOString(),
      requestMetadata: {
        type: 'PERMIT_BATCH_REISSUE',
        submitterId: 'submitterId',
        submitter: 'submitter_full_name2',
        submissionDate: new Date('2023-05-26').toISOString(),
        accountsReports: {
          '3': {
            permitId: 'permitId1',
            installationName: 'install1',
            operatorName: 'operator1',
          },
          '4': {
            permitId: 'permitId2',
            installationName: 'install2',
            operatorName: 'operator2',
          },
        },
      } as PermitBatchReissueRequestMetadata,
    } as RequestDetailsDTO,
  ],
  total: 0,
} as BatchReissuesResponseDTO;
