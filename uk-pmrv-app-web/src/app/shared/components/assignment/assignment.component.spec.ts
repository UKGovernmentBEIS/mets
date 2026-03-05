import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AuthStore } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { BasePage, mockClass } from '@testing';

import { TasksAssignmentService, TasksReleaseService } from 'pmrv-api';

import { SharedModule } from '../../shared.module';

describe('AssignmentComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let authStore: AuthStore;

  const tasksAssignmentService = mockClass(TasksAssignmentService);
  tasksAssignmentService.assignTask.mockReturnValue(of({}));
  tasksAssignmentService.getCandidateAssigneesByTaskId.mockReturnValue(
    of([
      { id: '3954e888-40fb-4d6b-a367-6416b354ba08', firstName: 'Obi', lastName: 'Wan' },
      { id: '7b91199c-4770-4d4b-a0ed-d6d9667de157', firstName: 'Darth', lastName: 'Vader' },
    ]),
  );

  @Component({
    template: `
      <app-assignment [info]="info" (submitted)="submitted($event)"></app-assignment>
    `,
    providers: [PendingRequestService],
  })
  class TestComponent {
    info = {
      requestTask: {
        assigneeUserId: '7b91199c-4770-4d4b-a0ed-d6d9667de157',
        assignable: true,
        id: 856,
      },
      userAssignCapable: true,
    };
    mockSubmit;

    constructor(private readonly _: PendingRequestService) {}

    submitted(_: any): void {
      this.mockSubmit = _;
    }
  }

  class Page extends BasePage<TestComponent> {
    get select(): HTMLSelectElement {
      return this.query('select');
    }
    get button(): HTMLButtonElement {
      return this.query('button');
    }
    get options(): string[] {
      return Array.from(this.select.options).map((option) => option.textContent.trim());
    }

    get selectValue(): string {
      return this.select.value;
    }
    set selectValue(value: string) {
      this.setInputValue('select', value);
    }

    get errorSummary(): HTMLDivElement {
      return this.query('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: TasksReleaseService, useValue: mockClass(TasksReleaseService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
        { provide: BusinessErrorService, useValue: mockClass(BusinessErrorService) },
      ],
      declarations: [TestComponent],
    }).compileComponents();
  });

  describe('for operator', () => {
    beforeEach(async () => {
      authStore = TestBed.inject(AuthStore);
      authStore.setUserState({ roleType: 'OPERATOR' });
    });

    it('should create', () => {
      createComponent();
      expect(hostComponent).toBeTruthy();
    });

    it('should populate the select with no currently selected and not release option', () => {
      createComponent();
      expect(page.options).toEqual(['Obi Wan']);
    });

    it('should display error if no assignee selected', () => {
      createComponent();
      const submitSpy = jest.spyOn(hostComponent, 'submitted');

      page.button.click();
      fixture.detectChanges();

      expect(tasksAssignmentService.assignTask).toHaveBeenCalledTimes(0);
      expect(submitSpy).toHaveBeenCalledTimes(0);

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a person']);
    });

    it('should post assignment and emit submitted', () => {
      createComponent();
      const submitSpy = jest.spyOn(hostComponent, 'submitted');

      expect(tasksAssignmentService.assignTask).toHaveBeenCalledTimes(0);
      expect(submitSpy).toHaveBeenCalledTimes(0);

      page.selectValue = '3954e888-40fb-4d6b-a367-6416b354ba08';
      page.button.click();

      expect(tasksAssignmentService.assignTask).toHaveBeenCalledTimes(1);
      expect(tasksAssignmentService.assignTask).toHaveBeenCalledWith({
        taskId: 856,
        userId: '3954e888-40fb-4d6b-a367-6416b354ba08',
      });
      expect(submitSpy).toHaveBeenCalledTimes(1);
      expect(submitSpy).toHaveBeenCalledWith('Obi Wan');
    });
  });

  describe('for regulator', () => {
    beforeEach(async () => {
      authStore = TestBed.inject(AuthStore);
      authStore.setUserState({ roleType: 'REGULATOR' });
    });

    it('should create', () => {
      createComponent();
      expect(hostComponent).toBeTruthy();
    });

    it('should populate the select with no currently selected and with release option', () => {
      createComponent();
      expect(page.options).toEqual(['Unassigned', 'Obi Wan']);
    });

    it('should post assignment and emit submitted', () => {
      createComponent();
      const submitSpy = jest.spyOn(hostComponent, 'submitted');

      expect(tasksAssignmentService.assignTask).toHaveBeenCalledTimes(0);
      expect(submitSpy).toHaveBeenCalledTimes(0);

      page.selectValue = '3954e888-40fb-4d6b-a367-6416b354ba08';
      page.button.click();

      expect(tasksAssignmentService.assignTask).toHaveBeenCalledTimes(1);
      expect(tasksAssignmentService.assignTask).toHaveBeenCalledWith({
        taskId: 856,
        userId: '3954e888-40fb-4d6b-a367-6416b354ba08',
      });
      expect(submitSpy).toHaveBeenCalledTimes(1);
      expect(submitSpy).toHaveBeenCalledWith('Obi Wan');
    });
  });
});
