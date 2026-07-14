# M8 chat research

## Current authorization boundary

M8 will add native station chat. Existing administrator permissions cover public queue/history and native
authentication, but explicitly do not cover chat reads or writes. No chat endpoint, page traffic, session value,
or message content has been inspected, requested, or captured for this milestone.

The safe groundwork contains only:

- a station-scoped `ChatRepository` contract;
- immutable unavailable, loading, ready, and error states;
- a minimal immutable author-and-message domain model;
- an unavailable implementation with no network transport;
- ViewModel observation only while Chat is selected; and
- unit coverage for station switching, lifecycle cancellation, and the unavailable boundary.

Chat capability flags remain disabled for all five stations. No posting method is defined until the permitted
write behavior and protocol are known.

## Authorization needed for live research

Before live protocol research or implementation, a station administrator must authorize the unofficial,
non-commercial native Android app to access the public-facing chat interfaces across the intended stations and
state whether permission includes:

1. reading messages;
2. posting user-entered messages through a retained authenticated session;
3. displaying participant names, timestamps, roles, avatars, links, or moderation notices;
4. a maximum polling frequency or permission to use the site's persistent connection mechanism; and
5. least-privileged testing, including a harmless test message if writes are authorized.

Authorization for authentication alone does not imply chat access. Song-request submission remains a separate
future milestone.

## Planned safeguards

- Keep Compose dependent only on immutable state and upward actions.
- Keep the ViewModel dependent only on `ChatRepository`.
- Keep authentication cookies inside the data layer and station scope.
- Bound message counts, text lengths, response sizes, redirects, and reconnect behavior.
- Render remote text as plain text; do not execute HTML or introduce a WebView.
- Do not persist message history unless separately justified and authorized.
- Keep public playback and queue/history independent of chat availability or authentication failure.
