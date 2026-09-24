# wicket-orientdb (library module)

The published artifact: `ru.ydn.wicket.wicket-orientdb:wicket-orientdb` jar **plus test-jar** (Orienteer uses both).
Everything is in package `ru.ydn.wicket.wicketorientdb`. Root rules: [`../AGENTS.md`](../AGENTS.md); plan: [`../REFRESH_PLAN.md`](../REFRESH_PLAN.md).

## Packages

- root — `OrientDbWebApplication` (abstract base app), `OrientDbWebSession` (OrientDB user + DB session per Wicket session),
  `OrientDbSettings`, `EmbeddOrientDbApplicationListener`, request-cycle listeners (transactions, lazy Basic auth),
  `AbstractDataInstallator`, `OUserCatchPasswordHook`, `OrientDefaultExceptionMapper`.
- `model` — ~40 models/data providers (`ODocumentModel`, `OClassModel`, `OPropertyModel`, `OQueryModel`,
  `OQueryDataProvider`, naming models, Java-sortable providers). `FunctionModel` takes danekja `SerializableFunction`.
- `security` — `@RequiredOrientResource(s)`, `OSecurityHelper`, `OrientPermission`, `ISecuredComponent`, authorization strategies.
- `rest` — `ReverseProxyResource` (OkHttp 4; uses `javax.servlet` and `okhttp3.internal.http.HttpMethod`) and
  `OrientDBHttpAPIResource` (bridge to the embedded server's HTTP API, `mountOrientDbRestApi`).
- `proto` — `AbstractPrototyper` builds `java.lang.reflect.Proxy` prototypes of `OClass`/`OProperty`/`OIndex`.
- `utils` — `DBClosure` (sudo work), `OSchemaHelper` (schema DSL), `LombokExtensions` (`asHttpServletRequest` is javax API),
  `ODocumentPropertyLocator` (PropertyResolver locator for `ODocument`), `query/filter` (filter criteria → SQL).
- `converter` — `HexConverter` (registered for `byte[]`), `ODocumentConverter`, `OIdentifiableConverter`, danekja-based joint converters.
- `validation`, `behavior`, `components`, `service` — small helpers.

## Dependencies

- Compile: wicket-core, wicket-extensions, wicket-auth-roles, orientdb-core, orientdb-server, Guava, OkHttp + logging-interceptor.
- `provided`: javax.servlet-api, Lombok. Test: JUnit 4 (also on the test-jar's API: `WicketOrientDbTesterScope` is a JUnit 4 rule).
- Transitive but used directly: commons-collections4 (arrives via wicket-util) in `rest/WicketSessionCookieJar`.
- OrientDB 3.2 brings GraalJS/Truffle 21.3.5 through a JDK-activated profile in its pom (needed for JS `OFunction`s).

## Test-jar (public API for Orienteer)

- `junit.WicketOrientDbTester extends WicketTester` — DB accessors, `signIn`, `executeUrl(...)` (runs REST calls through
  `MockHttpServletRequest`, implements an anonymous `javax.servlet.ServletInputStream`), `iterativelyTest`.
- `junit.WicketOrientDbTesterScope extends WicketTesterScope` — JUnit 4 `@ClassRule`; Wicket 9 removes `WicketTesterScope` (Stage B).
- The test-jar also carries `OrientDbTestWebApplication`, `TestDataInstallator`, `db.config.xml` and the test pages.

## Tests

- 12 test classes, 103 `@Test`, 17 `@Ignore` (10 `TestInAppOrientDBCompatibility`, 3 `TestFilters`,
  4 `TestStandaloneOrientDBCompatibility` which needs a remote server). Surefire: 100 run, 14 skipped.
  Only `ConvertersTests` is a plain unit test.
- They start an embedded OrientDB server with HTTP on 2480-2490; `TestRestApi` goes through it and calls JavaScript functions.

## Pitfalls

- Resources (`.html`, `.properties`) live next to classes in `src/main/java` and `src/test/java`; the pom copies them.
- Checkstyle applies only to main sources: every new package needs `package-info.java`, public types need Javadoc.
- Don't add a logging binding or logging config here (test scope only).
