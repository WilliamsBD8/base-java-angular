import { Convocation } from './convocation.model';
import { UserApp } from './user.model';

export type PetitionState = 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';

export interface Petition {
  id: number;
  convocation: Convocation;
  user: UserApp;
  state: PetitionState;
  createdAt: string;
  updatedAt: string;
}

export interface PetitionCreate {
  convocationId: number;
}

export interface PetitionUpdate {
  state: PetitionState;
}
