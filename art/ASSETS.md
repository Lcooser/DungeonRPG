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

Cada `Theme` (em `world/Theme.java`) define boss, famílias de monstros e o visual do mapa.

| Dungeon | Boss | Famílias | Mapa |
|---|---|---|---|
| Forgotten Crypt | Lich King | Skeleton Warrior, Skeleton Archer, Necromancer | tileset |
| Infested Caverns | Spider Queen | Giant Spider, Venom Broodling, Cultist Hunter, Slime | cores (falta tileset) |
| Infernal Forge | Magma Dragon | Fire Imp, Ember Knight, Magma Hound | cores (falta tileset) |

Para um tileset novo: mesmo layout do `crypt_tileset.png` e preencher `tilesetPath` no `Theme`.

## Monstros: arte final x protótipo

Folhas animadas de verdade (ChatGPT normalizado): `skeleton_warrior_t1..t3`, `giant_spider_t1`, `necromancer_t1..t3`, `death_knight`.

Protótipos do bundle (derivados de retratos estáticos, costas espelhadas): archers T1-T3, Lich King, bosses e os demais minions. Vale reexportar archers e Lich King com fundo transparente e rodar `tools/normalize_spritesheet.py`. Os Magma Hounds T2/T3 têm chamas cortadas na borda da célula.
