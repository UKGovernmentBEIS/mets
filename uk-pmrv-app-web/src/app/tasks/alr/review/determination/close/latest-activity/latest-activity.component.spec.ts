import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { alrMockReviewState, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { AlrLatestActivityComponent } from './latest-activity.component';

describe('LatestActivityComponent', () => {
  let component: AlrLatestActivityComponent;
  let fixture: ComponentFixture<AlrLatestActivityComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRoute = new ActivatedRouteStub({
    taskId: 1,
  });

  class Page extends BasePage<AlrLatestActivityComponent> {
    get summaryTemplate() {
      return this.query('app-alr-activity-summary-template');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrLatestActivityComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          ...(
            alrMockReviewState.requestTaskItem.requestTask
              .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
          )?.regulatorReviewOutcome,
          determination: {
            type: 'CLOSED_ALR',
            alrFile: '61b69a4b-51d1-44a3-a7f5-d895631fb2c4',
            files: ['8ab820c9-22eb-4f7a-b4ec-41cc7dba6d4a', '290660ba-4a86-40a7-b961-c3d06798bdf2'],
          },
        },
        regulatorReviewSectionsCompleted: {},
        regulatorReviewAttachments: {
          '61b69a4b-51d1-44a3-a7f5-d895631fb2c4': 'TestFile 1.txt',
          '8ab820c9-22eb-4f7a-b4ec-41cc7dba6d4a': 'TestFile 2.txt',
          '290660ba-4a86-40a7-b961-c3d06798bdf2': 'TestFile 3.txt',
        },
      }),
    );

    fixture = TestBed.createComponent(AlrLatestActivityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements and redirect to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.summaryTemplate).toBeTruthy();
    expect(page.submitButton).toBeTruthy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: activatedRoute });
  });
});
