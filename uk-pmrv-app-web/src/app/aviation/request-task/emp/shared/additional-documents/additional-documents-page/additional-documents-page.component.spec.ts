import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { render, RenderResult } from '@testing-library/angular';

import { AdditionalDocumentsFormProvider } from '../additional-documents-form.provider';
import { AdditionalDocumentsPageComponent } from './additional-documents-page.component';

describe('AdditionalDocumentsPageComponent', () => {
  let result: RenderResult<AdditionalDocumentsPageComponent>;

  beforeEach(async () => {
    result = await render(AdditionalDocumentsPageComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AdditionalDocumentsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });
  });

  it('should create', () => {
    expect(result.fixture.componentInstance).toBeTruthy();
  });
});
