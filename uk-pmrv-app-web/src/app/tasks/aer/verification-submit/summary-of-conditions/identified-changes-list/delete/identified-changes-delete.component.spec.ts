import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { IdentifiedChangesDeleteComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/delete/identified-changes-delete.component';
import { mockPostBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

describe('IdentifiedChangesDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: IdentifiedChangesDeleteComponent;
  let fixture: ComponentFixture<IdentifiedChangesDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<IdentifiedChangesDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(async () => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(IdentifiedChangesDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the item name', () => {
    expect(page.header.textContent.trim()).toEqual(`Are you sure you want to delete 'B1 Explanation B1'?`);
  });

  it('should delete and navigate to list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild(
        {
          summaryOfConditions: {
            ...mockVerificationApplyPayload.verificationReport.summaryOfConditions,
            notReportedChanges: [],
          },
        },
        { summaryOfConditions: [false] },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute });
  });
});
