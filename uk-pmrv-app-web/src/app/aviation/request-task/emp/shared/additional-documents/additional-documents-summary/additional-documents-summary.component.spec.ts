import { ReactiveFormsModule } from '@angular/forms';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { AdditionalDocumentsFormProvider } from '../additional-documents-form.provider';
import { AdditionalDocumentsSummaryComponent } from './additional-documents-summary.component';

describe('AdditionalDocumentsSummaryComponent', () => {
  let result: RenderResult<AdditionalDocumentsSummaryComponent>;

  beforeEach(async () => {
    result = await render(AdditionalDocumentsSummaryComponent, {
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AdditionalDocumentsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });
  });

  it('should create', () => {
    const {
      fixture: { componentInstance: component },
    } = result;

    expect(component).toBeTruthy();
  });
});
