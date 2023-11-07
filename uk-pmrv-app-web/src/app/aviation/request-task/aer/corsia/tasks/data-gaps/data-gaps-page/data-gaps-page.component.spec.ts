import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/setup/setup';
import produce from 'immer';

import { DataGapsFormProvider } from '../data-gaps-form.provider';
import DataGapsPageComponent from './data-gaps-page.component';

describe('DataGapsPageComponent', () => {
  let store: RequestTaskStore;
  let fixture: ComponentFixture<DataGapsPageComponent>;
  let user: UserEvent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataGapsPageComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: DataGapsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    user = userEvent.setup();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT',
            payload: AerCorsiaStoreDelegate.INITIAL_STATE as AerRequestTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(DataGapsPageComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(
      screen.getByText('Did any flights with offsetting obligations have data gaps during the scheme year?'),
    ).toBeInTheDocument();
  });

  it('should show error when yes or no has not been selected', async () => {
    await user.click(screen.getByRole('button', { name: /Continue/ }));

    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(
      screen.getAllByText(
        /Select if there have been any data gaps for flights with offsetting obligations during the scheme year/,
      ),
    ).toHaveLength(2);
  });
});
