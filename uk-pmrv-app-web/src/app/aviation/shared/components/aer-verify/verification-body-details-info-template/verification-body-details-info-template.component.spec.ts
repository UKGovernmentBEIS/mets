import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { VerificationBodyDetailsInfoTemplateComponent } from '@aviation/shared/components/aer-verify/verification-body-details-info-template/verification-body-details-info-template.component';
import { BasePage } from '@testing';

describe('VerificationBodyDetailsInfoTemplateComponent', () => {
  let page: Page;
  let component: VerificationBodyDetailsInfoTemplateComponent;
  let fixture: ComponentFixture<VerificationBodyDetailsInfoTemplateComponent>;

  class Page extends BasePage<VerificationBodyDetailsInfoTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerificationBodyDetailsInfoTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerificationBodyDetailsInfoTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      name: 'Verification body company',
      address: {
        line1: 'Korinthou 4, Neo Psychiko',
        line2: 'line 2 legal test',
        city: 'Athens',
        country: 'GR',
        postcode: '15452',
      },
      accreditationReferenceNumber: '1313',
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(3);
    expect(page.summaryValues).toEqual([
      ['Company name', 'Verification body company'],
      ['Address', `Korinthou 4, Neo Psychiko  , line 2 legal test Athens15452`],
      ['Accreditation number', '1313'],
    ]);
  });
});
