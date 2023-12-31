import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { SharedPermitModule } from '../../../permit-application/shared/shared-permit.module';
import { SharedModule } from '../../../shared/shared.module';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<SummaryDetailsComponent>;
  let page: Page;

  class Page extends BasePage<SummaryDetailsComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryDetailsComponent);
    component = fixture.componentInstance;
    component.permitVariationDetails = {
      reason: 'Reason',
      modifications: [
        {
          type: 'INSTALLATION_NAME',
        },
        {
          type: 'NEW_SOURCE_STREAMS',
        },
        {
          type: 'DEFAULT_VALUE_OR_ESTIMATION_METHOD',
        },
      ],
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryDetailsComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
    }).compileComponents();
  });

  describe('for activity data with highest required tier', () => {
    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display summary list', () => {
      expect(page.answers).toEqual([
        ['Description of changes', 'Reason'],
        [
          'Non-significant changes to the Monitoring Plan or the Monitoring Methodology Plan',
          'Change of installation name',
        ],
        ['Significant modifications to the Monitoring Plan', 'The introduction of new source streams'],
        [
          'Significant modifications to the Monitoring Methodology Plan',
          'The change of a default value or estimation method laid down in the monitoring methodology plan.',
        ],
      ]);
    });
  });
});
