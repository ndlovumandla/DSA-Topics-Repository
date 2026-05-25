# Queue: Telkom Call Centre Routing System

## Storyline
Telkom's national call centre handles around 45 000 inbound calls per day across technical faults, billing queries, and new connections.
Each tier has its own agent pool, and when all agents are busy the call enters a waiting queue until an agent becomes free.
Calls are answered strictly in the order they arrived, so the system must preserve FIFO behaviour exactly.
Developer Mpho builds one standard queue per tier and reports queue depth plus estimated wait time in real time.
A linked queue is the right tool here because enqueue and dequeue both stay O(1) without shifting elements around.

## Concepts Demonstrated
| Concept | Where |
|---|---|
| FIFO | each tier answers the oldest waiting call first |
| enqueue | `routeIncomingCall()` / `enqueueCall()` |
| dequeue | `answerNextCall()` / `dequeueNextCall()` |
| queue depth | dashboard counts and wait-time estimates |
| not-found handling | `cancelCallEverywhere()` |
| duplicate values | repeated caller names are preserved in order |

## How to Run (Windows PowerShell)
From the repository root:

```powershell
cd .\03-Queue\Telkom-Call-Centre-Routing-System
javac TelkomCallCentreApp.java
java TelkomCallCentreApp
```

## Difficulty
Intermediate
