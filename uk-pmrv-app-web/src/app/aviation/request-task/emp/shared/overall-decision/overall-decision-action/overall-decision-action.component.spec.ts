import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub } from '@testing';

import { OverallDecisionFormProvider } from '../overall-decision-form.provider';
import { OverallDecisionActionComponent } from './overall-decision-action.component';

describe('OverallDecisionComponent', () => {
  let component: OverallDecisionActionComponent;
  let fixture: ComponentFixture<OverallDecisionActionComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverallDecisionActionComponent, RouterTestingModule],
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
        requestTask: {
          payload: {
            ...EmpUkEtsStoreDelegate.INITIAL_STATE,
            reviewSectionsCompleted: {},
          } as EmpRequestTaskPayloadUkEts,
        },
        requestInfo: {
          type: 'EMP_ISSUANCE_UKETS',
        },
      },
    });

    fixture = TestBed.createComponent(OverallDecisionActionComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should user click approve button, call saveEmpOverallDecision method and navigate to reason page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveEmpOverallDecision = jest.spyOn(store.empDelegate, 'saveEmpOverallDecision').mockReturnValue(of({}));

    component.onContinue('APPROVED');

    const { type } = component.form.value;

    expect(type).toEqual('APPROVED');
    expect(saveEmpOverallDecision).toHaveBeenCalledTimes(1);
    expect(saveEmpOverallDecision).toHaveBeenCalledWith(component.form.value, false);
    expect(navigateSpy).toHaveBeenCalledWith(['reason'], { relativeTo: activatedRouteStub });
  });
});
