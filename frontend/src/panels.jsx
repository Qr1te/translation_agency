import {
  ORDER_STATUS_LABELS,
  PROFICIENCY_LABELS,
  formatRate,
  orderStatusOptions,
  proficiencyOptions,
  statusTone,
} from './lib'

export function ClientsPanel(props) {
  const {
    clientForm,
    setClientForm,
    editingClientId,
    onSubmit,
    onReset,
    clientQuery,
    setClientQuery,
    clients,
    getOrdersForClient,
    onEdit,
    onDelete,
    pendingAction,
  } = props

  return (
    <div className="section-layout">
      <div className="form-card">
        <form className="form-stack" onSubmit={onSubmit}>
          <div className="field-grid">
            <label className="field">
              <span>Имя</span>
              <input
                value={clientForm.firstName}
                onChange={(event) =>
                  setClientForm((current) => ({ ...current, firstName: event.target.value }))
                }
                placeholder="Анна"
              />
            </label>
            <label className="field">
              <span>Фамилия</span>
              <input
                value={clientForm.lastName}
                onChange={(event) =>
                  setClientForm((current) => ({ ...current, lastName: event.target.value }))
                }
                placeholder="Иванова"
              />
            </label>
          </div>
          <label className="field">
            <span>Электронная почта</span>
            <input
              type="email"
              value={clientForm.email}
              onChange={(event) =>
                setClientForm((current) => ({ ...current, email: event.target.value }))
              }
              placeholder="anna@example.com"
            />
          </label>
          <div className="form-actions">
            <button className="button primary" type="submit" disabled={!!pendingAction}>
              {editingClientId ? 'Сохранить изменения' : 'Добавить клиента'}
            </button>
            <button className="button ghost" type="button" onClick={onReset}>
              Очистить поля
            </button>
          </div>
        </form>
      </div>

      <div className="list-card">
        <div className="toolbar">
          <label className="field" style={{ flex: 1 }}>
            <span>Поиск клиента</span>
            <input
              value={clientQuery}
              onChange={(event) => setClientQuery(event.target.value)}
              placeholder="Введите имя или почту"
            />
          </label>
        </div>

        <div className="cards-grid">
          {clients.length === 0 ? (
            <div className="empty-state">По вашему запросу ничего не найдено.</div>
          ) : (
            clients.map((client) => {
              const relatedOrders = getOrdersForClient(client.id)

              return (
                <article key={client.id} className="card">
                  <div className="card-header">
                    <h3>
                      {client.firstName} {client.lastName}
                    </h3>
                    <span className="badge">{relatedOrders.length} заказов</span>
                  </div>
                  <div className="muted">{client.email}</div>
                  <div className="relationship-list">
                    {relatedOrders.length > 0 ? (
                      relatedOrders.map((order) => (
                        <span key={order.id} className="relationship-item">
                          {order.title}
                        </span>
                      ))
                    ) : (
                      <span className="mini-note">Для этого клиента пока нет заказов в текущем списке.</span>
                    )}
                  </div>
                  <div className="card-actions">
                    <button className="icon-button" type="button" onClick={() => onEdit(client)}>
                      Изменить
                    </button>
                    <button
                      className="icon-button danger"
                      type="button"
                      onClick={() => onDelete(client.id)}
                    >
                      Удалить
                    </button>
                  </div>
                </article>
              )
            })
          )}
        </div>
      </div>
    </div>
  )
}

