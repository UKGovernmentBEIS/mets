import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockWasteQdrPostBuild, mockWasteQdrSubmitPayload, wasteQdrSubmitMockState } from '@tasks/waste-qdr/test';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { WasteQdrProvideQdrComponent } from './provide-qdr.component';

describe('QdrComponent', () => {
  let component: WasteQdrProvideQdrComponent;
  let fixture: ComponentFixture<WasteQdrProvideQdrComponent>;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });

  class Page extends BasePage<WasteQdrProvideQdrComponent> {
    get reportProvidedRadio() {
      return this.queryAll<HTMLInputElement>('input[name$="reportProvided"]');
    }

    get reasonForUnprovidedTextArea() {
      return this.query<HTMLInputElement>('textarea[name$="reasonForUnprovided"]');
    }

    get reasonForUnprovided(): string {
      return this.getInputValue('#reasonForUnprovided');
    }
    set reasonForUnprovided(value: string) {
      this.setInputValue('#reasonForUnprovided', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WasteQdrProvideQdrComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(wasteQdrSubmitMockState);

    fixture = TestBed.createComponent(WasteQdrProvideQdrComponent);
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

  it('should submit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.reasonForUnprovidedTextArea).toBeDisabled();

    page.reportProvidedRadio[1].click();
    fixture.detectChanges();
    expect(page.reasonForUnprovidedTextArea).not.toBeDisabled();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toEqual(['Enter a reason']);

    page.reasonForUnprovided = 'A comment';

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockWasteQdrPostBuild(
        {
          qdr: {
            ...mockWasteQdrSubmitPayload.qdr,
            reportProvided: false,
            reasonForUnprovided: 'A comment',
          },
        },
        { qdr: false },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
  });
});
