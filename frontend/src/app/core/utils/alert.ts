import Swal from 'sweetalert2';

export async function showSuccess(message: string): Promise<void> {
  await Swal.fire({
    icon: 'success',
    title: 'Éxito',
    text: message,
    confirmButtonText: 'Aceptar'
  });
}

export async function showSuccessToast(message: string): Promise<void> {
  await Swal.fire({
    icon: 'success',
    title: 'Éxito',
    text: message,
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true
  });
}

export async function showError(message: string): Promise<void> {
  await Swal.fire({
    icon: 'error',
    title: 'Error',
    text: message,
    confirmButtonText: 'Aceptar'
  });
}

export async function confirmAction(title: string, text: string): Promise<boolean> {
  const result = await Swal.fire({
    icon: 'warning',
    title,
    text,
    showCancelButton: true,
    confirmButtonText: 'Sí, continuar',
    cancelButtonText: 'Cancelar'
  });
  return result.isConfirmed;
}

export function showWarning(message: string): void {
  Swal.fire({
    icon: 'warning',
    title: 'Advertencia',
    text: message,
    confirmButtonText: 'Aceptar',
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true,
    background: '#f8f9fa',
    color: '#343a40'
  });
}

export function getErrorMessage(error: unknown): string {
  const err = error as { error?: { message?: string; errors?: Record<string, string> }; message?: string };
  if (err?.error?.errors && Object.keys(err.error.errors).length > 0) {
    return Object.values(err.error.errors).join('\n');
  }
  return err?.error?.message || err?.message || 'Ocurrió un error inesperado';
}
