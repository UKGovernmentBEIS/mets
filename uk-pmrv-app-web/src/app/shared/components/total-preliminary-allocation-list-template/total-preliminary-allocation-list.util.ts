import { PreliminaryAllocation } from 'pmrv-api';

export function getTotalAllocationsPerYear(
  allocations: PreliminaryAllocation[],
): { year: number; allowances: number }[] {
  const totalAllocations: { year: number; allowances: number }[] = [];

  allocations.forEach((allocation) => {
    const exists = totalAllocations.findIndex((all) => all.year === allocation.year);

    if (exists >= 0) {
      totalAllocations[exists].allowances = totalAllocations[exists].allowances + allocation.allowances;
    } else {
      totalAllocations.push({ year: allocation.year, allowances: allocation.allowances });
    }
  });

  return totalAllocations.sort((a, b) => a.year - b.year);
}
