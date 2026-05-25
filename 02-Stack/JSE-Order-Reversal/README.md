# Stack: JSE Order Reversal

## Storyline
The JSE trading engine receives a continuous stream of buy and sell orders throughout the trading day.
When a listed company releases price-sensitive information, a trading halt requires all orders placed in the 90 seconds before the halt to be reversed in exact reverse chronological order.
Developer Rethabile implements the order reversal engine using a custom stack: orders are pushed as they arrive, and when the halt signal fires they are popped and inverted one by one.
A stack is the only structure that naturally preserves reverse-order access without sorting, indexing, or extra traversal overhead — push and pop are both O(1).

## Concepts Demonstrated
| Concept | Where |
|---|---|
| LIFO | newest order reversed first on halt |
| push | `captureOrder()` |
| pop | `popAndReverseOne()` |
| peek | `peekLatestOrder()` |
| undo mechanism | `executeHaltReversal()` sweeps entire window |
| traversal | `printCaptureWindow()` |

## How to Run (Windows PowerShell)
From the repository root:
```powershell
cd .\02-Stack\JSE-Order-Reversal
javac JSEOrderReversalApp.java
java JSEOrderReversalApp
```

## Difficulty
Advanced
