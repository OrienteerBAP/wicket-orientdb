# wicket-orientdb-demo

Sample war showing the library: `WicketApplication extends OrientDbWebApplication` with an embedded OrientDB server
(memory DB `WicketOrientDb`, `db.config.xml` next to the class), a `TestData` class filled with 50 random documents,
the REST bridge mounted at `/orientdb`, and `HomePage` listing the data. Root rules: [`../AGENTS.md`](../AGENTS.md).

- **Not published:** excluded from Central via `excludeArtifacts` of `central-publishing-maven-plugin` (root pom), and
  `maven-deploy-plugin` `skip=true`. Orienteer does not use it. **Kept** through Stage C, which ports it to Jetty 12 ee10 (plan D15).
- **Run:** `./mvnw install -DskipTests` once, then `./mvnw -pl wicket-orientdb-demo jetty:run` (Jetty 9.4.58 plugin, :8080;
  works on JDK 21 and 25 — the plugin gets ASM `${jetty.asm.version}` so it can scan Java 25 multi-release jars).
  `src/test/java/.../Start.java` is an alternative embedded-Jetty launcher using the test-scoped `jetty-all` uber jar
  and the Wicket quickstart `keystore` (expired certificate, password `wicket`).
- **Logging:** the only place with a logging binding (`log4j-slf4j-impl` 2.26.1) and `src/main/resources/log4j2.xml`. Keep it here.
- **Tests:** 1 (`TestHomePage`, WicketTester renders `HomePage`; starts the embedded server, binds 2424+/2480+).
- **Guest user** is `admin/admin` (demo only).

## Upgrade notes

- Stage C: `web.xml` is Servlet 2.5 (`java.sun.com` namespace) → Jakarta EE 10; Jetty 9.4 plugin and `Start.java`
  → Jetty 12 ee10 (`jetty-all` no longer exists); `SslContextFactory` → `SslContextFactory.Server`.
