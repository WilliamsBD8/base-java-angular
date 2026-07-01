import { Category } from './category.model';
import { Petition } from './petition.model';
import { UserApp } from './user.model';

export type ConvocationState = 'BORRADOR' | 'PUBLICADA' | 'CERRADA';

export interface Convocation {
  id: number;
  name: string;
  description: string;
  initialDate: string;
  finalDate: string;
  quota: number;
  state: ConvocationState;
  categories: Category[];
  petitions: Petition[];
  createdBy: UserApp;
  createdAt: string;
  updatedAt: string;
}

export interface ConvocationCreate {
  name: string;
  description: string;
  initialDate: string;
  finalDate: string;
  quota: number;
  categories: number[];
}

export interface ConvocationUpdate {
  name: string;
  description: string;
  initialDate: string;
  finalDate: string;
  quota: number;
  categories: number[];
}
