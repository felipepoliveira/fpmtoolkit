export function toQueryParams(data: object): string {
  const params = new URLSearchParams();

  Object.entries(data).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      params.append(key, encodeURIComponent(String(value)));
    }
  });

  return `?${params.toString()}`;
}