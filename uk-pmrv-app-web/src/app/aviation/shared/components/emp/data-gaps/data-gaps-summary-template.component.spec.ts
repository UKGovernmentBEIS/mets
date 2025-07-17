import { CommonModule } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { render, RenderResult, screen } from '@testing-library/angular';

import { EmpDataGaps } from 'pmrv-api';

import { DataGapsSummaryTemplateComponent } from './data-gaps-summary-template.component';

describe('DataGapsSummaryTemplate', () => {
  const dataGapsData: EmpDataGaps = {
    dataGaps: 'Data gaps description',
    secondaryDataSources: 'Secodary data sources description',
    substituteData: 'substitute data description',
    otherDataGapsTypes: 'other data gaps types desc',
  };
  let result: RenderResult<DataGapsSummaryTemplateComponent>;
  beforeEach(async () => {
    result = await render(DataGapsSummaryTemplateComponent, {
      imports: [RouterTestingModule, SharedModule, CommonModule],
      componentProperties: { isEditable: true, data: dataGapsData },
    });
    result.detectChanges();
  });

  it('should create', () => {
    const {
      fixture: { componentInstance: component },
    } = result;
    expect(component).toBeTruthy();
  });
  it('should render populated summary data', async () => {
    expect(screen.getByText(dataGapsData.dataGaps)).toBeInTheDocument();
    expect(screen.getByText(dataGapsData.otherDataGapsTypes)).toBeInTheDocument();
    expect(screen.getByText(dataGapsData.secondaryDataSources)).toBeInTheDocument();
    expect(screen.getByText(dataGapsData.substituteData)).toBeInTheDocument();
    expect((await screen.findAllByText('Change')).length).toEqual(4);
  });
});
