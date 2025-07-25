import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { ConfigStore } from '@core/config/config.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SourceStreamDetailsTemplateComponent } from '@shared/components/source-streams/source-stream-details/source-streams-details-template.component';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

describe('SourceStreamDetailsTemplateComponent', () => {
  let component: SourceStreamDetailsTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;
  const destroySubject = mockClass(DestroySubject);
  let configStore: ConfigStore;

  @Component({
    template: `
      <app-source-streams-details-template
        [form]="formGroup"
        [isEditing]="isEditing"></app-source-streams-details-template>
    `,
  })
  class TestComponent {
    isEditing = true;
    formGroup = new FormGroup({
      id: new FormControl(),
      reference: new FormControl(),
      description: new FormControl(),
      otherDescriptionName: new FormControl(),
      type: new FormControl(),
      otherTypeName: new FormControl(),
    });
    onSubmit: (form: FormGroup) => any | jest.SpyInstance<void, [FormGroup]>;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
      providers: [{ provide: DestroySubject, useValue: destroySubject }],
    }).compileComponents();

    configStore = TestBed.inject(ConfigStore);
    configStore.setState({ features: { 'co2-venting.permit-workflows.enabled': false } });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(SourceStreamDetailsTemplateComponent)).componentInstance;
    hostComponent.onSubmit = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the nested form', () => {
    expect(element.querySelector('.govuk-caption-l')).toBeTruthy();
    expect(element.querySelector('.govuk-heading-l')).toBeTruthy();
    expect(element.querySelector('button[govukbutton]')).toBeTruthy();
    expect(element.querySelector('button[type="submit"]')).toBeTruthy();
    expect(element.querySelectorAll('input').length).toEqual(3);
  });
});
