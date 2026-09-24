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
| A | Stage A: JDK 21/25 build on the **same** framework majors (Wicket 8.15, javax, OrientDB 3.2.x), `./mvnw install` | P0 | P3 | todo |
| B | Stage B: Wicket 9.x, still javax; JUnit 5 in the test-jar | A | P5 | todo |
| C | Stage C: Wicket 10.x, `jakarta.servlet` 6, `wicket-tester`, OkHttp current | B | P6 | todo |
| R | Release & CI: GitHub Actions, Maven Central via the Central Portal (releases + snapshots) | A (CI), C (final release) | P3 "resolvable from a real repo" | todo |
| X | Cleanup, docs, known bugs & security | any time | — | todo |

## Guiding principles

1. **Never break the Orienteer contract** (below): coordinates, version `2.0-SNAPSHOT`, jar + test-jar, public API.
2. **Keep the build green after every item.** One concern per commit; no big-bang upgrades.
3. **Same framework majors per stage:** A changes JDK/tooling only; B changes Wicket 8 → 9 only; C does jakarta + Wicket 10.
   Coupled libraries move together: Wicket ↔ servlet API ↔ jakarta ↔ Jetty (demo).
4. **Minimal, behavior-preserving code changes.** Any unavoidable public API change goes to "Handoff to Orienteer".
5. **No reformat-only diffs;** match the surrounding indentation (mostly tabs).

## Decisions log

