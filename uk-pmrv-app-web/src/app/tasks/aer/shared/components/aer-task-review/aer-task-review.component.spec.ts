import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';
import { AerTaskReviewComponent } from '@tasks/aer/shared/components/aer-task-review/aer-task-review.component';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('AerTaskReviewComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  class Page extends BasePage<TestComponent> {
    get pageHeadings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h1');
    }
    get headings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
    get links(): HTMLLinkElement {
      return this.query<HTMLLinkElement>('a');
    }
  }

  @Component({
    template: `
      <app-aer-task-review [breadcrumb]="true" heading="Transferred CO2">
        <h2 class="govuk-heading-m">Transferred CO2 details</h2>
      </app-aer-task-review>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerSharedModule, RouterTestingModule],
      declarations: [AerTaskReviewComponent, TestComponent],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should display all internal titles', () => {
    expect(page.pageHeadings).toHaveLength(1);
    expect(page.pageHeadings[0].textContent.trim()).toEqual('Transferred CO2');

    expect(page.headings).toHaveLength(1);
    expect(page.headings[0].textContent.trim()).toEqual('Transferred CO2 details');
  });
});
