import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { VerifierCommentGroupComponent } from './verifier-comment-group.component';

describe('VerifierCommentGroupComponent', () => {
  let component: VerifierCommentGroupComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let element: HTMLElement;

  function getRows() {
    return Array.from(element.querySelectorAll('tr')).map((el) =>
      Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
    );
  }

  @Component({
    template: `
      <app-verifier-comment-group
        [isEditable]="isEditable"
        [queryParams]="queryParams"
        [verifierComments]="verifierComments"
      ></app-verifier-comment-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    queryParams = { change: true };
    verifierComments = [{ reference: 'E1', explanation: 'explanation' }];
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifierCommentGroupComponent, SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(VerifierCommentGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the list table', () => {
    expect(getRows()).toEqual([[], ['E1', 'explanation', '', '']]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(getRows()).toEqual([[], ['E1', 'explanation', 'Change', 'Remove']]);
  });
});
