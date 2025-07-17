import { PreviewDocumentRequest } from 'pmrv-api';

export interface DocumentFilenameAndDocumentType {
  filename: string;
  documentType: PreviewDocumentRequest['documentType'];
}
