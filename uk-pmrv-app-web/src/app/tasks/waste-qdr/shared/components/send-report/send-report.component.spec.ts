import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { wasteQdrSubmitMockState } from '@tasks/waste-qdr/test';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { WasteQdrSendReportComponent } from './send-report.component';

describe('WasteQdrSendReportComponent', () => {
  let component: WasteQdrSendReportComponent;
  let fixture: ComponentFixture<WasteQdrSendReportComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

  class Page extends BasePage<WasteQdrSendReportComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1.govuk-heading-l');
    }

    get headerConfirm() {
      return this.query<HTMLHeadingElement>('h1.govuk-panel__title');
    }

    get bodyConfirm() {
      return this.query<HTMLDivElement>('.govuk-panel__body');
    }

    get bodyText() {
      return this.queryAll<HTMLElement>('p.govuk-body').map((element) => element.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WasteQdrSendReportComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(wasteQdrSubmitMockState);

    fixture = TestBed.createComponent(WasteQdrSendReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.headerConfirm).toBeFalsy();
    expect(page.header.textContent).toContain('Send to regulator');
    expect(page.bodyText).toEqual([
      'Your report will be sent directly to your regulator (Environment Agency).',
      'By selecting ‘Confirm and send’ you confirm that the information is correct to the best of your knowledge.',
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'WASTE_QDR_SUBMIT_TO_REGULATOR',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });

    // Remains on the same page as redirection is handled elsewhere
    expect(navigateSpy).toHaveBeenCalledTimes(0);

    expect(page.headerConfirm.textContent).toContain('Sent to regulator for review');
    expect(page.bodyConfirm.textContent).toContain(' Your reference number WQDR00126-2025-Q3');
  });
});
