# Queue: Cape Town MyCiTi Bus System

## Storyline
Cape Town's MyCiTi buses arrive every 6 to 12 minutes, and busy stops such as Civic Centre can hold around 200 waiting passengers.
Passengers board in strict first-come-first-served order, so the stop must behave like a queue: enqueue at the back and dequeue from the front.
When a bus reaches capacity, the remaining passengers stay at the front of the queue for the next service instead of being reshuffled.
Amara implements a circular queue so the stop can reuse fixed memory efficiently without shifting all passengers after each boarding.
The same queue idea also powers breadth-first search, so the program includes a small route explorer to show that connection clearly.

## Concepts Demonstrated
| Concept | Where |
|---|---|
| FIFO | passengers board in arrival order |
| enqueue | `enqueuePassenger()` |
| dequeue | `dequeuePassengerForBus()` |
| circular queue | wrap-around buffer in the main queue |
| BFS | `demonstrateBreadthFirstSearch()` |
| traversal | queue printing and graph visiting |

## How to Run (Windows PowerShell)
From the repository root:

```powershell
cd .\03-Queue\Cape-Town-MyCiTi-Bus-System
javac CapeTownMyCiTiApp.java
java CapeTownMyCiTiApp
```

## Difficulty
Beginner
