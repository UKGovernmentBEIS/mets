import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { DetailsSummaryTemplateComponent } from '@shared/components/hseti';
import { mockPostBuild, mockStateBuild } from '@tasks/hseti/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { HSETIApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get summaryTemplate() {
      return this.query('app-hseti-details-summary-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsSummaryTemplateComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild({
        hseti: {
          hsetiFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
          files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
        },
        hsetiSectionsCompleted: { details: false },
        hsetiAttachments: { 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'test.txt' },
      } as HSETIApplicationSubmitRequestTaskPayload),
    );
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Check your answers');
    expect(page.summaryTemplate).toBeTruthy();
    expect(page.submitButton).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild({
        hseti: {
          hsetiFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
          files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
        },
        hsetiSectionsCompleted: { details: true },
      } as HSETIApplicationSubmitRequestTaskPayload),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute });
  });
});
