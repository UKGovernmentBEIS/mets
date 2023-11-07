import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerRegulatedActivitiesGroupComponent } from '@shared/components/review-groups/opinion-statement-group/aer-regulated-activities-group/aer-regulated-activities-group.component';
import { GasPipe } from '@shared/pipes/gas.pipe';
import { RegulatedActivityTypePipe } from '@shared/pipes/regulated-activity-type.pipe';
import { SharedModule } from '@shared/shared.module';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BasePage } from '@testing';

describe('AerRegulatedActivitiesGroupComponent', () => {
  let page: Page;
  let component: AerRegulatedActivitiesGroupComponent;
  let fixture: ComponentFixture<AerRegulatedActivitiesGroupComponent>;

  const mockOpinionStatement = mockVerificationApplyPayload.verificationReport.opinionStatement;
  const regulatedActivityPipe = new RegulatedActivityTypePipe();
  const gasPipe = new GasPipe();

  class Page extends BasePage<AerRegulatedActivitiesGroupComponent> {
    get heading2(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h2');
    }

    get summaryListKeys(): string[] {
      return this.queryAll<HTMLDListElement>('dl dt').map((dt) => dt.textContent.trim());
    }

    get deleteLinks(): HTMLAnchorElement[] {
      return this.queryAll<HTMLAnchorElement>('a').filter((el) => el.textContent.trim() === 'Delete');
    }

    get addButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  const initComponentState = () => {
    component.regulatedActivities = mockOpinionStatement.regulatedActivities;
    component.isEditable = true;
    component.addRouterLink = 'add';
    component.deleteRouterLink = 'delete';
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AerRegulatedActivitiesGroupComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('when editable is true', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerRegulatedActivitiesGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the regulated activities, delete and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Regulated activities');
      const expectedActivities = mockOpinionStatement.regulatedActivities.map(
        (activity) => `${regulatedActivityPipe.transform(activity)} (${gasPipe.transform(activity)})`,
      );
      expect(page.summaryListKeys).toEqual(expectedActivities);
      expect(page.deleteLinks).toHaveLength(5);
      expect(page.addButton).toBeTruthy();
    });
  });

  describe('when editable is false', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerRegulatedActivitiesGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isEditable = false;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the regulated activities and not render delete and add buttons', () => {
      const expectedActivities = mockOpinionStatement.regulatedActivities.map(
        (activity) => `${regulatedActivityPipe.transform(activity)} (${gasPipe.transform(activity)})`,
      );

      expect(page.heading2.textContent.trim()).toEqual('Regulated activities');
      expect(page.summaryListKeys).toEqual(expectedActivities);
      expect(page.deleteLinks).toHaveLength(0);
      expect(page.addButton).toBeFalsy();
    });
  });
});
