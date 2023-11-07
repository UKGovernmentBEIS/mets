import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AviationRecallFromAmendsComponent } from './recall-from-amends.component';

describe('RecallFromAmendsComponent', () => {
  let component: AviationRecallFromAmendsComponent;
  let fixture: ComponentFixture<AviationRecallFromAmendsComponent>;
  let page: Page;
  let store: RequestTaskStore;

  const tasksService = mockClass(TasksService);
  const activatedRouteStub = new ActivatedRouteStub({ taskId: '237', index: '0' });

  class Page extends BasePage<AviationRecallFromAmendsComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AviationRecallFromAmendsComponent, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        requestTask: {
          type: 'EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS',
        },
      },
    });

    fixture = TestBed.createComponent(AviationRecallFromAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display context', () => {
    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Are you sure you want to recall the application?');
  });

  it('should submit emp application', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'EMP_ISSUANCE_UKETS_RECALL_FROM_AMENDS',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });

  it('should submit emp corsia application', () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        requestTask: {
          type: 'EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS',
        },
      },
    });

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'EMP_ISSUANCE_CORSIA_RECALL_FROM_AMENDS',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });

  it('should submit emp variation', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        requestTask: {
          type: 'EMP_VARIATION_UKETS_WAIT_FOR_AMENDS',
        },
      },
    });

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'EMP_VARIATION_UKETS_RECALL_FROM_AMENDS',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
