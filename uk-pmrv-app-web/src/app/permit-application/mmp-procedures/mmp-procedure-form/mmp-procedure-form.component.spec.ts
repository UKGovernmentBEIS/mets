import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormBuilder, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { Procedure } from 'pmrv-api';

import { MmpProcedureFormComponent } from './mmp-procedure-form.component';

describe('MmpProcedureFormComponent', () => {
  let component: MmpProcedureFormComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <app-mmp-procedure-form formGroupName="procedureForm"></app-mmp-procedure-form>
      </form>
    `,
  })
  class TestComponent {
    form = new FormGroup({
      procedureForm: new FormBuilder().group(MmpProcedureFormComponent.controlsFactory(null)),
    });
  }

  const procedure: Procedure = {
    procedureName: 'procedureName2',
    procedureReference: 'procedureReference2',
    diagramReference: 'diagramReference2',
    procedureDescription: 'procedureDescription2',
    dataMaintenanceResponsibleEntity: 'dataMaintenanceResponsibleEntity2',
    locationOfRecords: 'locationOfRecords2',
    itSystemUsed: 'itSystemUsed2',
    standardsAppliedList: 'standardsAppliedList2',
  };
  class Page extends BasePage<TestComponent> {
    get labels() {
      return this.queryAll<HTMLLabelElement>('label');
    }
    get inputs() {
      return this.queryAll<HTMLInputElement>('input');
    }
    get textAreas() {
      return this.queryAll<HTMLTextAreaElement>('textarea');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent, MmpProcedureFormComponent],
    })
      .overrideComponent(MmpProcedureFormComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(MmpProcedureFormComponent))?.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render procedure form labels', () => {
    expect(page.labels.map((label) => label.textContent.trim())).toEqual([
      'Name of the procedure',
      'Procedure reference',
      'Diagram reference (optional)',
      'Procedure description (optional)',
      'Department or role responsible for data maintenance (optional)',
      'Location of records (optional)',
      'IT system used (optional)',
      'List of EN or other standards applied (optional)',
    ]);
  });

  it('should update values on setValue', () => {
    hostComponent.form.get('procedureForm.procedureName').setValue(<never>procedure.procedureName);
    hostComponent.form.get('procedureForm.procedureReference').setValue(<any>procedure.procedureReference);
    hostComponent.form.get('procedureForm.diagramReference').setValue(<any>procedure.diagramReference);
    hostComponent.form.get('procedureForm.procedureDescription').setValue(<any>procedure.procedureDescription);
    hostComponent.form
      .get('procedureForm.dataMaintenanceResponsibleEntity')
      .setValue(<never>procedure.dataMaintenanceResponsibleEntity);
    hostComponent.form.get('procedureForm.locationOfRecords').setValue(<any>procedure.locationOfRecords);
    hostComponent.form.get('procedureForm.itSystemUsed').setValue(<any>procedure.itSystemUsed);
    hostComponent.form.get('procedureForm.standardsAppliedList').setValue(<never>procedure.standardsAppliedList);
    fixture.detectChanges();

    expect(page.inputs.map((input) => input.value)).toEqual([
      procedure.procedureName,
      procedure.procedureReference,
      procedure.diagramReference,
      procedure.dataMaintenanceResponsibleEntity,
      procedure.locationOfRecords,
      procedure.itSystemUsed,
      procedure.standardsAppliedList,
    ]);
    expect(page.textAreas.map((input) => input.value)).toEqual([procedure.procedureDescription]);
  });

  it('should apply field validations', () => {
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();

    const procedureDescription = hostComponent.form.get('procedureForm.procedureDescription');
    expect(procedureDescription.errors).toEqual(null);
    hostComponent.form.get('procedureForm.procedureDescription').setValue(<any>'a'.repeat(10001));
    fixture.detectChanges();
    expect(procedureDescription.errors).toEqual({
      maxlength: 'The procedure description should not be more than 10000 characters',
    });

    const procedureName = hostComponent.form.get('procedureForm.procedureName');
    expect(procedureName.errors).toEqual({ required: 'Enter the name of the procedure' });
    hostComponent.form.get('procedureForm.procedureName').setValue(<any>'a'.repeat(1001));
    fixture.detectChanges();
    expect(procedureName.errors).toEqual({
      maxlength: 'The procedure name should not be more than 1000 characters',
    });

    const procedureReference = hostComponent.form.get('procedureForm.procedureReference');
    expect(procedureReference.errors).toEqual({ required: 'Enter the procedure reference' });
    hostComponent.form.get('procedureForm.procedureReference').setValue(<any>'a'.repeat(501));
    fixture.detectChanges();
    expect(procedureReference.errors).toEqual({
      maxlength: 'The procedure reference should not be more than 500 characters',
    });

    const diagramReference = hostComponent.form.get('procedureForm.diagramReference');
    expect(diagramReference.errors).toBeFalsy();
    hostComponent.form.get('procedureForm.diagramReference').setValue(<any>'a'.repeat(501));
    fixture.detectChanges();
    expect(diagramReference.errors).toEqual({
      maxlength: 'The diagram reference should not be more than 500 characters',
    });

    const dataMaintenanceResponsibleEntity = hostComponent.form.get('procedureForm.dataMaintenanceResponsibleEntity');
    expect(dataMaintenanceResponsibleEntity.errors).toBeFalsy();
    hostComponent.form.get('procedureForm.dataMaintenanceResponsibleEntity').setValue(<any>'a'.repeat(1001));
    fixture.detectChanges();
    expect(dataMaintenanceResponsibleEntity.errors).toEqual({
      maxlength:
        'The name of the department or role responsible for data maintenance should not be more than 1000 characters',
    });

    const locationOfRecords = hostComponent.form.get('procedureForm.locationOfRecords');
    expect(locationOfRecords.errors).toBeFalsy();
    hostComponent.form.get('procedureForm.locationOfRecords').setValue(<any>'a'.repeat(2001));
    fixture.detectChanges();
    expect(locationOfRecords.errors).toEqual({
      maxlength: 'The location of the records should not be more than 2000 characters',
    });

    const itSystemUsed = hostComponent.form.get('procedureForm.itSystemUsed');
    expect(itSystemUsed.errors).toBeFalsy();
    hostComponent.form.get('procedureForm.itSystemUsed').setValue(<any>'a'.repeat(501));
    fixture.detectChanges();
    expect(itSystemUsed.errors).toEqual({
      maxlength: 'The IT system used should not be more than 500 characters',
    });

    const standardsAppliedList = hostComponent.form.get('procedureForm.standardsAppliedList');
    expect(standardsAppliedList.errors).toBeFalsy();
    hostComponent.form.get('procedureForm.standardsAppliedList').setValue(<any>'a'.repeat(2001));
    fixture.detectChanges();
    expect(standardsAppliedList.errors).toEqual({
      maxlength: 'The list of EN or other standards applied should not be more than 2000 characters',
    });
  });
});
