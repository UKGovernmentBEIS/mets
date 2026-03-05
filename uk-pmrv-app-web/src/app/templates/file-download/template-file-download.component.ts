import { AfterViewChecked, ChangeDetectionStrategy, Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { expand, map, Observable, switchMap, timer } from 'rxjs';

import { DocumentTemplateFilesService, FileDocumentTemplatesService, FileToken } from 'pmrv-api';

@Component({
  selector: 'app-template-file-download',
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p class="govuk-body">You should see your downloads in the downloads folder.</p>
    <a govukLink [href]="url$ | async" download #anchor>Click to restart download if it fails</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TemplateFileDownloadComponent implements AfterViewChecked {
  @ViewChild('anchor') readonly anchor: ElementRef<HTMLAnchorElement>;

  private hasDownloadedOnce = false;
  private fileDownloadDocumentTemplatePath = `${this.fileDocumentTemplatesService.configuration.basePath}/v1.0/file-document-templates/`;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly documentTemplateFilesService: DocumentTemplateFilesService,
    private readonly fileDocumentTemplatesService: FileDocumentTemplatesService,
  ) {}

  url$ = this.route.paramMap.pipe(
    map((params): Observable<FileToken> => this.documentTemplateDownloadInfo(params)),
    switchMap((request) =>
      request.pipe(
        expand((response) => timer(response.tokenExpirationMinutes * 1000 * 60).pipe(switchMap(() => request))),
      ),
    ),
    map((fileToken) => {
      return `${this.fileDownloadDocumentTemplatePath}${encodeURIComponent(String(fileToken.token))}`;
    }),
  );

  ngAfterViewChecked(): void {
    if (this.anchor.nativeElement.href.includes(this.fileDownloadDocumentTemplatePath) && !this.hasDownloadedOnce) {
      this.anchor.nativeElement.click();
      this.hasDownloadedOnce = true;
      onfocus = () => close();
    }
  }

  private documentTemplateDownloadInfo(params: ParamMap): Observable<FileToken> {
    return this.documentTemplateFilesService.generateGetDocumentTemplateFileToken(
      Number(params.get('templateId')),
      params.get('uuid'),
    );
  }
}
