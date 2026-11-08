# Contributing to DungeonRPG

Thanks for taking a look. Bug reports, balance ideas and pull requests are welcome.

> **License note.** DungeonRPG is source-available under the
> [PolyForm Noncommercial License 1.0.0](LICENSE). You can read it, run it, fork it and learn from it, but not use
> it commercially. By opening a pull request you agree that your contribution is distributed under the same
> license and that you have the right to submit it.

## Ways to help

- **Report a bug.** Open an issue with the dungeon, class, difficulty, what you did, and what you expected.
  A screenshot or a short clip helps a lot.
- **Suggest or tune gameplay.** Skill numbers, drop chances, monster speeds and difficulty multipliers are all
  easy to change. Explain the problem you felt while playing, not only the number you want.
- **Add content.** New monsters, skills, weapons or a new dungeon (see the recipes below).
- **Improve the docs.** Typos, clearer setup steps and translations are welcome.

## Getting set up

1. Install **JDK 21**.
2. Clone and run:

   ```bash
   git clone https://github.com/Lcooser/DungeonRPG.git
   cd DungeonRPG
   ./gradlew lwjgl3:run        # Windows: gradlew.bat lwjgl3:run
   ```

3. Compile only:

   ```bash
   ./gradlew core:compileJava
   ```

The game runs from the `assets/` folder, so keep that directory structure intact.

## Workflow

1. Open an issue first for anything bigger than a small fix, so we can agree on the approach.
2. Fork, then branch from `main`: `feature/short-name` or `fix/short-name`.
3. Keep each pull request focused on one thing.
4. Compile and **play the change**. There is no automated test suite yet, so describe in the PR what you
   tried (dungeon, class, difficulty). Screenshots or clips are great for visual changes.
5. Open the pull request against `main` and fill in what changed and why.

## Commit messages

Short, in English, in the form `type(scope): message`.

| Type | Use for |
|---|---|
| `feature` | New gameplay or content |
| `fix` | Bug fixes |
| `chore` | Assets, tooling, dependencies, repo housekeeping |
| `docs` | Documentation only |
| `refactor` | Code changes that do not change behaviour |

Examples: `feature(monsters): add ranged AI for archers`, `fix(combat): stop projectiles at walls`.

## Code style

- Java 21. Follow `.editorconfig` (spaces, LF line endings).
- Names in English, descriptive, no abbreviations that need a decoder ring.
- Match the surrounding code. Comments are rare, short, in English, and explain **why**, not what.
- Behaviour goes in systems, data goes in components. Avoid putting logic in components.
- Tuning values are named constants at the top of the class, not magic numbers in the middle of a method.

## Recipes

**Add a monster.** Add an entry to `MonsterType` (family, tier, stats, abilities, `visualSize`) and drop its
sprite sheet at `assets/sprites/monsters/<name>_sheet.png`. If it uses a new attack, add an `Ability`.
Sheets follow an 8-column grid, 10 rows (down, up and right for idle, walk and attack, then hurt and death).
`tools/normalize_spritesheet.py` turns a loosely aligned sheet into that grid.

**Add a skill.** Add a constant to `Ability` with the fluent builder (kind, cost, cooldown, damage, range,
effects) and assign it to a class in `CharacterClass`, or to a monster in `MonsterType`.

**Add a dungeon.** Add a `Theme` entry with its boss and monster families, a tileset at
`assets/maps/<name>_tileset.png` (16x8 tiles of 64px, same arrangement as the existing ones) and a
`TilesetLayout` describing which tiles to use for floors, walls and accents.

## Art and assets

- All current maps, characters, monsters and effects were **generated with AI tools**. See the README.
- Only submit art you created, or generated yourself, or that has a license compatible with this project's
  non-commercial terms. Say in the PR how it was made.
- Do not submit ripped or copyrighted art from other games.
- Keep sheets small and optimized. Source images and raw generations are not committed; the
  `art/` folder is git-ignored on purpose.

## Questions

Open a GitHub issue or discussion. Be kind, and assume good faith.
