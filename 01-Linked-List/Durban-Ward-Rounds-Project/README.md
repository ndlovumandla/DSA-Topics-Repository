# Linked List: Durban Hospital Patient Ward Rounds

## Storyline
King Edward VIII Hospital in Durban runs morning ward rounds in bed order, but overnight admissions and discharges make the order dynamic.
Ward administrator Zanele used paper arrows to track these changes, which became messy whenever patients were inserted between existing beds.
Sipho, the hospital systems developer, modeled the ward as a linked structure where each patient is a node.
The doctor can move forward through the round using next links and backward for revisits using previous links.
This matches real workflow better than array shifting because insertions and deletions are done by pointer rewiring.

## Why Linked Lists Here?
- New admissions can be inserted after a clinically relevant bed without shifting many elements.
- Discharged patients can be removed by reconnecting neighboring nodes.
- Forward rounds use next pointers (singly linked behavior).
- Backward revisits use previous pointers (doubly linked behavior).

## File
- Java source: DurbanWardRoundsApp.java

## How to Run (Windows PowerShell)
From repository root:

```powershell
cd .\01-Linked-List\Durban-Ward-Rounds-Project
javac DurbanWardRoundsApp.java
java DurbanWardRoundsApp
```

## Learning Goals Demonstrated
- Node structure for patients
- head pointer management
- next pointer traversal
- singly linked forward traversal
- doubly linked reverse traversal
- insertion and deletion by pointer updates
- edge cases: empty ward and not-found scenarios