export function TranslatorsPanel(props) {
  const {
    translatorForm,
    setTranslatorForm,
    editingTranslatorId,
    onSubmit,
    onReset,
    translatorQuery,
    setTranslatorQuery,
    translators,
    languages,
    getOrdersForTranslator,
    onEdit,
    onDelete,
    onLanguageChange,
    onLanguageAdd,
    onLanguageRemove,
    pendingAction,
  } = props

  return (
    <div className="section-layout">
      <div className="form-card">
        <form className="form-stack" onSubmit={onSubmit}>
          <div className="field-grid">
            <label className="field">
              <span>Имя</span>
              <input
                value={translatorForm.firstName}
                onChange={(event) =>
                  setTranslatorForm((current) => ({ ...current, firstName: event.target.value }))
                }
                placeholder="Иван"
              />
            </label>
            <label className="field">
              <span>Фамилия</span>
              <input
                value={translatorForm.lastName}
                onChange={(event) =>
                  setTranslatorForm((current) => ({ ...current, lastName: event.target.value }))
                }
                placeholder="Петров"
              />
            </label>
          </div>
          <label className="field">
            <span>Цена за страницу</span>
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={translatorForm.ratePerPage}
              onChange={(event) =>
                setTranslatorForm((current) => ({ ...current, ratePerPage: event.target.value }))
              }
              placeholder="15.50"
            />
          </label>
          <div className="helper-block">
            Выберите языки, с которыми работает переводчик, и укажите уровень
            владения каждым языком.
          </div>
          {translatorForm.languages.map((item, index) => (
            <div key={`${item.languageId}-${index}`} className="field-grid">
              <label className="field">
                <span>Язык</span>
                <select
                  value={item.languageId}
                  onChange={(event) => onLanguageChange(index, 'languageId', event.target.value)}
                >
                  <option value="">Выберите язык</option>
                  {languages.map((language) => (
                    <option key={language.id} value={language.id}>
                      {language.code.toUpperCase()} - {language.name}
                    </option>
                  ))}
                </select>
              </label>
              <label className="field">
                <span>Уровень владения</span>
                <select
                  value={item.proficiencyLevel}
                  onChange={(event) =>
                    onLanguageChange(index, 'proficiencyLevel', event.target.value)
                  }
                >
                  {proficiencyOptions.map(([value, label]) => (
                    <option key={value} value={value}>
                      {label}
                    </option>
                  ))}
                </select>
              </label>
              <div className="form-actions">
                <button className="button ghost" type="button" onClick={onLanguageAdd}>
                  Добавить язык
                </button>
                <button
                  className="button secondary"
                  type="button"
                  onClick={() => onLanguageRemove(index)}
                >
                  Убрать строку
                </button>
              </div>
            </div>
          ))}
          <div className="form-actions">
            <button className="button primary" type="submit" disabled={!!pendingAction}>
              {editingTranslatorId ? 'Сохранить изменения' : 'Добавить переводчика'}
            </button>
            <button className="button ghost" type="button" onClick={onReset}>
              Очистить поля
            </button>
          </div>
        </form>
      </div>

      <div className="list-card">
        <div className="toolbar">
          <label className="field" style={{ flex: 1 }}>
            <span>Поиск переводчика</span>
            <input
              value={translatorQuery}
              onChange={(event) => setTranslatorQuery(event.target.value)}
              placeholder="Введите имя, язык или уровень"
            />
          </label>
        </div>

        <div className="cards-grid">
          {translators.length === 0 ? (
            <div className="empty-state">По вашему запросу ничего не найдено.</div>
          ) : (
            translators.map((translator) => (
              <article key={translator.id} className="card">
                <div className="card-header">
                  <h3>
                    {translator.firstName} {translator.lastName}
                  </h3>
                  <span className="badge">{formatRate(translator.ratePerPage)}</span>
                </div>
                <div className="tag-list">
                  {translator.languages.map((language) => (
                    <span key={`${translator.id}-${language.languageId}`} className="tag">
                      {language.code.toUpperCase()} - {language.name} -{' '}
                      {PROFICIENCY_LABELS[language.proficiencyLevel]}
                    </span>
                  ))}
                </div>
                <div className="mini-note">
                  Заказов в текущем списке: {getOrdersForTranslator(translator.id).length}
                </div>
                <div className="card-actions">
                  <button
                    className="icon-button"
                    type="button"
                    onClick={() => onEdit(translator)}
                  >
                    Изменить
                  </button>
                  <button
                    className="icon-button danger"
                    type="button"
                    onClick={() => onDelete(translator.id)}
                  >
                    Удалить
                  </button>
                </div>
              </article>
            ))
          )}
        </div>
      </div>
    </div>
  )
}

