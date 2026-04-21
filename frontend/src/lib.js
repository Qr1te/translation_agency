export const SECTION_OPTIONS = [
  { id: 'orders', label: 'Заказы' },
  { id: 'clients', label: 'Клиенты' },
  { id: 'translators', label: 'Переводчики' },
  { id: 'documents', label: 'Документы' },
]

export const ORDER_STATUS_LABELS = {
  NEW: 'Новый',
  IN_PROGRESS: 'В работе',
  COMPLETED: 'Завершён',
  CANCELLED: 'Отменён',
}

export const PROFICIENCY_LABELS = {
  BEGINNER: 'Начальный',
  INTERMEDIATE: 'Средний',
  ADVANCED: 'Продвинутый',
  NATIVE: 'Родной',
}

export const orderStatusOptions = Object.entries(ORDER_STATUS_LABELS)
export const proficiencyOptions = Object.entries(PROFICIENCY_LABELS)

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '')
  .trim()
  .replace(/\/+$/, '')

const decimalFormatter = new Intl.NumberFormat('en-US', {
  maximumFractionDigits: 2,
})

const FIELD_LABELS = {
  firstName: 'Имя',
  lastName: 'Фамилия',
  email: 'Электронная почта',
  ratePerPage: 'Цена за страницу',
  languageId: 'Язык',
  proficiencyLevel: 'Уровень владения',
  sourceLanguageId: 'Язык оригинала',
  targetLanguageId: 'Язык перевода',
  clientId: 'Клиент',
  translatorId: 'Переводчик',
  documentIds: 'Документы',
  title: 'Название',
  status: 'Статус',
  pages: 'Страницы',
  type: 'Тип документа',
  orderId: 'Заказ',
  languages: 'Языки',
}

export function emptyClientForm() {
  return { firstName: '', lastName: '', email: '' }
}

export function emptyDocumentForm() {
  return { type: '', pages: '', orderId: '' }
}

export function emptyTranslatorForm() {
  return {
    firstName: '',
    lastName: '',
    ratePerPage: '',
    languages: [{ languageId: '', proficiencyLevel: 'INTERMEDIATE' }],
  }
}

export function emptyOrderForm() {
  return {
    title: '',
    status: 'NEW',
    sourceLanguageId: '',
    targetLanguageId: '',
    clientId: '',
    translatorId: '',
    documentIds: [],
  }
}

export function buildQuery(params) {
  const searchParams = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value !== '' && value !== null && value !== undefined) {
      searchParams.set(key, value)
    }
  })

  const serialized = searchParams.toString()
  return serialized ? `?${serialized}` : ''
}

export function toOptionalNumber(value) {
  return value === '' ? null : Number(value)
}

function normalizeValidationErrors(payload) {
  if (!payload?.validationErrors?.length) {
    return []
  }

  return payload.validationErrors.map(({ field, message }) => {
    const fieldLabel = FIELD_LABELS[field] ?? field
    return `${fieldLabel}: ${translateErrorText(message)}`
  })
}

function translateErrorText(message) {
  if (!message || typeof message !== 'string') {
    return 'Произошла ошибка'
  }

  return [
    ['Validation failed', 'Проверьте заполнение полей'],
    ['is required', 'обязательно для заполнения'],
    ['must be valid', 'заполнено неверно'],
    ['must be greater than zero', 'должно быть больше нуля'],
    ['must be positive', 'должно быть больше нуля'],
    ['must not be empty', 'не должно быть пустым'],
    ['must be at most', 'должно быть не длиннее'],
    ['characters', 'символов'],
    ['Request failed with status', 'Ошибка запроса, код'],
  ].reduce((text, [from, to]) => text.replaceAll(from, to), message)
}

function resolveApiUrl(path) {
  if (/^https?:\/\//.test(path)) {
    return path
  }

  if (!API_BASE_URL) {
    return path
  }

  return `${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`
}

export async function apiRequest(path, options = {}) {
  const response = await fetch(resolveApiUrl(path), {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {}),
    },
    ...options,
  })

  const rawBody = await response.text()
  let payload = null

  if (rawBody) {
    try {
      payload = JSON.parse(rawBody)
    } catch {
      payload = rawBody
    }
  }

  if (!response.ok) {
    const validationMessages = normalizeValidationErrors(payload)
    const message =
      validationMessages.join(' | ') ||
      translateErrorText(payload?.message) ||
      translateErrorText(payload?.error) ||
      (typeof payload === 'string' ? payload : '') ||
      `Ошибка запроса, код ${response.status}`

    throw new Error(message)
  }

  return payload
}

export function formatRate(rate) {
  if (rate === null || rate === undefined || rate === '') {
    return 'не указана'
  }

  return `${decimalFormatter.format(Number(rate))} за страницу`
}

export function statusTone(status) {
  if (status === 'COMPLETED') {
    return 'success'
  }

  if (status === 'CANCELLED') {
    return 'danger'
  }

  if (status === 'IN_PROGRESS') {
    return 'warning'
  }

  return ''
}
