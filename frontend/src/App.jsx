import {
  startTransition,
  useDeferredValue,
  useEffect,
  useEffectEvent,
  useState,
} from 'react'
import './App.css'
import {
  SECTION_OPTIONS,
  apiRequest,
  buildQuery,
  emptyClientForm,
  emptyDocumentForm,
  emptyLanguageForm,
  emptyOrderForm,
  emptyTranslatorForm,
  toOptionalNumber,
} from './lib'
import {
  ClientsPanel,
  ClientForm,
  DocumentsPanel,
  DocumentForm,
  LanguagesPanel,
  LanguageForm,
  OrdersPanel,
  OrderForm,
  TranslatorsPanel,
  TranslatorForm,
} from './panels'

const SECTION_CREATE_LABELS = {
  clients: 'Добавить клиента',
  translators: 'Добавить переводчика',
  languages: 'Добавить язык',
  orders: 'Добавить заказ',
  documents: 'Добавить документ',
}

const SECTION_EDIT_LABELS = {
  clients: 'Редактирование клиента',
  translators: 'Редактирование переводчика',
  languages: 'Редактирование языка',
  orders: 'Редактирование заказа',
  documents: 'Редактирование документа',
}

function FormDialog({ title, onClose, children }) {
  return (
    <div className="modal-backdrop" role="presentation" onClick={onClose}>
      <div
        className="modal-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="dialog-title"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="modal-header">
          <h3 id="dialog-title">{title}</h3>
          <button
            className="icon-button modal-close"
            type="button"
            onClick={onClose}
            aria-label="Закрыть окно"
          >
            Закрыть
          </button>
        </div>
        <div className="modal-body">{children}</div>
      </div>
    </div>
  )
}

