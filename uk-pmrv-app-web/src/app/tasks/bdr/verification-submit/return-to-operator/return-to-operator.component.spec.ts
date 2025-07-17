import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { mockStateBuild } from '../testing/mock-state';
import { BdrReturnToOperatorComponent, returnToOperatorFormProvider } from './';

describe('ReturnToOperatorComponent', () => {
  let component: BdrReturnToOperatorComponent;
  let fixture: ComponentFixture<BdrReturnToOperatorComponent>;
  let page: Page;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<BdrReturnToOperatorComponent> {
    set changesRequired(value: string) {
      this.setInputValue('#changesRequired', value);
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrReturnToOperatorComponent],
      providers: [returnToOperatorFormProvider, { provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockStateBuild());

    fixture = TestBed.createComponent(BdrReturnToOperatorComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and navigate to summary page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryListContents).toEqual(['Enter a comment']);
    expect(page.errorSummaryListContents.length).toEqual(1);

    page.changesRequired = 'Change';
    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
  });
});
