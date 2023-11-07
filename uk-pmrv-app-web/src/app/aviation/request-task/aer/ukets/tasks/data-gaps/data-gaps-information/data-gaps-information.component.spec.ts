import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { AerStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';
import produce from 'immer';

import { DataGapsFormProvider } from '../data-gaps-form.provider';
import DataGapsInformationComponent from './data-gaps-information.component';

describe('DataGapsInformationComponent', () => {
  let store: RequestTaskStore;
  let fixture: ComponentFixture<DataGapsInformationComponent>;
  let user: UserEvent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataGapsInformationComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: DataGapsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              pathFromRoot: [
                {
                  url: [
                    {
                      path: 'add-data-gap-information',
                    },
                  ],
                },
              ],
              url: [
                {
                  path: 'add-data-gap-information',
                },
              ],
            },
          },
        },
      ],
    }).compileComponents();

    user = userEvent.setup();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
            payload: AerStoreDelegate.INITIAL_STATE as AerRequestTaskPayload,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(DataGapsInformationComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display correct page heading', () => {
    expect(screen.getByText('Add data gap information')).toBeInTheDocument();
  });

  it('should show form errors', async () => {
    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Enter a reason for the data gap/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the type of the data gap/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the replacement method used for determining surrogate data/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the number of flights affected by the data gap/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the total emissions affected by the data gap/)).toHaveLength(2);
  });
});