function App() {
  const [activeSection, setActiveSection] = useState('orders')
  const [banner, setBanner] = useState(null)
  const [loadingReferenceData, setLoadingReferenceData] = useState(true)
  const [loadingOrders, setLoadingOrders] = useState(true)
  const [pendingAction, setPendingAction] = useState('')
  const [reloadToken, setReloadToken] = useState(0)
  const [dialogSection, setDialogSection] = useState(null)

  const [clients, setClients] = useState([])
  const [pagedClients, setPagedClients] = useState([])
  const [translators, setTranslators] = useState([])
  const [pagedTranslators, setPagedTranslators] = useState([])
  const [languages, setLanguages] = useState([])
  const [pagedLanguages, setPagedLanguages] = useState([])
  const [documents, setDocuments] = useState([])
  const [pagedDocuments, setPagedDocuments] = useState([])
  const [orderOptions, setOrderOptions] = useState([])
  const [orders, setOrders] = useState([])
  const [clientsPage, setClientsPage] = useState({
    size: 8,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  })
  const [translatorsPage, setTranslatorsPage] = useState({
    size: 8,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  })
  const [documentsPage, setDocumentsPage] = useState({
    size: 8,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  })
  const [ordersPage, setOrdersPage] = useState({
    size: 12,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  })
  const [languagePage, setLanguagePage] = useState({
    size: 8,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  })

  const [clientForm, setClientForm] = useState(emptyClientForm)
  const [documentForm, setDocumentForm] = useState(emptyDocumentForm)
  const [languageForm, setLanguageForm] = useState(emptyLanguageForm)
  const [translatorForm, setTranslatorForm] = useState(emptyTranslatorForm)
  const [orderForm, setOrderForm] = useState(emptyOrderForm)

  const [editingClientId, setEditingClientId] = useState(null)
  const [editingDocumentId, setEditingDocumentId] = useState(null)
  const [editingLanguageId, setEditingLanguageId] = useState(null)
  const [editingTranslatorId, setEditingTranslatorId] = useState(null)
  const [editingOrderId, setEditingOrderId] = useState(null)

  const [clientQuery, setClientQuery] = useState('')
  const [translatorQuery, setTranslatorQuery] = useState('')
  const [documentQuery, setDocumentQuery] = useState('')
  const [languageQuery, setLanguageQuery] = useState('')
  const [orderQuery, setOrderQuery] = useState('')
  const [documentFilterOrderId, setDocumentFilterOrderId] = useState('')
  const [orderFilters, setOrderFilters] = useState({
    status: '',
    clientId: '',
    translatorId: '',
    page: 0,
    size: 12,
  })
  const [clientFilters, setClientFilters] = useState({
    page: 0,
    size: 8,
  })
  const [translatorFilters, setTranslatorFilters] = useState({
    page: 0,
    size: 8,
  })
  const [documentFilters, setDocumentFilters] = useState({
    page: 0,
    size: 8,
  })
  const [languageFilters, setLanguageFilters] = useState({
    page: 0,
    size: 8,
  })
  const [loadingClients, setLoadingClients] = useState(true)
  const [loadingTranslators, setLoadingTranslators] = useState(true)
  const [loadingDocuments, setLoadingDocuments] = useState(true)
  const [loadingLanguages, setLoadingLanguages] = useState(true)

  const deferredClientQuery = useDeferredValue(clientQuery)
  const deferredTranslatorQuery = useDeferredValue(translatorQuery)
  const deferredDocumentQuery = useDeferredValue(documentQuery)
  const deferredLanguageQuery = useDeferredValue(languageQuery)
  const deferredOrderQuery = useDeferredValue(orderQuery)

  const loading =
    loadingReferenceData ||
    loadingClients ||
    loadingTranslators ||
    loadingDocuments ||
    loadingLanguages ||
    loadingOrders

  useEffect(() => {
    let cancelled = false

    async function loadReferenceData() {
      setLoadingReferenceData(true)
      try {
        const requests = [
          {
            path: `/api/clients${buildQuery({ size: 500, sort: 'id,asc' })}`,
            setter: setClients,
          },
          {
            path: `/api/translators${buildQuery({ size: 500, sort: 'id,asc' })}`,
            setter: setTranslators,
          },
          {
            path: `/api/languages${buildQuery({ size: 500, sort: 'id,asc' })}`,
            setter: setLanguages,
          },
          {
            path: `/api/documents${buildQuery({ size: 500, sort: 'id,asc' })}`,
            setter: setDocuments,
          },
          {
            path: `/api/orders${buildQuery({ size: 500, sort: 'id,asc' })}`,
            setter: setOrderOptions,
          },
        ]

        const results = await Promise.allSettled(
          requests.map(async ({ path, setter }) => {
            const nextData = await apiRequest(path)

            if (!cancelled) {
              setter(Array.isArray(nextData) ? nextData : nextData?.content ?? [])
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

    async function loadClientsPage() {
      setLoadingClients(true)

      try {
        const clientQueryString = buildQuery({
          page: clientFilters.page,
          size: clientFilters.size,
        })

        const nextClients = await apiRequest(`/api/clients${clientQueryString}`)

        if (cancelled) {
          return
        }

        const nextPage = nextClients?.page ?? {
          size: 8,
          number: 0,
          totalElements: 0,
          totalPages: 0,
        }

        if (
          clientFilters.page > 0 &&
          (nextPage.totalPages === 0 || nextPage.number >= nextPage.totalPages)
        ) {
          setClientFilters((current) => ({
            ...current,
            page: Math.max(nextPage.totalPages - 1, 0),
          }))
          return
        }

        setPagedClients(nextClients?.content ?? [])
        setClientsPage(nextPage)
      } catch (error) {
        if (!cancelled) {
          setBanner({ type: 'error', text: error.message })
        }
      } finally {
        if (!cancelled) {
          setLoadingClients(false)
        }
      }
    }

    loadClientsPage()

    return () => {
      cancelled = true
    }
  }, [clientFilters, reloadToken])

  useEffect(() => {
    let cancelled = false

    async function loadTranslatorsPage() {
      setLoadingTranslators(true)

      try {
        const translatorQueryString = buildQuery({
          page: translatorFilters.page,
          size: translatorFilters.size,
        })

        const nextTranslators = await apiRequest(`/api/translators${translatorQueryString}`)

        if (cancelled) {
          return
        }

        const nextPage = nextTranslators?.page ?? {
          size: 8,
          number: 0,
          totalElements: 0,
          totalPages: 0,
        }

        if (
          translatorFilters.page > 0 &&
          (nextPage.totalPages === 0 || nextPage.number >= nextPage.totalPages)
        ) {
          setTranslatorFilters((current) => ({
            ...current,
            page: Math.max(nextPage.totalPages - 1, 0),
          }))
          return
        }

        setPagedTranslators(nextTranslators?.content ?? [])
        setTranslatorsPage(nextPage)
      } catch (error) {
        if (!cancelled) {
          setBanner({ type: 'error', text: error.message })
        }
      } finally {
        if (!cancelled) {
          setLoadingTranslators(false)
        }
      }
    }

    loadTranslatorsPage()

    return () => {
      cancelled = true
    }
  }, [translatorFilters, reloadToken])

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

        const nextPage = nextOrders?.page ?? {
          size: 12,
          number: 0,
          totalElements: 0,
          totalPages: 0,
        }

        if (
          orderFilters.page > 0 &&
          (nextPage.totalPages === 0 || nextPage.number >= nextPage.totalPages)
        ) {
          setOrderFilters((current) => ({
            ...current,
            page: Math.max(nextPage.totalPages - 1, 0),
          }))
          return
        }

        setOrders(nextOrders?.content ?? [])
        setOrdersPage(nextPage)
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

  useEffect(() => {
    let cancelled = false

    async function loadDocumentsPage() {
      setLoadingDocuments(true)

      try {
        const documentQueryString = buildQuery({
          orderId: documentFilterOrderId,
          page: documentFilters.page,
          size: documentFilters.size,
        })

        const nextDocuments = await apiRequest(`/api/documents${documentQueryString}`)

        if (cancelled) {
          return
        }

        const nextPage = nextDocuments?.page ?? {
          size: 8,
          number: 0,
          totalElements: 0,
          totalPages: 0,
        }

        if (
          documentFilters.page > 0 &&
          (nextPage.totalPages === 0 || nextPage.number >= nextPage.totalPages)
        ) {
          setDocumentFilters((current) => ({
            ...current,
            page: Math.max(nextPage.totalPages - 1, 0),
          }))
          return
        }

        setPagedDocuments(nextDocuments?.content ?? [])
        setDocumentsPage(nextPage)
      } catch (error) {
        if (!cancelled) {
          setBanner({ type: 'error', text: error.message })
        }
      } finally {
        if (!cancelled) {
          setLoadingDocuments(false)
        }
      }
    }

    loadDocumentsPage()

    return () => {
      cancelled = true
    }
  }, [documentFilterOrderId, documentFilters, reloadToken])

  useEffect(() => {
    let cancelled = false

    async function loadLanguagesPage() {
      setLoadingLanguages(true)

      try {
        const languageQueryString = buildQuery({
          page: languageFilters.page,
          size: languageFilters.size,
        })

        const nextLanguages = await apiRequest(`/api/languages${languageQueryString}`)

        if (cancelled) {
          return
        }

        const nextPage = nextLanguages?.page ?? {
          size: 8,
          number: 0,
          totalElements: 0,
          totalPages: 0,
        }

        if (
          languageFilters.page > 0 &&
          (nextPage.totalPages === 0 || nextPage.number >= nextPage.totalPages)
        ) {
          setLanguageFilters((current) => ({
            ...current,
            page: Math.max(nextPage.totalPages - 1, 0),
          }))
          return
        }

        setPagedLanguages(nextLanguages?.content ?? [])
        setLanguagePage(nextPage)
      } catch (error) {
        if (!cancelled) {
          setBanner({ type: 'error', text: error.message })
        }
      } finally {
        if (!cancelled) {
          setLoadingLanguages(false)
        }
      }
    }

    loadLanguagesPage()

    return () => {
      cancelled = true
    }
  }, [languageFilters, reloadToken])

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
    const order = orderOptions.find((item) => item.id === orderId)
    return order ? order.title : `Заказ #${orderId}`
  }

  const getOrdersForClient = (clientId) =>
    orderOptions.filter((order) => order.clientId === clientId)
  const getOrdersForTranslator = (translatorId) =>
    orderOptions.filter((order) => order.translatorId === translatorId)
  const getDocumentsForOrder = (orderId) =>
    documents.filter((document) => document.orderId === orderId)

  function resetSectionState(section) {
    if (section === 'clients') {
      setEditingClientId(null)
      setClientForm(emptyClientForm())
      return
    }

    if (section === 'translators') {
      setEditingTranslatorId(null)
      setTranslatorForm(emptyTranslatorForm())
      return
    }

    if (section === 'languages') {
      setEditingLanguageId(null)
      setLanguageForm(emptyLanguageForm())
      return
    }

    if (section === 'orders') {
      setEditingOrderId(null)
      setOrderForm(emptyOrderForm())
      return
    }

    if (section === 'documents') {
      setEditingDocumentId(null)
      setDocumentForm(emptyDocumentForm())
    }
  }

  function openCreateDialog(section) {
    resetSectionState(section)
    setDialogSection(section)
  }

  function closeDialog() {
    if (dialogSection) {
      resetSectionState(dialogSection)
    }

    setDialogSection(null)
  }

  function openClientEditor(client) {
    setEditingClientId(client.id)
    setClientForm({
      firstName: client.firstName,
      lastName: client.lastName,
      email: client.email,
    })
    setDialogSection('clients')
  }

  function openTranslatorEditor(translator) {
    setEditingTranslatorId(translator.id)
    setTranslatorForm({
      firstName: translator.firstName,
      lastName: translator.lastName,
      ratePerPage: String(translator.ratePerPage),
      languages:
        translator.languages.length > 0
          ? translator.languages.map((item) => ({
              languageId: String(item.languageId),
              proficiencyLevel: item.proficiencyLevel,
            }))
          : [{ languageId: '', proficiencyLevel: 'INTERMEDIATE' }],
    })
    setDialogSection('translators')
  }

  function openLanguageEditor(language) {
    setEditingLanguageId(language.id)
    setLanguageForm({ code: language.code, name: language.name })
    setDialogSection('languages')
  }

  function openOrderEditor(order) {
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
    setDialogSection('orders')
  }

  function openDocumentEditor(document) {
    setEditingDocumentId(document.id)
    setDocumentForm({
      type: document.type,
      pages: String(document.pages),
      orderId: document.orderId ? String(document.orderId) : '',
    })
    setDialogSection('documents')
  }

  const closeDialogFromEffect = useEffectEvent(() => {
    if (dialogSection) {
      resetSectionState(dialogSection)
    }

    setDialogSection(null)
  })

  useEffect(() => {
    if (!dialogSection) {
      return undefined
    }

    const previousOverflow = document.body.style.overflow
    const handleKeyDown = (event) => {
      if (event.key === 'Escape') {
        closeDialogFromEffect()
      }
    }

    document.body.style.overflow = 'hidden'
    window.addEventListener('keydown', handleKeyDown)

    return () => {
      document.body.style.overflow = previousOverflow
      window.removeEventListener('keydown', handleKeyDown)
    }
  }, [dialogSection])

  const visibleClients = pagedClients.filter((client) =>
    `${client.firstName} ${client.lastName} ${client.email}`
      .toLowerCase()
      .includes(deferredClientQuery.trim().toLowerCase()),
  )

  const visibleTranslators = pagedTranslators.filter((translator) => {
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

  const visibleLanguages = pagedLanguages.filter((language) =>
    `${language.code} ${language.name}`
      .toLowerCase()
      .includes(deferredLanguageQuery.trim().toLowerCase()),
  )

  const visibleDocuments = pagedDocuments.filter((document) => {
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

  const currentSectionLabel =
    SECTION_OPTIONS.find((section) => section.id === activeSection)?.label ?? ''
  const dialogTitle = dialogSection
    ? ((dialogSection === 'clients' && editingClientId !== null) ||
        (dialogSection === 'translators' && editingTranslatorId !== null) ||
        (dialogSection === 'languages' && editingLanguageId !== null) ||
        (dialogSection === 'orders' && editingOrderId !== null) ||
        (dialogSection === 'documents' && editingDocumentId !== null)
        ? SECTION_EDIT_LABELS[dialogSection]
        : SECTION_CREATE_LABELS[dialogSection])
    : ''

  return (
    <div className="app-shell">
      <header className="hero-panel">
        <h1 className="page-title">Бюро переводов</h1>
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

      <main className="section-panel">
        <div className="section-header">
          <div>
            <h2>{currentSectionLabel}</h2>
            <p>
              {pendingAction ||
                (loading
                  ? 'Обновляем рабочие данные...'
                  : 'Данные синхронизированы, можно продолжать работу.')}
            </p>
          </div>
          <div className="section-actions">
            {loading ? <div className="spinner-line">Загружаем данные</div> : null}
            {pendingAction ? <div className="spinner-line">{pendingAction}</div> : null}
            <button
              className="button primary"
              type="button"
              onClick={() => openCreateDialog(activeSection)}
              disabled={loading}
            >
              {SECTION_CREATE_LABELS[activeSection]}
            </button>
            <button
              className="button ghost"
              type="button"
              onClick={() => queueRefresh('Данные обновлены.')}
              disabled={loading}
            >
              Обновить список
            </button>
          </div>
        </div>

        {activeSection === 'clients' ? (
          <ClientsPanel
            clientQuery={clientQuery}
            setClientQuery={setClientQuery}
            clients={visibleClients}
            clientsPage={clientsPage}
            setClientFilters={setClientFilters}
            getOrdersForClient={getOrdersForClient}
            onEdit={openClientEditor}
            onDelete={(id) => window.confirm('Удалить эту запись?') && mutate(`/api/clients/${id}`, 'DELETE', null, 'Клиент удалён.')}
            loading={loadingClients}
          />
        ) : null}

        {activeSection === 'translators' ? (
          <TranslatorsPanel
            translatorQuery={translatorQuery}
            setTranslatorQuery={setTranslatorQuery}
            translators={visibleTranslators}
            translatorsPage={translatorsPage}
            setTranslatorFilters={setTranslatorFilters}
            getOrdersForTranslator={getOrdersForTranslator}
            onEdit={openTranslatorEditor}
            onDelete={(id) => window.confirm('Удалить эту запись?') && mutate(`/api/translators/${id}`, 'DELETE', null, 'Переводчик удалён.')}
            loading={loadingTranslators}
          />
        ) : null}

        {activeSection === 'languages' ? (
          <LanguagesPanel
            languageQuery={languageQuery}
            setLanguageQuery={setLanguageQuery}
            languages={visibleLanguages}
            languagePage={languagePage}
            setLanguageFilters={setLanguageFilters}
            onEdit={openLanguageEditor}
            onDelete={(id) =>
              window.confirm('Удалить эту запись?') &&
              mutate(`/api/languages/${id}`, 'DELETE', null, 'Язык удалён.', () => {
                if (languagePage.number > 0 && visibleLanguages.length === 1) {
                  setLanguageFilters((current) => ({
                    ...current,
                    page: Math.max(current.page - 1, 0),
                  }))
                }
              })
            }
            loading={loadingLanguages}
          />
        ) : null}

        {activeSection === 'orders' ? (
          <OrdersPanel
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
            onEdit={openOrderEditor}
            onDelete={(id) => window.confirm('Удалить эту запись?') && mutate(`/api/orders/${id}`, 'DELETE', null, 'Заказ удалён.')}
            loading={loading}
          />
        ) : null}

        {activeSection === 'documents' ? (
          <DocumentsPanel
            documentQuery={documentQuery}
            setDocumentQuery={setDocumentQuery}
            documentFilterOrderId={documentFilterOrderId}
            setDocumentFilterOrderId={setDocumentFilterOrderId}
            documents={visibleDocuments}
            orders={orderOptions}
            getOrderTitle={getOrderTitle}
            documentsPage={documentsPage}
            setDocumentFilters={setDocumentFilters}
            onEdit={openDocumentEditor}
            onDelete={(id) => window.confirm('Удалить эту запись?') && mutate(`/api/documents/${id}`, 'DELETE', null, 'Документ удалён.')}
            loading={loadingDocuments}
          />
        ) : null}
      </main>

      {dialogSection ? (
        <FormDialog title={dialogTitle} onClose={closeDialog}>
          {dialogSection === 'clients' ? (
            <ClientForm
              clientForm={clientForm}
              setClientForm={setClientForm}
              editingClientId={editingClientId}
              onSubmit={(event) => {
                event.preventDefault()
                mutate(
                  editingClientId ? `/api/clients/${editingClientId}` : '/api/clients',
                  editingClientId ? 'PUT' : 'POST',
                  clientForm,
                  editingClientId ? 'Данные клиента обновлены.' : 'Клиент добавлен.',
                  closeDialog,
                )
              }}
              onCancel={closeDialog}
              pendingAction={pendingAction}
            />
          ) : null}

          {dialogSection === 'translators' ? (
            <TranslatorForm
              translatorForm={translatorForm}
              setTranslatorForm={setTranslatorForm}
              editingTranslatorId={editingTranslatorId}
              onSubmit={(event) => {
                event.preventDefault()
                mutate(
                  editingTranslatorId
                    ? `/api/translators/${editingTranslatorId}`
                    : '/api/translators',
                  editingTranslatorId ? 'PUT' : 'POST',
                  {
                    firstName: translatorForm.firstName,
                    lastName: translatorForm.lastName,
                    ratePerPage: Number(translatorForm.ratePerPage),
                    languages: translatorForm.languages.map((item) => ({
                      languageId: Number(item.languageId),
                      proficiencyLevel: item.proficiencyLevel,
                    })),
                  },
                  editingTranslatorId
                    ? 'Данные переводчика обновлены.'
                    : 'Переводчик добавлен.',
                  closeDialog,
                )
              }}
              onCancel={closeDialog}
              onLanguageChange={(index, field, value) =>
                setTranslatorForm((current) => ({
                  ...current,
                  languages: current.languages.map((item, currentIndex) =>
                    currentIndex === index ? { ...item, [field]: value } : item,
                  ),
                }))
              }
              onLanguageAdd={() =>
                setTranslatorForm((current) => ({
                  ...current,
                  languages: [
                    ...current.languages,
                    { languageId: '', proficiencyLevel: 'INTERMEDIATE' },
                  ],
                }))
              }
              onLanguageRemove={(index) =>
                setTranslatorForm((current) => ({
                  ...current,
                  languages:
                    current.languages.length === 1
                      ? current.languages
                      : current.languages.filter(
                          (_, currentIndex) => currentIndex !== index,
                        ),
                }))
              }
              languages={languages}
              pendingAction={pendingAction}
            />
          ) : null}

          {dialogSection === 'languages' ? (
            <LanguageForm
              languageForm={languageForm}
              setLanguageForm={setLanguageForm}
              editingLanguageId={editingLanguageId}
              onSubmit={(event) => {
                event.preventDefault()
                mutate(
                  editingLanguageId ? `/api/languages/${editingLanguageId}` : '/api/languages',
                  editingLanguageId ? 'PUT' : 'POST',
                  languageForm,
                  editingLanguageId ? 'Данные языка обновлены.' : 'Язык добавлен.',
                  closeDialog,
                )
              }}
              onCancel={closeDialog}
              pendingAction={pendingAction}
            />
          ) : null}

          {dialogSection === 'orders' ? (
            <OrderForm
              orderForm={orderForm}
              setOrderForm={setOrderForm}
              editingOrderId={editingOrderId}
              onSubmit={(event) => {
                event.preventDefault()
                mutate(
                  editingOrderId ? `/api/orders/${editingOrderId}` : '/api/orders',
                  editingOrderId ? 'PUT' : 'POST',
                  {
                    title: orderForm.title,
                    status: orderForm.status,
                    sourceLanguageId: toOptionalNumber(orderForm.sourceLanguageId),
                    targetLanguageId: toOptionalNumber(orderForm.targetLanguageId),
                    clientId: toOptionalNumber(orderForm.clientId),
                    translatorId: toOptionalNumber(orderForm.translatorId),
                    documentIds: orderForm.documentIds.map(Number),
                  },
                  editingOrderId ? 'Данные заказа обновлены.' : 'Заказ добавлен.',
                  closeDialog,
                )
              }}
              onCancel={closeDialog}
              documents={documents}
              clients={clients}
              translators={translators}
              languages={languages}
              onDocumentToggle={(documentId) =>
                setOrderForm((current) => ({
                  ...current,
                  documentIds: current.documentIds.includes(documentId)
                    ? current.documentIds.filter((item) => item !== documentId)
                    : [...current.documentIds, documentId],
                }))
              }
              pendingAction={pendingAction}
            />
          ) : null}

          {dialogSection === 'documents' ? (
            <DocumentForm
              documentForm={documentForm}
              setDocumentForm={setDocumentForm}
              editingDocumentId={editingDocumentId}
              onSubmit={(event) => {
                event.preventDefault()
                mutate(
                  editingDocumentId ? `/api/documents/${editingDocumentId}` : '/api/documents',
                  editingDocumentId ? 'PUT' : 'POST',
                  {
                    type: documentForm.type,
                    pages: Number(documentForm.pages),
                    orderId: toOptionalNumber(documentForm.orderId),
                  },
                  editingDocumentId ? 'Данные документа обновлены.' : 'Документ добавлен.',
                  closeDialog,
                )
              }}
              onCancel={closeDialog}
              orders={orderOptions}
              pendingAction={pendingAction}
            />
          ) : null}
        </FormDialog>
      ) : null}
    </div>
  )
}

export default App

