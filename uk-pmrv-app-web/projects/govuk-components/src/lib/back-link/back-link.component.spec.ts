import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BackLinkComponent } from './back-link.component';

describe('BackLinkComponent', () => {
  @Component({ template: '<govuk-back-link [link]="link" [route]="route"></govuk-back-link>' })
  class MockParentComponent {
    link = '../back';
    route = inject(ActivatedRoute).snapshot;
  }

  let fixture: ComponentFixture<MockParentComponent>;
  let parentComponent: MockParentComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [BackLinkComponent, MockParentComponent],
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
});
