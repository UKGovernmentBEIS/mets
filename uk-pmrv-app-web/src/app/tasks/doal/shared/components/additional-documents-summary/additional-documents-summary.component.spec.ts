import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AdditionalDocumentsSummaryComponent } from './additional-documents-summary.component';

describe('AdditionalDocumentsSummaryComponent', () => {
  let page: Page;
  let component: AdditionalDocumentsSummaryComponent;
  let fixture: ComponentFixture<AdditionalDocumentsSummaryComponent>;

  class Page extends BasePage<AdditionalDocumentsSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get appDoalAdditionalDocumentsSummaryTemplate() {
      return this.query('app-doal-additional-documents-summary-template');
    }

    get appReturnTaskLink() {
      return this.query('app-task-return-link');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdditionalDocumentsSummaryComponent, DoalTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [KeycloakService],
    }).compileComponents();

    fixture = TestBed.createComponent(AdditionalDocumentsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.taskType$ = of('DOAL_WAIT_FOR_PEER_REVIEW');
    component.additionalDocuments$ = of({
      exist: false,
      comment: 'Test comment 1',
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1.textContent.trim()).toEqual('Upload additional documents');
    expect(page.appDoalAdditionalDocumentsSummaryTemplate).toBeTruthy();
    expect(page.appReturnTaskLink).toBeTruthy();
  });
});
