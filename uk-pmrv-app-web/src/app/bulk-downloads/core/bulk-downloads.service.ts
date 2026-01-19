import { Injectable } from '@angular/core';

import { shareReplay } from 'rxjs';

import { AttachedFile } from '@shared/types/attached-file.type';

import { BulkDownloadService, RequestDetailsDTO } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class BulkDownloadsService {
  get getBulkDownloadWorkflows$() {
    return this.bulkDownloadService.getAvailableWorkflows().pipe(shareReplay({ bufferSize: 1, refCount: false }));
  }

  constructor(private readonly bulkDownloadService: BulkDownloadService) {}

  getBulkDownloadPeriods(workflow: string) {
    return this.bulkDownloadService.getAvailablePeriods(workflow).pipe(shareReplay({ bufferSize: 1, refCount: false }));
  }

  getStreamingBulkDownloadUrl(workflowName: RequestDetailsDTO['requestType'], period: string): AttachedFile {
    const url = `/bulk-downloads/attachment/${workflowName}/${period}`;
    return {
      downloadUrl: url,
      fileName: `${workflowName}-${period}.zip`,
    };
  }
}
