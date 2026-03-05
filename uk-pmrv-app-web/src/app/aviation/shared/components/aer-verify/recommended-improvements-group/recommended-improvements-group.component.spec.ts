import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { RecommendedImprovementsGroupComponent } from './recommended-improvements-group.component';

describe('RecommendedImprovementsGroupComponent', () => {
  let component: RecommendedImprovementsGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  function getRows() {
    return Array.from(element.querySelectorAll('tr')).map((el) =>
      Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
    );
  }

  @Component({
    template: `
      <app-recommended-improvements-group-template
        [isEditable]="isEditable"
        [queryParams]="queryParams"
        [verifierComments]="verifierComments"></app-recommended-improvements-group-template>
    `,
  })
  class TestComponent {
    isEditable = false;
    queryParams = { change: true };
    verifierComments = [{ reference: 'A1', explanation: 'explanation' }];
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecommendedImprovementsGroupComponent, SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(RecommendedImprovementsGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the list table', () => {
    expect(getRows()).toEqual([[], ['A1', 'explanation', '', '']]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(getRows()).toEqual([[], ['A1', 'explanation', 'Change', 'Remove']]);
  });
});
