import { AfterViewChecked, ChangeDetectionStrategy, Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { expand, map, Observable, switchMap, timer } from 'rxjs';

import { FileToken, RegulatorUsersService, UsersService } from 'pmrv-api';

@Component({
  selector: 'app-signature-file-download',
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p class="govuk-body">You should see your downloads in the downloads folder.</p>
    <a govukLink [href]="url$ | async" download #anchor>Click to restart download if it fails</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SignatureFileDownloadComponent implements AfterViewChecked {
  @ViewChild('anchor') readonly anchor: ElementRef<HTMLAnchorElement>;

  private hasDownloadedOnce = false;
  private userSignaturePath = `${this.usersService.configuration.basePath}/v1.0/user-signatures/`;

  url$ = this.route.paramMap.pipe(
    map((params): Observable<FileToken> => {
      return params.has('userId')
        ? this.regulatorSignatureDownloadInfo(params)
        : this.currentUserSignatureDownloadInfo(params);
    }),
    switchMap((request) =>
      request.pipe(
        expand((response) => timer(response.tokenExpirationMinutes * 1000 * 60).pipe(switchMap(() => request))),
      ),
    ),
    map((fileToken) => {
      return `${this.userSignaturePath}${encodeURIComponent(String(fileToken.token))}`;
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly regulatorUsersService: RegulatorUsersService,
    private readonly usersService: UsersService,
  ) {}

  ngAfterViewChecked(): void {
    if (this.anchor.nativeElement.href.includes(this.userSignaturePath) && !this.hasDownloadedOnce) {
      this.anchor.nativeElement.click();
      this.hasDownloadedOnce = true;
      onfocus = () => close();
    }
  }

  private currentUserSignatureDownloadInfo(params: ParamMap): Observable<FileToken> {
    return this.usersService.generateGetCurrentUserSignatureToken(params.get('uuid'));
  }

  private regulatorSignatureDownloadInfo(params: ParamMap): Observable<FileToken> {
    return this.regulatorUsersService.generateGetRegulatorSignatureToken(params.get('userId'), params.get('uuid'));
  }
}
