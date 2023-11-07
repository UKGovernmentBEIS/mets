import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { RegulatorComponent } from '@tasks/aer/submit/send-report/regulator/regulator.component';
import { mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('RegulatorComponent', () => {
  let component: RegulatorComponent;
  let fixture: ComponentFixture<RegulatorComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<RegulatorComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get paragraphsContent() {
      return this.queryAll('p[class="govuk-body"]').map((p) => p.textContent.trim());
    }
    get confirmationTitle() {
      return this.query<HTMLHeadingElement>('h1.govuk-panel__title');
    }
    get confirmationPanelContent() {
      return this.query<HTMLElement>('.govuk-panel__body');
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(RegulatorComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toEqual('Send report to regulator');
    expect(page.paragraphsContent[0]).toEqual('Your report will be sent directly to Natural Resources Wales.');
  });

  it('should submit', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.confirmationTitle).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'AER_SUBMIT_APPLICATION',
      requestTaskId: mockState.requestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });

    expect(page.confirmationTitle).toBeTruthy();
    expect(page.confirmationTitle.textContent.trim()).toEqual('Report sent to regulator');
    expect(page.confirmationPanelContent.textContent.trim()).toEqual('Your reference is AEM210-2021');
    expect(page.paragraphsContent).toEqual(['Your report has been sent to Natural Resources Wales.']);
  });
});
