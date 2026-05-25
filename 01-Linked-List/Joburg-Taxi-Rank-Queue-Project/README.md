# Linked List: Joburg Taxi Rank Queue

## Storyline
Noord Street taxi rank in Johannesburg is one of the busiest in Africa, moving thousands of commuters across more than 200 destinations every morning.
The old clipboard-and-marshal system caused commuters to be skipped or double-counted, so the rank authority hired developer Thabo to build a digital tracker.
Each waiting commuter is represented as a node storing a name, destination, and a reference to the next person in line.
Commuters join at the back in normal circumstances, but marshals can immediately place elderly or disabled passengers at the front with O(1) priority insertion.
When someone leaves for another reason, the marshal removes them from anywhere in the chain by rewiring pointers — no shifting, no overflow.
A linked list is the ideal structure here because the queue changes hundreds of times per hour and has no fixed upper limit on size.

## Concepts Demonstrated
| Concept | Where it appears |
|---|---|
| Node | `CommuterNode` class |
| head | `queueFront` pointer |
| next pointer | `nextCommuter` field |
| singly linked | `printQueueForward()` |
| doubly linked | `prevCommuter` field, `printQueueBackward()` |
| traversal | `printQueueForward`, `printQueueBackward` |
| insertion | `joinAtBack`, `priorityInsertAtFront` |
| deletion | `boardTaxiFromFront`, `removeCommuterByName` |

## How to Run (Windows PowerShell)
From the repository root:

```powershell
cd .\01-Linked-List\Joburg-Taxi-Rank-Queue-Project
javac JoburgTaxiRankApp.java
java JoburgTaxiRankApp
```

## Difficulty
Beginner — clean single implementation, thorough inline comments and Big-O labels throughout.
