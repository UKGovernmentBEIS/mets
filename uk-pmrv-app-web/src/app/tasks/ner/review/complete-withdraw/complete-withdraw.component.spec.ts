import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { nerReviewMockState } from '@tasks/ner/test';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { NerCompleteWithdrawComponent } from './complete-withdraw.component';

describe('CompleteWithdrawComponent', () => {
  let component: NerCompleteWithdrawComponent;
  let fixture: ComponentFixture<NerCompleteWithdrawComponent>;
  let store: CommonTasksStore;
  let page: Page;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<NerCompleteWithdrawComponent> {
    get confirmationTemplate() {
      return this.query('app-confirmation-shared');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerCompleteWithdrawComponent],
      providers: [CapitalizeFirstPipe, provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(nerReviewMockState);

    fixture = TestBed.createComponent(NerCompleteWithdrawComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 1,
      requestTaskActionType: 'NER_WITHDRAW_APPLICATION',
      requestTaskActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
    });

    expect(page.confirmationTemplate).toBeTruthy();
  });
});
