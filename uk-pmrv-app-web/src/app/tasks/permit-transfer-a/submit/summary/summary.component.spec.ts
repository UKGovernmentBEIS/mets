import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { PermitTransferAModule } from '@tasks/permit-transfer-a/permit-transfer-a.module';
import { mockPostBuild, mockState, mockTransferPayload } from '@tasks/permit-transfer-a/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const govukDatePipe = new GovukDatePipe();

  class Page extends BasePage<SummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelectorAll('dd')[0], row.querySelectorAll('dd')[1]])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent],
      imports: [PermitTransferAModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockState });
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and navigate to task list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.summaryListValues).toEqual([
      ['Reason for the transfer', 'Reason of transfer', 'Change'],
      ['Files added', 'None', 'Change'],
      ['Actual or estimated date of transfer', govukDatePipe.transform(mockTransferPayload.transferDate), 'Change'],
      ['Who is paying for the transfer?', 'Receiver', 'Change'],
      ['Who is completing the annual emission report?', 'Transferer', 'Change'],
      ['Transfer code', '123456789', 'Change'],
    ]);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(mockPostBuild({ sectionCompleted: true }));

    expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: activatedRoute, state: { notification: true } });
  });
});
