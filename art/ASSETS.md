# Assets

Estrutura de `assets/` e o estado de cada arte.

```
assets/
  sprites/characters/   barbarian_sheet, mage_sheet e as versões _tired
  sprites/monsters/     <familia>_t1..t3_sheet, minions e bosses (<nome>_sheet)
  sprites/items/        weapon_icons
  sprites/vfx/          efeitos de combate, skills, projéteis e bosses
  maps/                 crypt_tileset (16x8 tiles de 64px)
  ui/                   atlas, fonte e retratos do HUD
```

Brutos ficam em `art/raw/`. O que o bundle trouxe e o jogo não usa está em `art/bundle/` (duplicados, protótipos substituídos, variações de arma e VFX procedurais).

## Dungeons

Cada `Theme` (em `world/Theme.java`) define boss, famílias de monstros e o tileset do mapa.

| Dungeon | Boss | Famílias | Tileset |
|---|---|---|---|
| Forgotten Crypt | Lich King | Skeleton Warrior, Skeleton Archer, Necromancer | `crypt_tileset` |
| Infested Caverns | Spider Queen | Giant Spider, Venom Broodling, Cultist Hunter, Slime | `caverns_tileset` |
| Infernal Forge | Magma Dragon | Fire Imp, Ember Knight, Magma Hound | `forge_tileset` |

Os tilesets seguem o mesmo layout (16x8 tiles de 64px) e `world/TilesetLayout.java` diz quais tiles cada dungeon usa para chão, decoração, paredes e tochas/cristais.

## Monstros: arte final x protótipo

Folhas animadas de verdade: `skeleton_warrior_t1..t3`, `skeleton_archer_t1..t3`, `giant_spider_t1`, `necromancer_t1..t3`, `death_knight`, `lich_king`, `magma_hound_t2..t3`.

Ainda protótipos (derivados de retratos estáticos): `magma_hound_t1`, bosses (`spider_queen`, `magma_dragon`), minions e as demais famílias de Caverns e Forge.

Normalização: `tools/normalize_spritesheet.py`. Quando os quadros de uma linha ficam em alturas diferentes (pulo, morte), use `--row-by-center`; para folhas com linhas de grade fracas, `--alpha-threshold 200`. Os brutos ficam em `art/raw/`.
