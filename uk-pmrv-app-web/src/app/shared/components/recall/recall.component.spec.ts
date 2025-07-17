import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockBdrState } from '../../../tasks/bdr/submit/testing/mock-bdr-payload';
import { RecallSharedComponent } from './recall.component';

describe('RecallComponent', () => {
  let component: RecallSharedComponent;
  let fixture: ComponentFixture<RecallSharedComponent>;
  let store: CommonTasksStore;
  let page: Page;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<RecallSharedComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1');
    }

    get confirmationComponent() {
      return this.query<HTMLElement>('app-confirmation-shared');
    }

    get returnToText() {
      return this.query<HTMLElement>('a').textContent.trim();
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecallSharedComponent],
      providers: [provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockBdrState, requestTaskItem: { requestTask: { id: 1, type: 'BDR_WAIT_FOR_VERIFICATION' } } });

    fixture = TestBed.createComponent(RecallSharedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should recall BDR', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.heading).toBeTruthy();
    expect(page.confirmationComponent).toBeFalsy();
    expect(page.returnToText).toEqual('Return to: Baseline data report');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'BDR_RECALL_FROM_VERIFICATION',
      requestTaskId: store.getState().requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });

    expect(page.confirmationComponent).toBeTruthy();
    expect(page.heading).toBeFalsy();
  });
});
