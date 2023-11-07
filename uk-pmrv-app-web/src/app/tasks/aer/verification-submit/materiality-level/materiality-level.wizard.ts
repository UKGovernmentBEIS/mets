import { MaterialityLevel } from 'pmrv-api';

export function materialityLevelWizard(materialityLevelInfo: MaterialityLevel): boolean {
  return (
    !!materialityLevelInfo &&
    !!materialityLevelInfo.materialityDetails &&
    materialityLevelInfo.accreditationReferenceDocumentTypes?.length > 0
  );
}
