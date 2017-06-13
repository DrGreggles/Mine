# Mine
Minesweeper with an arbitrary topology

Functional, n-Dimensional minesweeper solver

Current topologies:
* Flat (normal game)
* Cylendar (wraps around right/left)
* Torus (wraps around right/left and top/bottom)
* Hypercube (n-dimensional cube)

The main observation is that all we need to define minesweeper is some set of indexes and a notion of "is next to" (essentially, a topology).

Here we have a generic solver that solves board independent of which topology we choose.

Coming soon...
* More topologies (Mobius strip, Klein bottle, etc...)
* Ability to play the boards yourself!
