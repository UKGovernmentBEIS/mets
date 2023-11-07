import { CommonModule } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { render, RenderResult, screen } from '@testing-library/angular';

import { AviationAerCorsiaDataGaps } from 'pmrv-api';

import { DataGapsSummaryTemplateComponent } from './data-gaps-summary-template.component';

describe('DataGapsSummaryTemplate', () => {
  const dataGaps: AviationAerCorsiaDataGaps = {
    exist: true,
    dataGapsDetails: {
      dataGapsPercentage: '3',
      dataGapsPercentageType: 'MORE_THAN_FIVE_PER_CENT',
      affectedFlightsPercentage: '20',
      dataGaps: [
        {
          reason: 'reason 1',
          type: 'type 1',
          replacementMethod: 'replacement method 1',
          flightsAffected: 5,
          totalEmissions: '7',
        },
        {
          reason: 'reason 2',
          type: 'type 2',
          replacementMethod: 'replacement method 2',
          flightsAffected: 6,
          totalEmissions: '8',
        },
      ],
    },
  };
  let result: RenderResult<DataGapsSummaryTemplateComponent>;
  beforeEach(async () => {
    result = await render(DataGapsSummaryTemplateComponent, {
      imports: [RouterTestingModule, SharedModule, CommonModule],
      componentProperties: { isEditable: true, data: dataGaps },
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
    expect(
      screen.getByText('Did any flights with offsetting obligations have data gaps during the scheme year?'),
    ).toBeInTheDocument();
    expect(
      screen.getByText('What percentage of flights with offsetting obligations had data gaps during the scheme year?'),
    ).toBeInTheDocument();
    expect(screen.getByText('Total affected flights')).toBeInTheDocument();
    expect(screen.getByText('Percentage of flights affected by data gaps')).toBeInTheDocument();
    expect((await screen.findAllByText('Change')).length).toEqual(2);
  });
});
