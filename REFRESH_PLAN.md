# wicket-orientdb Refresh Plan

Living plan for bringing wicket-orientdb from its 2024 state (Java 8, Wicket 8.15, javax, OrientDB 3.2.27) to current,
supported versions, in lockstep with [Orienteer's plan](https://github.com/OrienteerBAP/Orienteer/blob/master/REFRESH_PLAN.md)
(local: `~/Development/Orienteer/REFRESH_PLAN.md`). **Tick items off (`[ ]` → `[x]`) in the same commit that completes
them**, and add a short note (date, deviations) after the item when useful. Module details: each module's `AGENTS.md`.

Started: 2026-09-24. Versions below were checked on Maven Central on that date — re-check at the start of every stage
(`./mvnw versions:display-dependency-updates versions:display-plugin-updates`).

## Progress overview

| Phase | Goal | Depends on | Unblocks Orienteer | Status |
|---|---|---|---|---|
| P0 | AI initialization: AGENTS.md files + this plan | — | — | done |
| A | Stage A: JDK 21/25 build on the **same** framework majors (Wicket 8.15, javax, OrientDB 3.2.x), `./mvnw install` | P0 | P3 | done (local install; published artifact → R) |
| B | Stage B: Wicket 9.x, still javax; JUnit 5 in the test-jar → `2.1` | A, 2.0 release (D13) | P5 | todo — **not started**; Orienteer consumes Stage A during its P1–P4 (D12) |
| C | Stage C: Wicket 10.x, `jakarta.servlet` 6, `wicket-tester`, OkHttp current → `3.0` | B (D13) | P6 | todo |
| R | Release & CI: GitHub Actions, Maven Central via the Central Portal: `2.0-SNAPSHOT` now, `2.0` after Orienteer P3 is green (D13/D14) | A | P3 "resolvable from a real repo" | `2.0-SNAPSHOT` **published** to the Central Portal snapshots repo (2026-09-24); `2.0` release pending (Orienteer P3, GPG key) |
| X | Cleanup, docs, known bugs & security | any time | — | todo |

## Guiding principles

1. **Never break the Orienteer contract** (below): coordinates, version `2.0-SNAPSHOT`/`2.0` (= Stage A; B = `2.1`, C = `3.0`, D13), jar + test-jar, public API.
2. **Keep the build green after every item.** One concern per commit; no big-bang upgrades.
3. **Same framework majors per stage:** A changes JDK/tooling only; B changes Wicket 8 → 9 only; C does jakarta + Wicket 10.
   Coupled libraries move together: Wicket ↔ servlet API ↔ jakarta ↔ Jetty (demo).
4. **Minimal, behavior-preserving code changes.** Any unavoidable public API change goes to "Handoff to Orienteer".
5. **No reformat-only diffs;** match the surrounding indentation (mostly tabs).

## Decisions log

| # | Date | Decision | Status |
|---|---|---|---|
| D1 | 2026-09-24 | Target `--release 21`; verify on JDK 21 **and** 25 (Temurin 25.0.4 via SDKMAN). JDK 27 (non-LTS) is not targeted. Same as Orienteer D1. | accepted |
| D2 | 2026-09-24 | Coordinates and version stay `ru.ydn.wicket.wicket-orientdb:wicket-orientdb:2.0-SNAPSHOT` (+ `test-jar`, parent `wicket-orientdb-parent`) through all stages. The release version is decided in phase R. | accepted for Stage A (`2.0-SNAPSHOT` → `2.0`); "through all stages" is **superseded by D13** (decided) |
| D3 | 2026-09-24 | Stage A compiles and tests against **OrientDB 3.2.56** (latest 3.2.x; Orienteer contract row). Orienteer's `dependencyManagement` decides the runtime version. | accepted |
| D4 | 2026-09-24 | Checkstyle is upgraded in Stage A (plugin 3.6.0 + Checkstyle 14.1.0) with a semantically equivalent `check_style.xml`; only Javadoc fixes or documented deviations if the new engine flags more. | accepted |
| D5 | 2026-09-24 | OkHttp stays **4.9.0** in Stage A (no transitive change for Orienteer's rproxy). Upgrade in Stage C (known CVEs: CVE-2021-0341 in okhttp < 4.9.2, CVE-2020-29582 / CVE-2022-24329 in kotlin-stdlib 1.4.10). | accepted |
| D6 | 2026-09-24 | Tests stay on JUnit 4.13.2 in Stage A; Mockito is not added (unused). Jupiter + vintage come in Stage B because Wicket 9 testers need JUnit 5. | accepted |
| D7 | 2026-09-24 | Publishing (`distributionManagement`, `release` profile, nexus-staging, maven-release-plugin) is left untouched in Stage A and replaced in phase R (Central Portal). Only the dead `<repositories>` entry is removed in A. | accepted |
| D8 | 2026-09-24 | The demo module stays in the reactor while it builds; if it blocks a stage it is parked (removed from `<modules>`, code kept). | accepted; for Stage C superseded by D15 (keep and port, no parking) |
| D9 | 2026-09-24 | Keep `org.danekja` `SerializableFunction` in the public API (`FunctionModel`, `SerializableConverter`, `AbstractJointConverter`): wicket-core 9.24.0 **and** 10.11.0 still depend on `org.danekja:jdk-serializable-functional`. Orienteer P5's "replace danekja lambdas" is optional, not forced by this library. | proposed |
| D10 | 2026-09-24 | Stage B keeps `junit.WicketOrientDbTesterScope` as a JUnit 4 rule with the same public API (re-implemented on `ExternalResource`, since Wicket 9 removes `WicketTesterScope`) and adds a Jupiter extension next to it; tests run via `junit-vintage-engine`. | proposed |
| D11 | 2026-09-24 | **GraalJS swap:** OrientDB 3.2.56 still brings GraalVM/Truffle 21.3.5 (via a JDK-activated profile in `orientdb-core`), which crashes every embedded OrientDB start on JDK 22+ (`NoSuchMethodError: sun.misc.Unsafe.ensureClassInitialized`). The library excludes `org.graalvm.{sdk,truffle,js,tools}` from `orientdb-core` and declares GraalJS **25.0.4** (`polyglot`, `js-scriptengine` compile; `js` pom runtime — same roles/scopes as OrientDB's). OrientDB only uses the stable `org.graalvm.polyglot` API + `javax.script`. Orienteer must mirror the exclusions on its own `orientdb-core` declaration (handoff). Revisit when OrientDB upgrades Graal. | accepted by the owner (2026-09-24); Orienteer mirrors it as its D8 |
| D12 | 2026-09-24 | **Stage B stays a separate stage** (Wicket 9, still javax), consistent with Orienteer's two-step plan (Orienteer D5): no A → C jump. It is **not started now**: Orienteer builds against Stage A during its P1–P4; Stage B starts only when the owner gives the go-ahead and D13 is decided. | accepted (owner, 2026-09-24) |
| D13 | 2026-09-24 | **Amends D2 — distinct versions after Stage A.** Stage B/C builds must never overwrite `2.0-SNAPSHOT` in `~/.m2` while Orienteer builds against Stage A (constraint from Orienteer D9, proposed). Options: **(a)** do Stage B on a branch with version `2.1-SNAPSHOT` (Wicket 9) and Stage C with `3.0-SNAPSHOT` (Wicket 10/jakarta), keeping `master` at `2.0-SNAPSHOT` = Stage A; or **(b)** release Stage A first (e.g. `2.0`, phase R) and continue B/C on newer snapshot versions. Under both options groupId/artifactId, the `test-jar` and the parent `wicket-orientdb-parent` stay unchanged; only the version moves, set with `./mvnw versions:set` across parent and modules. **Decided (owner delegated, 2026-09-24) — option (b):** (1) publish Stage A now as `2.0-SNAPSHOT` to the Central Portal snapshots repository (`https://central.sonatype.com/repository/maven-snapshots/`), so Orienteer and CI resolve it without a local install; (2) release `2.0` from Stage A **only after Orienteer's P3 build is green against it** (Orienteer P3 is the real integration test; Central releases are immutable) — fixes found there go into `2.0-SNAPSHOT` first; (3) after the `2.0` release `master` moves to `2.1-SNAPSHOT` for Stage B (Wicket 9), released as `2.1`; Stage C (Wicket 10/jakarta, breaking `LombokExtensions.asHttpServletRequest`) becomes `3.0-SNAPSHOT`, released as `3.0`; (4) if Stage B must start before `2.0` is released, it goes on a branch with `2.1-SNAPSHOT`, never `2.0-SNAPSHOT`. | decided (2026-09-24) |
| D14 | 2026-09-24 | **Publishing:** the `ru.ydn` namespace is verified on the Central Portal; publishing is approved in principle. Snapshots: **yes** (`2.0-SNAPSHOT` → Central Portal snapshots, D13). Releases go through the Central Portal with `central-publishing-maven-plugin` (server id `central`); every upload (snapshot deploy, release) is run by the owner — agents only prepare and validate locally. | accepted (owner, 2026-09-24) |
| D15 | 2026-09-24 | **Demo module is kept after Stage C**; its Jetty 12 ee10 port (plugin, `web.xml`, `Start.java`) is part of Stage C. It is never published to Central. | accepted (owner, 2026-09-24) |
| D16 | 2026-09-24 | **Stale remote branches are kept for now** (`future`, `wicket7-upgrade`, `wicket8`, `dependabot/maven/org.apache.wicket-wicket-core-8.12.0`). Nobody deletes branches without a new owner decision. | accepted (owner, 2026-09-24) |

### Open questions (decide when the phase starts; record the answer above)

- [x] **Stage B at all?** Orienteer D5 allows merging P5+P6 if this library jumps straight to Wicket 10. Deliver B separately (recommended: cheap here, gives Orienteer a javax-only intermediate) or go A → C? (B) — 2026-09-24, owner: separate stage, not started now → D12
- [x] **Versions for Stage B/C** (D13, amends D2): branch + `2.1-SNAPSHOT` / `3.0-SNAPSHOT`, or release Stage A first? Blocks the start of Stage B. (owner) — 2026-09-24: `2.0-SNAPSHOT` published now, `2.0` released after Orienteer P3 is green, then `2.1-SNAPSHOT` (B) and `3.0-SNAPSHOT` (C); a pre-release B start uses a `2.1-SNAPSHOT` branch → D13
- [x] **Release version and namespace:** release `2.0` after Stage C? Is the `ru.ydn` namespace verified on the Central Portal (needs DNS control of `ydn.ru`)? (R) — 2026-09-24: `2.0` is released **from Stage A** (after Orienteer P3 is green), not after Stage C; `ru.ydn` is verified, publishing approved in principle → D13, D14
- [x] **Snapshots:** publish `2.0-SNAPSHOT` to `https://central.sonatype.com/repository/maven-snapshots/` so Orienteer CI does not need a local install? (R) — 2026-09-24: yes → D14
- [x] **Demo module:** keep after Stage C (Jetty 12 ee10 port) or drop it? (C) — 2026-09-24: keep; Jetty 12 ee10 port in Stage C → D15
- [x] **Stale remote branches:** delete `future`, `wicket7-upgrade`, `wicket8` (fully merged) and `dependabot/maven/org.apache.wicket-wicket-core-8.12.0` (superseded)? (X) — 2026-09-24: **keep for now**, don't delete → D16
- [ ] **Credential forwarding:** `OrientDBHttpAPIResource` sends the session user's password to OrientDB's HTTP API; `OrientDbWebSession` keeps the password in the session. Keep, or switch to a token/`root`-proxy model? (X)

## Contract with Orienteer (from Orienteer's "External dependency requirements")

| Stage | Orienteer phase | Required from this library |
|---|---|---|
| A | P3 (Java 21, javax, Wicket 8) | builds & runs on JDK 21 with Wicket 8.15 + OrientDB 3.2.56; Lombok ≥ 1.18.48; release (or a published snapshot) |
| B | P5 (Wicket 9) | ported to Wicket 9.x (page store, lambdas, `java.time`) — for this library: tester/JUnit 5 and servlet 3.1 only (see Appendix D) |
| C | P6 (Wicket 10, jakarta) | ported to Wicket 10.x, `jakarta.servlet` 6, Guice 7/`jakarta.inject` if used (not used), `wicket-tester`; OkHttp current |

Orienteer consumes: `OrientDbWebApplication`/`OrientDbWebSession`, `OrientDBHttpAPIResource.mountOrientDbRestApi`,
`rest.ReverseProxyResource` (rproxy), `security.*` (`@RequiredOrientResource(s)`, `OSecurityHelper`, `OrientPermission`),
`utils.DBClosure`, `utils.LombokExtensions`, `proto.*Prototyper`, models/providers, query filters, and from the
**test-jar** `junit.WicketOrientDbTester` (`OrienteerTester extends` it). 252 Orienteer source files import this library.

---

## P0 — AI initialization

- [x] Analyze the repository: modules, poms, plugins, JDK blockers, Stage B/C inventory, tests, branches — 2026-09-24
- [x] Root `AGENTS.md` (contract, stack, build status, commands, layout, conventions, testing, pitfalls) — 2026-09-24
- [x] `AGENTS.md` in `wicket-orientdb/` and `wicket-orientdb-demo/` — 2026-09-24
- [x] This plan with decisions log, contract, stages, appendices — 2026-09-24

## A — Stage A: JDK 21/25 on the current stack (→ Orienteer P3)

Goal: `./mvnw clean verify` green on JDK 21 and 25 with `--release 21`, Wicket 8.15, javax, OrientDB 3.2.x;
`./mvnw install` puts jar + test-jar + parent pom for `2.0-SNAPSHOT` into `~/.m2`.
Baseline (2026-09-24, JDK 21): Checkstyle 6.18 passes; `compile` fails with `IllegalAccessError` from Lombok 1.18.16.

- [x] A1 Maven Wrapper 3.9.16 (`mvn wrapper:wrapper -Dmaven=3.9.16 -Dtype=only-script`): commit `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties` — 2026-09-24, wrapper plugin 3.3.4
- [x] A1 `.sdkmanrc` (`java=21.0.11-tem`) and a minimal `.editorconfig` (UTF-8, LF, tabs for `*.java`/`*.xml`/`*.html`; no trailing-whitespace or final-newline enforcement — 31 files lack one; **no reformat**) — 2026-09-24
- [x] A2 Compiler: `maven.compiler.release=21` replaces `java.version` source/target (there was no `-parameters`; not added) — 2026-09-24
- [x] A2 Lombok 1.18.16 → 1.18.48, declared in `maven-compiler-plugin` `annotationProcessorPaths` (required from JDK 23) — 2026-09-24
- [x] A2 `converter.HexConverter` (+ `ConvertersTests`): `javax.xml.bind.DatatypeConverter` → `java.util.HexFormat` (upper-case output, case-insensitive parsing, `IllegalArgumentException` on bad input — same behavior) — 2026-09-24; 2 tests added (odd length, illegal character) plus case assertions
- [x] A2 Surefire `argLine`: add `--add-opens`/`--add-exports` only if a test run proves them necessary; list them in the handoff — 2026-09-24: **none needed**. Found (not needed by tests): Wicket 8 `CheckingObjectOutputStream` (serialization diagnostics) silently disables itself without `--add-opens java.base/java.io=ALL-UNNAMED` (verified with a probe) → optional dev flag, see handoff
- [x] A2 First green `./mvnw clean verify` on JDK 21; record test counts in Appendix C — 2026-09-24: library 100 run / 14 skipped, demo 1 run; compiler plugin still 3.7.0 (bumped in A5)
- [x] A3 Remove the dead `snapshots-repo` (oss.sonatype.org) from `<repositories>` (inherited by every consumer); `distributionManagement` stays until R (D7) — 2026-09-24; no bintray/jcenter entries existed. Everything now resolves from Maven Central only
- [x] A4 Remove dead plugins: `maven-eclipse-plugin` (+ `wtp.version`), `coveralls-maven-plugin`, `cobertura-maven-plugin`, m2e `lifecycle-mapping`, `maven-bundle-plugin` extension; drop `<type>bundle</type>` from the OrientDB entries in `dependencyManagement` and the explicit `${orientdb.version}` in the module pom — 2026-09-24; dependency tree unchanged (the `bundle` entries never matched the `jar` dependencies)
- [x] A5 Pin every build plugin once in root `<pluginManagement>` (Appendix B); remove module-level versions (jar 3.0.2, deploy 2.7, jetty); release-profile source/javadoc/gpg pinned to current (javadoc 2.x is JDK-incompatible), nexus-staging/release-plugin left for R — 2026-09-24; also pinned dependency 3.11.0, wrapper 3.3.4, versions 2.22.0 (helper goals). nexus-staging (build extension) keeps its version in the `release` profile until R
- [x] A5 Add `maven-enforcer-plugin`: `requireMavenVersion [3.9,)`, `requireJavaVersion [21,)` — 2026-09-24 (build-time only; not inherited by consumers, who don't use this parent)
- [x] A6 Checkstyle plugin 3.0.0 → 3.6.0 with Checkstyle 14.1.0; migrate `check_style.xml` (DTD 1.3; `JavadocMethod`/`JavadocType` split into `Missing*` + validation; `LineLength` moved under `Checker` with `fileExtensions=java`); still `failOnViolation`, still at `validate` — 2026-09-24: 0 violations, no code changes; each rule verified with a throwaway probe file (missing type/method Javadoc, wrong `@param`/`@return`, interface name, line length, constant name, missing `package-info.java` all fail the build)
- [x] A7 JUnit 4.13.1 → 4.13.2 — 2026-09-24 (test scope; Jupiter/vintage deferred to B, Mockito not added — D6)
- [x] A8 OrientDB 3.2.27 → 3.2.56 (D3); record the transitive dependency diff in the handoff — 2026-09-24. **Bug found and fixed:** `OQueryModel.iterator(first, count[, transformer])` returned a lazy stream iterator of an `OResultSet` already closed by try-with-resources; 3.2.27 still iterated it, 3.2.56 yields nothing → every `OQueryDataProvider` page (query-backed tables) would be empty. Fix: collect the page into a list before closing (signature unchanged; works on both versions). `TestModels.testOQueryProvider` now asserts the iterated count (it passed vacuously before). Transitive diff: see handoff
- [x] A9 Library hygiene: servlet API `provided`; Lombok `provided`; no logging binding in compile/runtime scope; no `log4j2.xml`/`logback.xml` in the jar or test-jar (verify with `jar tf`); demo `log4j-slf4j-impl` 2.14.0 → 2.26.1 (Log4Shell-era version) — 2026-09-24: library already clean (jar: classes + `OrientDbWebApplication.properties`; test-jar: classes + `OrientDbTestPage.html`, `db.config.xml`, `OrientDbTestWebApplication.properties`); JUnit `test`; no hard pins beyond parent `dependencyManagement` (consumers override with their own). Demo war now ships log4j-core/api 2.26.1 instead of **2.14.0 (CVE-2021-44228)**; version in property `log4j.version`
- [x] A10 Demo: Jetty 9.4.12 → 9.4.58 (`jetty-maven-plugin`, test-scoped `jetty-all`); `jetty:run` serves the home page — 2026-09-24: `jetty:run` on JDK 21 → `GET /` 200 with the `TestData` table, `GET /orientdb/query/db/sql/select count(*) from TestData` (admin) 200 → 50 via the REST bridge. `Start.java` compiles (deprecated `SslContextFactory()`, Stage C)
- [x] A11 JDK 25 blocker: OrientDB's GraalVM/Truffle 21.3.5 → GraalJS 25.0.4 (D11); `TestRestApi.testExecuteFunction` (JS `OFunction`s over the REST bridge) passes on 21 and 25 — 2026-09-24. First JDK 25 run without it: all 11 embedded-server test classes failed in `initializationError`
- [x] A11 Demo `jetty:run` on JDK 25: Jetty 9.4.58's annotation scanner (ASM 9.7.1) failed on the Java 25 multi-release classes of `bcprov-jdk18on` 1.85 (new transitive dep of OrientDB 3.2.56 server) → ASM 9.10.1 as a `jetty-maven-plugin` dependency (`jetty.asm.version`); home page + REST bridge verified on 21 and 25 — 2026-09-24
- [x] A11 Install Temurin 25.0.4 (SDKMAN, not default); `./mvnw clean verify` green on 21 **and** 25 — 2026-09-24: both 103 run / 17 skipped / 0 failures (+ demo 1/0)
- [x] A11 `./mvnw install`; confirm `~/.m2/repository/ru/ydn/wicket/wicket-orientdb/wicket-orientdb/2.0-SNAPSHOT/` has the jar **and** `-tests.jar`, and the parent pom is installed; class files are major version 65 — 2026-09-24: jar (177 classes) + `-tests.jar` (42 classes) + pom, parent pom; all class files major 65; manifest `Build-Jdk-Spec: 21`
- [x] A11 Update `AGENTS.md` (build status, stack) and write "Handoff to Orienteer" below — 2026-09-24
- [x] Exit check: `./mvnw clean verify` green on JDK 21 and 25; `./mvnw install` done; test results match Appendix C (differences explained) — 2026-09-24: 103 run / 17 skipped (= the 17 static `@Ignore`s) / 0 failures on both JDKs; +2 tests vs baseline (A2); demo 1/0. **Stage A done**
- [x] Owner acceptance — 2026-09-24: re-verified independently on JDK 25.0.4 with `./mvnw -o clean verify` (104 run across the reactor = 103 library + 1 demo, 0 failures, 17 skipped); Orienteer's plan records the handoff (Orienteer D8; P3 items for the GraalVM exclusions, OrientDB 3.2.56, ASM ≥ 9.8)

## B — Stage B: Wicket 8 → 9, still javax (→ Orienteer P5)

Goal: Wicket 9.24 (last 9.x), javax Servlet 3.1, green on 21/25, test-jar usable by Orienteer with JUnit 5.
Migration guide: https://cwiki.apache.org/confluence/display/WICKET/Migration+to+Wicket+9.0 · Size: **S (0.5–1 day)**.
**Don't start until the owner gives the go-ahead** (D12); version `2.1-SNAPSHOT` → `2.1` (D13); never install a Stage B build as `2.0-SNAPSHOT`.

- [x] Resolve the open question "Stage B at all?" — 2026-09-24: separate stage, not started now (D12)
- [ ] Prerequisite (D13): `2.0` released from Stage A and `master` moved to `2.1-SNAPSHOT`; if Stage B must start earlier, work on a branch with `2.1-SNAPSHOT` (never `2.0-SNAPSHOT`)
- [ ] Wicket 8.15.0 → 9.24.0 (`wicket-core`, `wicket-extensions`, `wicket-auth-roles`, demo `wicket-devutils`)
- [ ] `javax.servlet-api` 3.0.1 → 3.1.0 `provided` (Wicket 9 baseline); test-jar `WicketOrientDbTester.executeUrl`: the anonymous `ServletInputStream` must implement `isFinished()`, `isReady()`, `setReadListener()`
- [ ] Test-jar: `org.apache.wicket.util.tester.WicketTesterScope` is removed in Wicket 9 → re-implement `WicketOrientDbTesterScope` on JUnit 4 `ExternalResource` with the same constructors, `create()`, `createApplication()`, `getTester()` (D10); same for `filter.WicketOrientDbFilterTesterScope`
- [ ] Test-jar: add a Jupiter extension (e.g. `WicketOrientDbTesterExtension`) for new tests
- [ ] Tests: `junit-bom` + `junit-jupiter` + `junit-vintage-engine` (test scope); Wicket 9 testers use `org.junit.jupiter.api.Assertions` (wicket-core declares Jupiter as `provided`, so the test classpath must have it — note for Orienteer)
- [ ] `junit.framework.AssertionFailedError` in `WicketOrientDbTester.IterativeTestResult`: keep (JUnit 4 stays via vintage) or switch to `AssertionError` (API change → handoff)
- [ ] Compile against Wicket 9 and fix what breaks; expected touch points (Appendix D): `OrientDbWebApplication` (`PropertyResolver.setLocator`, `getAjaxRequestTargetListeners`, `getExceptionMapperProvider`), `NvlModel extends ChainingModel`, request-cycle listeners, `OrientDefaultExceptionMapper extends DefaultExceptionMapper`
- [ ] Verify no inline JS/CSS is emitted by the library (Wicket 9 strict CSP); none found in the Stage A inventory
- [ ] danekja lambdas: keep (D9); `org.apache.wicket.util.time`, page store, `ModalWindow`: not used — confirm with `rg`
- [ ] Demo: `wicket-devutils` 9.24; Jetty 9.4 stays (javax)
- [ ] Update `AGENTS.md` files and "Handoff to Orienteer" (JUnit 5 requirement for `OrienteerTester`)
- [ ] Exit check: `./mvnw clean verify` green on 21/25; `./mvnw install`; Orienteer's `orienteer-core` compiles against it on its P5 branch

## C — Stage C: jakarta, Wicket 10, OkHttp current (→ Orienteer P6)

Goal: no `javax.servlet` left; Wicket 10.x; `jakarta.servlet-api` 6.0 `provided`; OkHttp current. Size: **S–M (1–2 days)**.

- [ ] Prerequisite: Stage B done (`2.1` released); version `3.0-SNAPSHOT` → `3.0` (D13; breaking `asHttpServletRequest`) — never installed as `2.0-SNAPSHOT` or `2.1-SNAPSHOT`
- [ ] Wicket 9.24 → 10.x (10.11.0 seen); add `org.apache.wicket:wicket-tester` (the test-jar's `WicketOrientDbTester` needs it — Orienteer must add it too)
- [ ] `javax.servlet` → `jakarta.servlet` (3 files: `rest/ReverseProxyResource`, `utils/LombokExtensions`, test-jar `junit/WicketOrientDbTester`); `LombokExtensions.asHttpServletRequest` return type changes (**API change → handoff**)
- [ ] `javax.servlet-api` → `jakarta.servlet:jakarta.servlet-api` 6.0.0 `provided` (Wicket 10 baseline)
- [ ] Guice 7 / `jakarta.inject`: not used by this library — nothing to do (verify with `rg 'javax.inject|com.google.inject'`)
- [ ] OkHttp 4.9.0 → current 5.x (or at least 4.12.0): replace internal `okhttp3.internal.http.HttpMethod.permitsRequestBody` in `ReverseProxyResource.mapRequestBody` with a local check; `RequestBody.create(MediaType, byte[])` → `RequestBody.create(byte[], MediaType)`; check whether Maven needs `okhttp-jvm` for 5.x; bump `logging-interceptor`; report kotlin-stdlib/okio changes (Orienteer rproxy uses okhttp3 transitively)
- [ ] SLF4J 2 arrives with Wicket 10: demo binding `log4j-slf4j-impl` → `log4j-slf4j2-impl`
- [ ] Demo: `web.xml` 2.5 → Jakarta EE 10 `web-app_6_0.xsd`; `jetty-maven-plugin` 9.4 → `org.eclipse.jetty.ee10:jetty-ee10-maven-plugin` 12.1.x; rewrite `Start.java` for Jetty 12 (`jetty-all` no longer exists) — the demo is kept (D15)
- [ ] Enforcer `bannedDependencies`: `javax.servlet:*`, `javax.inject:javax.inject`
- [ ] Update `AGENTS.md` files and "Handoff to Orienteer"
- [ ] Exit check: `rg 'javax\.servlet'` finds nothing; `./mvnw clean verify` green on 21/25; `./mvnw install`; Orienteer P6 compiles against it

## R — Release & CI

- [x] GitHub Actions workflow: push + PR, matrix JDK 21/25 (`actions/setup-java`, `distribution: temurin`, `cache: maven`), `./mvnw -B verify`, upload surefire reports, `concurrency` to cancel superseded runs (tests bind ports 2424+/2480+ — one job per runner) — 2026-09-24: `.github/workflows/ci.yml` (push to any branch, PR, manual; `actions/checkout@v7`, `setup-java@v6`, `upload-artifact@v7`; `contents: read`). Validated against the GitHub workflow JSON schema; first real run happens when the owner pushes. No publishing in CI (D14)
- [x] Remove `.travis.yml`; remove Travis/Coveralls badges from `README.md` — 2026-09-24; README now shows the GitHub Actions CI badge
- [x] Replace OSSRH `distributionManagement` and `nexus-staging-maven-plugin` with `org.sonatype.central:central-publishing-maven-plugin` (0.11.0 seen); `ru.ydn` namespace verified on the Central Portal (owner, 2026-09-24, D14) — 2026-09-24: plugin 0.11.0 as build extension in the root `<build><plugins>` (version in `pluginManagement`), `publishingServerId=central`, `autoPublish` default false (manual publish in the portal), `deploymentName` = `wicket-orientdb <version>`; `distributionManagement` removed entirely (the plugin would otherwise fall back to it for snapshots)
- [x] Release profile: sources + javadoc jars (fix doclint errors, e.g. the broken `{@link LegacyOrientDBHttpAPIResource}` in `OrientDBHttpAPIResource`), GPG signing (`maven-gpg-plugin` 3.x, loopback pinentry in CI), required POM metadata check — 2026-09-24: profile `release` = sources + test-sources + javadoc jars + `gpg:sign` (3.2.8, only in this profile; it adds `--pinentry-mode loopback` itself when `MAVEN_GPG_PASSPHRASE` is set). Javadoc: the 2 doclint errors (`LegacyOrientDBHttpAPIResource`) fixed; `doclint=all,-missing` → 0 warnings. POM metadata: name, description, url, licenses (now https), developers, scm present; `url`/`scm` no longer get `/<artifactId>` appended in modules (`child.*.inherit.append.path=false`), scm `connection` fixed to `scm:git:https://…`. Demo skips sources/javadoc/gpg. Without a key `gpg:sign` fails as expected ("No secret key")
- [x] Snapshot publishing to `https://central.sonatype.com/repository/maven-snapshots/` (decided: yes, D14); tell Orienteer to add that repository (snapshots only) — 2026-09-24: configured via property `central.snapshots.url` (`centralSnapshotsUrl`); the first real deploy is the owner item below; the repository snippet for Orienteer is in the handoff
- [x] maven-release-plugin 2.5.3 → 3.x or a CI-driven `versions:set` + tag; keep tag format `v@{project.version}`; first release version: `2.0` from Stage A (D13) — 2026-09-24: 3.3.1, `pushChanges=false` + `localCheckout=true` (prepare commits/tags locally, the owner pushes; perform builds the local tag with `-Prelease` and runs `deploy`); deprecated `useReleaseProfile` dropped. Default next development version after `2.0` is `2.1-SNAPSHOT` (matches D13)
- [x] Demo module keeps `maven-deploy-plugin` `skip` (and is excluded from Central publishing) — 2026-09-24: `excludeArtifacts` = `wicket-orientdb-demo`; the plugin (0.11.0, checked in its bytecode) applies it on both the snapshot and the release path
- [x] `.github/dependabot.yml` (maven + github-actions, weekly, grouped); ignore majors of Wicket/servlet/OkHttp until their stages — 2026-09-24: groups for Maven plugins, GraalVM and all GitHub Actions; ignored until their stages: Wicket majors, `javax.servlet-api`, OkHttp, Jetty minor/major (demo), OrientDB minor/major (3.2.x), JUnit 4 major, Log4j major. Validated against the Dependabot JSON schema; active once pushed
- [x] JaCoCo report (replaces cobertura/coveralls), optional CodeQL — 2026-09-24: `jacoco-maven-plugin` 0.8.15, `prepare-agent` + `report` at `verify`, instrumenting only `ru/ydn/**` (OrientDB/GraalJS untouched); no thresholds. Green on 21 and 25; baseline coverage of `wicket-orientdb`: 51% instructions, 53% lines (150 classes). CI uploads the report from the JDK 21 job. CodeQL not added (optional, owner's call)
- [x] Local validation without any upload — 2026-09-24: `./mvnw -B clean verify` green on JDK 21 and 25 (103/17/0 + demo 1/0, JaCoCo on); `-Prelease -Dgpg.skip` builds sources, test-sources and javadoc jars (0 doclint warnings). A Central bundle can't be produced by the plugin without running `deploy`: in 0.11.0 `skipPublishing=true` drops every artifact before staging (it's a per-module exclude, not "bundle only"), and a release deploy needs the `central` credentials before staging. So the bundle was assembled offline exactly like the plugin does (Maven layout, `maven-metadata` removed, md5/sha1/sha256/sha512) from a `-Prelease -Dgpg.skip verify` build of a throwaway `git archive` copy set to `2.0` (never installed): 7 artifacts + 28 checksums, required POM metadata OK on both POMs, library `.pom/.jar/-sources/-javadoc` present, no demo artifacts; only the `.asc` signatures are missing (gpg skipped). File list in the handoff
- [x] Owner: Central Portal user token in `~/.m2/settings.xml` (server `central`), SNAPSHOTs enabled for `ru.ydn` in the portal, first `2.0-SNAPSHOT` deploy (D14) — runbook below — 2026-09-24: the token is in the settings server `ossrh` (not `central`), so the server id became a pom property (`central.server.id`, default `central`). At the owner's explicit request the agent ran `./mvnw -B clean deploy -Prelease -Dgpg.skip -Dcentral.server.id=ossrh` from `45cac93` on JDK 21: tests 103/17/0 + demo 1/0, uploaded build **`2.0-20260925.025933-1`** (parent pom; library pom, jar, `tests`, `sources`, `test-sources`, `javadoc`), demo not uploaded, no credentials in the log. Unsigned (no GPG key yet)
- [ ] Owner: release `2.0` once Orienteer P3 is green against `2.0-SNAPSHOT` (GPG key published to a keyserver); then `master` → `2.1-SNAPSHOT` (D13)
- [ ] Optional, owner's call: publish from CI (GitHub secrets + a workflow on tags) and CodeQL — not implemented; CI only builds and tests
- [x] Exit check: a tagged release (or snapshot) resolves from Central in a clean `~/.m2`; Orienteer's "Stage A delivered" item can be ticked — 2026-09-24 (snapshot): a throwaway consumer pom with only the Central Portal snapshots repository resolved `wicket-orientdb` jar + test-jar `2.0-SNAPSHOT` and all 71 artifacts into an empty local repo; both jars byte-identical (SHA-1) to the local build; no GraalVM 21.3.5. The `2.0` release remains open (item above)
- [ ] Owner, recommended: `~/.m2/settings.xml` is world-readable (`644`) with plain-text tokens → `chmod 600 ~/.m2/settings.xml` and/or encrypt (`mvn --encrypt-master-password`, `mvn --encrypt-password`; the plugin supports encrypted passwords). Optionally copy the Portal token to a server `central` to drop `-Dcentral.server.id=ossrh`

### Publishing runbook (owner only — D14)

Prerequisites (one-off):
1. **Central Portal user token** (https://central.sonatype.com → account → *Generate User Token*) in `~/.m2/settings.xml`,
   server id `central` (the plugin also accepts a password encrypted with `mvn --encrypt-password`):
   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username><!-- token username --></username>
         <password><!-- token password --></password>
       </server>
     </servers>
   </settings>
   ```
   If the token sits under another server id, add `-Dcentral.server.id=<id>` to the deploy/release commands (property in the root pom).
2. **Enable SNAPSHOTs** for the `ru.ydn` namespace: https://central.sonatype.com/publishing/namespaces → `ru.ydn` → ⋮ → *Enable SNAPSHOTs*.
3. **GPG key (needed for `2.0`, not for snapshots):** `gpg --full-generate-key` (RSA 4096, `Ilia Naryzhny <phantom@ydn.ru>`),
   then `gpg --keyserver keyserver.ubuntu.com --send-keys <KEYID>` (Central also looks at keys.openpgp.org — verify the e-mail there).
4. **CI publishing (optional, not set up):** GitHub secrets `CENTRAL_TOKEN_USERNAME`, `CENTRAL_TOKEN_PASSWORD`,
   `MAVEN_GPG_PRIVATE_KEY` (`gpg --armor --export-secret-keys <KEYID>`), `MAVEN_GPG_PASSPHRASE`, plus a workflow using
   `actions/setup-java` `server-id: central`, `server-username`/`server-password`, `gpg-private-key`, `gpg-passphrase`.

Snapshot `2.0-SNAPSHOT` (now; repeat after every fix Orienteer P3 needs):
```bash
git switch master && git pull && git status                 # clean tree at 2.0-SNAPSHOT
./mvnw -B clean deploy -Prelease -Dgpg.skip -Dcentral.server.id=ossrh   # token currently under server `ossrh`; -SNAPSHOT → Central Portal snapshots
curl -s https://central.sonatype.com/repository/maven-snapshots/ru/ydn/wicket/wicket-orientdb/wicket-orientdb/2.0-SNAPSHOT/maven-metadata.xml
```
(Once a GPG key exists, drop `-Dgpg.skip` to sign snapshots too. Snapshots are not validated and expire after 90 days.)

Release `2.0` (only after Orienteer P3 is green against `2.0-SNAPSHOT` — D13):
```bash
./mvnw -B release:prepare -DreleaseVersion=2.0 -DdevelopmentVersion=2.1-SNAPSHOT -Dtag=v2.0   # local commits + tag only
./mvnw -B release:perform -Darguments=-Dcentral.server.id=ossrh   # builds tag v2.0 from the local clone with -Prelease: signs, uploads the bundle, waits for validation
# → https://central.sonatype.com/publishing/deployments : inspect "wicket-orientdb 2.0", then Publish (or Drop and fix)
git push origin master && git push origin v2.0                                                 # after publishing
```
If validation fails before anything was pushed: Drop the deployment, `git tag -d v2.0`, `git reset --hard HEAD~2`
(the two `[maven-release-plugin]` commits), `./mvnw release:clean`, fix, repeat. Central releases are immutable once published.
Not exercised yet (would upload): the plugin's snapshot/release upload paths and `release:perform`; first real run is the owner's.

## X — Cleanup, docs, known bugs & security

### Docs & cleanup
- [ ] README: ~~drop dead badges~~ (done in R, 2026-09-24), add prerequisites (JDK 21, `./mvnw`), fix outdated examples (`OServerAdmin`/`local:` URLs → `OrientDB`/`createIfNotExists`, `setAdminUserPassword` → `setAdminPassword`)
- [ ] `.gitignore`: add `.DS_Store`, `.vscode/`, `*.log`
- [ ] Library tests run without an SLF4J binding (NOP warning, Wicket logs dropped); optionally add a test-scoped binding + `src/test/resources` config (never in the jar)
- [x] ~~Delete stale remote branches~~ `future`, `wicket7-upgrade`, `wicket8`, `dependabot/maven/org.apache.wicket-wicket-core-8.12.0` — 2026-09-24, owner: **keep for now** (D16); closed, nothing deleted
- [ ] `rest.WicketSessionCookieJar` uses `org.apache.commons.collections4.map.HashedMap` (transitive via wicket-util) → `java.util.HashMap`, or declare the dependency
- [ ] Keep all `AGENTS.md` files current at the end of every stage

### Known bugs / tech debt
- [ ] Audit tests that iterate without asserting a count (the `OQueryModel.iterator` bug hid behind such a loop in `testOQueryProvider`)
- [ ] Re-check the 17 `@Ignore`d tests against OrientDB 3.2.56 (issues #8067, #9421 and the unnamed ones in `TestInAppOrientDBCompatibility`, `TestFilters`); re-enable the ones that pass
- [ ] `TestInAppOrientDBCompatibility.getImmutableClass` reflects into OrientDB's private `ODocument.getImmutableSchemaClass` — fragile across OrientDB upgrades
- [ ] Workarounds waiting for OrientDB fixes: `model/ODocumentModel` (line ~106), `utils/query/filter/ClassInstanceOfFilterCriteria` (OrientDB #8797), `OUserCatchPasswordHook`
- [ ] Guava `Function`/`Predicate` in public signatures — long-term candidate for `java.util.function` (breaking; coordinate with Orienteer)

### Security
- [ ] Credential handling (open question): session password kept in `OrientDbWebSession` and forwarded as Basic auth by `OrientDBHttpAPIResource`
- [ ] OkHttp/kotlin-stdlib CVEs (D5) — closed by Stage C
- [ ] Demo: default `admin/admin` guest user and an expired quickstart keystore — demo only, document

---

## Appendix A — Dependency versions (current → target)

"Latest" = latest stable on Maven Central on 2026-09-24.

| Dependency | Current | Latest seen | Target | Stage |
|---|---|---|---|---|
| JDK (`release`) | 1.8 | 25 LTS (27 non-LTS) | 21, verified on 21 + 25 | A |
| Apache Wicket (core, extensions, auth-roles, devutils) | 8.15.0 | 10.11.0 (9.x: 9.24.0; 8.x: 8.19.0) | 8.15 (A) → 9.24 (B) → 10.x (C) | B/C |
| Servlet API (`provided`) | javax 3.0.1 | jakarta 6.1.0 | javax 3.1.0 (B) → jakarta 6.0.0 (C) | B/C |
| OrientDB (core, server, client) | 3.2.27 | 3.2.56 | 3.2.56 | A |
| GraalJS/Truffle (OrientDB brings 21.3.5 via its `java11` profile) | 21.3.5 | 25.2.4 (25.0.x line: 25.0.4) | 25.0.4, declared by the library (D11); back to "follow OrientDB" once it ships a JDK 22+-capable Graal | A |
| Guava | 33.0.0-jre | 33.7.1-jre | keep in A; bump in X/R | — |
| OkHttp + logging-interceptor | 4.9.0 | 5.5.0 (4.x: 4.12.0) | 4.9.0 (A, D5) → current (C) | C |
| Lombok (`provided`) | 1.18.16 | 1.18.48 | 1.18.48 | A |
| JUnit 4 / Jupiter | 4.13.1 / — | 4.13.2 / 6.x | 4.13.2 (A) + Jupiter/vintage (B) | A/B |
| Mockito | — (unused) | 5.24.0 | not added | — |
| SLF4J (via Wicket) | 1.7.25 | 2.0.x | follows Wicket (2.x with Wicket 10) | C |
| Log4j 2 binding (demo only) | log4j-slf4j-impl 2.14.0 | 2.26.1 | 2.26.1 (A) → `log4j-slf4j2-impl` (C) | A/C |
| Jetty (demo plugin + `jetty-all` test dep) | 9.4.12.v20180830 | 12.1.x (9.4.58 last 9.4) | 9.4.58 (A) → 12.1 ee10 (C) | A/C |
| Maven | 3.9.16 (Homebrew) | 3.9.16 (4.0 RC) | wrapper 3.9.16 | A |

## Appendix B — Maven plugins (current → latest seen 2026-09-24)

| Plugin | Current | Latest seen | Action (stage) |
|---|---|---|---|
| maven-compiler-plugin | 3.7.0 | 3.16.0 | bump, `release` 21, Lombok APT path (A) |
| maven-surefire-plugin | unpinned (Maven default 3.5.4) | 3.6.0 | pin (A) |
| maven-jar-plugin | 3.0.2 (module) | 3.5.1 | pin in root (A) |
| maven-war-plugin (demo) | unpinned (Maven default 3.5.1) | 3.5.1 | pin — < 3.3.1 fails on JDK 16+ (A) |
| maven-resources/clean/install/deploy | unpinned / deploy 2.7 (demo) | 3.5.0 / 3.5.0 / 3.2.0 / 3.2.0 | pin (A) |
| maven-dependency / versions (helper goals) | unpinned | 3.11.0 / 2.22.0 | pin (A) |
| maven-checkstyle-plugin (+ Checkstyle) | 3.0.0 (6.18) | 3.6.0 (14.1.0) | bump + config migration (A, D4) |
| maven-enforcer-plugin | — | 3.6.3 | add (A) |
| maven-wrapper-plugin | — | 3.3.4 | wrapper (A) |
| jetty-maven-plugin (demo) | 9.4.12 | 9.4.58 → ee10 12.1.x | bump (A), replace (C) |
| maven-source / javadoc / gpg (release profile) | 3.0.1 / 2.10.4 / 1.6 | 3.4.0 / 3.12.0 / 3.2.8 | pinned (A), wired up in `release` profile (R, done) |
| maven-release-plugin | 2.5.3 | 3.3.1 | 3.3.1 (R, done) |
| nexus-staging-maven-plugin | 1.6.8 | OSSRH gone | removed → central-publishing-maven-plugin 0.11.0 (R, done) |
| maven-bundle-plugin (extension) | 3.3.0 | 6.x | remove with `type=bundle` (A) |
| maven-eclipse-plugin, m2e lifecycle-mapping | 2.10, 1.0.0 | retired / IDE-only | remove (A) |
| cobertura-maven-plugin, coveralls-maven-plugin | 2.7, 4.3.0 | dead | removed (A) → jacoco-maven-plugin 0.8.15 (R, done) |

## Appendix C — Test inventory (static count 2026-09-24, confirmed by surefire in A2)

Surefire 3.5.4 (Maven default, used in A2) reports a class-level `@Ignore` as **one** skipped test; surefire 3.6.0
(pinned in A5) reports each of its 4 methods, so from A5 on the totals are **103 run / 17 skipped**.

| Test class | `@Test` | `@Ignore` | Surefire JDK 21 (A2): run / skipped | Notes |
|---|---|---|---|---|
| `TestModels` | 18 | 0 | 18 / 0 | |
| `filter.TestFilters` | 26 | 3 | 26 / 3 | own scope `WicketOrientDbFilterTesterScope` |
| `orientdb.TestInAppOrientDBCompatibility` | 22 | 10 | 22 / 10 | OrientDB behavior checks; #8067, #9421 |
| `orientdb.TestStandaloneOrientDBCompatibility` | 4 | 4 (class-level) | 1 / 1 | needs a remote OrientDB server |
| `rest.TestRestApi` | 8 | 0 | 8 / 0 | real HTTP to the embedded server; JS functions (GraalJS) |
| `TestSecurity` | 6 (+1 commented out) | 0 | 6 / 0 | |
| `MainUtilsTest` | 6 | 0 | 6 / 0 | |
| `proto.TestPrototypers` | 5 | 0 | 5 / 0 | |
| `TestRegisterHooks` | 2 | 0 | 2 / 0 | |
| `TestFlexyMetaDataKey` | 2 | 0 | 2 / 0 | |
| `OrientDBSettingsTest` | 1 | 0 | 1 / 0 | |
| `converter.ConvertersTests` | 1 → 3 (A2) | 0 | 3 / 0 | plain unit test (`HexConverter`) |
| **wicket-orientdb total** | **101 → 103** | **17** | **100 / 14**, 0 failures | 12 classes |
| demo `TestHomePage` | 1 | 0 | 1 / 0 | WicketTester + embedded server |

Stage A exit (A11, surefire 3.6.0, OrientDB 3.2.56, GraalJS 25.0.4): **JDK 21.0.11 and JDK 25.0.4: 103 run, 17 skipped,
0 failures, 0 errors**; demo 1 run. The 17 skipped are exactly the static `@Ignore`s above.

## Appendix D — Stage B/C inventory (2026-09-24)

| Item | Where (main / test-jar) | Stage |
|---|---|---|
| `javax.servlet` | `rest/ReverseProxyResource`, `utils/LombokExtensions` (public `asHttpServletRequest`) / `junit/WicketOrientDbTester` | C |
| `javax.inject`, Guice, `javax.mail` | none | — |
| `javax.xml.bind` | `converter/HexConverter` / `converter/ConvertersTests` | A |
| `org.danekja` serializable lambdas | `model/FunctionModel`, `converter/SerializableConverter`, `converter/AbstractJointConverter` (public API) | keep (D9) |
| `org.apache.wicket.util.time`, `IDataStore`/page store, `ModalWindow`, `AbstractRequestCycleListener`, `IChainingModel`, `ClientInfo` | none | — |
| `WicketTesterScope` (removed in Wicket 9) | — / `junit/WicketOrientDbTesterScope`, `filter/WicketOrientDbFilterTesterScope`, 9 test classes use them | B |
| `junit.framework.AssertionFailedError` | — / `junit/WicketOrientDbTester` | B |
| Servlet 3.1 abstract methods on `ServletInputStream` | — / `junit/WicketOrientDbTester` | B |
| `WicketTester` in separate `wicket-tester` artifact | — / `junit/*` | C |
| OkHttp internal API `okhttp3.internal.http.HttpMethod` | `rest/ReverseProxyResource` | C |
| Inline JS/CSS markup (CSP) | none (no `.html` in main) | — |
| Demo: `web.xml` 2.5, Jetty 9 `Start.java` + `jetty-all` | demo | C |

## Appendix E — Useful commands

```bash
./mvnw versions:display-dependency-updates versions:display-plugin-updates   # what's outdated
./mvnw -pl wicket-orientdb dependency:tree -Dincludes=<groupId>              # who pulls a dependency
./mvnw -pl wicket-orientdb dependency:analyze                                # used-undeclared / unused-declared
./mvnw -pl wicket-orientdb test -Dtest=TestRestApi                           # one test class
unzip -l wicket-orientdb/target/wicket-orientdb-2.0-SNAPSHOT-tests.jar      # what the test-jar ships
javap -v -cp wicket-orientdb/target/classes ru.ydn.wicket.wicketorientdb.OrientDbWebApplication | grep major
```

## Handoff to Orienteer

Stage A, 2026-09-24. **`2.0-SNAPSHOT` is published** to the Central Portal snapshots repository (build `2.0-20260925.025933-1`, unsigned): Orienteer resolves it with the repository snippet in §4, no local install needed. Snapshots expire after 90 days; each redeploy (runbook, phase R) creates a new timestamped build. `2.0` is released after Orienteer P3 is green (D13).

### 1. Installed artifacts (`~/.m2/repository/ru/ydn/wicket/wicket-orientdb/`)

| Coordinates | File |
|---|---|
| `ru.ydn.wicket.wicket-orientdb:wicket-orientdb-parent:2.0-SNAPSHOT:pom` | `wicket-orientdb-parent/2.0-SNAPSHOT/wicket-orientdb-parent-2.0-SNAPSHOT.pom` |
| `ru.ydn.wicket.wicket-orientdb:wicket-orientdb:2.0-SNAPSHOT:jar` | `wicket-orientdb/2.0-SNAPSHOT/wicket-orientdb-2.0-SNAPSHOT.jar` (177 classes) |
| `ru.ydn.wicket.wicket-orientdb:wicket-orientdb:2.0-SNAPSHOT:test-jar` (classifier `tests`) | `wicket-orientdb/2.0-SNAPSHOT/wicket-orientdb-2.0-SNAPSHOT-tests.jar` (42 classes) |
| (demo, not for Orienteer) `wicket-orientdb-demo:2.0-SNAPSHOT:war` | `wicket-orientdb-demo/2.0-SNAPSHOT/` |

- **Bytecode:** `--release 21`, so every class file is major version **65**; the manifest says `Build-Jdk-Spec: 21`. JDK 17 cannot load the classes.
- **Built and tested with:** Temurin 21.0.11 and Temurin 25.0.4, using Maven 3.9.16 through `./mvnw`.

### 2. Versions compiled against

| Library | Version | Scope in the library pom |
|---|---|---|
| Apache Wicket (core, extensions, auth-roles) | 8.15.0 | compile |
| Servlet API | `javax.servlet:javax.servlet-api` 3.0.1 | **provided** |
| OrientDB (core, server → client, tools) | **3.2.56** (was 3.2.27) | compile |
| GraalJS (`polyglot`, `js-scriptengine`; `js` pom at runtime) | **25.0.4**, replacing OrientDB's GraalVM 21.3.5 (D11) | compile / runtime |
| Guava | 33.0.0-jre | compile |
| OkHttp + logging-interceptor | 4.9.0 (kotlin-stdlib 1.4.10, okio 2.8.0) | compile |
| Jackson | `jackson-core` 2.22.0, transitive from OrientDB (was 2.15.2); no databind | — |
| SLF4J | `slf4j-api` 1.7.25, transitive from Wicket; **no binding** | — |
| Log4j | not a library dependency. The demo only uses `log4j-slf4j-impl` 2.26.1 | — |
| Guice / `javax.inject` | not used | — |
| Lombok | 1.18.48, declared in `annotationProcessorPaths` | provided |
| JUnit | 4.13.2 | test |

### 3. JVM flags Orienteer must mirror

- **Required: none.** Every test passes on JDK 21 and on JDK 25 without `--add-opens` or `--add-exports`.
- **Optional, and only for quieter logs or better diagnostics:**
  - `--add-opens java.base/java.io=ALL-UNNAMED` (valid on JDK 21 and 25): Wicket 8's `CheckingObjectOutputStream` needs this to print a field path for "not serializable" errors in development mode. Without it Wicket silently falls back to a less detailed message.
  - `--enable-native-access=ALL-UNNAMED` (JDK 21 and 25): silences the JDK 25 warning about Truffle's `System::load`.
  - `--sun-misc-unsafe-memory-access=allow`: silences the `sun.misc.Unsafe::objectFieldOffset` warning Truffle triggers on JDK 25. This flag exists **only on JDK 23+**, and JDK 21 refuses to start with it, so put it in a JDK-activated profile or leave it out.
  - `-Dpolyglot.engine.WarnInterpreterOnly=false`: silences Truffle's "fallback runtime" warning, which appears on both JDKs and existed before this change.
- **Not seen in tests:** OrientDB's jar manifest lists `Add-Opens: jdk.unsupported/sun.misc java.base/sun.security.x509`. The tests didn't need these, so only add them if the Orienteer standalone jar or Docker image shows problems.

### 4. Actions required in Orienteer (P1/P3)

1. **Exclude GraalVM 21.3.5 from Orienteer's own `orientdb-core` declaration.**
   - Why it's needed: Orienteer declares `orientdb-core` directly. Maven then takes the GraalVM artifacts from that declaration, and Orienteer ends up with a mix of 21.3.5 and 25.0.4. `graal-sdk` 21.3.5 and `polyglot` 25.0.4 both contain `org.graalvm.polyglot`, and `truffle-api` 21.3.5 sits next to `truffle-runtime` 25.0.4. I reproduced this with a throwaway consumer pom.
   - Fix: add these exclusions to the `orientdb-core` entry in the root `dependencyManagement`. With them the mix disappears, which I also verified. GraalJS 25.0.4 then arrives transitively through wicket-orientdb.
     ```xml
     <exclusions>
       <exclusion><groupId>org.graalvm.sdk</groupId><artifactId>*</artifactId></exclusion>
       <exclusion><groupId>org.graalvm.truffle</groupId><artifactId>*</artifactId></exclusion>
       <exclusion><groupId>org.graalvm.js</groupId><artifactId>*</artifactId></exclusion>
       <exclusion><groupId>org.graalvm.tools</groupId><artifactId>*</artifactId></exclusion>
     </exclusions>
     ```
     Among the OrientDB 3.2.56 artifacts Orienteer uses, only `orientdb-core` pulls in GraalVM; server, client, distributed, graphdb, etl, lucene and tools do not.
   - This also covers Orienteer P3's "Nashorn replacement" item: GraalJS is available through `javax.script` under the names `js`, `javascript` and `graal.js`.
2. **Jetty 9.4 on JDK 25:** Jetty 9.4.58 bundles ASM 9.7.1, and its annotation scanning fails on the Java 25 multi-release classes in `bcprov-jdk18on` 1.85. OrientDB 3.2.56 brings that jar in through `orientdb-server` → `bcpkix`. Orienteer's Jetty 9.4 stopgap therefore needs ASM ≥ 9.8 (this repo uses 9.10.1) as a `jetty-maven-plugin` dependency, and likewise on any Jetty 9.4 runtime classpath (standalone or Docker). The problem goes away with Jetty 12 in P6.
3. **OrientDB result sets:** OrientDB 3.2.56 returns nothing from an `OResultSet` once it has been closed. `OQueryModel.iterator` depended on that and is now fixed. I found no such pattern in Orienteer's 12 files that use `OResultSet`, but watch for code that returns a stream or iterator out of a `try (OResultSet …)` block.
4. **Repositories:** the parent pom declares no repositories. `2.0-SNAPSHOT` is deployed (phase R), so Orienteer adds this to its
   root pom (snapshots only) instead of relying on a local install (verified with a clean local repo); drop it when Orienteer switches to `2.0`:
   ```xml
   <repository>
     <id>central-portal-snapshots</id>
     <url>https://central.sonatype.com/repository/maven-snapshots/</url>
     <releases><enabled>false</enabled></releases>
     <snapshots><enabled>true</enabled></snapshots>
   </repository>
   ```

### 5. Public API changes

- **None**, in either the main jar or the test-jar (`ru.ydn.wicket.wicketorientdb.junit`). Only two main-source files changed, and only their implementation:
  - `converter/HexConverter`: `javax.xml.bind` replaced with `java.util.HexFormat`. Output and error behavior are the same.
  - `model/OQueryModel.iterator(...)`: now reads the whole page before closing the result set. The signature is unchanged. This is a behavior fix: on OrientDB 3.2.56 every page came back empty.

### 6. Transitive dependency changes (runtime classpath of `wicket-orientdb`, baseline → Stage A)

| Change | Artifacts | Cause |
|---|---|---|
| upgraded | `orientdb-core/-server/-client/-tools` 3.2.27 → 3.2.56; `jackson-core` 2.15.2 → 2.22.0 | OrientDB bump |
| relocated | `org.lz4:lz4-java` 1.8.0 → `at.yawk.lz4:lz4-java` 1.11.0 (same `net.jpountz` packages) | OrientDB 3.2.56 |
| **removed** | `commons-lang:commons-lang` 2.6 (Orienteer has no `org.apache.commons.lang.` imports) | OrientDB 3.2.56 |
| **added** | `org.bouncycastle:bcprov/bcpkix/bcutil-jdk18on` 1.85 | OrientDB 3.2.56 server |
| **replaced** | `org.graalvm.sdk:graal-sdk`, `truffle-api` (compile), `js`, `js-scriptengine`, `regex`, `com.ibm.icu:icu4j` 71.1, `org.graalvm.tools:profiler`/`chromeinspector` 21.3.5 → `polyglot`, `collections`, `nativeimage`, `word`, `js-scriptengine` (compile), `js-language`, `regex`, `truffle-api`/`-runtime`/`-compiler`, `jniutils`, `org.graalvm.shadowed:icu4j`/`xz` 25.0.4 (runtime) | D11 |
| unchanged | Wicket 8.15.0 and its dependencies (openjson, commons-io 2.6, commons-fileupload 1.5, commons-collections4 4.4, danekja 1.8.6, slf4j-api 1.7.25), Guava 33.0.0-jre, OkHttp 4.9.0 / kotlin 1.4.10, jnr/asm 9.2, JAXB 3.0.x from orientdb-tools | — |

In total the runtime classpath went from 61 to 69 artifacts. Note that `truffle-api` moved from compile to runtime scope, which only matters if Orienteer imports `com.oracle.truffle`; it doesn't.

### 7. Test results

| JDK | wicket-orientdb | demo |
|---|---|---|
| Temurin 21.0.11 | 103 run, 0 failures, 0 errors, 17 skipped | 1 run, 0 failures |
| Temurin 25.0.4 | 103 run, 0 failures, 0 errors, 17 skipped | 1 run, 0 failures |

The 17 skipped tests are `@Ignore`d in the source; nothing was disabled in Stage A:
- 10 in `TestInAppOrientDBCompatibility`: OrientDB behavior issues, including #8067 and #9421.
- 3 in `TestFilters`.
- 4 in `TestStandaloneOrientDBCompatibility`: the whole class is ignored because it needs a remote OrientDB on `localhost`.

Two tests were added (`ConvertersTests`), and one existing assertion was strengthened (`TestModels.testOQueryProvider` now checks how many items it iterated). The demo's `jetty:run` serves its home page and the REST bridge on both JDKs. Checkstyle 14.1.0 reports 0 violations.

### 8. Work for Stages B and C (plan sections B and C)

- **Stage B** (Wicket 9.24, javax, Servlet 3.1). Estimate: **S, 0.5–1 day.**
  - Rewrite `WicketOrientDbTesterScope` and `WicketOrientDbFilterTesterScope`, because Wicket 9 removes `WicketTesterScope`. Keep the same public API on top of JUnit 4 `ExternalResource`, and add a Jupiter extension.
  - Implement the Servlet 3.1 `ServletInputStream` methods in `WicketOrientDbTester.executeUrl`.
  - Add Jupiter and vintage to the test classpath. Orienteer's tests must add them too, because Wicket 9 testers use Jupiter assertions.
  - Fix whatever fails to compile.
  - Nothing else in the migration guide applies: no page store, `ModalWindow`, `util.time` or inline JS. Danekja lambdas can stay (D9).
- **Stage C** (Wicket 10, jakarta Servlet 6, OkHttp current). Estimate: **S–M, 1–2 days.**
  - `javax.servlet` → `jakarta.servlet` in 3 files. This is **one API change**: the return type of `LombokExtensions.asHttpServletRequest`.
  - Add the `wicket-tester` artifact. Orienteer needs it too.
  - Move to OkHttp 5 (or 4.12), dropping the internal `HttpMethod` and fixing the `RequestBody.create` argument order.
  - Switch the demo to `log4j-slf4j2-impl` and port it to Jetty 12 ee10 (the demo is kept, D15).
  - Guice 7 and `jakarta.inject` are **not needed**: the library uses neither.

### 9. Open questions for the owner (answers recorded 2026-09-24)

1. **GraalJS swap (D11):** do you accept it? — **Accepted**; Orienteer mirrors it as its D8.
2. **Stage B:** deliver it separately, or go straight from A to C? — **Separate stage (D12), not started now**; Orienteer consumes Stage A during its P1–P4. New constraint from Orienteer D9 (proposed): Stage B/C must not overwrite `2.0-SNAPSHOT` in `~/.m2` → **D13 (decided)**: `2.0-SNAPSHOT` published now, `2.0` released after Orienteer P3 is green, then `2.1-SNAPSHOT` (B) and `3.0-SNAPSHOT` (C).
3. **Snapshots vs. release:** publish `2.0-SNAPSHOT` to the Central Portal snapshot repository, or release it? Is the `ru.ydn` namespace verified on the Central Portal? Orienteer's "Stage A delivered" item needs one of the two (phase R). — **Both, in order (D13/D14):** `2.0-SNAPSHOT` to the snapshots repository now, `2.0` release after Orienteer P3 is green. `ru.ydn` is verified.
4. **Demo module:** keep it through Stage C (Jetty 12 port), or drop it? — **Keep, Jetty 12 ee10 port in Stage C (D15).**
5. **Cleanup:** delete the fully merged remote branches `future`, `wicket7-upgrade` and `wicket8`, plus the stale `dependabot/*` branch? — **Keep for now (D16); no branches deleted.**

### 10. Phase R — Central bundle (local dry run, 2026-09-24)

What the owner's `release:perform` for `2.0` will upload, minus the `.asc` signatures (every file below also gets `.asc` when signed).
Each file has `.md5`, `.sha1`, `.sha256` and `.sha512` next to it (28 checksum files); 35 entries in total. The demo is excluded.

| Size (bytes) | Path |
|---|---|
| 15543 | `ru/ydn/wicket/wicket-orientdb/wicket-orientdb-parent/2.0/wicket-orientdb-parent-2.0.pom` |
| 3886 | `ru/ydn/wicket/wicket-orientdb/wicket-orientdb/2.0/wicket-orientdb-2.0.pom` |
| 245957 | `ru/ydn/wicket/wicket-orientdb/wicket-orientdb/2.0/wicket-orientdb-2.0.jar` |
| 87728 | `ru/ydn/wicket/wicket-orientdb/wicket-orientdb/2.0/wicket-orientdb-2.0-tests.jar` |
| 131154 | `ru/ydn/wicket/wicket-orientdb/wicket-orientdb/2.0/wicket-orientdb-2.0-sources.jar` |
| 35298 | `ru/ydn/wicket/wicket-orientdb/wicket-orientdb/2.0/wicket-orientdb-2.0-test-sources.jar` |
| 926769 | `ru/ydn/wicket/wicket-orientdb/wicket-orientdb/2.0/wicket-orientdb-2.0-javadoc.jar` |
