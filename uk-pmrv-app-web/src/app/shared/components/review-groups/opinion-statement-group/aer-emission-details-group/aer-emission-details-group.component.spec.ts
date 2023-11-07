import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerEmissionDetailsGroupComponent } from '@shared/components/review-groups/opinion-statement-group/aer-emission-details-group/aer-emission-details-group.component';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BasePage } from '@testing';

describe('AerEmissionDetailsGroupComponent', () => {
  let page: Page;
  let component: AerEmissionDetailsGroupComponent;
  let fixture: ComponentFixture<AerEmissionDetailsGroupComponent>;

  const mockOpinionStatement = mockVerificationApplyPayload.verificationReport.opinionStatement;

  class Page extends BasePage<AerEmissionDetailsGroupComponent> {
    get heading2(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h2');
    }

    get summaryListKeys(): string[] {
      return this.queryAll<HTMLDListElement>('dl dt').map((dt) => dt.textContent.trim());
    }

    get summaryListValues(): string[] {
      return this.queryAll<HTMLDListElement>('dl dd:nth-child(2)').map((dd) => dd.textContent.trim());
    }

    get changeLinks(): HTMLAnchorElement[] {
      return this.queryAll<HTMLAnchorElement>('a').filter((el) => el.textContent.trim() === 'Change');
    }
  }

  const initComponentState = () => {
    component.monitoringApproachDescription = mockOpinionStatement.monitoringApproachDescription;
    component.emissionFactorsDescription = mockOpinionStatement.emissionFactorsDescription;
    component.isEditable = true;
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AerEmissionDetailsGroupComponent],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  describe('when editable is true', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerEmissionDetailsGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the emission details and change buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Emission details');
      expect(page.summaryListKeys).toEqual(['Monitoring approaches used', 'Emission factors']);
      expect(page.summaryListValues).toEqual([
        mockOpinionStatement.monitoringApproachDescription,
        mockOpinionStatement.emissionFactorsDescription,
      ]);
      expect(page.changeLinks).toHaveLength(2);
    });
  });

  describe('when editable is false', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerEmissionDetailsGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isEditable = false;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the emission details and not render change buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Emission details');
      expect(page.summaryListKeys).toEqual(['Monitoring approaches used', 'Emission factors']);
      expect(page.summaryListValues).toEqual([
        mockOpinionStatement.monitoringApproachDescription,
        mockOpinionStatement.emissionFactorsDescription,
      ]);
      expect(page.changeLinks).toHaveLength(0);
    });
  });
});