| # | Date | Decision | Status |
|---|---|---|---|
| D1 | 2026-09-24 | Target `--release 21`; verify on JDK 21 **and** 25 (Temurin 25.0.4 via SDKMAN). JDK 27 (non-LTS) is not targeted. Same as Orienteer D1. | accepted |
| D2 | 2026-09-24 | Coordinates and version stay `ru.ydn.wicket.wicket-orientdb:wicket-orientdb:2.0-SNAPSHOT` (+ `test-jar`, parent `wicket-orientdb-parent`) through all stages. The release version is decided in phase R. | accepted |
| D3 | 2026-09-24 | Stage A compiles and tests against **OrientDB 3.2.56** (latest 3.2.x; Orienteer contract row). Orienteer's `dependencyManagement` decides the runtime version. | accepted |
| D4 | 2026-09-24 | Checkstyle is upgraded in Stage A (plugin 3.6.0 + Checkstyle 14.1.0) with a semantically equivalent `check_style.xml`; only Javadoc fixes or documented deviations if the new engine flags more. | accepted |
| D5 | 2026-09-24 | OkHttp stays **4.9.0** in Stage A (no transitive change for Orienteer's rproxy). Upgrade in Stage C (known CVEs: CVE-2021-0341 in okhttp < 4.9.2, CVE-2020-29582 / CVE-2022-24329 in kotlin-stdlib 1.4.10). | accepted |
| D6 | 2026-09-24 | Tests stay on JUnit 4.13.2 in Stage A; Mockito is not added (unused). Jupiter + vintage come in Stage B because Wicket 9 testers need JUnit 5. | accepted |
| D7 | 2026-09-24 | Publishing (`distributionManagement`, `release` profile, nexus-staging, maven-release-plugin) is left untouched in Stage A and replaced in phase R (Central Portal). Only the dead `<repositories>` entry is removed in A. | accepted |
| D8 | 2026-09-24 | The demo module stays in the reactor while it builds; if it blocks a stage it is parked (removed from `<modules>`, code kept). | accepted |
| D9 | 2026-09-24 | Keep `org.danekja` `SerializableFunction` in the public API (`FunctionModel`, `SerializableConverter`, `AbstractJointConverter`): wicket-core 9.24.0 **and** 10.11.0 still depend on `org.danekja:jdk-serializable-functional`. Orienteer P5's "replace danekja lambdas" is optional, not forced by this library. | proposed |
| D10 | 2026-09-24 | Stage B keeps `junit.WicketOrientDbTesterScope` as a JUnit 4 rule with the same public API (re-implemented on `ExternalResource`, since Wicket 9 removes `WicketTesterScope`) and adds a Jupiter extension next to it; tests run via `junit-vintage-engine`. | proposed |

### Open questions (decide when the phase starts; record the answer above)

- [ ] **Stage B at all?** Orienteer D5 allows merging P5+P6 if this library jumps straight to Wicket 10. Deliver B separately (recommended: cheap here, gives Orienteer a javax-only intermediate) or go A → C? (B)
- [ ] **Release version and namespace:** release `2.0` after Stage C? Is the `ru.ydn` namespace verified on the Central Portal (needs DNS control of `ydn.ru`)? (R)
- [ ] **Snapshots:** publish `2.0-SNAPSHOT` to `https://central.sonatype.com/repository/maven-snapshots/` so Orienteer CI does not need a local install? (R)
- [ ] **Demo module:** keep after Stage C (Jetty 12 ee10 port) or drop it? (C)
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
- [ ] A3 Remove the dead `snapshots-repo` (oss.sonatype.org) from `<repositories>` (inherited by every consumer); `distributionManagement` stays until R (D7)
- [ ] A4 Remove dead plugins: `maven-eclipse-plugin` (+ `wtp.version`), `coveralls-maven-plugin`, `cobertura-maven-plugin`, m2e `lifecycle-mapping`, `maven-bundle-plugin` extension; drop `<type>bundle</type>` from the OrientDB entries in `dependencyManagement` and the explicit `${orientdb.version}` in the module pom
- [ ] A5 Pin every build plugin once in root `<pluginManagement>` (Appendix B); remove module-level versions (jar 3.0.2, deploy 2.7, jetty); release-profile source/javadoc/gpg pinned to current (javadoc 2.x is JDK-incompatible), nexus-staging/release-plugin left for R
- [ ] A5 Add `maven-enforcer-plugin`: `requireMavenVersion [3.9,)`, `requireJavaVersion [21,)`
- [ ] A6 Checkstyle plugin 3.0.0 → 3.6.0 with Checkstyle 14.1.0; migrate `check_style.xml` (DTD 1.3; `JavadocMethod`/`JavadocType` split into `Missing*` + validation; `LineLength` moved under `Checker` with `fileExtensions=java`); still `failOnViolation`, still at `validate`
- [ ] A7 JUnit 4.13.1 → 4.13.2
- [ ] A8 OrientDB 3.2.27 → 3.2.56 (D3); record the transitive dependency diff in the handoff
- [ ] A9 Library hygiene: servlet API `provided`; Lombok `provided`; no logging binding in compile/runtime scope; no `log4j2.xml`/`logback.xml` in the jar or test-jar (verify with `jar tf`); demo `log4j-slf4j-impl` 2.14.0 → 2.26.1 (Log4Shell-era version)
- [ ] A10 Demo: Jetty 9.4.12 → 9.4.58 (`jetty-maven-plugin`, test-scoped `jetty-all`); `jetty:run` serves the home page
- [ ] A11 Install Temurin 25.0.4 (SDKMAN, not default); `./mvnw clean verify` green on 21 **and** 25
- [ ] A11 `./mvnw install`; confirm `~/.m2/repository/ru/ydn/wicket/wicket-orientdb/wicket-orientdb/2.0-SNAPSHOT/` has the jar **and** `-tests.jar`, and the parent pom is installed; class files are major version 65
- [ ] A11 Update `AGENTS.md` (build status, stack) and write "Handoff to Orienteer" below
- [ ] Exit check: `./mvnw clean verify` green on JDK 21 and 25; `./mvnw install` done; test results match Appendix C (differences explained)

## B — Stage B: Wicket 8 → 9, still javax (→ Orienteer P5)

Goal: Wicket 9.24 (last 9.x), javax Servlet 3.1, green on 21/25, test-jar usable by Orienteer with JUnit 5.
Migration guide: https://cwiki.apache.org/confluence/display/WICKET/Migration+to+Wicket+9.0 · Size: **S (0.5–1 day)**.

- [ ] Resolve the open question "Stage B at all?"
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

- [ ] Wicket 9.24 → 10.x (10.11.0 seen); add `org.apache.wicket:wicket-tester` (the test-jar's `WicketOrientDbTester` needs it — Orienteer must add it too)
- [ ] `javax.servlet` → `jakarta.servlet` (3 files: `rest/ReverseProxyResource`, `utils/LombokExtensions`, test-jar `junit/WicketOrientDbTester`); `LombokExtensions.asHttpServletRequest` return type changes (**API change → handoff**)
- [ ] `javax.servlet-api` → `jakarta.servlet:jakarta.servlet-api` 6.0.0 `provided` (Wicket 10 baseline)
- [ ] Guice 7 / `jakarta.inject`: not used by this library — nothing to do (verify with `rg 'javax.inject|com.google.inject'`)
- [ ] OkHttp 4.9.0 → current 5.x (or at least 4.12.0): replace internal `okhttp3.internal.http.HttpMethod.permitsRequestBody` in `ReverseProxyResource.mapRequestBody` with a local check; `RequestBody.create(MediaType, byte[])` → `RequestBody.create(byte[], MediaType)`; check whether Maven needs `okhttp-jvm` for 5.x; bump `logging-interceptor`; report kotlin-stdlib/okio changes (Orienteer rproxy uses okhttp3 transitively)
- [ ] SLF4J 2 arrives with Wicket 10: demo binding `log4j-slf4j-impl` → `log4j-slf4j2-impl`
- [ ] Demo: `web.xml` 2.5 → Jakarta EE 10 `web-app_6_0.xsd`; `jetty-maven-plugin` 9.4 → `org.eclipse.jetty.ee10:jetty-ee10-maven-plugin` 12.1.x; rewrite or delete `Start.java` (`jetty-all` no longer exists) — or park the demo (D8)
- [ ] Enforcer `bannedDependencies`: `javax.servlet:*`, `javax.inject:javax.inject`
- [ ] Update `AGENTS.md` files and "Handoff to Orienteer"
- [ ] Exit check: `rg 'javax\.servlet'` finds nothing; `./mvnw clean verify` green on 21/25; `./mvnw install`; Orienteer P6 compiles against it

## R — Release & CI

- [ ] GitHub Actions workflow: push + PR, matrix JDK 21/25 (`actions/setup-java`, `distribution: temurin`, `cache: maven`), `./mvnw -B verify`, upload surefire reports, `concurrency` to cancel superseded runs (tests bind ports 2424+/2480+ — one job per runner)
- [ ] Remove `.travis.yml`; remove Travis/Coveralls badges from `README.md`
- [ ] Replace OSSRH `distributionManagement` and `nexus-staging-maven-plugin` with `org.sonatype.central:central-publishing-maven-plugin` (0.11.0 seen); verify the `ru.ydn` namespace on the Central Portal (owner)
- [ ] Release profile: sources + javadoc jars (fix doclint errors, e.g. the broken `{@link LegacyOrientDBHttpAPIResource}` in `OrientDBHttpAPIResource`), GPG signing (`maven-gpg-plugin` 3.x, loopback pinentry in CI), required POM metadata check
- [ ] Snapshot publishing to `https://central.sonatype.com/repository/maven-snapshots/` (if the open question says yes); tell Orienteer to add that repository (snapshots only)
- [ ] maven-release-plugin 2.5.3 → 3.x or a CI-driven `versions:set` + tag; keep tag format `v@{project.version}`; decide the first release version (open question)
- [ ] Demo module keeps `maven-deploy-plugin` `skip` (and is excluded from Central publishing)
- [ ] `.github/dependabot.yml` (maven + github-actions, weekly, grouped); ignore majors of Wicket/servlet/OkHttp until their stages
- [ ] JaCoCo report (replaces cobertura/coveralls), optional CodeQL
- [ ] Exit check: a tagged release (or snapshot) resolves from Central in a clean `~/.m2`; Orienteer's "Stage A delivered" item can be ticked

## X — Cleanup, docs, known bugs & security

### Docs & cleanup
- [ ] README: drop dead badges, add prerequisites (JDK 21, `./mvnw`), fix outdated examples (`OServerAdmin`/`local:` URLs → `OrientDB`/`createIfNotExists`, `setAdminUserPassword` → `setAdminPassword`)
- [ ] `.gitignore`: add `.DS_Store`, `.vscode/`, `*.log`
- [ ] Delete stale remote branches (owner): `future`, `wicket7-upgrade`, `wicket8` (fully merged), `dependabot/maven/org.apache.wicket-wicket-core-8.12.0` (superseded by 8.15)
- [ ] `rest.WicketSessionCookieJar` uses `org.apache.commons.collections4.map.HashedMap` (transitive via wicket-util) → `java.util.HashMap`, or declare the dependency
- [ ] Keep all `AGENTS.md` files current at the end of every stage

### Known bugs / tech debt
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
| GraalJS/Truffle (via OrientDB `java11` profile) | 21.3.5 | — | follow OrientDB | — |
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
| maven-surefire-plugin | unpinned (Maven default) | 3.6.0 | pin (A) |
| maven-jar-plugin | 3.0.2 (module) | 3.5.1 | pin in root (A) |
| maven-war-plugin (demo) | unpinned | 3.5.1 | pin — < 3.3.1 fails on JDK 16+ (A) |
| maven-resources/clean/install/deploy | unpinned / deploy 2.7 (demo) | 3.5.0 / 3.5.0 / 3.2.0 / 3.2.0 | pin (A) |
| maven-checkstyle-plugin (+ Checkstyle) | 3.0.0 (6.18) | 3.6.0 (14.1.0) | bump + config migration (A, D4) |
| maven-enforcer-plugin | — | 3.6.3 | add (A) |
| maven-wrapper-plugin | — | 3.3.4 | wrapper (A) |
| jetty-maven-plugin (demo) | 9.4.12 | 9.4.58 → ee10 12.1.x | bump (A), replace (C) |
| maven-source / javadoc / gpg (release profile) | 3.0.1 / 2.10.4 / 1.6 | 3.4.0 / 3.12.0 / 3.2.8 | pin (A), wire up (R) |
| maven-release-plugin | 2.5.3 | 3.3.1 | R |
| nexus-staging-maven-plugin | 1.6.8 | OSSRH gone | → central-publishing-maven-plugin 0.11.0 (R) |
| maven-bundle-plugin (extension) | 3.3.0 | 6.x | remove with `type=bundle` (A) |
| maven-eclipse-plugin, m2e lifecycle-mapping | 2.10, 1.0.0 | retired / IDE-only | remove (A) |
| cobertura-maven-plugin, coveralls-maven-plugin | 2.7, 4.3.0 | dead | remove (A) → JaCoCo (R) |

## Appendix C — Test inventory (static count 2026-09-24, confirmed by surefire in A2)

Surefire reports a class-level `@Ignore` as **one** skipped test, so "run" = active `@Test` − 4 + 1.

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

_Filled in when Stage A is complete (A11)._
