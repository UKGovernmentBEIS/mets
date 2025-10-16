import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { mockPostBuild, mockState } from '@tasks/permit-transfer-a/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { TransferAActivityLevelReportComponent } from './activity-level-report.component';

describe('ActivityLevelReportComponent', () => {
  let component: TransferAActivityLevelReportComponent;
  let fixture: ComponentFixture<TransferAActivityLevelReportComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<TransferAActivityLevelReportComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get reportOptions() {
      return this.queryAll<HTMLInputElement>('input[name$="alrLiable"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferAActivityLevelReportComponent],
      providers: [provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockState });

    fixture = TestBed.createComponent(TransferAActivityLevelReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not submit form and navigate to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledTimes(0);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
  });

  it('should submit form and navigate to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.reportOptions[1].click();
    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'code'], { relativeTo: route });
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockPostBuild({ alrLiable: 'RECEIVER' }));
  });
});
