# Future product scope

## Request messages and requester display

Members should be able to add an optional message during an explicit, user-initiated song request. The native queue should display the station-provided requester identity and message when present. Before implementation, verify the station form field, length and content rules, server confirmation behavior, and representation across all five stations. Preserve eligibility and cooldown rules, never automate submissions, and do not infer requester data from unrelated page content.

## Private Messages

Signed-in members should be able to use a fully native station-scoped Private Messages experience: Inbox, message reading, composing, replying, sending, and receiving/refreshing. This requires explicit authorization and least-privileged protocol research for the authenticated PM interfaces before implementation. Define production refresh limits, notification behavior, local retention, deletion semantics, attachment scope, and cross-station account behavior first. Message bodies must not be logged, committed, or retained outside the minimum product requirement; protected station sessions remain in Android-protected storage.
