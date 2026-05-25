# Linked List: Cape Town Power Outage Fault Log

## Storyline
City Power Cape Town receives fault reports throughout load-shedding periods, each with a fault number, suburb, severity, and logged time.
Because work is prioritized by severity instead of arrival time, the log must allow inserting faults into priority positions.
On heavy nights, hundreds of faults arrive quickly, so fixed arrays become fragile due to capacity limits and costly shifting.
Naledi rebuilt the system as a linked list where each fault is a node connected by pointers.
Critical faults can be inserted at the head in O(1), resolved faults can be removed by rewiring links, and supervisors can traverse the list for real-time status.

## Why This Implementation
- Core structure is built from scratch (no java.util collections for list logic).
- Live fault log uses a singly linked list.
- A doubly linked snapshot is generated for reverse audit traversal.
- Demonstrates insertion, deletion, traversal, duplicates, and not-found edge cases.

## File
- Java source: CapeTownFaultLogApp.java

## How to Run (Windows PowerShell)
From repository root:

```powershell
cd .\01-Linked-List\Cape-Town-Fault-Log-Project
javac CapeTownFaultLogApp.java
java CapeTownFaultLogApp
```

## Learning Goals Demonstrated
- Node and head pointer fundamentals
- next pointer and singly linked traversal
- insertion at head O(1)
- insertion by severity O(n)
- deletion by rewiring pointers O(n)
- doubly linked reverse traversal via snapshot
