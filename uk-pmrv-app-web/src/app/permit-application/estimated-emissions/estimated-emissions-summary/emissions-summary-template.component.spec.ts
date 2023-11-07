import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { EmissionsSummaryTemplateComponent } from './emissions-summary-template.component';

describe('EmissionsSummaryTemplateComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let page: Page;
  let component: EmissionsSummaryTemplateComponent;
  let fixture: ComponentFixture<EmissionsSummaryTemplateComponent>;

  class Page extends BasePage<EmissionsSummaryTemplateComponent> {
    get labels() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__key');
    }
    get values() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__value');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(EmissionsSummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionsSummaryTemplateComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('for GHGE', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(mockState);
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the selected category', () => {
      expect(page.labels.map((el) => el.textContent.trim())).toEqual([
        'What is the installation’s estimated annual CO2e?',
        'Installation Category',
      ]);
      expect(page.values.map((el) => el.textContent.trim())).toEqual(['33 tonnes', 'Category A (Low emitter)']);
    });
  });

  describe('for HSE', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState({
        ...mockState,
        permitType: 'HSE',
      });
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not display the selected category', () => {
      expect(page.labels.map((el) => el.textContent.trim())).toEqual([
        'What is the installation’s estimated annual CO2e?',
      ]);
      expect(page.values.map((el) => el.textContent.trim())).toEqual(['33 tonnes']);
    });
  });
});
