import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiPage, ApiResponse } from '../models/api.model';
import { Petition, PetitionCreate, PetitionUpdate } from '../models/petition.model';

@Injectable({ providedIn: 'root' })
export class PetitionService {
  private readonly baseUrl = `${environment.apiUrl}/petitions`;

  constructor(private readonly http: HttpClient) {}

  getAll(page = 0, size = 10): Observable<ApiResponse<ApiPage<Petition>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<ApiPage<Petition>>>(`${this.baseUrl}/all`, { params });
  }

  getById(id: number): Observable<ApiResponse<Petition>> {
    return this.http.get<ApiResponse<Petition>>(`${this.baseUrl}/${id}`);
  }

  create(request: PetitionCreate): Observable<ApiResponse<Petition>> {
    return this.http.post<ApiResponse<Petition>>(`${this.baseUrl}/create`, request);
  }

  update(id: number, request: PetitionUpdate): Observable<ApiResponse<Petition>> {
    return this.http.put<ApiResponse<Petition>>(`${this.baseUrl}/${id}`, request);
  }
}
