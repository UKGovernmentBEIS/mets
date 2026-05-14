import {
  AfterViewChecked,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  InjectionToken,
  Optional,
  signal,
  ViewChild,
} from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import {
  asyncScheduler,
  combineLatest,
  expand,
  map,
  Observable,
  of,
  SchedulerLike,
  shareReplay,
  switchMap,
  timer,
} from 'rxjs';

import {
  AccountFileAttachmentService,
  BulkDownloadService,
  EmpsService,
  FileAttachmentsService,
  FileDocumentsService,
  FileToken,
  PermitsService,
  RequestActionAttachmentsHandlingService,
  RequestActionFileDocumentsHandlingService,
  RequestTaskAttachmentsHandlingService,
} from 'pmrv-api';

export interface FileDownloadInfo {
  request: Observable<FileToken>;
  fileType: 'attachment' | 'document' | 'stream';
}

export const FILE_DOWNLOAD_SCHEDULER = new InjectionToken<SchedulerLike>('FILE_DOWNLOAD_SCHEDULER');

@Component({
  selector: 'app-file-download',
  standalone: false,
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p class="govuk-body">You should see your downloads in the downloads folder.</p>
    <a govukLink [href]="url$ | async" [attr.download]="streamFilename()" #anchor>
      Click to restart download if it fails
    </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileDownloadComponent implements AfterViewChecked {
  @ViewChild('anchor') readonly anchor: ElementRef<HTMLAnchorElement>;

  private hasDownloadedOnce = false;
  private fileDownloadAttachmentPath = `${this.fileAttachmentsService.configuration.basePath}/v1.0/file-attachments/`;
  private fileDownloadDocumentPath = `${this.fileDocumentsService.configuration.basePath}/v1.0/file-documents/`;
  private bulkDownloadFilePath = `${this.bulkDownloadService.configuration.basePath}/v1.0/bulk-download/file/`;

  streamFilename = signal(null);

  url$ = this.route.paramMap.pipe(
    map((params): FileDownloadInfo => {
      return params.has('taskId')
        ? this.requestTaskDownloadInfo(params)
        : params.has('actionId')
          ? this.requestActionDownloadInfo(params)
          : params.has('detailsAccountId')
            ? this.accountDownloadInfo(params)
            : params.has('empId')
              ? this.empsDownloadInfo(params)
              : params.has('workflow')
                ? this.requestBulkDownloadsDownloadInfo(params)
                : this.permitDownloadInfo(params);
    }),
    switchMap(({ request, fileType }) =>
      combineLatest([
        of(fileType),
        request.pipe(
          expand((response) =>
            timer(response?.tokenExpirationMinutes * 1000 * 60, this.scheduler).pipe(switchMap(() => request)),
          ),
        ),
      ]),
    ),
    switchMap(([fileType, fileToken]) => {
      const token = encodeURIComponent(String(fileToken.token));
      if (fileType === 'stream') {
        this.streamFilename.set('bulk-export.zip');
        return of(`${this.bulkDownloadFilePath}${token}`);
      }

      return of(
        fileType === 'attachment'
          ? `${this.fileDownloadAttachmentPath}${token}`
          : `${this.fileDownloadDocumentPath}${token}`,
      );
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly requestTaskAttachmentsHandlingService: RequestTaskAttachmentsHandlingService,
    private readonly requestActionAttachmentsHandlingService: RequestActionAttachmentsHandlingService,
    private readonly requestActionFileDocumentsHandlingService: RequestActionFileDocumentsHandlingService,
    private readonly fileAttachmentsService: FileAttachmentsService,
    private readonly fileDocumentsService: FileDocumentsService,
    private readonly bulkDownloadService: BulkDownloadService,
    private readonly permitsService: PermitsService,
    private readonly empsService: EmpsService,
    private readonly accountFileAttachmentService: AccountFileAttachmentService,
    @Optional() @Inject(FILE_DOWNLOAD_SCHEDULER) private readonly scheduler: SchedulerLike = asyncScheduler,
  ) {}

  ngAfterViewChecked(): void {
    if (
      (this.anchor.nativeElement.href.includes(this.fileDownloadAttachmentPath) ||
        this.anchor.nativeElement.href.includes(this.fileDownloadDocumentPath) ||
        this.anchor.nativeElement.href.includes(this.bulkDownloadFilePath)) &&
      !this.hasDownloadedOnce
    ) {
      this.anchor.nativeElement.click();
      this.hasDownloadedOnce = true;
      onfocus = () => close();
    }
  }

  private requestBulkDownloadsDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.bulkDownloadService.generateBulkDownloadExportToken(params.get('workflow'), params.get('period')),
      fileType: 'stream',
    };
  }

  private requestTaskDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.requestTaskAttachmentsHandlingService.generateRequestTaskGetFileAttachmentToken(
        Number(params.get('taskId')),
        params.get('uuid'),
      ),
      fileType: 'attachment',
    };
  }

  private requestActionDownloadInfo(params: ParamMap): FileDownloadInfo {
    if (params.get('fileType') === 'document') {
      return {
        request: this.requestActionFileDocumentsHandlingService.generateRequestActionGetFileDocumentToken(
          Number(params.get('actionId')),
          params.get('uuid'),
        ),
        fileType: 'document',
      };
    } else {
      return {
        request: this.requestActionAttachmentsHandlingService.generateRequestActionGetFileAttachmentToken(
          Number(params.get('actionId')),
          params.get('uuid'),
        ),
        fileType: 'attachment',
      };
    }
  }

  private empsDownloadInfo(params: ParamMap): FileDownloadInfo {
    if (params.get('fileType') === 'document') {
      return {
        request: this.empsService.generateGetEmpDocumentToken(params.get('empId'), params.get('uuid')),
        fileType: 'document',
      };
    } else {
      return {
        request: this.empsService.generateGetEmpAttachmentToken(params.get('empId'), params.get('uuid')),
        fileType: 'attachment',
      };
    }
  }

  private permitDownloadInfo(params: ParamMap): FileDownloadInfo {
    if (params.get('fileType') === 'document') {
      return {
        request: this.permitsService.generateGetPermitDocumentToken(params.get('permitId'), params.get('uuid')),
        fileType: 'document',
      };
    } else {
      return {
        request: this.permitsService.generateGetPermitAttachmentToken(params.get('permitId'), params.get('uuid')),
        fileType: 'attachment',
      };
    }
  }

  private accountDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.accountFileAttachmentService.generateGetFileAccountFileAttachmentToken(
        +params.get('accountId'),
        params.get('uuid'),
      ),
      fileType: 'attachment',
    };
  }
}
