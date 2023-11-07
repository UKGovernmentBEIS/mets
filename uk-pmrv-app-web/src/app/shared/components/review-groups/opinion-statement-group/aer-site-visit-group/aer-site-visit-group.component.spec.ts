import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerSiteVisitGroupComponent } from '@shared/components/review-groups/opinion-statement-group/aer-site-visit-group/aer-site-visit-group.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { InPersonSiteVisit, NoSiteVisit, VirtualSiteVisit } from 'pmrv-api';

describe('AerSiteVisitGroupComponent', () => {
  let page: Page;
  let component: AerSiteVisitGroupComponent;
  let fixture: ComponentFixture<AerSiteVisitGroupComponent>;

  class Page extends BasePage<AerSiteVisitGroupComponent> {
    get heading2(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h2');
    }

    get summaryList() {
      return Array.from(this.queryAll<HTMLTableRowElement>('dl>div')).map((el) => [
        Array.from(el.querySelectorAll('dt, dd')).map((el) => el.textContent.trim()),
      ]);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AerSiteVisitGroupComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('when siteVisit is IN_PERSON', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerSiteVisitGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      component.siteVisit = {
        visitDates: ['2018-01-02', '2020-12-28'],
        siteVisitType: 'IN_PERSON',
        teamMembers: 'Alex, Joe',
      } as InPersonSiteVisit;
      component.isEditable = true;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the site verification details', () => {
      expect(page.heading2.textContent.trim()).toEqual('Site verification');
      expect(page.summaryList).toEqual([
        [['Did your team conduct any site visits?', 'In person visit', 'Change']],
        [['Dates of visit', 'Jan 2, 2018 Dec 28, 2020', 'Change']],
        [['Team members who conducted the site visit', 'Alex, Joe', 'Change']],
      ]);
    });
  });

  describe('when siteVisit is NO_VISIT', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerSiteVisitGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      component.siteVisit = {
        siteVisitType: 'NO_VISIT',
        reason: 'reason',
      } as NoSiteVisit;
      component.isEditable = true;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the site verification details', () => {
      expect(page.heading2.textContent.trim()).toEqual('Site verification');
      expect(page.summaryList).toEqual([
        [['Did your team conduct any site visits?', 'No visit', 'Change']],
        [['Reasons for not conducting a site visit', 'reason', 'Change']],
      ]);
    });
  });

  describe('when siteVisit is VIRTUAL', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(AerSiteVisitGroupComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      component.siteVisit = {
        visitDates: ['2018-01-02', '2020-12-28'],
        siteVisitType: 'VIRTUAL',
        reason: 'Some reason',
        teamMembers: 'Alex, Joe',
      } as VirtualSiteVisit;
      component.isEditable = true;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the site verification details', () => {
      expect(page.heading2.textContent.trim()).toEqual('Site verification');
      expect(page.summaryList).toEqual([
        [['Did your team conduct any site visits?', 'Virtual visit', 'Change']],
        [['Dates of visit', 'Jan 2, 2018 Dec 28, 2020', 'Change']],
        [['Team members who conducted the site visit', 'Alex, Joe', 'Change']],
        [['Reasons for conducting a virtual site visit', 'Some reason', 'Change']],
      ]);
    });
  });
});
