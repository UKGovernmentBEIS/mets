import { HttpEvent } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  filter,
  first,
  map,
  Observable,
  switchMap,
  takeUntil,
  withLatestFrom,
} from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { requestTaskReassignedError } from '@shared/errors/request-task-error';
import { FileUploadService } from '@shared/file-input/file-upload.service';
import { createCommonFileAsyncValidators } from '@shared/file-input/file-validators';

import { GovukValidators } from 'govuk-components';

import { FileUuidDTO, NotePayload, RequestNotesService } from 'pmrv-api';

import { WorkflowItemAbstractComponent } from '../../workflow-item-abstract.component';

@Component({
  selector: 'app-request-note',
  templateUrl: './request-note.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class RequestNoteComponent extends WorkflowItemAbstractComponent implements OnInit {
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  noteId$ = this.route.paramMap.pipe(map((parameters) => +parameters.get('noteId')));
  notePayload$: Observable<NotePayload> = this.noteId$.pipe(
    filter((noteId) => !!noteId),
    switchMap((noteId) => this.requestNotesService.getRequestNote(noteId)),
    map((result) => result.payload),
  );

  fileDowloadUrl$: Observable<string>;

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
    protected readonly authStore: AuthStore,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    protected readonly backLinkService: BackLinkService,
    protected readonly destroy$: DestroySubject,
    private readonly fb: UntypedFormBuilder,
    private readonly requestNotesService: RequestNotesService,
    private readonly fileUploadService: FileUploadService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly pendingRequest: PendingRequestService,
  ) {
    super(authStore, router, route, backLinkService, destroy$);
  }

  ngOnInit(): void {
    this.prefixUrl$
      .pipe(withLatestFrom(this.accountId$, this.requestId$), takeUntil(this.destroy$))
      .subscribe(([prefixUrl, accountId, requestId]) =>
        accountId
          ? this.backLinkService.show(`${prefixUrl}/workflows/${requestId}`, 'notes')
          : this.backLinkService.show(`${prefixUrl}/${requestId}`, 'notes'),
      );

    this.fileDowloadUrl$ = this.noteId$.pipe(
      takeUntil(this.destroy$),
      map((noteId) => (!noteId ? `../../file-download/` : `../../../file-download/`)),
    );

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

  onSubmit() {
    if (this.form.valid) {
      const note = this.form.get('note').value;
      const files = (this.form.get('files').value?.map((file) => file.uuid) as string[]).filter((uuid) => uuid);

      combineLatest([this.requestId$, this.noteId$])
        .pipe(
          first(),
          switchMap(([requestId, noteId]) => {
            return noteId
              ? this.requestNotesService.updateRequestNote(noteId, { note, files })
              : this.requestNotesService.createRequestNote({ requestId, note, files });
          }),
          withLatestFrom(this.accountId$, this.prefixUrl$, this.requestId$),
          this.pendingRequest.trackRequest(),
        )
        //eslint-disable-next-line @typescript-eslint/no-unused-vars
        .subscribe(([response, accountId, prefixUrl, requestId]) =>
          this.router.navigate([accountId ? `${prefixUrl}/workflows/${requestId}` : `${prefixUrl}/${requestId}`], {
            fragment: 'notes',
          }),
        );
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }

  private uploadFile(file: File): Observable<HttpEvent<FileUuidDTO>> {
    const requestId = this.route.snapshot.paramMap.get('request-id');
    return this.requestNotesService
      .uploadRequestNoteFile(requestId, file, 'events', true)
      .pipe(
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }
}
