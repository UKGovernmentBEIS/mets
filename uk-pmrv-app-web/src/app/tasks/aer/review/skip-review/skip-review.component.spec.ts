import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { SkipReviewComponent } from './skip-review.component';

describe('SkipReviewComponent', () => {
  let component: SkipReviewComponent;
  let fixture: ComponentFixture<SkipReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;
  const tasksService = mockClass(TasksService);
  const route = {
    routeConfig: { path: 'skip-review' },
    parent: { routeConfig: { path: '' } },
  };

  class Page extends BasePage<SkipReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('.govuk-heading-xl').textContent.trim();
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        { provide: BusinessErrorService, useValue: mockClass(BusinessErrorService) },
      ],

      declarations: [SkipReviewComponent],
      imports: [AerModule, SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    (tasksService.processRequestTaskAction as jest.Mock).mockClear(); // Clear the mock
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(SkipReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit with reason', () => {
    expect(page.heading).toEqual('Skip the review and complete the report');

    component.form.controls['reason'].setValue('Test');
    component.form.controls['type'].setValue('OTHER');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AER_SKIP_REVIEW',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'AER_SKIP_REVIEW_PAYLOAD',
        type: 'OTHER',
        reason: 'Test',
      } as RequestTaskActionPayload,
    });
  });

  it('should submit without reason', () => {
    expect(page.heading).toEqual('Skip the review and complete the report');

    component.form.controls['type'].setValue('NOT_REQUIRED');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AER_SKIP_REVIEW',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'AER_SKIP_REVIEW_PAYLOAD',
        type: 'NOT_REQUIRED',
      } as RequestTaskActionPayload,
    });
  });
});
