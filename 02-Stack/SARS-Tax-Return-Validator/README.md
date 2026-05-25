# Stack: SARS Tax Return Validator

## Storyline
SARS processes 6 million eFiling tax returns each year, each containing deeply nested section tags — income blocks inside employment sections inside personal profiles.
A common submission error is a mismatched or unclosed tag, which breaks downstream processing and delays refunds.
Developer Priya builds a bracket-matching validator: every opening tag is pushed onto a stack, and every closing tag pops and checks for a correct pair.
If the stack is empty at the end and every pop matched, the return is valid — otherwise the exact mismatch position is reported back to the taxpayer.
A stack is the only correct structure because nested sections must close in reverse opening order, which is exactly the LIFO contract.

## Concepts Demonstrated
| Concept | Where |
|---|---|
| LIFO | innermost open tag checked first on close |
| push | `pushOpeningTag()` |
| pop | `popOpeningTag()` |
| peek | `peekInnermostTag()` — error reporting |
| bracket matching | full `validate()` algorithm |
| call stack | nesting mirrors JVM call frames |

## How to Run (Windows PowerShell)
From the repository root:
```powershell
cd .\02-Stack\SARS-Tax-Return-Validator
javac SARSTaxReturnValidatorApp.java
java SARSTaxReturnValidatorApp
```

## Difficulty
Advanced
