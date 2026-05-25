# Stack: Eskom Substation Control Panel

## Storyline
Eskom's Megawatt Park control room manages the national grid through a panel where every operator action — isolating a substation, rerouting load, boosting generation — is logged as a command.
During a 2019 grid emergency, a sequence of wrongly executed commands caused a cascade fault that had to be manually reversed in exact reverse order to restore the grid.
This incident proved that an undo engine built on a stack is the only safe recovery model: the most recent command must always be reversed first.
Developer Ayanda implements the command stack from scratch — no java.util.Stack — so the LIFO property and its O(1) cost are fully visible to the engineering team.

## Concepts Demonstrated
| Concept | Where |
|---|---|
| LIFO | newest command reversed first |
| push | `executeCommand()` |
| pop | `undoLastCommand()` |
| peek | `peekLastCommand()` |
| undo mechanism | `emergencyRollbackAll()` |
| traversal | `printCommandAuditTrail()` |

## How to Run (Windows PowerShell)
From the repository root:
```powershell
cd .\02-Stack\Eskom-Substation-Control-Panel
javac EskomControlPanelApp.java
java EskomControlPanelApp
```

## Difficulty
Beginner
