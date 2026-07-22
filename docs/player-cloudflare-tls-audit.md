# Player Cloudflare and TLS read-only audit

Audited July 22, 2026. This record contains no Cloudflare account identifier,
zone identifier, origin address, token value, mail value, certificate private
material, or validation token. No Cloudflare, DNS, Webuzo, SSL, site-content,
GitHub, or production setting was changed.

## Verified Cloudflare state

| Area | Verified state |
| --- | --- |
| Zone | `jamesjennison.net` is active, unpaused, full-setup, and on the Free Website plan |
| Authoritative DNS | Cloudflare nameservers are authoritative |
| Apex | Proxied A record points to the Webuzo origin; TTL is Auto |
| `www` | Proxied CNAME points to the apex; TTL is Auto |
| Status | Proxied Cloudflare record remains present |
| Mail | DNS-only mail record plus MX, SRV, and TXT dependencies remain present and out of scope |
| Player | No A, AAAA, or CNAME record exists |
| Player aliases | No `www.player` or `mail.player` record exists |
| Wildcard DNS | No wildcard record exists |
| CAA | No CAA record exists |
| Legacy Page Rules | No active or disabled Page Rules exist |
| DNS settings | CNAME flatten-all disabled; multi-provider DNS disabled; secondary overrides disabled |

The dedicated DNS token is active and can read the zone and DNS configuration.
The existing Wrangler OAuth session can identify the zone. Neither credential
has permission to read Zone Settings, SSL and Certificates, or modern Rulesets.
The API returned authorization failures without exposing either credential.

The owner then supplied a separate user API token for the expanded audit. Its
secret is stored outside the repository with mode `0600`, and Cloudflare's token
verification endpoint reports it active. Cloudflare nevertheless rejects direct
reads of the approved zone, DNS, Zone Settings, SSL/certificate, and Rulesets
resources. This indicates that its effective resource selection or permission
policy does not include the target zone/account. No settings data was returned,
and repeated authentication attempts were stopped after Cloudflare began
rate-limiting the failed requests.

## Verified public behavior

- Cloudflare redirects HTTP apex and HTTP `www` requests permanently to HTTPS
  on the same hostname while preserving path and query string.
- The direct Webuzo HTTP origin does not perform that redirect. The redirect is
  therefore applied at the Cloudflare layer, but its exact implementation is
  unverified because Zone Settings and modern Rulesets are not readable.
- HTTPS `www` does not redirect to apex. The approved master canonical redirect
  is still not configured.
- The active Cloudflare edge certificate presented for `www` contains the apex
  and `*.jamesjennison.net`, is issued by Google Trust Services, and expires
  October 17, 2026.
- A direct SNI probe for `player.jamesjennison.net` at the Cloudflare edge
  presents that valid wildcard certificate. Cloudflare then returns its
  no-DNS-record response, as expected.

This proves that edge certificate coverage is ready for a first-level Player
record. It does not prove the zone's origin encryption mode or certificate-pack
dashboard state.

## Verified Webuzo origin state

| Certificate | Coverage | Issuer and trust | Validity | Status |
| --- | --- | --- | --- | --- |
| Existing master wildcard | Apex and `*.jamesjennison.net` | Let's Encrypt; publicly trusted | Through August 27, 2026 | Valid now; recent renewal health is not established |
| Player placeholder | `player.jamesjennison.net` common name only | Self-signed; not publicly trusted | Through July 22, 2027 | Staging only; incompatible with Full (Strict) |

Webuzo runs its Let's Encrypt renewal command daily at midnight. Webuzo also
attempted a Player certificate immediately after the approved subdomain was
created, despite certificate issuance being disabled in the API request. It
pre-checked the generated `www.player` and `mail.player` aliases, skipped them
when they could not resolve, and attempted only the primary Player hostname.
Issuance failed solely because Player had no A or AAAA record, which was the
approved staging state.

