package ratelimiter;


// Rate Limiting Using Token Bucket Filter

// Imagine you have a bucket that gets filled with tokens at the rate of 1 token per second.
// The bucket can hold a maximum of N tokens. Implement a thread-safe class that lets threads get a token when one is
// available. If no token is available, then the token-requesting threads should block.
// The class should expose an API called getToken that various threads can call to get a token
// Implementing rate limiting using a naive token bucket filter algorithm.