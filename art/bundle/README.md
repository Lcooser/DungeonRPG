# DungeonRPG — Procedural animation prototypes

All PNG sheets are genuine RGBA with exact 8-column grids. Standard mobs and minions: 1024x1280, 128px cells; ranged mobs: 1024x1408, 11 rows; bosses: 1536x2112, 192px cells.

**Limitations:** These are temporary sprite-sheet prototypes generated from static front-facing concept portraits; walk, attack, hurt and death use deterministic sprite transformations, not genuine articulated animation. Back-facing frames are mirrored/darkened stand-ins, not actual back views, and side-facing frames are narrowed stand-ins. Boss VFX are variations, not fully authored attacks. Do not ship as finished art. They are loadable prototypes for animation-engine integration, collision testing and frame-index validation.

For true production animation, draw or regenerate directional views and independently articulated frames, retain these exact dimensions and counts, and substitute the PNGs one-by-one.
