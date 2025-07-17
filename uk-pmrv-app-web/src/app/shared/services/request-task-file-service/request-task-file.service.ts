import { HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl } from '@angular/forms';

import { Observable } from 'rxjs';

import { createCommonFileAsyncValidators } from '@shared/file-input/file-validators';

import { FileUuidDTO, RequestTaskAttachmentActionProcessDTO, RequestTaskAttachmentsHandlingService } from 'pmrv-api';

import { BusinessErrorService } from '../../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { requestTaskReassignedError } from '../../errors/request-task-error';
import { FileUploadService } from '../../file-input/file-upload.service';
import { FileUploadEvent } from '../../file-input/file-upload-event';

@Injectable({ providedIn: 'root' })
export class RequestTaskFileService {
  readonly upload = (
    requestTaskId: number,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
  ) => this.fileUploadService.upload((file) => this.storeUpload(requestTaskId, file, requestTaskActionType));

  readonly uploadMany = (
    requestTaskId: number,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
  ) => this.fileUploadService.uploadMany((file) => this.storeUpload(requestTaskId, file, requestTaskActionType));

  constructor(
    private readonly fileUploadService: FileUploadService,
    private readonly requestTaskAttachmentsHandlingService: RequestTaskAttachmentsHandlingService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly formBuilder: UntypedFormBuilder,
  ) {}

  buildFormControl(
    requestTaskId: number,
    uuid: string | string[],
    attachments: { [key: string]: string },
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
    required = false,
    disabled = false,
  ): UntypedFormControl {
    return this.formBuilder.control(
      {
        value: !uuid
          ? null
          : Array.isArray(uuid)
            ? (uuid || []).map((id) => this.buildFileEvent(id, attachments))
            : this.buildFileEvent(uuid, attachments),
        disabled,
      },
      {
        asyncValidators: [
          ...createCommonFileAsyncValidators(required),
          Array.isArray(uuid)
            ? this.uploadMany(requestTaskId, requestTaskActionType)
            : this.upload(requestTaskId, requestTaskActionType),
        ],
        updateOn: 'change',
      },
    );
  }

  private buildFileEvent(uuid: string, attachments: { [key: string]: string }): Pick<FileUploadEvent, 'uuid' | 'file'> {
    //TODO This should be refactored and replace the store with what is absolutely  necessary.
    // currently an assumption is mde that either the state will have
    // the property key property or the store will have a getter for the specific property
    // check common-tasks.store.ts
    return {
      uuid,
      file: { name: attachments?.[uuid] } as File,
    };
  }

  private storeUpload(
    requestTaskId: number,
    file: File,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
  ): Observable<HttpEvent<FileUuidDTO>> {
    return this.requestTaskAttachmentsHandlingService
      .uploadRequestTaskAttachment(
        {
          requestTaskActionType,
          requestTaskId,
        },
        file,
        'events',
        true,
      )
      .pipe(
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }
}
