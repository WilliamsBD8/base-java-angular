import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.model';
import {
  ConvocationCategoryReport,
  PetitionConvocationReport,
  PetitionStateReport
} from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly baseUrl = `${environment.apiUrl}/reports`;

  constructor(private readonly http: HttpClient) {}

  getConvocationsByCategory(): Observable<ApiResponse<ConvocationCategoryReport[]>> {
    return this.http.get<ApiResponse<ConvocationCategoryReport[]>>(
      `${this.baseUrl}/convocations-categories`
    );
  }

  getPetitionsByConvocation(): Observable<ApiResponse<PetitionConvocationReport[]>> {
    return this.http.get<ApiResponse<PetitionConvocationReport[]>>(
      `${this.baseUrl}/petitions-convocations`
    );
  }

  getPetitionStates(): Observable<ApiResponse<PetitionStateReport[]>> {
    return this.http.get<ApiResponse<PetitionStateReport[]>>(
      `${this.baseUrl}/petitions-states`
    );
  }
}
