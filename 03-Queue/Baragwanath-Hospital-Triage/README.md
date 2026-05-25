# Queue: Baragwanath Hospital Emergency Triage

## Storyline
Chris Hani Baragwanath Hospital in Soweto receives over 1 000 emergency patients every day.
The triage desk classifies patients into four priority levels: red, orange, yellow, and green.
Critical patients skip ahead of everyone, but patients in the same severity band must still be served in the order they arrived.
Developer Lungelo builds a priority queue from scratch using four FIFO lanes so the highest-priority non-empty lane is always served first.
That design preserves urgency across levels while keeping arrival order stable within each level.

## Concepts Demonstrated
| Concept | Where |
|---|---|
| FIFO | patients in the same lane are treated in arrival order |
| enqueue | `enqueuePatient()` |
| dequeue | `dequeueNextPatient()` |
| circular queue | each severity lane uses a wrap-around buffer |
| priority queue | the highest non-empty lane is always selected first |
| not-found handling | `dischargePatientById()` |

## How to Run (Windows PowerShell)
From the repository root:

```powershell
cd .\03-Queue\Baragwanath-Hospital-Triage
javac BaragwanathTriageApp.java
java BaragwanathTriageApp
```

## Difficulty
Advanced
