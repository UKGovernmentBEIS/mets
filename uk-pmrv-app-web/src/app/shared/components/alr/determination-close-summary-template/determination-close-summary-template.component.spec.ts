import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { DeterminationCloseSummaryTemplateComponent } from './determination-close-summary-template.component';

describe('DeterminationCloseSummaryTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    standalone: false,
    template: `
      <app-alr-determination-close-summary-template
        [determination]="determination"
        [editable]="editable"
        [alrFile]="alrFile"></app-alr-determination-close-summary-template>
    `,
  })
  class TestComponent {
    editable = true;
    determination: any = {
      type: 'CLOSED_ALR',
      reason: 'reason',
      alrFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
    };
    alrFile: {
      downloadUrl: '/downloads/111111';
      fileName: 'ALR.png';
    };
  }

  class Page extends BasePage<TestComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }

    get values() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__value');
    }

    get actions() {
      return this.queryAll<HTMLElement>('.govuk-summary-list .govuk-summary-list__actions');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [SharedModule, DeterminationCloseSummaryTemplateComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display data', () => {
    expect(page.values.map((el) => el.textContent.trim())).toEqual(['reason']);
  });

  it('should show all html elements ', () => {
    component.alrFile = {
      downloadUrl: '/downloads/111111',
      fileName: 'ALR.png',
    };
    fixture.detectChanges();

    expect(page.pageContents).toEqual([
      'Reason for decision',
      'reason',
      'Change',
      'Uploaded activity level report',
      'ALR.png',
      'Change',
    ]);
  });

  it('should display change links', () => {
    expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(1);
  });
});
