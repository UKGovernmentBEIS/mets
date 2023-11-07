import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { BusinessTestingModule } from '@error/testing/business-error';
import { TaskStatusPipe } from '@permit-application/shared/pipes/task-status.pipe';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockPermitCompletePayload } from '@permit-application/testing/mock-permit-apply-action';
import { mockState } from '@permit-application/testing/mock-state';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';

import { TasksService } from 'pmrv-api';

import { PermitTransferStore } from '../store/permit-transfer.store';
import { PermitTransferSummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: PermitTransferSummaryComponent;
  let fixture: ComponentFixture<PermitTransferSummaryComponent>;
  let store: PermitTransferStore;
  let route: ActivatedRouteStub;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(null)),
  };

  const runOnPushChangeDetection = async (fixture: ComponentFixture<any>): Promise<void> => {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  };

  class Page extends BasePage<PermitTransferSummaryComponent> {
    get heading() {
      return this.query('h1.govuk-heading-l');
    }

    get pageBodies() {
      return this.queryAll<HTMLParagraphElement>('p[class="govuk-body"]');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub({ taskId: 237 });
    await TestBed.configureTestingModule({
      declarations: [PermitTransferSummaryComponent],
      imports: [RouterTestingModule, BusinessTestingModule, SharedPermitModule, SharedModule],
      providers: [
        TaskStatusPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitTransferStore,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitTransferStore);

    fixture = TestBed.createComponent(PermitTransferSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not allow to submit if not completed', async () => {
    store.setState({
      ...mockState,
      requestTaskType: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT',
      payloadType: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD',
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: { ...mockPermitCompletePayload.permitSectionsCompleted },
    });

    await runOnPushChangeDetection(fixture);

    expect(page.pageBodies[0].textContent.trim()).toEqual(
      'All tasks must be completed before you can submit your application.',
    );
  });

  it('should allow to submit if all completed', async () => {
    store.setState({
      ...mockState,
      requestTaskType: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT',
      payloadType: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD',
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: { ...mockPermitCompletePayload.permitSectionsCompleted, transferDetails: [true] },
    });

    await runOnPushChangeDetection(fixture);

    expect(page.pageBodies[0].textContent.trim()).toEqual(
      'By sending this application you are confirming that, to the best of your knowledge, the details you are providing are correct.',
    );
  });

  it('should show submitted info upon pressing complete button', async () => {
    store.setState({
      ...mockState,
      requestTaskType: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT',
      payloadType: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD',
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: { ...mockPermitCompletePayload.permitSectionsCompleted, transferDetails: [true] },
    });

    await runOnPushChangeDetection(fixture);

    page.submitButton.click();
    fixture.detectChanges();

    expect(Array.from(page.pageBodies).map((p) => p.textContent.trim())).toEqual([
      'We’ve sent your application to Environment Agency',
      'The regulator will make a decision and respond within 2 calendar months.',
    ]);

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_TRANSFER_B_SUBMIT_APPLICATION',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});
