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
import { certPayloadPopulated, createDataGapsFixture } from '../tests/fixture';
import { setupStore } from '../tests/setup-store';
import { SecondaryDataSourcesExistComponent } from './secondary-data-sources-exist.component';

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
    setupStore(store, certPayloadPopulated);
    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/data-gaps/secondary-data-sources-exist', SecondaryDataSourcesExistComponent);
    harness.detectChanges();
  });
  it('should be in root location path', () => {
    expect(TestBed.inject(Location).path()).toEqual('/data-gaps/secondary-data-sources-exist');
  });
  it('should render secondary data sources form', () => {
    expect(screen.getByTestId('secondary-data-sources-exist')).toBeInTheDocument();
  });
  it('should populate form', async () => {
    const input = 'Secondary Data Sources description';
    await user.click(document.getElementById('secondarySourcesDataGapsExist-option0'));
    const textarea = document.getElementById('secondarySourcesDataGapsConditions');
    await user.type(textarea, input);
    harness.detectChanges();
    await user.click(screen.getByText('Continue'));
    const req = httpTestingController.expectOne(basePath + '/v1.0/tasks/actions');
    expect(req.request.method).toEqual('POST');
    req.flush(
      createDataGapsFixture({
        secondarySourcesDataGapsExist: true,
        secondarySourcesDataGapsConditions: input,
      }),
    );
    harness.detectChanges();
    await waitForElementToBeRemoved(textarea);

    expect(
      (store.getValue().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia).emissionsMonitoringPlan
        .dataGaps.secondarySourcesDataGapsExist,
    ).toBeTruthy();

    expect(
      (store.getValue().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia).emissionsMonitoringPlan
        .dataGaps.secondarySourcesDataGapsConditions,
    ).toBeTruthy();

    harness.detectChanges();
    expect(await screen.findByTestId('data-gaps-summary')).toBeInTheDocument();
  });
});
