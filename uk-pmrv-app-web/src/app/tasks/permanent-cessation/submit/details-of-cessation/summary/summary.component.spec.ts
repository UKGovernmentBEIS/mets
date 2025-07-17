import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { PermanentCessationService } from '@tasks/permanent-cessation/shared';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockPermanentCessationData } from '../../testing/mock-permanent-cessation-payload';
import { permanentCessationMockPostBuild, permanentCessationMockStateBuild } from '../../testing/mock-state';
import { PermanentCessationDetailsSummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: PermanentCessationDetailsSummaryComponent;
  let fixture: ComponentFixture<PermanentCessationDetailsSummaryComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1 });
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<PermanentCessationDetailsSummaryComponent> {
    get summaryTemplate() {
      return this.query('app-permanent-cessation-details-summary-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermanentCessationDetailsSummaryComponent],
      providers: [
        PermanentCessationService,
        ItemNamePipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      permanentCessationMockStateBuild({
        permanentCessation: mockPermanentCessationData,
        permanentCessationSectionsCompleted: {},
        permanentCessationAttachments: { 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5': 'test.txt' },
      }),
    );

    fixture = TestBed.createComponent(PermanentCessationDetailsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
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
      permanentCessationMockPostBuild(
        {
          ...store.getState().requestTaskItem.requestTask.payload,
          payloadType: 'PERMANENT_CESSATION_SAVE_APPLICATION_PAYLOAD',
        },
        { details: true },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute });
  });
});
