import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { AdditionalInfoGroupComponent } from '@shared/components/review-groups/additional-info-group/additional-info-group.component';
import { SharedModule } from '@shared/shared.module';
import { mockAerApplyPayload } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { KeycloakService } from 'keycloak-angular';

describe('AdditionalInfoGroupComponent', () => {
  let component: AdditionalInfoGroupComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-additional-info-group [aerData]="aerData"></app-additional-info-group>
    `,
  })
  class TestComponent {
    aerData = mockAerApplyPayload.aer;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(By.directive(AdditionalInfoGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name')).map(
        (el) => el.textContent.trim(),
      ),
    ).toEqual(['Abbreviations and definitions', 'Additional documents and information', 'Confidentiality statement']);
  });
});
