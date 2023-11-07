import { FormControl, FormGroup } from '@angular/forms';

import { EmpDataGapsCorsia } from 'pmrv-api';

export type DataGapsFormModel = FormGroup<{ [key in keyof EmpDataGapsCorsia]: FormControl<EmpDataGapsCorsia[key]> }>;
