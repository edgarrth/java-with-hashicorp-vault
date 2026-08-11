# Secure Java Applications with Vault

PoC de microservicio REST en Java, Spring Boot y arquitectura hexagonal, Secret Management, KV Secrets Engine, Spring Boot + Vault, credenciales dinámicas PostgreSQL, PKI, Transit Engine, auditoría, segmentación y operación básica.

## Caso de negocio DDD

Dominio: **payment processing**. El microservicio autoriza pagos, cifra el PAN de tarjeta con Vault Transit, guarda solo tarjeta enmascarada + ciphertext y persiste usando credenciales dinámicas generadas por Vault para PostgreSQL.

## Mapeo del temario a la PoC

| Sesión | Tema | Implementación |
|---|---|---|
| 01 | Secret Management & Vault | Docker de Vault, token root dev, policies y token de aplicación |
| 02 | KV Secrets Engine | `secret/payment-processing/dev` y endpoint `/security/v1/vault-labs/kv` |
| 03 | Spring Boot Integration | Adaptador `VaultSecretManagerAdapter` consumiendo Vault desde Spring Boot |
| 04 | Dynamic Database Credentials | Vault Database Engine emite usuarios temporales para PostgreSQL |
| 05 | PKI | Endpoint para emitir certificado PEM desde PKI Secrets Engine |
| 06 | Transit Engine | Endpoints encrypt/decrypt y cifrado real del PAN al crear pagos |
| 07 | Security & Operations | Policy mínima, auditoría file audit y script de backup dev |
| 08 | Workshop | Integración completa microservicio + Vault + PostgreSQL |
| 09 | Evaluación final | Solución funcional exportable a IntelliJ IDEA |

## Estructura

```text
infraestructura/
  docker-compose.yml
  Dockerfile
  postgres/init.sql
  vault/policies/payment-service.hcl
  vault/scripts/setup-vault.sh
  vault/scripts/backup-vault-dev.sh
  requests/*.http
src/main/java/com/edgar/vaultpoc/
  domain/        # Entidades, value objects y puertos
  application/   # Casos de uso
  infrastructure/# Adaptadores REST, Vault y PostgreSQL
src/main/resources/
  application.yml
  properties.yml
```

## Levantar infraestructura

```bash
cd infraestructura
docker compose up -d

docker compose ps

docker exec -it vault-poc sh /vault/scripts/setup-vault.sh
```

Obtén el token de aplicación:

```bash
docker exec vault-poc cat /vault/file/payment-service.token
```

## Ejecutar


Configura variables de entorno:

```bash
VAULT_ADDR=http://localhost:8200
VAULT_TOKEN=<token generado o root para laboratorio>
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
```

4. Ejecuta `SecureJavaVaultPocApplication`.

## Ejecutar por consola

```bash
mvn clean package
VAULT_ADDR=http://localhost:8200 VAULT_TOKEN=root java -jar target/secure-java-vault-poc-1.0.0.jar
```

## Endpoints principales

Crear pago:

```http
POST /payments/v1/payments
{
  "merchantId": "mrc-001",
  "amount": 120.50,
  "currency": "USD",
  "cardPan": "4111111111111111"
}
```

Respuesta:

```json
{
  "id": "uuid",
  "merchantId": "mrc-001",
  "amount": 120.50,
  "currency": "USD",
  "maskedCard": "411111******1111",
  "status": "AUTHORIZED",
  "createdAt": "2026-01-01T00:00:00Z"
}
```

Labs Vault:

```text
GET  /security/v1/vault-labs/kv
POST /security/v1/vault-labs/transit/encrypt
POST /security/v1/vault-labs/transit/decrypt
GET  /security/v1/vault-labs/database/credentials
POST /security/v1/vault-labs/pki/certificates
```

Los requests de ejemplo están en `infraestructura/requests` y pueden abrirse desde IntelliJ IDEA o VS Code REST Client.

## Notas

Esta PoC usa Vault en modo dev para laboratorio. 
En producción no usar token root, habilitar TLS, storage persistente/HA, rotación de tokens, unseal seguro, namespaces/paths por dominio.e.
