import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiPage, ApiResponse } from '../models/api.model';
import { Convocation, ConvocationCreate, ConvocationUpdate } from '../models/convocation.model';

@Injectable({ providedIn: 'root' })
export class ConvocationService {
  private readonly baseUrl = `${environment.apiUrl}/convocations`;

  constructor(private readonly http: HttpClient) {}

  getAll(page = 0, size = 10): Observable<ApiResponse<ApiPage<Convocation>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<ApiPage<Convocation>>>(`${this.baseUrl}/all`, { params });
  }

  getById(id: number): Observable<ApiResponse<Convocation>> {
    return this.http.get<ApiResponse<Convocation>>(`${this.baseUrl}/${id}`);
  }

  create(request: ConvocationCreate): Observable<ApiResponse<Convocation>> {
    return this.http.post<ApiResponse<Convocation>>(`${this.baseUrl}/create`, request);
  }

  update(id: number, request: ConvocationUpdate): Observable<ApiResponse<Convocation>> {
    return this.http.put<ApiResponse<Convocation>>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }

  publish(id: number): Observable<ApiResponse<Convocation>> {
    return this.http.put<ApiResponse<Convocation>>(`${this.baseUrl}/${id}/publish`, {});
  }

  close(id: number): Observable<ApiResponse<Convocation>> {
    return this.http.put<ApiResponse<Convocation>>(`${this.baseUrl}/${id}/close`, {});
  }
}
