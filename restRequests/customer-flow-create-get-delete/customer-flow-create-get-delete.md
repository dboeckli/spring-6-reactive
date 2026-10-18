# customer Flow: createCustomer → getCustomer → deleteCustomer

Datei: `restRequests/customer-flow-create-get-delete/customer-flow-create-get-delete.http`

## Ziel

Einen neuen Kunden anlegen, die erzeugte Ressource per GET prüfen und anschließend wieder löschen.

## Ablauf (Services)

```mermaid
sequenceDiagram
    actor C as HTTP Client (IntelliJ)
    participant API as spring-6-reactive
    participant DB as H2 (R2DBC)

    C->>API: POST /api/v2/customer
    API->>DB: INSERT customer
    API-->>C: 201 (Location: .../customer/{customerId})

    C->>API: GET /api/v2/customer/{customerId}
    API->>DB: SELECT customer
    API-->>C: 200 (created customer)

    C->>API: DELETE /api/v2/customer/{customerId}
    API->>DB: DELETE customer
    API-->>C: 204 (No Content)
```

## Requests (in dieser Reihenfolge)

1. `createCustomer` – legt den Kunden an und liest `customerId` aus dem `Location`-Header.
2. `getCustomerById` – prüft, dass der Kunde existiert und `customerName` = `Flow Customer` ist.
3. `deleteCustomer` – löscht den zuvor erzeugten Kunden (hinterlässt keine Testdaten).

## Benötigte Globals

|       Global        |         gesetzt von         |             genutzt von             |
|---------------------|-----------------------------|-------------------------------------|
| `customerId`        | `createCustomer`            | `getCustomerById`, `deleteCustomer` |
| `traceId`, `spanId` | `../scripts/traceparent.js` | alle Requests                       |

## Asserts

- `createCustomer` → `201` mit `Location`-Header.
- `getCustomerById` → `200`, `customerName` = `Flow Customer`.
- `deleteCustomer` → `204`.

## Hinweise

- Basis-URL ist `{{application-port}}` (Profil `local` = 8082, `k8s` = 30082).
- Der `Location`-Header wird als `http://localhost:8080/api/v2/customer/{id}` erzeugt; die ID wird über das letzte
  Pfadsegment extrahiert (der Port im Header ist für die ID-Extraktion irrelevant).
- `customerName` ist Pflicht (`@NotBlank`, 3–255 Zeichen).
- Der Flow ist wiederholbar: Der angelegte Kunde wird am Ende gelöscht.
- Alle Requests benötigen ein Bearer-Token (`auth-id` aus `http-client.env.json`); dafür muss der Auth-Server auf
  Port `9000` laufen (siehe `docker compose up`).

