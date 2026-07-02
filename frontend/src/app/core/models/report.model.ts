export interface ConvocationCategoryReport {
  category: string;
  count: number;
  countDraft: number;
  countPublished: number;
  countClosed: number;
}

export interface PetitionConvocationReport {
  convocationName: string;
  countPetitions: number;
  countPetitionsPending: number;
  countPetitionsAccepted: number;
  countPetitionsRejected: number;
}

export interface PetitionStateReport {
  state: string;
  countPetitions: number;
}

export type ReportTab = 'categories' | 'petitions-convocations' | 'petitions-states';

export type ReportViewMode = 'table' | 'chart';
