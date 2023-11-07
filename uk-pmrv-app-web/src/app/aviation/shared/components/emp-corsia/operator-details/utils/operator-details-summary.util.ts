import { FileUpload } from '@shared/file-input/file-upload-event';

import { AviationCorsiaOperatorDetails, AviationOperatorDetails, EmpOperatorDetails } from 'pmrv-api';

export interface ViewModel {
  operatorDetails: EmpOperatorDetails & AviationOperatorDetails & AviationCorsiaOperatorDetails;
  certificationFiles: { fileName: string; downloadUrl: string }[];
  evidenceFiles: { fileName: string; downloadUrl: string }[];
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision?: boolean;
  showVariationDecision?: boolean;
  showVariationRegLedDecision?: boolean;
  showDiff?: boolean;
  originalOperatorDetails?: EmpOperatorDetails & AviationOperatorDetails;
  originalCertificationFiles?: { fileName: string; downloadUrl: string }[];
  originalEvidenceFiles?: { fileName: string; downloadUrl: string }[];
}

export const transformFiles = (files: string[], downloadUrl: string) => {
  return (
    (files as unknown as FileUpload[])?.map((doc) => {
      return {
        fileName: doc.file?.name,
        downloadUrl: `${downloadUrl}${doc.uuid}`,
      };
    }) ?? []
  );
};
