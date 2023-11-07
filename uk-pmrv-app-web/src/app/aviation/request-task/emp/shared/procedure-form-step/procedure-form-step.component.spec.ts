import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { TasksService } from 'pmrv-api';

import { ProcedureFormBuilder } from './procedure-form.builder';
import { ProcedureFormStepComponent } from './procedure-form-step.component';

@Component({
  selector: 'app-mock-parent',
  template: `
    <app-procedure-form-step [form]="form">
      <h3 procedureFormPageHeader>Page heading</h3>
    </app-procedure-form-step>
  `,
  standalone: true,
  imports: [ProcedureFormStepComponent],
})
class MockParentComponent {
  form = ProcedureFormBuilder.createProcedureForm();
}

describe('ProcedureFormStepComponent', () => {
  let fixture: ComponentFixture<MockParentComponent>;
  const user = userEvent.setup();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockParentComponent],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    const store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT' } });

    fixture = TestBed.createComponent(MockParentComponent);
    fixture.detectChanges();
  });

  it('should display provided header', async () => {
    expect(screen.getByRole('heading', { name: /Page heading/ })).toBeVisible();
  });

  it('should display return link', async () => {
    expect(screen.getByRole('link', { name: /Return to/ })).toBeVisible();
  });

  it('should display form errors', async () => {
    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Enter a description for the procedure/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the name of the procedure document/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter a procedure reference/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the name of the department or role responsible/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter the physical location of the records/)).toHaveLength(2);
  });
});
