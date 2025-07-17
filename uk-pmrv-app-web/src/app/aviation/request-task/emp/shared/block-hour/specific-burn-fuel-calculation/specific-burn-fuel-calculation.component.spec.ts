import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { BlockHourProceduresFormProvider } from '../block-hour-procedures-form.provider';
import { SpecificBurnFuelCalculationComponent } from './specific-burn-fuel-calculation.component';

describe('SpecificBurnFuelCalculationComponent', () => {
  let component: SpecificBurnFuelCalculationComponent;
  let fixture: ComponentFixture<SpecificBurnFuelCalculationComponent>;
  let store: RequestTaskStore;

  const activatedRouteStub = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: BlockHourProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestInfo: { type: 'EMP_ISSUANCE_CORSIA' },
      },
    } as any);

    fixture = TestBed.createComponent(SpecificBurnFuelCalculationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
