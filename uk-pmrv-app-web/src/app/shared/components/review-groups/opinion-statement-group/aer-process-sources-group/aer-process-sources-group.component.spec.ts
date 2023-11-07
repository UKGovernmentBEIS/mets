import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerProcessSourcesGroupComponent } from '@shared/components/review-groups/opinion-statement-group/aer-process-sources-group/aer-process-sources-group.component';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BasePage } from '@testing';

describe('AerProcessSourcesGroupComponent', () => {
  let page: Page;
  let component: AerProcessSourcesGroupComponent;
  let fixture: ComponentFixture<AerProcessSourcesGroupComponent>;

  const mockOpinionStatement = mockVerificationApplyPayload.verificationReport.opinionStatement;

  class Page extends BasePage<AerProcessSourcesGroupComponent> {
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
    component.processSources = mockOpinionStatement.processSources;
    component.isEditable = true;
    component.addRouterLink = 'add';
    component.deleteRouterLink = 'delete';
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AerProcessSourcesGroupComponent],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  describe('when editable is true', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerProcessSourcesGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the process sources, delete and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Process sources');
      expect(page.summaryListKeys).toEqual(mockOpinionStatement.processSources);
      expect(page.deleteLinks).toHaveLength(2);
      expect(page.addButton).toBeTruthy();
    });
  });

  describe('when editable is false', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerProcessSourcesGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isEditable = false;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the process sources and not render delete and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Process sources');
      expect(page.summaryListKeys).toEqual(mockOpinionStatement.processSources);
      expect(page.deleteLinks).toHaveLength(0);
      expect(page.addButton).toBeFalsy();
    });
  });
});
