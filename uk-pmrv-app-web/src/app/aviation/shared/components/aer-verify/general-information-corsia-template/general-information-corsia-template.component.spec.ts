import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { GeneralInformationCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/general-information-corsia-template/general-information-corsia-template.component';
import { BasePage } from '@testing';

describe('GeneralInformationCorsiaTemplateComponent', () => {
  let page: Page;
  let component: GeneralInformationCorsiaTemplateComponent;
  let fixture: ComponentFixture<GeneralInformationCorsiaTemplateComponent>;

  class Page extends BasePage<GeneralInformationCorsiaTemplateComponent> {
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneralInformationCorsiaTemplateComponent, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralInformationCorsiaTemplateComponent);
    component = fixture.componentInstance;

    component.data = {
      verificationCriteria: 'My verification criteria',
      operatorData: 'My operator data',
    };
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryValues).toHaveLength(2);
    expect(page.summaryValues).toEqual([
      ['Verification criteria', 'My verification criteria'],
      ['Information and data used by the operator', 'My operator data'],
    ]);
  });
});
