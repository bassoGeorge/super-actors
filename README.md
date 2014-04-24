# Super-actors
##### An akka library containing useful actors and configurations implementing various design patterns

version 0.1
By [Anish 'basso' George](http://anishgeorge.com)

## Packages
### Mediator
This includes a standalone actor class which implements the mediator pattern (original Gang of Four)
so that you can use it as a single point of communication across a number of actors.
It has the ability to route messages from an actor to a particular set of actors or else broadcast a message
to all actors registered, all depending on the message type. It handles delivery correctly so that you don't ever
receive what you send and moreover, don't receive multiple copies.

### Filter
This package provides a system to create a Filtered service actor which can register one or more pre-processor
and post-processor filter actors. Just mix in the `Filter` trait into a class (and implement a single method), create a `FilteredService` with the target actor and make sure all your intented messages mix in the `Message` trait, your good to go. A single filter can be registered with multiple FilteredServices and there is more flexiblity involved with the ability to enable or disable filters on the go.
