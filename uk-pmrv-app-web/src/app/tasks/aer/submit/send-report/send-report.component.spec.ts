import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { getAvailableSections } from '@tasks/aer/core/aer-task-statuses';
import { SendReportComponent } from '@tasks/aer/submit/send-report/send-report.component';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('SendReportComponent', () => {
  let component: SendReportComponent;
  let fixture: ComponentFixture<SendReportComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  class Page extends BasePage<SendReportComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get paragraphsContent() {
      return this.queryAll('p[class="govuk-body"]').map((p) => p.textContent.trim());
    }
    get options() {
      return this.queryAll<HTMLInputElement>('input[name$="option"]');
    }
    get submitButton(): HTMLButtonElement {
      return this.query('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(SendReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('when send report is available', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...mockState,
        requestTaskItem: {
          ...mockState.requestTaskItem,
          requestTask: {
            ...mockState.requestTaskItem.requestTask,
            payload: {
              ...mockAerApplyPayload,
              aerSectionsCompleted: {
                ...getAvailableSections(mockAerApplyPayload).reduce((res, key) => ({ ...res, [key]: [true] }), {}),
              },
            },
          },
        },
      } as CommonTasksState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show content', () => {
      expect(page.heading.textContent.trim()).toEqual('Send report');
      expect(page.paragraphsContent).toEqual([
        'As a HSE permit holder, you can either send your emissions report to a verifier for a verification opinion or directly to Natural Resources Wales.',
      ]);
    });

    it('should navigate report to verification', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.options[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledWith(['verification'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });

    it('should navigate report to regulator', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.options[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledWith(['regulator'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });

  describe('when send report is not available', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({}, { abbreviations: [false] }));
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show content', () => {
      expect(page.heading.textContent.trim()).toEqual('Submit report');
      expect(page.paragraphsContent).toEqual(['You must complete all tasks before submitting your report.']);
    });
  });
});
