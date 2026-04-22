import { startTransition, useDeferredValue, useEffect, useState } from 'react'
import './App.css'
import {
  SECTION_OPTIONS,
  ORDER_STATUS_LABELS,
  apiRequest,
  buildQuery,
  emptyClientForm,
  emptyDocumentForm,
  emptyOrderForm,
  emptyTranslatorForm,
  toOptionalNumber,
} from './lib'
import {
  ClientsPanel,
  DocumentsPanel,
  OrdersPanel,
  TranslatorsPanel,
} from './panels'

function App() {
  const [activeSection, setActiveSection] = useState('orders')
  const [banner, setBanner] = useState(null)
  const [loadingReferenceData, setLoadingReferenceData] = useState(true)
  const [loadingOrders, setLoadingOrders] = useState(true)
  const [pendingAction, setPendingAction] = useState('')
  const [reloadToken, setReloadToken] = useState(0)

  const [clients, setClients] = useState([])
  const [translators, setTranslators] = useState([])
  const [languages, setLanguages] = useState([])
  const [documents, setDocuments] = useState([])
  const [orders, setOrders] = useState([])
  const [ordersPage, setOrdersPage] = useState({
    size: 12,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  })

  const [clientForm, setClientForm] = useState(emptyClientForm)
  const [documentForm, setDocumentForm] = useState(emptyDocumentForm)
  const [translatorForm, setTranslatorForm] = useState(emptyTranslatorForm)
  const [orderForm, setOrderForm] = useState(emptyOrderForm)

  const [editingClientId, setEditingClientId] = useState(null)
  const [editingDocumentId, setEditingDocumentId] = useState(null)
  const [editingTranslatorId, setEditingTranslatorId] = useState(null)
  const [editingOrderId, setEditingOrderId] = useState(null)

  const [clientQuery, setClientQuery] = useState('')
  const [translatorQuery, setTranslatorQuery] = useState('')
  const [documentQuery, setDocumentQuery] = useState('')
  const [orderQuery, setOrderQuery] = useState('')
  const [documentFilterOrderId, setDocumentFilterOrderId] = useState('')
  const [orderFilters, setOrderFilters] = useState({
    status: '',
    clientId: '',
    translatorId: '',
    page: 0,
    size: 12,
  })

  const deferredClientQuery = useDeferredValue(clientQuery)
  const deferredTranslatorQuery = useDeferredValue(translatorQuery)
  const deferredDocumentQuery = useDeferredValue(documentQuery)
  const deferredOrderQuery = useDeferredValue(orderQuery)

  const loading = loadingReferenceData || loadingOrders

  useEffect(() => {
    let cancelled = false

    async function loadReferenceData() {
      setLoadingReferenceData(true)
      try {
        const requests = [
          { path: '/api/clients', setter: setClients },
          { path: '/api/translators', setter: setTranslators },
          { path: '/api/languages', setter: setLanguages },
          { path: '/api/documents', setter: setDocuments },
        ]

        const results = await Promise.allSettled(
          requests.map(async ({ path, setter }) => {
            const nextData = await apiRequest(path)

            if (!cancelled) {
              setter(nextData ?? [])
            }
          }),
        )

        if (!cancelled) {
          const failedRequest = results.find((result) => result.status === 'rejected')

          if (failedRequest) {
            setBanner({ type: 'error', text: failedRequest.reason.message })
          }
        }
      } finally {
        if (!cancelled) {
          setLoadingReferenceData(false)
        }
      }
    }

    loadReferenceData()

    return () => {
      cancelled = true
    }
  }, [reloadToken])

  useEffect(() => {
    let cancelled = false

    async function loadOrders() {
      setLoadingOrders(true)

      try {
        const orderQueryString = buildQuery({
          status: orderFilters.status,
          clientId: orderFilters.clientId,
          translatorId: orderFilters.translatorId,
          page: orderFilters.page,
          size: orderFilters.size,
        })

        const nextOrders = await apiRequest(`/api/orders${orderQueryString}`)

        if (cancelled) {
          return
        }

        setOrders(nextOrders?.content ?? [])
        setOrdersPage(nextOrders?.page ?? { size: 12, number: 0, totalElements: 0, totalPages: 0 })
      } catch (error) {
        if (!cancelled) {
          setBanner({ type: 'error', text: error.message })
        }
      } finally {
        if (!cancelled) {
          setLoadingOrders(false)
        }
      }
    }

    loadOrders()

    return () => {
      cancelled = true
    }
  }, [orderFilters, reloadToken])

  function queueRefresh(message) {
    if (message) {
      setBanner({ type: 'success', text: message })
    }
    setReloadToken((current) => current + 1)
  }

  function getClientName(clientId) {
    const client = clients.find((item) => item.id === clientId)
    return client ? `${client.firstName} ${client.lastName}` : 'Клиент не выбран'
  }

  function getTranslatorName(translatorId) {
    const translator = translators.find((item) => item.id === translatorId)
    return translator ? `${translator.firstName} ${translator.lastName}` : 'Переводчик не выбран'
  }

  function getLanguageName(languageId) {
    const language = languages.find((item) => item.id === languageId)
    return language ? `${language.code.toUpperCase()} - ${language.name}` : 'Язык не выбран'
  }

  function getOrderTitle(orderId) {
    const order = orders.find((item) => item.id === orderId)
    return order ? order.title : `Заказ #${orderId}`
  }

  const getOrdersForClient = (clientId) => orders.filter((order) => order.clientId === clientId)
  const getOrdersForTranslator = (translatorId) =>
    orders.filter((order) => order.translatorId === translatorId)
  const getDocumentsForOrder = (orderId) =>
    documents.filter((document) => document.orderId === orderId)

  const visibleClients = clients.filter((client) =>
    `${client.firstName} ${client.lastName} ${client.email}`
      .toLowerCase()
      .includes(deferredClientQuery.trim().toLowerCase()),
  )

  const visibleTranslators = translators.filter((translator) => {
    const languageBlob = translator.languages
      .map((item) => `${item.code} ${item.name} ${item.proficiencyLevel}`)
      .join(' ')

    return `${translator.firstName} ${translator.lastName} ${translator.ratePerPage} ${languageBlob}`
      .toLowerCase()
      .includes(deferredTranslatorQuery.trim().toLowerCase())
  })

  const visibleOrders = orders.filter((order) =>
    `${order.title} ${getClientName(order.clientId)} ${getTranslatorName(order.translatorId)}`
      .toLowerCase()
      .includes(deferredOrderQuery.trim().toLowerCase()),
  )

  const visibleDocuments = documents.filter((document) => {
    const matchesOrder =
      documentFilterOrderId === '' || String(document.orderId ?? '') === documentFilterOrderId

    if (!matchesOrder) {
      return false
    }

    return `${document.type} ${document.pages} ${getOrderTitle(document.orderId)}`
      .toLowerCase()
      .includes(deferredDocumentQuery.trim().toLowerCase())
  })

  async function mutate(path, method, body, successMessage, onDone) {
    setPendingAction(method === 'DELETE' ? 'Удаляем запись...' : 'Сохраняем изменения...')
    setBanner(null)

    try {
      await apiRequest(path, {
        method,
        body: body ? JSON.stringify(body) : undefined,
      })
      onDone?.()
      queueRefresh(successMessage)
    } catch (error) {
      setBanner({ type: 'error', text: error.message })
    } finally {
      setPendingAction('')
    }
  }

  const currentOrderFilterSummary = [
    orderFilters.status ? ORDER_STATUS_LABELS[orderFilters.status] : null,
    orderFilters.clientId ? getClientName(Number(orderFilters.clientId)) : null,
    orderFilters.translatorId ? getTranslatorName(Number(orderFilters.translatorId)) : null,
  ].filter(Boolean)

  return (
    <div className="app-shell">
      <header className="hero-panel">
        <div className="hero-copy">
          <div className="eyebrow">Бюро переводов</div>
          <h1>Удобная работа с клиентами, заказами и документами</h1>
          <p>
            Здесь удобно вести клиентов, заказы, документы и переводчиков в одном
            месте. Нужную запись можно быстро найти, открыть и изменить без
            лишних шагов.
          </p>
          <div className="hero-actions">
            <button className="button primary" type="button" onClick={() => queueRefresh('Список обновлён.')} disabled={loading}>
              Обновить данные
            </button>
            <button className="button secondary" type="button" onClick={() => startTransition(() => setActiveSection('orders'))}>
              Перейти к заказам
            </button>
          </div>
          <div className="stats-grid">
            <div className="stat-card"><strong>{clients.length}</strong><span>клиентов в списке</span></div>
            <div className="stat-card"><strong>{translators.length}</strong><span>переводчиков в списке</span></div>
            <div className="stat-card"><strong>{ordersPage.totalElements}</strong><span>заказов найдено</span></div>
            <div className="stat-card"><strong>{documents.length}</strong><span>документов в списке</span></div>
          </div>
        </div>
      </header>

      {banner ? <div className={`notice ${banner.type}`}>{banner.text}</div> : null}

      <nav className="tabs" aria-label="Разделы сайта">
        {SECTION_OPTIONS.map((section) => (
          <button
            key={section.id}
            className={`tab-button ${activeSection === section.id ? 'active' : ''}`}
            type="button"
            onClick={() => startTransition(() => setActiveSection(section.id))}
          >
            {section.label}
          </button>
        ))}
      </nav>

      <section className="insights-grid">
        <article className="insight-card">
          <h2>Клиенты и их заказы</h2>
          <p>
            Сейчас {clients.filter((client) => getOrdersForClient(client.id).length > 0).length}{' '}
            клиентов уже связаны с заказами из текущего списка.
          </p>
        </article>
        <article className="insight-card">
          <h2>Языки переводчиков</h2>
          <p>
            Сейчас видно{' '}
            {translators.reduce((total, translator) => total + translator.languages.length, 0)}{' '}
            записей о языках, которыми владеют переводчики.
          </p>
        </article>
        <article className="insight-card">
          <h2>Поиск и отбор</h2>
          <p>
            {currentOrderFilterSummary.length > 0
              ? `Сейчас включены условия: ${currentOrderFilterSummary.join(', ')}.`
              : 'Сейчас показываются все заказы. Ниже можно сузить список.'}
          </p>
        </article>
      </section>

      <main className="section-panel">
        <div className="section-header">
          <div>
            <h2>{SECTION_OPTIONS.find((section) => section.id === activeSection)?.label}</h2>
            <p>{pendingAction || (loading ? 'Загружаем данные...' : 'Можно работать дальше.')}</p>
          </div>
          <div className="section-actions">
            {loading ? <div className="spinner-line">Загружаем данные</div> : null}
            {pendingAction ? <div className="spinner-line">{pendingAction}</div> : null}
            <button className="button ghost" type="button" onClick={() => queueRefresh('Данные обновлены.')} disabled={loading}>
              Обновить список
            </button>
          </div>
        </div>

        {activeSection === 'clients' ? (
          <ClientsPanel
            clientForm={clientForm}
            setClientForm={setClientForm}
            editingClientId={editingClientId}
            onSubmit={(event) => {
              event.preventDefault()
              mutate(editingClientId ? `/api/clients/${editingClientId}` : '/api/clients', editingClientId ? 'PUT' : 'POST', clientForm, editingClientId ? 'Данные клиента обновлены.' : 'Клиент добавлен.', () => {
                setEditingClientId(null)
                setClientForm(emptyClientForm())
              })
            }}
            onReset={() => {
              setEditingClientId(null)
              setClientForm(emptyClientForm())
            }}
            clientQuery={clientQuery}
            setClientQuery={setClientQuery}
            clients={visibleClients}
            getOrdersForClient={getOrdersForClient}
            onEdit={(client) => {
              setEditingClientId(client.id)
              setClientForm({ firstName: client.firstName, lastName: client.lastName, email: client.email })
            }}
            onDelete={(id) => window.confirm('Удалить эту запись?') && mutate(`/api/clients/${id}`, 'DELETE', null, 'Клиент удалён.')}
            pendingAction={pendingAction}
          />
        ) : null}

        {activeSection === 'translators' ? (
          <TranslatorsPanel
            translatorForm={translatorForm}
            setTranslatorForm={setTranslatorForm}
            editingTranslatorId={editingTranslatorId}
            onSubmit={(event) => {
              event.preventDefault()
              mutate(editingTranslatorId ? `/api/translators/${editingTranslatorId}` : '/api/translators', editingTranslatorId ? 'PUT' : 'POST', {
                firstName: translatorForm.firstName,
                lastName: translatorForm.lastName,
                ratePerPage: Number(translatorForm.ratePerPage),
                languages: translatorForm.languages.map((item) => ({ languageId: Number(item.languageId), proficiencyLevel: item.proficiencyLevel })),
              }, editingTranslatorId ? 'Данные переводчика обновлены.' : 'Переводчик добавлен.', () => {
                setEditingTranslatorId(null)
                setTranslatorForm(emptyTranslatorForm())
              })
            }}
            onReset={() => {
              setEditingTranslatorId(null)
              setTranslatorForm(emptyTranslatorForm())
            }}
            translatorQuery={translatorQuery}
            setTranslatorQuery={setTranslatorQuery}
            translators={visibleTranslators}
            languages={languages}
            getOrdersForTranslator={getOrdersForTranslator}
            onEdit={(translator) => {
              setEditingTranslatorId(translator.id)
              setTranslatorForm({
                firstName: translator.firstName,
                lastName: translator.lastName,
                ratePerPage: String(translator.ratePerPage),
                languages: translator.languages.length > 0
                  ? translator.languages.map((item) => ({
                      languageId: String(item.languageId),
                      proficiencyLevel: item.proficiencyLevel,
                    }))
                  : [{ languageId: '', proficiencyLevel: 'INTERMEDIATE' }],
              })
            }}
            onDelete={(id) => window.confirm('Удалить эту запись?') && mutate(`/api/translators/${id}`, 'DELETE', null, 'Переводчик удалён.')}
            onLanguageChange={(index, field, value) =>
              setTranslatorForm((current) => ({
                ...current,
                languages: current.languages.map((item, currentIndex) => currentIndex === index ? { ...item, [field]: value } : item),
              }))
            }
            onLanguageAdd={() =>
              setTranslatorForm((current) => ({
                ...current,
                languages: [...current.languages, { languageId: '', proficiencyLevel: 'INTERMEDIATE' }],
              }))
            }
            onLanguageRemove={(index) =>
              setTranslatorForm((current) => ({
                ...current,
                languages: current.languages.length === 1 ? current.languages : current.languages.filter((_, currentIndex) => currentIndex !== index),
              }))
            }
            pendingAction={pendingAction}
          />
        ) : null}

        {activeSection === 'orders' ? (
          <OrdersPanel
            orderForm={orderForm}
            setOrderForm={setOrderForm}
            editingOrderId={editingOrderId}
            onSubmit={(event) => {
              event.preventDefault()
              mutate(editingOrderId ? `/api/orders/${editingOrderId}` : '/api/orders', editingOrderId ? 'PUT' : 'POST', {
                title: orderForm.title,
                status: orderForm.status,
                sourceLanguageId: toOptionalNumber(orderForm.sourceLanguageId),
                targetLanguageId: toOptionalNumber(orderForm.targetLanguageId),
                clientId: toOptionalNumber(orderForm.clientId),
                translatorId: toOptionalNumber(orderForm.translatorId),
                documentIds: orderForm.documentIds.map(Number),
              }, editingOrderId ? 'Данные заказа обновлены.' : 'Заказ добавлен.', () => {
                setEditingOrderId(null)
                setOrderForm(emptyOrderForm())
              })
            }}
            onReset={() => {
              setEditingOrderId(null)
              setOrderForm(emptyOrderForm())
            }}
            documents={documents}
            ordersPage={ordersPage}
            orderQuery={orderQuery}
            setOrderQuery={setOrderQuery}
            orderFilters={orderFilters}
            setOrderFilters={setOrderFilters}
            clients={clients}
            translators={translators}
            languages={languages}
            visibleOrders={visibleOrders}
            getClientName={getClientName}
            getTranslatorName={getTranslatorName}
            getLanguageName={getLanguageName}
            getDocumentsForOrder={getDocumentsForOrder}
            onEdit={(order) => {
              setEditingOrderId(order.id)
              setOrderForm({
                title: order.title,
                status: order.status,
                sourceLanguageId: order.sourceLanguageId ? String(order.sourceLanguageId) : '',
                targetLanguageId: order.targetLanguageId ? String(order.targetLanguageId) : '',
                clientId: order.clientId ? String(order.clientId) : '',
                translatorId: order.translatorId ? String(order.translatorId) : '',
                documentIds: (order.documentIds ?? []).map(Number),
              })
            }}
            onDelete={(id) => window.confirm('Удалить эту запись?') && mutate(`/api/orders/${id}`, 'DELETE', null, 'Заказ удалён.')}
            onDocumentToggle={(documentId) =>
              setOrderForm((current) => ({
                ...current,
                documentIds: current.documentIds.includes(documentId)
                  ? current.documentIds.filter((item) => item !== documentId)
                  : [...current.documentIds, documentId],
              }))
            }
            pendingAction={pendingAction}
            loading={loading}
          />
        ) : null}

        {activeSection === 'documents' ? (
          <DocumentsPanel
            documentForm={documentForm}
            setDocumentForm={setDocumentForm}
            editingDocumentId={editingDocumentId}
            onSubmit={(event) => {
              event.preventDefault()
              mutate(editingDocumentId ? `/api/documents/${editingDocumentId}` : '/api/documents', editingDocumentId ? 'PUT' : 'POST', {
                type: documentForm.type,
                pages: Number(documentForm.pages),
                orderId: toOptionalNumber(documentForm.orderId),
              }, editingDocumentId ? 'Данные документа обновлены.' : 'Документ добавлен.', () => {
                setEditingDocumentId(null)
                setDocumentForm(emptyDocumentForm())
              })
            }}
            onReset={() => {
              setEditingDocumentId(null)
              setDocumentForm(emptyDocumentForm())
            }}
            documentQuery={documentQuery}
            setDocumentQuery={setDocumentQuery}
            documentFilterOrderId={documentFilterOrderId}
            setDocumentFilterOrderId={setDocumentFilterOrderId}
            documents={visibleDocuments}
            orders={orders}
            getOrderTitle={getOrderTitle}
            onEdit={(document) => {
              setEditingDocumentId(document.id)
              setDocumentForm({
                type: document.type,
                pages: String(document.pages),
                orderId: document.orderId ? String(document.orderId) : '',
              })
            }}
            onDelete={(id) => window.confirm('Удалить эту запись?') && mutate(`/api/documents/${id}`, 'DELETE', null, 'Документ удалён.')}
            pendingAction={pendingAction}
          />
        ) : null}
      </main>
    </div>
  )
}

export default App

