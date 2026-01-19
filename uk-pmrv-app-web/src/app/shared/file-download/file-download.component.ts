import { HttpResponse } from '@angular/common/http';
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
  iif,
  map,
  Observable,
  of,
  SchedulerLike,
  shareReplay,
  switchMap,
  tap,
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
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p class="govuk-body">You should see your downloads in the downloads folder.</p>
    <a govukLink [href]="url$ | async" [download]="streamFilename()" #anchor>Click to restart download if it fails</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileDownloadComponent implements AfterViewChecked {
  @ViewChild('anchor') readonly anchor: ElementRef<HTMLAnchorElement>;

  private hasDownloadedOnce = false;
  private fileDownloadAttachmentPath = `${this.fileAttachmentsService.configuration.basePath}/v1.0/file-attachments/`;
  private fileDownloadDocumentPath = `${this.fileDocumentsService.configuration.basePath}/v1.0/file-documents/`;

  private downloadBlob(blob: Blob, filename: string) {
    if (!this.hasDownloadedOnce) {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      a.style.display = 'none';
      document.body.appendChild(a);
      a.click();
      a.remove();
      this.hasDownloadedOnce = true;
      URL.revokeObjectURL(url);
    }
  }

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
    switchMap(([fileType, fileToken]) =>
      iif(
        () => fileType !== 'stream',

        // TRUE: return a URL string (or null) as an Observable
        of(
          fileType === 'attachment'
            ? `${this.fileDownloadAttachmentPath}${encodeURIComponent(String(fileToken.token))}`
            : `${this.fileDownloadDocumentPath}${encodeURIComponent(String(fileToken.token))}`,
        ),

        // FALSE: stream download side-effect; return something for url$ (null is typical)
        this.bulkDownloadService
          .bulkDownloadExport(fileToken.token, 'response', null, { httpHeaderAccept: 'application/octet-stream' })
          .pipe(
            tap((res: HttpResponse<Blob>) => {
              const blob = res.body;
              if (!blob) throw new Error('Empty body');

              const filename = extractFilename(res, 'bulk-export.zip');
              this.streamFilename.set(filename);
              this.downloadBlob(blob, filename);
            }),
            map((res) => URL.createObjectURL(res.body)),
          ),
      ),
    ),

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
        this.anchor.nativeElement.href.includes(this.fileDownloadDocumentPath)) &&
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

function extractFilename(res: HttpResponse<Blob>, fallback = 'download.zip'): string {
  const cd = res.headers.get('content-disposition');
  if (!cd) return fallback;

  // RFC 5987 / 6266 (filename*)
  const utf8Match = cd.match(/filename\*\s*=\s*UTF-8''([^;]+)/i);
  if (utf8Match) {
    return decodeURIComponent(utf8Match[1]);
  }

  // Basic filename=
  const asciiMatch = cd.match(/filename\s*=\s*"?([^";]+)"?/i);
  if (asciiMatch) {
    return asciiMatch[1];
  }

  return fallback;
}
