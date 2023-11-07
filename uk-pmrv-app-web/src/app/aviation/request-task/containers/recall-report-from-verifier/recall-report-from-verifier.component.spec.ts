import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import RecallReportFromVerifierComponent from './recall-report-from-verifier.component';

class Page extends BasePage<RecallReportFromVerifierComponent> {
  get heading() {
    return this.query<HTMLHeadingElement>('h1');
  }
  get submitButton() {
    return this.queryAll<HTMLButtonElement>('button')[0];
  }
}

describe('RecallReportFromVerifierComponent', () => {
  let fixture: ComponentFixture<RecallReportFromVerifierComponent>;
  let page: Page;

  const tasksService = mockClass(TasksService);
  const activatedRouteStub = new ActivatedRouteStub({ taskId: '237', index: '0' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecallReportFromVerifierComponent, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RecallReportFromVerifierComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should submit', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Are you sure you want to recall the report from the verifier?');

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AVIATION_AER_RECALL_FROM_VERIFICATION',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
