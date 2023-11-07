import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import {
  EmpRequestTaskPayloadCorsia,
  EmpRequestTaskPayloadUkEts,
  RequestTaskStore,
} from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub } from '@testing';
import produce from 'immer';

import { OverallDecisionFormProvider } from '../overall-decision-form.provider';
import { OverallDecisionSummaryComponent } from './overall-decision-summary.component';

describe('OverallDecisionSummaryComponent', () => {
  let component: OverallDecisionSummaryComponent;
  let fixture: ComponentFixture<OverallDecisionSummaryComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverallDecisionSummaryComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: OverallDecisionFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    const state = store.getState();

    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestInfo: {
          type: 'EMP_ISSUANCE_UKETS',
        },
        requestTask: {
          payload: {
            ...EmpUkEtsStoreDelegate.INITIAL_STATE,
            reviewSectionsCompleted: {},
          } as EmpRequestTaskPayloadUkEts,
        },
      },
    });

    fixture = TestBed.createComponent(OverallDecisionSummaryComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call saveEmpOverallDecision method with the correct data and navigate to review task list page (UKETS)', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveEmpOverallDecision = jest.spyOn(store.empDelegate, 'saveEmpOverallDecision').mockReturnValue(of({}));

    component.form.setValue({ type: 'APPROVED', reason: 'Reason' });
    component.onSubmit();

    expect(saveEmpOverallDecision).toHaveBeenCalledTimes(1);
    expect(saveEmpOverallDecision).toHaveBeenCalledWith(component.form.value, true);
    expect(navigateSpy).toHaveBeenCalledWith(['../../../../'], { relativeTo: activatedRouteStub });
  });

  it('should call saveEmpOverallDecision method with the correct data and navigate to review task list page (CORSIA)', () => {
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          ...state.requestTaskItem,
          requestInfo: {
            type: 'EMP_ISSUANCE_CORSIA',
          },
          requestTask: {
            payload: {
              ...EmpCorsiaStoreDelegate.INITIAL_STATE,
              reviewSectionsCompleted: {},
            } as EmpRequestTaskPayloadCorsia,
          },
        };
      }),
    );

    fixture.detectChanges();

    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveEmpOverallDecision = jest.spyOn(store.empDelegate, 'saveEmpOverallDecision').mockReturnValue(of({}));

    component.form.setValue({ type: 'APPROVED', reason: 'Reason' });
    component.onSubmit();

    expect(saveEmpOverallDecision).toHaveBeenCalledTimes(1);
    expect(saveEmpOverallDecision).toHaveBeenCalledWith(component.form.value, true);
    expect(navigateSpy).toHaveBeenCalledWith(['../../../../'], { relativeTo: activatedRouteStub });
  });
});
