# Super-actors
### An akka library containing useful actors and configurations encapsulating implementing various design patterns

version 0.1

#### Packages
##### Mediator
This includes a standalone actor class which implements the mediator pattern (original Gang of Four)
so that you can use it as a single point of communication across a number of actors.
It has the ability to route messages from an actor to a particular set of actors or else broadcast a message
to all actors registered, all depending on the message type. It handles delivery correctly so that you don't ever
receive what you send and moreover, don't receive multiple copies.

Check out my [blog](www.blog.anishgeorge.com)
