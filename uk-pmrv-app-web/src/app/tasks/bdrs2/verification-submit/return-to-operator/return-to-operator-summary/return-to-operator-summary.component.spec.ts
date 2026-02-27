import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { returnToOperatorFormProvider } from '..';
import { BdrS2ReturnToOperatorSummaryComponent } from './return-to-operator-summary.component';

describe('ReturnToOperatorSummaryComponent', () => {
  let component: BdrS2ReturnToOperatorSummaryComponent;
  let fixture: ComponentFixture<BdrS2ReturnToOperatorSummaryComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<BdrS2ReturnToOperatorSummaryComponent> {
    get summaryContents() {
      return this.queryAll('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }

    get confirmationContents() {
      return this.query('app-confirmation-shared');
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrS2ReturnToOperatorSummaryComponent],
      providers: [returnToOperatorFormProvider, provideRouter([]), { provide: TasksService, useValue: tasksService }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateBuild());

    fixture = TestBed.createComponent(BdrS2ReturnToOperatorSummaryComponent);
    component = fixture.componentInstance;
    component.form.patchValue({ changesRequired: 'Changes' });
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary content, submit and display confirmation content', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.confirmationContents).toBeFalsy();
    expect(page.summaryContents).toEqual(['Changes required from operator', 'Changes', 'Change']);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      ...mockPostBuild({
        changesRequired: component.form.value.changesRequired,
      }),
      requestTaskActionType: 'BDRS2_VERIFICATION_RETURN_TO_OPERATOR',
      requestTaskActionPayload: {
        payloadType: 'BDRS2_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD',
        changesRequired: 'Changes',
      },
    });
    expect(page.confirmationContents).toBeTruthy();
  });
});
