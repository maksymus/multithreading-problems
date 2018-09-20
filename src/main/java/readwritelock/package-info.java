package readwritelock;

// ReadWrite Lock
// Synchronization of multiple reader threads and a single writer thread.

// Imagine you have an application where you have multiple readers and a single writer.
// You are asked to design a lock which lets multiple readers read at the same time, but only one writer write at a time.