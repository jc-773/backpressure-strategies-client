# Backpressure Strategies

## What is it

Modern reactive systems must handle data flow effectively under varying demand and supply conditions. This client demonstrates how to apply different backpressure techniques in a real-world setting, using a simulated web traffic source to mimic high-throughput environments.
Think of Reactor Sinks as the nozzle on a water hose. It controls when and how the water (data) is released.

## Examples include

### BackPressureBackupQueue.java

- Implements a backup queue that collects dropped items when the downstream can’t keep up. Uses onBackpressureBuffer with OverflowStrategy.OLDEST, which retains the most recent data and drops the oldest when the buffer overflows
- Created a custom queue for WebTraffic events. This custom queue has a default load factor of 75%. The queue will pause from accepting events and drain if it crosses the default threshold (the plan to make this concurrent is in the works)
- Using Flux.merge() from an infinite source caused "source starvation" where one large source starved the other. Forcing the infinite source to run on its own thread resolved the issue

### BackPressureLimitRate.java

- Uses limitRate(n) from Project Reactor to manage request flow from downstream to upstream. This controls how many elements a subscriber can request at a time, effectively applying backpressure at the source

### BackPressureSinksBuffer.java

- Demonstrates use of Reactor Sinks (such as Sinks.many().multicast() or unicast()) with buffering behavior when demand is backpressured. The sink can buffer emitted elements until subscribers are ready.
