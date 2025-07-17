import { RequestDetailsDTO } from 'pmrv-api';

export const inspectionsTypesMap: Record<string, Record<string, RequestDetailsDTO['requestType'][]>> = {
  INSTALLATION: {
    'On-site inspection': ['INSTALLATION_ONSITE_INSPECTION'],
    'Audit report': ['INSTALLATION_AUDIT'],
  },
};
export const inspectionsTypesTagsMap: Record<string, any> = {
  INSTALLATION_ONSITE_INSPECTION: 'On-site inspection',
  INSTALLATION_AUDIT: 'Audit report',
};

export const inspectionsStatusesMap: Record<string, Partial<Record<RequestDetailsDTO['requestStatus'], string>>> = {
  INSTALLATION: {
    CANCELLED: 'Cancelled',
    COMPLETED: 'Completed',
    IN_PROGRESS: 'In Progress',
  },
};

export const inspectionsStatusesTagMap: Partial<Record<RequestDetailsDTO['requestStatus'], string>> = {
  CANCELLED: 'CANCELLED',
  COMPLETED: 'COMPLETED',
  IN_PROGRESS: 'IN PROGRESS',
};
