import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskState, RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { mockState, mockStateCannotSubmit } from '../testing/mock-state';
import { SendReportPageComponent } from './index';
import { sendReportFormProvider } from './send-report-form.provider';

describe('SendReportPageComponent', () => {
  let component: SendReportPageComponent;
  let fixture: ComponentFixture<SendReportPageComponent>;
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;

  class Page extends BasePage<SendReportPageComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
    get paragraphsContent() {
      return this.queryAll('p').map((p) => p.textContent.trim());
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
      imports: [SendReportPageComponent, RouterTestingModule],
      providers: [
        KeycloakService,
        sendReportFormProvider,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(SendReportPageComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('when send report is available', () => {
    beforeEach(() => {
      store = TestBed.inject(RequestTaskStore);
      store.setState({
        ...mockState,
      } as RequestTaskState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show content', () => {
      expect(page.heading.textContent.trim()).toEqual('Submit your report');
      expect(page.paragraphsContent).toEqual([
        'You can either send your emissions report to a verifier for a verification opinion or send it directly to Environment Agency.',
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
      store = TestBed.inject(RequestTaskStore);
      store.setState(mockStateCannotSubmit);
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
