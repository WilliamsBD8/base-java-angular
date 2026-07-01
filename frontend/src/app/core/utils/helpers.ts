export function hasRole(roles: string[], role: string): boolean {
  return roles.some((r) => r === `ROLE_${role}` || r === role);
}

export function hasAnyRole(roles: string[], ...checkRoles: string[]): boolean {
  return checkRoles.some((role) => hasRole(roles, role));
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) return '-';
  return new Date(value).toLocaleString('es-CO', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

export function toLocalDateTimeInput(value: string | null | undefined): string {
  if (!value) return '';
  const date = new Date(value);
  const offset = date.getTimezoneOffset();
  const local = new Date(date.getTime() - offset * 60_000);
  return local.toISOString().slice(0, 16);
}

export function fromLocalDateTimeInput(value: string): string {
  return `${value}:00`;
}