The earlier master-wildcard renewal attempt failed when the Cloudflare-served
HTTP-01 challenge returned 404. That separate renewal problem remains a risk to
the master domain and prevents assuming that future Player renewal will work
without a controlled test.

## Facts still requiring read-only access

The next Cloudflare read must confirm:

- SSL/TLS encryption mode, including whether it is already Full (Strict);
- Always Use HTTPS and Automatic HTTPS Rewrites;
- Universal SSL and edge certificate-pack status;
- minimum TLS version and TLS 1.3;
- HSTS state;
- redirect, transform, origin, response-header, and cache Rulesets;
- account-level bulk redirects that could affect the zone.

Use a separate read-only API token limited to the JamesJennison.net zone and
account with these permissions where available:

- Zone — Zone — Read;
- Zone — DNS — Read;
- Zone — Zone Settings — Read;
- Zone — SSL and Certificates — Read;
- Zone — Page Rules — Read;
- Zone — Transform Rules — Read;
- Account — Mass URL Redirects — Read;
- Account — Account Rulesets — Read.

The token must be stored locally with mode `0600` or supplied through a
connected Cloudflare integration. It must not be pasted into chat, committed,
or stored under a public document root.

## Recommended Player TLS and DNS sequence

The preferred certificate is a dedicated Webuzo-managed Let's Encrypt
certificate because it remains publicly trusted outside Cloudflare and keeps
Webuzo authoritative for issuance and renewal. Cloudflare Origin CA is the
documented fallback if Webuzo's HTTP-01 path cannot be made reliable; it should
not be the first choice while hosting independence is valuable.

1. Complete the missing read-only Cloudflare settings and Rulesets audit.
2. Take a fresh Restic snapshot of the Player virtual host, Webuzo database,
   Apache configuration, and certificate state.
3. Through Webuzo's SSL manager, temporarily assign the existing valid wildcard
   certificate to the Player virtual host. Do not hand-edit generated Apache
   configuration.
4. Validate direct origin HTTPS for the Player hostname and its complete chain.
5. Create exactly one Cloudflare A record: `player.jamesjennison.net`, pointing
   to the same origin target as the apex, proxied, TTL Auto. Do not create
   `www.player`, `mail.player`, AAAA, CNAME, or wildcard records.
6. Confirm edge certificate coverage, origin reachability, and Full (Strict)
   operation before treating the hostname as available.
7. Place a short-lived randomized HTTP-01 probe under the Player document root,
   retrieve it through Cloudflare without cache interference, then remove it.
8. If the probe passes, ask Webuzo to issue a dedicated Let's Encrypt
   certificate only for `player.jamesjennison.net`; generated aliases remain
   non-resolving and must not be requested as public names.
9. Validate the new certificate chain, Webuzo renewal ownership, route matrix,
   custom 404, logs, existing master/status/mail health, and the GitHub Pages
   fallback.
10. Request separate approval for security/cache headers and production
    cutover. Keep GitHub Pages unchanged until those gates pass.

If HTTP-01 fails, withdraw the Player DNS record or retain the valid temporary
wildcard while the hostname remains unannounced. Do not lower the zone from
Full (Strict). The alternative is a dedicated Cloudflare Origin CA certificate
generated from an origin-held key or CSR and installed through Webuzo. Origin
CA is compatible with Full (Strict), but direct browsers do not trust it and it
requires explicit expiry monitoring.

## Rollback

Before public DNS, restore the prior Player certificate and Webuzo state from
the fresh snapshot if any SSL installation fails. After DNS creation, remove
only the Player record to return the hostname to its current non-resolving
state, restore the prior Player certificate through Webuzo, and re-run apex,
`www`, status, mail, and GitHub Pages checks. No rollback step may alter the
master-site files, status service, mail DNS, unrelated domains, or shared
server configuration.

## Approval boundary

This audit does not authorize certificate installation or issuance, challenge
file creation, DNS records, Cloudflare setting changes, security headers,
GitHub changes, staging promotion, or production cutover.
