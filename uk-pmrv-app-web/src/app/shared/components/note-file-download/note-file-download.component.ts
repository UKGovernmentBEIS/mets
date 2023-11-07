import { AfterViewChecked, ChangeDetectionStrategy, Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { expand, map, Observable, switchMap, timer } from 'rxjs';

import { AccountNotesService, FileNotesService, FileToken, RequestNotesService } from 'pmrv-api';

@Component({
  selector: 'app-note-file-download',
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p class="govuk-body">You should see your downloads in the downloads folder.</p>
    <a govukLink [href]="url$ | async" download #anchor>Click to restart download if it fails</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NoteFileDownloadComponent implements AfterViewChecked {
  @ViewChild('anchor') readonly anchor: ElementRef<HTMLAnchorElement>;

  private hasDownloadedOnce = false;
  private fileNotesPath = `${this.fileNotesService.configuration.basePath}/v1.0/file-notes/`;

  url$ = this.route.paramMap.pipe(
    map((params): Observable<FileToken> => {
      return params.has('request-id') ? this.requestNoteDownloadInfo(params) : this.accountNoteDownloadInfo(params);
    }),
    switchMap((request) =>
      request.pipe(
        expand((response) => timer(response.tokenExpirationMinutes * 1000 * 60).pipe(switchMap(() => request))),
      ),
    ),
    map((fileToken) => {
      return `${this.fileNotesPath}${encodeURIComponent(String(fileToken.token))}`;
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly accountNotesService: AccountNotesService,
    private readonly requestNotesService: RequestNotesService,
    private readonly fileNotesService: FileNotesService,
  ) {}

  ngAfterViewChecked(): void {
    if (this.anchor.nativeElement.href.includes(this.fileNotesPath) && !this.hasDownloadedOnce) {
      this.anchor.nativeElement.click();
      this.hasDownloadedOnce = true;
      onfocus = () => close();
    }
  }

  private requestNoteDownloadInfo(params: ParamMap): Observable<FileToken> {
    return this.requestNotesService.generateGetRequestFileNoteToken(params.get('request-id'), params.get('uuid'));
  }

  private accountNoteDownloadInfo(params: ParamMap): Observable<FileToken> {
    return this.accountNotesService.generateGetAccountFileNoteToken(
      Number(params.get('accountId')),
      params.get('uuid'),
    );
  }
}
