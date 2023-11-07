import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { RequestTaskPageComponent } from '@aviation/request-task/containers';
import { EMP_CHILD_ROUTES } from '@aviation/request-task/emp/corsia/emp.routes';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/dom';

import { dataGapsFormProvider } from '../data-gaps-form.provider';
import { dataGapPayload, summaryPayload } from '../tests/fixture';
import { setupStore } from '../tests/setup-store';
import { DataGapsSummaryComponent } from './data-gaps-summary.component';

describe('Data Gaps Form', () => {
  let store: RequestTaskStore;
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [RequestTaskPageComponent],
      imports: [ReactiveFormsModule],
      providers: [
        provideRouter(EMP_CHILD_ROUTES),
        provideHttpClient(),
        provideHttpClientTesting(),
        dataGapsFormProvider,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });
    await TestBed.compileComponents();
    store = TestBed.inject(RequestTaskStore);
    setupStore(store, summaryPayload);
    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/data-gaps', DataGapsSummaryComponent);
    harness.detectChanges();
  });
  it('should be in root location path', () => {
    expect(TestBed.inject(Location).path()).toEqual('/data-gaps/summary');
  });
  it('should render data gaps summary form', () => {
    harness.detectChanges();
    expect(screen.getByTestId('data-gaps-summary')).toBeInTheDocument();
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    for (const key of Object.keys(dataGapPayload)) {
      let textValue = dataGapPayload[key];
      if (typeof textValue === 'boolean') {
        textValue = textValue ? 'Yes' : 'No';
      }
      expect(screen.getByText(textValue)).toBeInTheDocument();
    }
  });
});
