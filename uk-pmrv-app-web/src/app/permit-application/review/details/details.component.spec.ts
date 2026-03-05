import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store';

import { CompanyInformationService, PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewState } from '../../testing/mock-state';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let authStore: AuthStore;
  let authService: Partial<jest.Mocked<AuthService>>;
  const companyInformationService = mockClass(CompanyInformationService);
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'INSTALLATION_DETAILS',
    },
  );

  @Component({
    selector: 'app-review-group-decision-container',
    template: `
      <div>
        Review group decision component.
        <div>Key:{{ groupKey }}</div>
        <div>Can edit:{{ canEdit }}</div>
      </div>
    `,
  })
  class MockDecisionComponent {
    @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
    @Input() canEdit = true;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  class Page extends BasePage<DetailsComponent> {
    get reviewSections() {
      return this.queryAll<HTMLLIElement>('li[app-task-item]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
        { provide: AuthService, useValue: authService },
        { provide: CompanyInformationService, useValue: companyInformationService },
        DestroySubject,
      ],
      declarations: [DetailsComponent, MockDecisionComponent],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });

    companyInformationService.getCompanyProfileByRegistrationNumber.mockReturnValue(
      of({
        name: 'COMPANY 91634248 LIMITED',
        registrationNumber: '91634248',
        address: {
          line1: 'Companies House',
          line2: 'Crownway',
          city: 'Cardiff',
          country: 'United Kingdom',
          postcode: 'CF14 3UZ',
        },
      }),
    );
  });

  describe('without review group decision', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockReviewState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display appropriate sections for review', () => {
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('span').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Installation and operator details', 'completed'],
        ['Environmental permits and licenses', 'completed'],
        ['Description of the installation', 'completed'],
        ['Regulated activity', 'completed'],
        ['Estimated annual CO2e', 'completed'],
      ]);
    });
  });

  describe('with review group decision summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            type: 'ACCEPTED',
            details: { notes: 'notes' },
          },
        },
        reviewSectionsCompleted: {
          INSTALLATION_DETAILS: true,
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display appropriate sections for review', () => {
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('span').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Installation and operator details', 'completed'],
        ['Environmental permits and licenses', 'completed'],
        ['Description of the installation', 'completed'],
        ['Regulated activity', 'completed'],
        ['Estimated annual CO2e', 'completed'],
      ]);
    });
  });
});
