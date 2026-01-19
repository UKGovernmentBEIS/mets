import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockWasteQdrPostBuild, mockWasteQdrSubmitStateBuild } from '@tasks/waste-qdr/test';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { WasteQdrChangesRequestedComponent } from './changes-requested.component';

describe('ChangesRequestedComponent', () => {
  let component: WasteQdrChangesRequestedComponent;
  let fixture: ComponentFixture<WasteQdrChangesRequestedComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });
  const currentPayload = {
    qdr: {
      reportProvided: false,
      reasonForUnprovided: 'A reason',
    },
    wasteQDRAttachments: {},
    wasteQDRSectionsCompleted: { qdr: true },
    reviewDecision: {
      type: 'OPERATOR_AMENDS_NEEDED',
      details: { notes: 'A note', requiredChanges: [{ reason: 'A regulator reason' }] },
    },
  };

  class Page extends BasePage<WasteQdrChangesRequestedComponent> {
    get checkboxes() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WasteQdrChangesRequestedComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockWasteQdrSubmitStateBuild(currentPayload));

    fixture = TestBed.createComponent(WasteQdrChangesRequestedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display error on empty form submit', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryListContents).toEqual([
      'Check the box to confirm you have made changes and want to mark as complete',
    ]);
    expect(page.errorSummaryListContents.length).toEqual(1);
  });

  it('should submit a valid form and navigate to next page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.checkboxes[0].click();

    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockWasteQdrPostBuild(
        { qdr: { ...currentPayload.qdr } },
        {
          changesRequested: true,
          qdr: true,
        },
      ),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../'], { relativeTo: route });
  });
});