export function OrdersPanel(props) {
  const {
    orderForm,
    setOrderForm,
    editingOrderId,
    onSubmit,
    onReset,
    documents,
    ordersPage,
    orderQuery,
    setOrderQuery,
    orderFilters,
    setOrderFilters,
    clients,
    translators,
    languages,
    visibleOrders,
    getClientName,
    getTranslatorName,
    getLanguageName,
    getDocumentsForOrder,
    onEdit,
    onDelete,
    onDocumentToggle,
    pendingAction,
    loading,
  } = props

  return (
    <div className="section-layout">
      <div className="form-card">
        <form className="form-stack" onSubmit={onSubmit}>
          <label className="field">
            <span>Название заказа</span>
            <input
              value={orderForm.title}
              onChange={(event) =>
                setOrderForm((current) => ({ ...current, title: event.target.value }))
              }
              placeholder="Перевод договора"
            />
          </label>
          <div className="field-grid">
            <label className="field">
              <span>Статус</span>
              <select
                value={orderForm.status}
                onChange={(event) =>
                  setOrderForm((current) => ({ ...current, status: event.target.value }))
                }
              >
                {orderStatusOptions.map(([value, label]) => (
                  <option key={value} value={value}>
                    {label}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              <span>Клиент</span>
              <select
                value={orderForm.clientId}
                onChange={(event) =>
                  setOrderForm((current) => ({ ...current, clientId: event.target.value }))
                }
              >
                <option value="">Выберите клиента</option>
                {clients.map((client) => (
                  <option key={client.id} value={client.id}>
                    {client.firstName} {client.lastName}
                  </option>
                ))}
              </select>
            </label>
          </div>
          <div className="field-grid">
            <label className="field">
              <span>Переводчик</span>
              <select
                value={orderForm.translatorId}
                onChange={(event) =>
                  setOrderForm((current) => ({ ...current, translatorId: event.target.value }))
                }
              >
                <option value="">Выберите переводчика</option>
                {translators.map((translator) => (
                  <option key={translator.id} value={translator.id}>
                    {translator.firstName} {translator.lastName}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              <span>Язык оригинала</span>
              <select
                value={orderForm.sourceLanguageId}
                onChange={(event) =>
                  setOrderForm((current) => ({
                    ...current,
                    sourceLanguageId: event.target.value,
                  }))
                }
              >
                <option value="">Выберите язык</option>
                {languages.map((language) => (
                  <option key={language.id} value={language.id}>
                    {language.code.toUpperCase()} - {language.name}
                  </option>
                ))}
              </select>
            </label>
          </div>
          <label className="field">
            <span>Язык перевода</span>
            <select
              value={orderForm.targetLanguageId}
              onChange={(event) =>
                setOrderForm((current) => ({
                  ...current,
                  targetLanguageId: event.target.value,
                }))
              }
            >
              <option value="">Выберите язык</option>
              {languages.map((language) => (
                <option key={language.id} value={language.id}>
                  {language.code.toUpperCase()} - {language.name}
                </option>
              ))}
            </select>
          </label>
          <div className="field">
            <span>Прикрепить документы</span>
            <div className="checkbox-grid">
              {documents.length > 0 ? (
                documents.map((document) => (
                  <label key={document.id} className="checkbox-option">
                    <input
                      type="checkbox"
                      checked={orderForm.documentIds.includes(document.id)}
                      onChange={() => onDocumentToggle(document.id)}
                    />
                    <span>
                      {document.type} - {document.pages} стр.
                    </span>
                  </label>
                ))
              ) : (
                <div className="helper-block">
                  Сначала добавьте документ, а потом привяжите его к заказу.
                </div>
              )}
            </div>
          </div>
          <div className="form-actions">
            <button className="button primary" type="submit" disabled={!!pendingAction}>
              {editingOrderId ? 'Сохранить изменения' : 'Добавить заказ'}
            </button>
            <button className="button ghost" type="button" onClick={onReset}>
              Очистить поля
            </button>
          </div>
        </form>
      </div>

      <div className="list-card">
        <div className="toolbar">
          <label className="field" style={{ flex: 1 }}>
            <span>Поиск по списку</span>
            <input
              value={orderQuery}
              onChange={(event) => setOrderQuery(event.target.value)}
              placeholder="Введите название, клиента или переводчика"
            />
          </label>
        </div>
        <div className="pill-grid">
          <label className="field">
            <span>Статус</span>
            <select
              value={orderFilters.status}
              onChange={(event) =>
                setOrderFilters((current) => ({
                  ...current,
                  status: event.target.value,
                  page: 0,
                }))
              }
            >
              <option value="">Все статусы</option>
              {orderStatusOptions.map(([value, label]) => (
                <option key={value} value={value}>
                  {label}
                </option>
              ))}
            </select>
          </label>
          <label className="field">
            <span>Клиент</span>
            <select
              value={orderFilters.clientId}
              onChange={(event) =>
                setOrderFilters((current) => ({
                  ...current,
                  clientId: event.target.value,
                  page: 0,
                }))
              }
            >
              <option value="">Все клиенты</option>
              {clients.map((client) => (
                <option key={client.id} value={client.id}>
                  {client.firstName} {client.lastName}
                </option>
              ))}
            </select>
          </label>
          <label className="field">
            <span>Переводчик</span>
            <select
              value={orderFilters.translatorId}
              onChange={(event) =>
                setOrderFilters((current) => ({
                  ...current,
                  translatorId: event.target.value,
                  page: 0,
                }))
              }
            >
              <option value="">Все переводчики</option>
              {translators.map((translator) => (
                <option key={translator.id} value={translator.id}>
                  {translator.firstName} {translator.lastName}
                </option>
              ))}
            </select>
          </label>
          <div className="form-actions">
            <button
              className="button secondary"
              type="button"
              onClick={() =>
                setOrderFilters({
                  status: '',
                  clientId: '',
                  translatorId: '',
                  page: 0,
                  size: 12,
                })
              }
            >
              Сбросить условия
            </button>
          </div>
        </div>

        <div className="pagination">
          <div className="mini-note">
            Страница {ordersPage.number + 1} из {Math.max(ordersPage.totalPages, 1)} -{' '}
            всего записей: {ordersPage.totalElements}
          </div>
          <div className="card-actions">
            <button
              className="icon-button"
              type="button"
              disabled={ordersPage.number === 0 || loading}
              onClick={() =>
                setOrderFilters((current) => ({
                  ...current,
                  page: Math.max(current.page - 1, 0),
                }))
              }
            >
              Назад
            </button>
            <button
              className="icon-button"
              type="button"
              disabled={
                ordersPage.totalPages === 0 ||
                ordersPage.number >= ordersPage.totalPages - 1 ||
                loading
              }
              onClick={() =>
                setOrderFilters((current) => ({
                  ...current,
                  page: current.page + 1,
                }))
              }
            >
              Далее
            </button>
          </div>
        </div>

        <div className="cards-grid">
          {visibleOrders.length === 0 ? (
            <div className="empty-state">По выбранным условиям ничего не найдено.</div>
          ) : (
            visibleOrders.map((order) => (
              <article key={order.id} className="card">
                <div className="card-header">
                  <h3>{order.title}</h3>
                  <span className={`badge ${statusTone(order.status)}`}>
                    {ORDER_STATUS_LABELS[order.status]}
                  </span>
                </div>
                <div className="inline-meta">
                  <span className="tag">{getClientName(order.clientId)}</span>
                  <span className="tag">{getTranslatorName(order.translatorId)}</span>
                </div>
                <div className="mini-note">
                  {getLanguageName(order.sourceLanguageId)} {'->'}{' '}
                  {getLanguageName(order.targetLanguageId)}
                </div>
                <div className="relationship-list">
                  {getDocumentsForOrder(order.id).length > 0 ? (
                    getDocumentsForOrder(order.id).map((document) => (
                      <span key={document.id} className="relationship-item">
                        {document.type} - {document.pages} стр.
                      </span>
                    ))
                  ) : (
                    <span className="mini-note">Документы к этому заказу пока не добавлены.</span>
                  )}
                </div>
                <div className="card-actions">
                  <button className="icon-button" type="button" onClick={() => onEdit(order)}>
                    Изменить
                  </button>
                  <button
                    className="icon-button danger"
                    type="button"
                    onClick={() => onDelete(order.id)}
                  >
                    Удалить
                  </button>
                </div>
              </article>
            ))
          )}
        </div>
      </div>
    </div>
  )
}

export function DocumentsPanel(props) {
  const {
    documentForm,
    setDocumentForm,
    editingDocumentId,
    onSubmit,
    onReset,
    documentQuery,
    setDocumentQuery,
    documentFilterOrderId,
    setDocumentFilterOrderId,
    documents,
    orders,
    getOrderTitle,
    onEdit,
    onDelete,
    pendingAction,
  } = props

  return (
    <div className="section-layout">
      <div className="form-card">
        <form className="form-stack" onSubmit={onSubmit}>
          <label className="field">
            <span>Название документа</span>
            <input
              value={documentForm.type}
              onChange={(event) =>
                setDocumentForm((current) => ({ ...current, type: event.target.value }))
              }
              placeholder="Паспорт"
            />
          </label>
          <div className="field-grid">
            <label className="field">
              <span>Количество страниц</span>
              <input
                type="number"
                min="1"
                value={documentForm.pages}
                onChange={(event) =>
                  setDocumentForm((current) => ({ ...current, pages: event.target.value }))
                }
                placeholder="12"
              />
            </label>
            <label className="field">
              <span>Связанный заказ</span>
              <select
                value={documentForm.orderId}
                onChange={(event) =>
                  setDocumentForm((current) => ({ ...current, orderId: event.target.value }))
                }
              >
                <option value="">Без заказа</option>
                {orders.map((order) => (
                  <option key={order.id} value={order.id}>
                    {order.title}
                  </option>
                ))}
              </select>
            </label>
          </div>
          <div className="form-actions">
            <button className="button primary" type="submit" disabled={!!pendingAction}>
              {editingDocumentId ? 'Сохранить изменения' : 'Добавить документ'}
            </button>
            <button className="button ghost" type="button" onClick={onReset}>
              Очистить поля
            </button>
          </div>
        </form>
      </div>

      <div className="list-card">
        <div className="toolbar">
          <label className="field" style={{ flex: 1 }}>
            <span>Поиск документа</span>
            <input
              value={documentQuery}
              onChange={(event) => setDocumentQuery(event.target.value)}
              placeholder="Введите название документа или заказа"
            />
          </label>
          <label className="field" style={{ minWidth: '220px' }}>
            <span>Показать по заказу</span>
            <select
              value={documentFilterOrderId}
              onChange={(event) => setDocumentFilterOrderId(event.target.value)}
            >
              <option value="">Все заказы</option>
              {orders.map((order) => (
                <option key={order.id} value={order.id}>
                  {order.title}
                </option>
              ))}
            </select>
          </label>
        </div>

        <div className="cards-grid">
          {documents.length === 0 ? (
            <div className="empty-state">По вашему запросу ничего не найдено.</div>
          ) : (
            documents.map((document) => (
              <article key={document.id} className="card">
                <div className="card-header">
                  <h3>{document.type}</h3>
                  <span className="badge">{document.pages} стр.</span>
                </div>
                <div className="muted">
                  {document.orderId
                    ? `Связан с заказом: ${getOrderTitle(document.orderId)}`
                    : 'Заказ пока не выбран'}
                </div>
                <div className="card-actions">
                  <button className="icon-button" type="button" onClick={() => onEdit(document)}>
                    Изменить
                  </button>
                  <button
                    className="icon-button danger"
                    type="button"
                    onClick={() => onDelete(document.id)}
                  >
                    Удалить
                  </button>
                </div>
              </article>
            ))
          )}
        </div>
      </div>
    </div>
  )
}

