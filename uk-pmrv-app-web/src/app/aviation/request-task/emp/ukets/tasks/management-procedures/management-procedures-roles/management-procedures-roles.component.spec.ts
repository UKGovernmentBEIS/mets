import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';
import { screen } from '@testing-library/dom';

import { TasksService } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresRolesComponent } from './management-procedures-roles.component';

describe('ManagementProceduresRolesComponent', () => {
  let component: ManagementProceduresRolesComponent;
  let fixture: ComponentFixture<ManagementProceduresRolesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule, SharedModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ManagementProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    TestBed.inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER).addSmallEmittersForms();
    fixture = TestBed.createComponent(ManagementProceduresRolesComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct description`, () => {
    expect(
      screen.getByText(
        'Describe the main roles that are responsible for monitoring and reporting on emissions, and the main duties that the holders of these roles perform.',
      ),
    ).toBeInTheDocument();
  });

  it('should have only one button to add another role, and no other buttons', () => {
    expect(screen.queryAllByRole('button', { name: /Add another/ })).toHaveLength(1);
    expect(screen.queryAllByRole('button')).toHaveLength(1);
  });
});
