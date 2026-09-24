# wicket-orientdb

Apache Wicket integration for OrientDB: an application/session bound to OrientDB users and transactions,
Wicket models and data providers over documents and schema, OrientDB-security-driven authorization,
a REST bridge / reverse proxy, schema "prototypes" and query filters.
It is the foundation of [Orienteer](https://github.com/OrienteerBAP/Orienteer) (local checkout `~/Development/Orienteer`)
and is modernized in lockstep with it. Dormant from Feb 2024 to Sep 2026.
**Read [`REFRESH_PLAN.md`](REFRESH_PLAN.md) before touching dependencies, build or infra; tick items off there in the same commit.**

## Contract with Orienteer — don't break it

- Coordinates `ru.ydn.wicket.wicket-orientdb:wicket-orientdb` **and its `test-jar`**, parent `wicket-orientdb-parent`,
  version **`2.0-SNAPSHOT`**. Never change groupId/artifactId/version without the owner.
- Orienteer uses nearly everything: `OrienteerWebApplication extends OrientDbWebApplication`, REST via
  `OrientDBHttpAPIResource.mountOrientDbRestApi`, `orienteer-rproxy` extends `rest.ReverseProxyResource`,
  `@RequiredOrientResource(s)` / `OSecurityHelper` / `OrientPermission`, `DBClosure`, the models, `proto.*Prototyper`,
  `utils.LombokExtensions` (`@ExtensionMethod`), and `OrienteerTester extends junit.WicketOrientDbTester` (test-jar).
- Public API = public/protected members of the main jar **and** of `ru.ydn.wicket.wicketorientdb.junit` in the test-jar.
  Avoid signature changes; if one is unavoidable, list it in the plan's "Handoff to Orienteer".
- Stages: A → Orienteer P3 (JDK 21, Wicket 8, javax), B → P5 (Wicket 9), C → P6 (Wicket 10, jakarta).

## Stack (current → target)

| Area | Current | Target (see plan) |
|---|---|---|
| Java | `--release 21` (`maven.compiler.release` in root pom) | verified on JDK 21 and 25 (Stage A) |
| Web | Wicket 8.15 (core, extensions, auth-roles), `javax.servlet-api` 3.0.1 `provided` | Wicket 9.x (B) → 10.x + jakarta Servlet 6 (C) |
| DB | OrientDB 3.2.56 (core + server; embedded server supported) | latest 3.2.x; Orienteer decides the runtime version |
| Other | Guava 33, OkHttp 4.9 (+ logging-interceptor), Lombok 1.18.48 `provided`, SLF4J 1.7 (via Wicket) | OkHttp current (C) |
| Tests | JUnit 4.13, `WicketTester`, embedded OrientDB | JUnit 4.13.2 (A), Jupiter (B, required by Wicket 9 testers) |
| DI | none (no Guice, no `javax.inject`) | nothing to do |

## Build status — read first

- Stage A in progress: the build is green on JDK 21 with `--release 21` (Lombok 1.18.48 via `annotationProcessorPaths`,
  `HexFormat` instead of `javax.xml.bind`). JDK 8 is gone for good; don't try to keep a JDK 8 build working.
- Dev machine: Maven 3.9 (Homebrew), Temurin 21 via SDKMAN (default). Homebrew JDK 27 is non-LTS — don't target it.
- Dead infra: oss.sonatype.org (OSSRH) and jcenter/bintray. Publishing moves to the Central Portal (plan phase R).

## Commands

```bash
mvn clean verify                                   # build + tests + checkstyle (runs at validate, fails the build)
mvn -DskipTests install                            # fast install into ~/.m2 for Orienteer
mvn -pl wicket-orientdb test -Dtest=TestModels     # single test class
mvn -pl wicket-orientdb-demo jetty:run             # demo app on :8080 with embedded OrientDB
```

## Layout and key classes

- `wicket-orientdb/` — the library (jar + test-jar), package `ru.ydn.wicket.wicketorientdb`. Has its own `AGENTS.md`.
  - root: `OrientDbWebApplication` (abstract; installs DB thread factory, hooks, transaction listener, authorization,
    converters, `ODocumentPropertyLocator`), `OrientDbWebSession`, `OrientDbSettings`/`IOrientDbSettings`,
    `EmbeddOrientDbApplicationListener` (embedded `OServer`), `TransactionRequestCycleListener`,
    `LazyAuthorizationRequestCycleListener` (HTTP Basic), `AbstractDataInstallator`, `OrientDefaultExceptionMapper`.
  - `model` (models/data providers), `security`, `rest` (OkHttp reverse proxy + OrientDB REST bridge),
    `proto` (JDK dynamic-proxy prototypes of `OClass`/`OProperty`/`OIndex`), `utils` (`DBClosure`, `OSchemaHelper`,
    `LombokExtensions`, `query/filter`), `converter`, `validation`, `behavior`, `components`, `service`.
- `wicket-orientdb-demo/` — sample war (never deployed). May be parked if it blocks a stage. Has its own `AGENTS.md`.
- `check_style.xml` — Checkstyle rules for main sources.

## Conventions

- Indentation is mostly tabs (~90% of Java files) but not everywhere: match the surrounding file. No reformat-only diffs.
- Checkstyle fails the build on: missing Javadoc on public types and on public methods longer than 2 lines, missing
  `package-info.java`, interfaces not starting with `I`, lines > 180, methods > 200 lines, anonymous classes > 100 lines.
- OrientDB-related types start with `O` (`OClassModel`, `OSchemaHelper`), interfaces with `I`.
- Logging: SLF4J only (`LoggerFactory` or Lombok `@Slf4j`). Never add a logging binding or `log4j2.xml`/`logback.xml`
  to the library — only to tests or the demo.
- Lombok (`provided`) is used in 5 main files (`@ExtensionMethod`, `@Slf4j`, `@UtilityClass`); don't spread it.
- Guava types (`Function`, `Predicate`) appear in public signatures — Guava is part of the API surface.

## Testing

- JUnit 4. Most tests are integration tests: `@ClassRule WicketOrientDbTesterScope` boots `OrientDbTestWebApplication`
  with an **embedded OrientDB server** (memory DB `WicketOrientTestDb`, `src/test/resources/.../db.config.xml`,
  binary port 2424-2430, HTTP 2480-2490). REST tests go through that real HTTP listener.
- Users: admin/admin, reader/reader, writer/writer (OrientDB default users). `TestDataInstallator` creates the test
  schema and data, including JavaScript `OFunction`s (executed by GraalJS, pulled in by OrientDB).
- 17 tests are `@Ignore`d on purpose (OrientDB issues, remote server needed) — see plan Appendix C.

## Pitfalls

- Tests bind ports 2424+/2480+: don't run this build in parallel with Orienteer's tests or a local OrientDB.
- Coupled libraries move together: Wicket ↔ servlet API ↔ jakarta (Stages B/C). Never bump one alone.
- GraalJS: `orientdb-core`'s own GraalVM 21.3.5 is excluded and replaced by `graalvm.version` (it crashes on JDK 22+).
  Don't drop the exclusions; consumers declaring `orientdb-core` directly need the same exclusions (plan D11).
- `rest.ReverseProxyResource` uses the OkHttp-internal `okhttp3.internal.http.HttpMethod` (breaks on OkHttp 5).
- `OrientDBHttpAPIResource` forwards the signed-in user's password to OrientDB's HTTP API as Basic auth.
- The parent pom's `<repositories>`/`<distributionManagement>` are inherited by consumers — keep them clean.
