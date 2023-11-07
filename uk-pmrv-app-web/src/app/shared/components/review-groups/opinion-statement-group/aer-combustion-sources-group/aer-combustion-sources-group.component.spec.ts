import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { BasePage } from '@testing';

import { AerCombustionSourcesGroupComponent } from './aer-combustion-sources-group.component';

describe('AerCombustionSourcesGroupComponent', () => {
  let page: Page;
  let component: AerCombustionSourcesGroupComponent;
  let fixture: ComponentFixture<AerCombustionSourcesGroupComponent>;

  const mockOpinionStatement = mockVerificationApplyPayload.verificationReport.opinionStatement;

  class Page extends BasePage<AerCombustionSourcesGroupComponent> {
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
    component.combustionSources = mockOpinionStatement.combustionSources;
    component.isEditable = true;
    component.addRouterLink = 'add';
    component.deleteRouterLink = 'delete';
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AerCombustionSourcesGroupComponent],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  describe('when editable is true', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerCombustionSourcesGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the combustion sources, delete and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Source streams');
      expect(page.summaryListKeys).toEqual(mockOpinionStatement.combustionSources);
      expect(page.deleteLinks).toHaveLength(2);
      expect(page.addButton).toBeTruthy();
    });
  });

  describe('when editable is false', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerCombustionSourcesGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      initComponentState();
      component.isEditable = false;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the combustion sources and not render delete and add buttons', () => {
      expect(page.heading2.textContent.trim()).toEqual('Source streams');
      expect(page.summaryListKeys).toEqual(mockOpinionStatement.combustionSources);
      expect(page.deleteLinks).toHaveLength(0);
      expect(page.addButton).toBeFalsy();
    });
  });
});
