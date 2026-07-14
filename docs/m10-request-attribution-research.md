# M10 request attribution research

## Authorization

The existing administrator authorization permits requester names and request messages from the public Queue and
History interfaces to be displayed in this unofficial, non-commercial Android app. The existing 60-second shared
automatic/manual refresh limit remains unchanged.

## Verified public representation

On July 14, 2026, a read-only check of the public `Queue_Played` interface confirmed that an attributed row exposes
one `req-text` element. The requester is explicit profile-link text following `Request By:`. When a member supplied
a message, it appeared as a separate italic child after the requester. Rows without a request have no attribution
element. No cookie or authenticated value is needed to read this data.

The parser therefore keeps attribution separate from the track title, accepts only the explicit `req-text`
structure, bounds the requester to 80 characters and the optional message to 240 characters, and renders both as
plain text. It does not infer a requester from other links or community data. Death.FM continues to use its compact
feed, which does not expose per-row attribution.

## Optional message submission

On July 14, 2026, the administrator exposed the authenticated StreamingSoundtracks.com screen shown immediately
after a request is accepted. Its visible confirmation states that the request has already been delivered, then
offers a separate optional message form. The form uses `POST` to the same-origin Album module's `submitmessage`
action with the verified album and numeric song identifiers. It names the message field `msg`, includes the submit
value `send=Send`, and its published counter truncates input at 80 characters.

The native confirmation dialog mirrors that 80-character limit. A single explicit confirmation first performs the
existing one-shot song request. A non-blank message produces at most one same-origin authenticated form post only
when the accepted response contains a valid station-generated message form. The song mutation is never retried.
Blank messages produce no second request, and the message remains transient and is neither logged nor persisted.

The first live attempt queued the song but did not display its message. Follow-up inspection found two legacy-form
details missing from the initial adapter: the browser also submits the read-only remaining-character control, and
the station redirects the accepted HTTPS request through its public `www` HTTP alias. The corrected adapter submits
all three successful controls (`msg`, `send`, and `remLen`) and canonicalizes only the verified SST alias back to
`https://streamingsoundtracks.com`. Protected cookies are never sent over HTTP, and no alias was assumed for the
other stations.

A second controlled Razr attempt and a subsequent app attempt both queued songs without their messages. The earlier
timeout-recovery theory was then tested with a fresh authenticated browser request rather than another guessed app
mutation. `Just Testing` from *Soleil Rouge* used song ID `263260` in the request link, but the accepted response
generated message-record ID `2055716` in the optional form. The message `M10 fresh browser workflow` was submitted
through that fresh form, acknowledged as saved, and displayed in the public Queue.

The distinct IDs establish the root cause: the adapter had incorrectly reused the song ID for `submitmessage`.
It now parses the station-generated form action from the accepted response; validates its same-origin scheme, host,
path, Album action, matching album ID, numeric message ID, and expected controls; uses the actual response URL as
referer; and recognizes the station's saved-message confirmation. If the accepted response cannot be read, the
required message ID is unavailable, so the adapter does not guess, post a message, or retry the song. A 60-second
read timeout gives the slow response more time.

The next native attempt also showed that a response can remain open after the complete confirmation form has
arrived. The adapter now stops reading the accepted response as soon as that complete form is present, and stops
reading the message response as soon as its saved-message acknowledgement is present. This keeps the existing
one-shot request/message contract while avoiding dependence on the remainder of a large legacy page. The behavior
is covered by a regression test using responses larger than the normal safety limit.

Final native verification used the VIP account and an explicit least-played suggestion. `Clipped Ears` by Nicholas
Pike from *For the Love of Spock* entered Queue with `Requested by MorG` and the exact message
`M10-VIP-least-2-20260714`. The native Queue rendered the requester and message separately. Earlier station-reported
success pages that did not produce a Queue row demonstrated why acknowledgements are not described as verified:
the app now requires an explicit request-success phrase before posting a message and directs the listener to confirm
Queue before requesting again.

Only StreamingSoundtracks.com advertises the request-message capability because that is the station whose exact
authenticated form contract was inspected. The other four stations retain native song requesting without the
message field until their post-request forms are independently verified.
