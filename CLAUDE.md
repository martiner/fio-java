# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Java SDK (`cz.geek:fio-java`) wrapping the [Fio Bank REST API](http://www.fio.cz/bank-services/internetbanking-api). It downloads account statements as XML and exposes them as typed Java objects, plus exports statements in other formats (pdf, csv, etc.). Ships Spring Boot autoconfiguration.

## Commands

```shell
./mvnw test            # unit tests (TestNG, no network)
./mvnw verify          # unit + integration tests; IT needs a real TOKEN
./mvnw package         # build + dependency analysis (fails on unused/undeclared deps)
```

Run a single test:
```shell
./mvnw test -Dtest=FioClientTest#shouldGetTransactionsById
```

Integration tests (`*IT`, run by failsafe during `verify`) hit the live API and require a real token:
```shell
export TOKEN=$(cat)   # paste token, then Ctrl-D
./mvnw verify
```

Releasing uses `mvnw release:prepare` / `release:perform` (GPG-signed, also needs TOKEN). See README for the exact sequence — there must be a delay between prepare and perform to avoid Fio rate limiting.

## Build specifics

- **Java 17**, Maven wrapper, parent POM `cz.geek:geek-parent`. Lombok is used throughout (`@Data`, `@Slf4j`) — annotation processing must be enabled in the IDE.
- **JAXB code generation**: `cxf-xjc-plugin` runs in `generate-sources` and turns `src/main/resources/IBSchema.xsd` into Java classes (`AccountStatement`, `Info`, `TransactionList`, `Transaction`, `Column0`..`Column26`, etc.) under `target/generated/`. These are **not** committed; they are required to compile and are regenerated on every build. `IBSchema.xjb` controls the binding (package `cz.geek.fio`, top-level scoping so no nested classes).
- `maven-dependency-plugin:analyze-only` runs at `package` with `failOnWarning=true`. Adding or removing imports without updating `pom.xml` dependencies will fail the build.

## Architecture

The request flow for getting a statement:

`FioClient` → `RestTemplate.execute(...)` → `FioExtractor` → `NamespaceIgnoringJaxb2HttpMessageConverter` (XML → generated JAXB types) → `FioConversionService` (generated types → public `Fio*` types).

Key pieces:

- **`FioClient`** — the public entry point. Builds the URI for each endpoint (`/v1/rest/by-id/...`, `/periods/...`, `/last/...`, `set-last-date`, `set-last-id`), injects the token into the path, sets the User-Agent, and delegates to `RestTemplate`. `getStatement*` parse into objects; `exportStatement*` stream the raw body to an `OutputStream` via `OutputStreamResponseExtractor`. There is a package-private constructor taking `protocol/host/port` used by tests to point at a local mock.

- **Namespace handling** — the Fio API returns XML *without* the namespace declared in `IBSchema.xsd`. `NamespaceIgnoringJaxb2HttpMessageConverter` plus `NamespaceFilter` inject `http://www.fio.cz/IBSchema` into the SAX stream during parsing so JAXB unmarshalling matches. If statement parsing ever breaks after an XSD/namespace change, look here first.

- **`FioConversionService`** — maps generated JAXB types to the public API types (`FioAccountStatement`, `FioAccountInfo`, `FioTransaction`). The Fio statement format encodes transaction fields as numbered columns (`Column0`..`Column26`); `TransactionConverter` maps each column to a named (Czech) field on `FioTransaction`. **To expose a new transaction attribute, add the `getColumnNN()` mapping here** and a field on `FioTransaction`. Most columns are nullable and guarded with null checks.

- **Error handling** — `FioErrorHandler` (a `DefaultResponseErrorHandler`) turns non-2xx responses into `FioRestException`; HTTP 409 specifically becomes `FioTooMuchRequestsException` (Fio's rate-limit signal), a deprecated subclass of `FioTooManyRequestsException` kept thrown for binary compatibility — it goes away with the next major version. All client exceptions extend `FioException`.

- **Spring Boot** — `FioClientAutoConfiguration` (registered in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`) creates a `FioClient` bean from `FioClientSettings`, which is `@ConfigurationProperties(prefix = "fio.client")`. Set `fio.client.token` (plus optional `connectionTimeout`/`socketTimeout`) to autoconfigure. The token is excluded from `toString()` to avoid leaking it into logs.

## Conventions

- Public domain types (`FioTransaction`, `FioAccountInfo`) use **Czech field names** matching Fio's terminology (`objem`, `protiucet`, `variabilniSymbol`, ...). Keep that naming for new fields.
- Prefer Lombok over hand-written accessors — `@Data` for data holders, `@Getter` when only accessors are needed.
- Unit tests use **TestNG** + **Jadler** (HTTP mock); fixtures are XML files under `src/test/resources` loaded via `ResourceUtils`.
