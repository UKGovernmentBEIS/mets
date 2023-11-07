import { RecommendedImprovements } from 'pmrv-api';

export function recommendedImprovementsWizardComplete(recommendedImprovements: RecommendedImprovements): boolean {
  return recommendedImprovements && recommendedImprovementsComplete(recommendedImprovements);
}

function recommendedImprovementsComplete(recommendedImprovements: RecommendedImprovements): boolean {
  return (
    recommendedImprovements?.areThereRecommendedImprovements === false ||
    (recommendedImprovements?.areThereRecommendedImprovements &&
      recommendedImprovements?.recommendedImprovements?.length > 0)
  );
}
