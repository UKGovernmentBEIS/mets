import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ConformityItemDeleteComponent } from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/conformity-item-delete/conformity-item-delete.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

describe('ConformityItemDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let component: ConformityItemDeleteComponent;
  let fixture: ComponentFixture<ConformityItemDeleteComponent>;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ index: '0' });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ConformityItemDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get submitButton(): HTMLButtonElement {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConformityItemDeleteComponent, RouterTestingModule],
      providers: [
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);

    store.setState({
      requestTaskItem: {
        requestInfo: { type: 'AVIATION_AER_CORSIA' },
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            verificationReport: {
              uncorrectedNonConformities: {
                existUncorrectedNonConformities: true,
                uncorrectedNonConformities: [
                  {
                    reference: 'B1',
                    explanation: 'non-compliance 1',
                    materialEffect: true,
                  },
                  {
                    reference: 'B2',
                    explanation: 'non-compliance 2',
                    materialEffect: false,
                  },
                ],
              },
            },
            verificationSectionsCompleted: { uncorrectedNonConformities: [false] },
          },
        },
      },
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(ConformityItemDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the item name', () => {
    expect(page.header.textContent.trim()).toEqual(`Are you sure you want to delete ‘B1’?`);
  });

  it('should delete and navigate to `list` page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 19,
      requestTaskActionType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION',
      requestTaskActionPayload: {
        uncorrectedNonConformities: {
          existUncorrectedNonConformities: true,
          uncorrectedNonConformities: [
            {
              reference: 'B1',
              explanation: 'non-compliance 2',
              materialEffect: false,
            },
          ],
        },
        verificationSectionsCompleted: { uncorrectedNonConformities: [false] },
        payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute, queryParams: { change: true } });
  });
});
