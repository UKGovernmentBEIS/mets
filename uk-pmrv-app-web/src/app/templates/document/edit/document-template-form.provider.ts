import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { DocumentTemplateDTO, DocumentTemplateFilesService } from 'pmrv-api';

import { FileType } from '../../../shared/file-input/file-type.enum';
import { FileUploadService } from '../../../shared/file-input/file-upload.service';
import { FileUploadEvent } from '../../../shared/file-input/file-upload-event';
import {
  commonFileValidators,
  FileValidators,
  requiredFileValidator,
} from '../../../shared/file-input/file-validators';

export const DOCUMENT_TEMPLATE_FORM = new InjectionToken<UntypedFormGroup>('Document Template Form');

export const DocumentTemplateFormProvider = {
  provide: DOCUMENT_TEMPLATE_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, FileUploadService, DocumentTemplateFilesService],
  useFactory: (fb: UntypedFormBuilder, route: ActivatedRoute) => {
    const documentTemplate = route.snapshot.data.documentTemplate as DocumentTemplateDTO;

    return fb.group({
      documentFile: new UntypedFormControl(
        documentTemplate.fileUuid
          ? ({
              uuid: documentTemplate.fileUuid,
              file: { name: documentTemplate.filename } as File,
            } as Pick<FileUploadEvent, 'file' | 'uuid'>)
          : null,
        {
          validators: commonFileValidators.concat(
            requiredFileValidator,
            FileValidators.validContentTypes([FileType.DOCX]),
          ),
          updateOn: 'change',
        },
      ),
    });
  },
};
