# nexarank-java-sdk

Java/Spring Boot SDK for NexaRank — two independent clients over the same
`NexaRankConfig`:

- **`NexaRankClient`** — the search query pipeline (`/api/v1/rules/enrich`): boosts, pins, buries, synonyms, personalization/diversity hints.
- **`ContentEnrichClient`** (NR-104) — Experience Manager content zones (`/api/v1/content/enrich`): banners, promo grids, and other page content controlled by content rules.

Both fail open: on any network error, timeout, or non-2xx response they
return an empty/passthrough result rather than throwing, so a NexaRank
outage never breaks the storefront.

Not published to Maven Central (deferred to Phase 29 — see ADR-01/CLAUDE.md).
Install locally:

```
mvn install -Dgpg.skip=true
```

## ContentEnrichClient

```java
ContentEnrichResult content = contentEnrichClient.enrich(
    List.of(ContentZone.HERO_BANNER, ContentZone.ANNOUNCEMENT_BAR),
    PageContext.of().with("pageType", "homepage")
);

ContentZoneResult hero = content.get(ContentZone.HERO_BANNER);
if (hero != null) {
    renderBanner(hero.getImageUrl(), hero.getHeadline());
}
```

With the Spring Boot starter, `ContentEnrichClient` is auto-configured from
`nexarank.*` properties (`base-url`, `api-key`, `tenant-id`, `project-id`,
`connect-timeout-ms`, `read-timeout-ms`, `max-retries`, `base-delay-ms`) —
the same properties `NexaRankClient` uses, since both clients talk to the
same nexarank-api deployment. See
[`examples/SpringBootContentExample.java`](examples/SpringBootContentExample.java).

`max-retries`/`base-delay-ms` are read by `ContentEnrichClient` only —
`NexaRankClient`'s query-pipeline `enrich()` predates retry support and is
unchanged by this addition.

## Modules

- `nexarank-client` — plain Java, no Spring dependency. `HttpURLConnection`, not `java.net.http.HttpClient` (Java 25 AArch64 SSL bug — see CLAUDE.md).
- `nexarank-spring-boot-starter` — auto-configuration wiring both clients as beans.
