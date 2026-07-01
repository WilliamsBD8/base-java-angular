import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ConvocationService } from '../../../core/services/convocation.service';
import { CategoryService } from '../../../core/services/category.service';
import { Category } from '../../../core/models/category.model';
import { fromLocalDateTimeInput, toLocalDateTimeInput } from '../../../core/utils/helpers';
import { getErrorMessage, showError, showSuccess } from '../../../core/utils/alert';

@Component({
  selector: 'app-convocation-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './convocation-form.component.html'
})
export class ConvocationFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly convocationService = inject(ConvocationService);
  private readonly categoryService = inject(CategoryService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly categories = signal<Category[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly isEdit = signal(false);
  private convocationId: number | null = null;

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: ['', Validators.required],
    initialDate: ['', Validators.required],
    finalDate: ['', Validators.required],
    quota: [1, [Validators.required, Validators.min(1)]],
    categories: [[] as number[], Validators.required]
  });

  ngOnInit(): void {
    this.loadCategories();

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit.set(true);
      this.convocationId = Number(id);
      this.loadConvocation(this.convocationId);
    }
  }

  private loadCategories(): void {
    this.categoryService.getAll(0, 100).subscribe({
      next: (response) => this.categories.set(response.data.content),
      error: async (error) => await showError(getErrorMessage(error))
    });
  }

  private loadConvocation(id: number): void {
    this.loading.set(true);
    this.convocationService.getById(id).subscribe({
      next: (response) => {
        const convocation = response.data;
        this.form.patchValue({
          name: convocation.name,
          description: convocation.description,
          initialDate: toLocalDateTimeInput(convocation.initialDate),
          finalDate: toLocalDateTimeInput(convocation.finalDate),
          quota: convocation.quota,
          categories: convocation.categories.map((item) => item.id)
        });
        this.loading.set(false);
      },
      error: async (error) => {
        this.loading.set(false);
        await showError(getErrorMessage(error));
        this.router.navigate(['/convocations']);
      }
    });
  }

  toggleCategory(categoryId: number, checked: boolean): void {
    const current = [...this.form.controls.categories.value];
    if (checked) {
      if (!current.includes(categoryId)) current.push(categoryId);
    } else {
      const index = current.indexOf(categoryId);
      if (index >= 0) current.splice(index, 1);
    }
    this.form.controls.categories.setValue(current);
    this.form.controls.categories.markAsTouched();
  }

  isCategorySelected(categoryId: number): boolean {
    return this.form.controls.categories.value.includes(categoryId);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload = {
      name: raw.name,
      description: raw.description,
      initialDate: fromLocalDateTimeInput(raw.initialDate),
      finalDate: fromLocalDateTimeInput(raw.finalDate),
      quota: raw.quota,
      categories: raw.categories
    };

    this.saving.set(true);
    const request$ = this.isEdit() && this.convocationId
      ? this.convocationService.update(this.convocationId, payload)
      : this.convocationService.create(payload);

    request$.subscribe({
      next: async (response) => {
        this.saving.set(false);
        await showSuccess(response.message);
        this.router.navigate(['/convocations']);
      },
      error: async (error) => {
        this.saving.set(false);
        await showError(getErrorMessage(error));
      }
    });
  }
}
