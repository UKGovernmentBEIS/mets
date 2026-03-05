import { HttpEvent } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, filter, first, map, Observable, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { requestTaskReassignedError } from '@shared/errors/request-task-error';
import { FileUploadService } from '@shared/file-input/file-upload.service';
import { createCommonFileAsyncValidators } from '@shared/file-input/file-validators';

import { GovukValidators } from 'govuk-components';

import { AccountNotesService, FileUuidDTO, NotePayload } from 'pmrv-api';

@Component({
  selector: 'app-account-note',
  templateUrl: './account-note.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountNoteComponent implements OnInit {
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  accountId$ = this.route.paramMap.pipe(map((parameters) => +parameters.get('accountId')));
  noteId$ = this.route.paramMap.pipe(map((parameters) => +parameters.get('noteId')));
  notePayload$: Observable<NotePayload> = this.noteId$.pipe(
    filter((noteId) => !!noteId),
    switchMap((noteId) => this.accountNotesService.getAccountNote(noteId)),
    map((result) => result.payload),
  );
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  domain: string;

  form = this.fb.group({
    note: [
      '',
      [GovukValidators.required('Enter a note'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
    ],
    files: this.fb.control(
      {
        value: [],
        disabled: false,
      },
      {
        asyncValidators: [
          ...createCommonFileAsyncValidators(false),
          this.fileUploadService.uploadMany((file) => this.uploadFile(file)),
        ],
        updateOn: 'change',
      },
    ),
  });

  constructor(
    readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly fb: UntypedFormBuilder,
    private readonly accountNotesService: AccountNotesService,
    private readonly fileUploadService: FileUploadService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly backlinkService: BackLinkService,
    readonly pendingRequest: PendingRequestService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.currentDomain$.pipe(takeUntil(this.destroy$)).subscribe((domain) => {
      this.domain = domain === 'AVIATION' ? '/' + domain.toLowerCase() + '/' : '';
    });
    this.backlinkService.show();

    this.notePayload$.pipe(first()).subscribe((payload) => {
      this.form.patchValue({
        note: payload?.note,
        files: this.transformFiles(payload?.files),
      });
    });
  }

  transformFiles(transformedFiles: NotePayload['files']) {
    return transformedFiles
      ? Object.entries(transformedFiles).map((keyValue) => ({
          uuid: keyValue[0],
          file: {
            name: keyValue[1],
          },
        }))
      : [];
  }

  getDownloadUrl() {
    const accountId = this.route.snapshot.paramMap.get('accountId');
    return `${this.domain}/accounts/${accountId}/file-download/`;
  }

  onSubmit() {
    if (this.form.valid) {
      const note = this.form.get('note').value;
      const fileUuids = (this.form.get('files').value?.map((file) => file.uuid) as string[]).filter((uuid) => uuid);

      combineLatest([this.accountId$, this.noteId$])
        .pipe(
          first(),
          switchMap(([accountId, noteId]) => {
            return noteId
              ? this.accountNotesService.updateAccountNote(noteId, { note, files: fileUuids })
              : this.accountNotesService.createAccountNote({ accountId, note, files: fileUuids });
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() =>
          this.router.navigate([`${this.domain}accounts/${this.route.snapshot.paramMap.get('accountId')}`], {
            fragment: 'notes',
          }),
        );
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }

  private uploadFile(file: File): Observable<HttpEvent<FileUuidDTO>> {
    return this.accountNotesService
      .uploadAccountNoteFile(+this.route.snapshot.paramMap.get('accountId'), file, 'events', true)
      .pipe(
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }
}
