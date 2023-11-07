import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { CompleteReviewComponent } from './complete-review.component';

describe('CompleteReviewComponent', () => {
  let component: CompleteReviewComponent;
  let fixture: ComponentFixture<CompleteReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CompleteReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('.govuk-heading-xl').textContent.trim();
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, AerSharedModule],
      declarations: [CompleteReviewComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(CompleteReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit', () => {
    expect(page.heading).toEqual('Are you sure you want to complete this task?');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AER_COMPLETE_REVIEW',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      } as RequestTaskActionPayload,
    });
  });
});
