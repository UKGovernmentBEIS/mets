import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { PermitTransferAModule } from '@tasks/permit-transfer-a/permit-transfer-a.module';
import { mockState, mockTransferPayload } from '@tasks/permit-transfer-a/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SendApplicationComponent } from './send-application.component';

describe('SendApplicationComponent', () => {
  let component: SendApplicationComponent;
  let fixture: ComponentFixture<SendApplicationComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let tasksService: MockType<TasksService>;

  class Page extends BasePage<SendApplicationComponent> {
    get submitionHeading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get confirmationHeading(): string {
      return this.query<any>('app-confirmation-shared');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelectorAll('dd')[0]])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get errorContent() {
      return this.query<HTMLDivElement>('.errorContent h2').textContent.trim();
    }
  }

  beforeEach(async () => {
    tasksService = {
      processRequestTaskAction: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      declarations: [SendApplicationComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      imports: [PermitTransferAModule, RouterTestingModule],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockState });

    fixture = TestBed.createComponent(SendApplicationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render heading content', () => {
    expect(page.submitionHeading).toEqual('Send to the new operator');
    expect(page.confirmationHeading).toBeFalsy();
  });

  it('should display the permit transfer for transferer summary', () => {
    const govukDatePipe = new GovukDatePipe();

    expect(page.summaryListValues).toEqual([
      ['Reason for the transfer', 'Reason of transfer'],
      ['Files added', 'None'],
      ['Actual or estimated date of transfer', govukDatePipe.transform(mockTransferPayload.transferDate)],
      ['Who is paying for the transfer?', 'Receiver'],
      ['Who is completing the annual emission report?', 'Transferer'],
      ['Transfer code', '123456789'],
    ]);
  });

  it('should show transfer code error message when transfer code is not exists or it used in an other transfer', () => {
    jest.spyOn(tasksService, 'processRequestTaskAction').mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: {
              code: 'FORM1001',
              data: ['The transfer code is not valid'],
              message: 'Form validation failed',
            },
            status: 400,
          }),
      ),
    );

    page.submitButton.click();

    fixture.detectChanges();

    expect(page.errorContent).toEqual('The transfer code is not valid');
  });

  it('should send application and show confirmation', () => {
    page.submitButton.click();

    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalled();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_TRANSFER_A_SUBMIT_APPLICATION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
    expect(page.confirmationHeading).toBeTruthy();
  });
});
