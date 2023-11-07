import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { RequestTaskPageComponent } from '@aviation/request-task/containers';
import { EMP_CHILD_ROUTES } from '@aviation/request-task/emp/corsia/emp.routes';
import { EmpRequestTaskPayloadCorsia, RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen, waitForElementToBeRemoved } from '@testing-library/dom';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/setup/setup';

import { dataGapsFormProvider } from '../data-gaps-form.provider';
import { createDataGapsFixture, FUMPayload } from '../tests/fixture';
import { setupStore } from '../tests/setup-store';
import { SecondaryDataSourcesComponent } from './secondary-data-sources.component';

describe('Secondary data sources form', () => {
  const basePath = 'http://localhost:8080/api';
  let store: RequestTaskStore;
  let harness: RouterTestingHarness;
  let httpTestingController: HttpTestingController;
  let user: UserEvent;
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
    user = userEvent.setup();
    httpTestingController = TestBed.inject(HttpTestingController);
    store = TestBed.inject(RequestTaskStore);
    setupStore(store, FUMPayload);
    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/data-gaps/secondary-data-sources', SecondaryDataSourcesComponent);
    harness.detectChanges();
  });
  it('should be in root location path', () => {
    expect(TestBed.inject(Location).path()).toEqual('/data-gaps/secondary-data-sources');
  });
  it('should render secondary data sources form', () => {
    expect(screen.getByTestId('secondary-data-sources')).toBeInTheDocument();
  });
  it('should populate form', async () => {
    const input = 'Secondary Data Sources desc';
    const textarea = document.getElementById('secondaryDataSources');

    await user.type(textarea, input);
    harness.detectChanges();

    await user.click(screen.getByText('Continue'));
    const req = httpTestingController.expectOne(basePath + '/v1.0/tasks/actions');
    expect(req.request.method).toEqual('POST');
    req.flush(createDataGapsFixture({ secondaryDataSources: input }));
    harness.detectChanges();
    await waitForElementToBeRemoved(textarea);

    expect(
      (store.getValue().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia).emissionsMonitoringPlan
        .dataGaps.secondaryDataSources,
    ).toBeTruthy();
    expect(await screen.findByTestId('secondary-data-sources-exist')).toBeInTheDocument();
  });
});
