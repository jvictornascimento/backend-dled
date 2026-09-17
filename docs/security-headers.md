# Production Security Headers

## Decisions

The backend sets security headers for every Spring Security response. The reverse proxy remains responsible for TLS termination, HTTP to HTTPS redirects, certificate renewal and any edge-specific header overrides.

Production must run behind HTTPS. When TLS terminates before the application, the proxy must forward `X-Forwarded-Proto`, `X-Forwarded-Host` and `X-Forwarded-Port`; the `prod` profile enables `server.forward-headers-strategy=framework` so Spring can understand the original request.

Configured headers:

| Header | Production value | Decision |
| --- | --- | --- |
| `Strict-Transport-Security` | `max-age=31536000 ; includeSubDomains` | Enforce HTTPS for one year on the domain and subdomains. Do not enable `preload` until every subdomain is confirmed HTTPS-only and the team is ready for browser preload submission. |
| `Content-Security-Policy` | API-focused `default-src 'self'` policy with Swagger-compatible local inline script/style support | Keep a restrictive default while allowing local Swagger/dev tooling. Swagger is disabled in production. |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Preserve useful same-origin referrers and avoid leaking full paths to other origins. |
| `Permissions-Policy` | Disable browser capabilities that the API does not need | Reduce accidental exposure of camera, microphone, geolocation, payment and similar APIs. |

## Proxy Responsibility

Configure the reverse proxy to:

- Redirect plain HTTP traffic to HTTPS before it reaches the application.
- Forward the original scheme and host with `X-Forwarded-*` headers.
- Avoid replacing application headers with weaker values.
- Optionally duplicate HSTS at the edge only if the value matches this document.

## Rollout

1. Deploy to a staging environment behind the same kind of proxy used in production.
2. Verify headers with `curl -I https://<domain>/actuator/health`.
3. Confirm authenticated browser flows still work with the frontend origin configured in `CORS_ORIGIN`.
4. Watch browser console CSP reports manually during the first production deploy.
5. Consider HSTS `preload` only after all current and future subdomains are HTTPS-only.
