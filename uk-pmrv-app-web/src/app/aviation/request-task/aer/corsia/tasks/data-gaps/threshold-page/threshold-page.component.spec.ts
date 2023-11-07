import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import produce from 'immer';

import { AviationAerCorsiaDataGaps, TasksService } from 'pmrv-api';

import { DataGapsFormProvider } from '../data-gaps-form.provider';
import ThresholdPageComponent from './threshold-page.component';

describe('ThresholdPageComponent', () => {
  let store: RequestTaskStore;
  let fixture: ComponentFixture<ThresholdPageComponent>;
  let formProvider: DataGapsFormProvider;
  const tasksService = mockClass(TasksService);

  const dataGaps: AviationAerCorsiaDataGaps = {
    exist: true,
    dataGapsDetails: {
      dataGapsPercentageType: 'MORE_THAN_FIVE_PER_CENT',
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThresholdPageComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: DataGapsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT',
            payload: {
              aer: {
                ...AerCorsiaStoreDelegate.INITIAL_STATE,
                dataGaps,
              },
            } as any,
          },
        };
      }),
    );
    formProvider = TestBed.inject<DataGapsFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(dataGaps as AviationAerCorsiaDataGaps);
    formProvider.form.updateValueAndValidity();

    fixture = TestBed.createComponent(ThresholdPageComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(
      screen.getByText('What percentage of these flights had data gaps during the scheme year?'),
    ).toBeInTheDocument();
  });

  it('should call the saveAer function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const saveAerSpy = jest.spyOn(store.aerDelegate, 'saveAer').mockReturnValue(of({}));
    fixture.componentInstance.onSubmit();

    expect(saveAerSpy).toHaveBeenCalledTimes(1);
    expect(saveAerSpy).toHaveBeenCalledWith({ dataGaps: dataGaps }, 'in progress');
  });
});
