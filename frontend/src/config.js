// Базовый URL API. В продакшене (Docker) пустой — запросы идут на тот же origin,
// а nginx проксирует /api на бэкенд. В dev значение берётся из .env.development.
export const API_URL = process.env.REACT_APP_API_URL || "";
