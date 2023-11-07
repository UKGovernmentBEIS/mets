import { ReactiveFormsModule } from '@angular/forms';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { additionalDocumentsFormProvider } from '../additional-documents-form.provider';
import { AdditionalDocumentsSummaryComponent } from './additional-documents-summary.component';

describe('AdditionalDocumentsSummaryComponent', () => {
  let result: RenderResult<AdditionalDocumentsSummaryComponent>;

  beforeEach(async () => {
    result = await render(AdditionalDocumentsSummaryComponent, {
      imports: [ReactiveFormsModule],
      providers: [additionalDocumentsFormProvider, { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    });
  });

  it('should create', () => {
    const {
      fixture: { componentInstance: component },
    } = result;
    expect(component).toBeTruthy();
  });
});
