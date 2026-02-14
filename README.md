# Backpressure Strategies

## What is it

Modern reactive systems must handle data flow effectively under varying demand and supply conditions. This client demonstrates how to apply different w techniques in a real-world setting, using a simulated web traffic source to mimic high-throughput environments.2
Think of Reactor Sinks as the nozzle on a water hose. It controls when and how the water (data) is released.

## Examples include

### BackPressureBackupQueue.java

- Implements a backup queue that collects dropped items when the downstream can’t keep up.
- Uses onBackpressureBuffer with OverflowStrategy.OLDEST, which retains the most recent data and drops the oldest when the buffer overflows
- I created a custom queue for webtraffic events for when the buffer does overflow
- The custom queue will store overflowed events until the original buffer mentioned in step 1 empties out tp 75%
- Once the overflowed buffer empties out enough events to be down to 75% capacity, the backup queue will drain back into the buffer
- It is important to bring up my project reactor strategy. I created a separate flux that handles capacity and draining logic and one that handles the buffer
- Using Flux.merge() from an infinite source caused "source starvation" where one large source starved the other. Forcing the infinite source to run on its own thread resolved the issue

### BackPressureLimitRate.java

- Uses limitRate(n) from Project Reactor to manage request flow from downstream to upstream. This controls how many elements a subscriber can request at a time, effectively applying backpressure at the source

### BackPressureSinksBuffer.java

- Demonstrates use of Reactor Sinks (such as Sinks.many().multicast() or unicast()) with buffering behavior when demand is backpressured. The sink can buffer emitted elements until subscribers are ready.
