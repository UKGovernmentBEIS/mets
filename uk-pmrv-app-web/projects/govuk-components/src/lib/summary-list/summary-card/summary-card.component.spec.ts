import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, RouterLink } from '@angular/router';

import { LinkDirective } from '../../directives';
import { SummaryCardComponent } from './summary-card.component';

describe('SummaryCardComponent', () => {
  let component: SummaryCardComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    imports: [SummaryCardComponent, LinkDirective, RouterLink],
    standalone: true,
    template: `
      <div govuk-summary-card [title]="title">
        <ng-template #actions>
          <a govukLink="summaryAction" routerLink="/change">Change</a>
          <a govukLink="summaryAction" routerLink="/delete">Delete</a>
        </ng-template>
        <p>Test content</p>
      </div>
    `,
  })
  class TestComponent {
    title = 'University of Gloucestershire';
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).overrideComponent(SummaryCardComponent, { set: { host: { 'data-test-id': 'summary-card-spec' } } });

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have two rows', () => {
    const nativeElement = fixture.nativeElement;
    const titleContents = nativeElement.querySelector('.govuk-summary-card__title').textContent.trim();
    const ulElements = nativeElement.querySelectorAll('.govuk-summary-card__actions') as HTMLUListElement[];
    const liElements = nativeElement.querySelectorAll('.govuk-summary-card__action') as HTMLLIElement[];
    const liContents = Array.from(liElements).map((item) => item.textContent.trim());
    const pContents = (
      nativeElement.querySelector('.govuk-summary-card__content p') as HTMLParagraphElement
    ).textContent.trim();

    expect(titleContents).toEqual('University of Gloucestershire');
    expect(ulElements.length).toEqual(1);
    expect(liElements.length).toEqual(2);
    expect(liContents).toEqual(['Change', 'Delete']);
    expect(pContents).toEqual('Test content');
  });
});
