import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { BackLinkComponent } from './back-link.component';

describe('BackLinkComponent', () => {
  @Component({
    imports: [BackLinkComponent],
    template: '<govuk-back-link [link]="link" [route]="route" [inverse]="inverse" />',
  })
  class MockParentComponent {
    link = '../back';
    route = inject(ActivatedRoute).snapshot;
    inverse = false;
  }

  let fixture: ComponentFixture<MockParentComponent>;
  let parentComponent: MockParentComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackLinkComponent, MockParentComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(() => {
    TestBed.createComponent(MockParentComponent);
    fixture = TestBed.createComponent(MockParentComponent);
    parentComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(parentComponent).toBeTruthy();
  });

  it('should have inverse color class', () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    const backlinkDiv = hostElement.querySelector<HTMLElement>('.govuk-back-link');
    expect(backlinkDiv.classList).not.toContain('govuk-back-link--inverse');

    fixture.componentInstance.inverse = true;
    fixture.detectChanges();

    expect(backlinkDiv.classList).toContain('govuk-back-link--inverse');
  });
});
