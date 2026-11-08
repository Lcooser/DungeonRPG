# ADR 0001: Build on libGDX with an Ashley ECS, and keep game content in code

- **Status:** Accepted
- **Date:** 2026-11-08
- **Author:** Luiz Coser

## Context

DungeonRPG is a top-down action RPG in the spirit of Dungeon Rampage: procedural dungeons, real-time melee and
ranged combat, skills, loot, and a boss at the end. It is a solo project that doubles as a portfolio piece, so
three things matter more than raw engine features:

1. The code has to be readable and easy to extend by one person (new monster, new dungeon, new skill).
2. It has to run on a regular desktop from a plain `git clone`, with no editor or launcher to install.
3. Art is generated with AI image tools and arrives as sprite sheets, so the engine must be comfortable with
   2D atlases, tile sheets and custom frame layouts.

## Decision

- **Engine: [libGDX](https://libgdx.com/) 1.14 on the LWJGL3 desktop backend**, Java 21, built with Gradle.
- **Architecture: an entity-component-system with [Ashley](https://github.com/libgdx/ashley).** Entities are bags
  of small data components (`PositionComponent`, `HealthComponent`, `AttackComponent`, ...). Behaviour lives in
  systems (`MovementSystem`, `AttackSystem`, `MonsterAiSystem`, ...) that run in a fixed priority order.
- **Content as enums and builders, not files.** Monsters (`MonsterType`), skills (`Ability`), classes
  (`CharacterClass`), rarities, weapons and dungeons (`Theme`) are Java enums with fluent builders. Adding a
  monster is one enum entry plus a sprite sheet.
- **Procedural levels.** `DungeonMap` carves rooms and L-shaped corridors from a seed. A `Theme` decides the
  tileset, the monster families and the boss, so one generator serves every dungeon.
- **UI with Scene2D, HUD drawn by hand.** The title screen uses Scene2D; the in-game HUD is drawn with a custom
  pixel-art atlas and an integer pixel scale, so it stays crisp at any window size.

## Alternatives considered

| Option | Why not |
|---|---|
| Unity / Godot | Faster to start, but the project would live in an editor and binary scene files. I wanted plain, diffable code and a game that builds with one command. |
| Plain libGDX without ECS | Fine for a prototype, but combat, status effects, AI and loot all touch the same entities. Inheritance would have grown into a god `Monster` class. |
| Another ECS (Artemis-odb, custom) | Ashley is maintained by the libGDX team, integrates with `Engine`/`Family` naturally and is small enough to read in an afternoon. |
| Content in JSON/YAML | Better for non-programmers, but there is only one author. Enums give compile-time checks, refactoring and IDE navigation for free. |
| Kotlin for game code | The libGDX template wires Kotlin in, but Java 21 keeps the codebase uniform and the contributor barrier low. |

## Consequences

**Good**

- New behaviour is usually a new component plus a system that reads it. Status effects (stun, slow, buffs) are
  separate components handled by one `StatusEffectSystem`, and other systems only check for them.
- The whole game builds and runs with `./gradlew lwjgl3:run`; the only prerequisite is a JDK 21.
- Content is type-checked: a typo in a monster family fails at compile time, not at runtime.
- Visual checks are easy: a throwaway harness can boot a `GameScreen` in a hidden window, place entities and save
  the framebuffer.

**Trade-offs**

- Content changes need a recompile; there is no hot reload and no data-driven modding yet.
- ECS adds indirection. Following one attack means reading `PlayerInputSystem`, `AttackSystem`,
  `ProjectileSystem`, `HealthSystem` and the effects code.
- libGDX gives no scene editor, so maps and UI are built in code. This is intentional for now.
- There is no automated test suite yet. Rules such as drop tables and damage formulas are plain functions and are
  good candidates for the first unit tests (see the roadmap).

## Follow-ups

- Extract tuning values (drop chances, difficulty multipliers) into a single balance class.
- Add unit tests for `Progression`, `ItemGenerator` and `LootTier`.
- Revisit data-driven content if community contributions ever need it.
