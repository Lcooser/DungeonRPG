# Art prompts and sheet formats

Guia de geração de arte do DungeonRPG. Os prompts estão em inglês (funcionam melhor nos geradores) e as explicações em português.

## 0. Regras gerais

- Sempre anexe `art/barbarian_sprites.png` como referência de estilo.
- Gere em duas etapas: primeiro uma imagem de referência única do personagem/arma, depois a folha, anexando a referência.
- Fundo transparente, sem texto, sem marca d'água, sem sombra no chão.
- Cole este bloco no começo de todos os prompts:

```
STYLE: clean pixel art, crisp 1-pixel dark outlines, limited palette, rich shading,
3/4 top-down RPG view, same rendering style, line weight and proportions (chunky,
slightly oversized head and hands) as the attached barbarian sprite sheet. Original
design, not copied from any existing game. Transparent background, no text, no watermark.
```

- Depois de gerar, salve a imagem bruta em `art/` e rode o normalizador (veja cada seção). Se ele reclamar da contagem de frames, regenere só a linha com problema.

Convenção de nomes: bruto em `art/<nome>_sprites.png`, normalizado em `assets/sprites/<nome>_sheet.png`.

---

## 1. Armas

### 1.1 Lista de armas

Cada tipo de arma muda a animação de ataque do personagem, por isso cada tipo precisa da sua própria folha (seção 2).

**Bárbaro (físicas, escalam com força)**

| Tipo | Ataque | Observação |
|---|---|---|
| AXE | golpe diagonal rápido | já existe |
| GREATAXE | giro largo, duas mãos | lento, acerta em área |
| SWORD | corte rápido, estocada | alcance médio |
| HAMMER | pancada vertical | onda de choque no chão |
| SPEAR | estocada longa | alcance maior, área estreita |

**Mago (mágicas, escalam com inteligência)**

| Tipo | Ataque | Observação |
|---|---|---|
| STAFF | projétil | já existe |
| WAND | disparo rápido | cadência alta, dano baixo |
| TOME | feitiço em área | livro flutuando, círculo mágico |
| ORB | esfera orbitando que dispara | mão aberta, esfera no ar |

### 1.2 Ícones das armas (inventário e drop no chão)

Uma folha com todas as armas, em oito evoluções de material por tipo. A raridade é mostrada pelo jogo com moldura e brilho, mas o visual também deve evoluir (enferrujado até lendário).

Coluna 0-1 comum, 2-3 raro, 4-5 épico, 6 lendário, 7 único.

```
[STYLE] Weapon icon sheet. Canvas 1024x1152 px, strict grid of 8 columns x 9 rows, each
cell exactly 128x128 px, one weapon per cell, centered, drawn diagonally (handle bottom-left,
tip top-right), no hands, no character. Same weapon family per row, 8 progressive tiers
from left to right:
 tier 1-2: plain rusty / iron, simple wrapped grip
 tier 3-4: steel with colored inlays and small gems
 tier 5-6: ornate, glowing runes, purple or teal energy
 tier 7: legendary, golden or orange flames, dramatic silhouette
 tier 8: unique, one-of-a-kind artifact, strange materials, red and gold glow, distinctive silhouette
Rows top to bottom: 1 one-handed axe, 2 greataxe, 3 sword, 4 war hammer, 5 spear,
6 magic staff, 7 wand, 8 spell tome, 9 magic orb.
Every weapon in a row must look like the same type but clearly more powerful toward the right.
```

Normalizar: não precisa (é uma grade de ícones). Se a grade sair torta, me envie e eu corto.

### 1.3 Drop no chão e brilho de raridade

O ícone da arma é reaproveitado no chão (o jogo faz ele flutuar). O brilho vem desta folha:

```
[STYLE] Loot drop VFX sheet. Canvas 768x640 px, grid of 6 columns x 5 rows, each cell 128x128 px.
Each row is one rarity, a looping 6-frame animation of a ground glow ring with a light
pillar rising from it, particles floating up, centered in the cell, transparent background,
semi-transparent additive-style glow:
Row 1 common: soft white
Row 2 rare: blue
Row 3 epic: purple
Row 4 legendary: orange
Row 5 unique: red and gold, with sparkling stars
Higher rarities have a taller pillar and more particles.
```

---

## 2. Folha do personagem por tipo de arma

Mesmo formato das folhas atuais, para o código carregar sem mudanças: 8 colunas x 10 linhas, células de 128x128.

Linhas (frames da esquerda para a direita): 1 frente parado (4), 2 frente andando (6), 3 frente atacando (5), 4 costas parado (4), 5 costas andando (6), 6 costas atacando (5), 7 lado direito parado (4), 8 lado andando (6), 9 lado atacando (5), 10 dano (2) e morte (6).

Modelo (troque `{CLASS}`, `{WEAPON}` e a descrição do ataque):

```
[STYLE] Using the attached {CLASS} reference image as the exact character, create a sprite sheet
of the same character now wielding {WEAPON}. Canvas 1024x1280 px, strict grid of 8 columns x
10 rows, each cell exactly 128x128. Feet on the same baseline in every cell, character
centered horizontally. Rows: [paste the 10-row list above]. In the attack rows: {ATTACK DESCRIPTION}.
Same proportions and colors in every frame.
```

Descrições de ataque por tipo:

| Arma | `{ATTACK DESCRIPTION}` |
|---|---|
| GREATAXE | two-handed wide spinning sweep, wind-up, full body turn, heavy recovery |
| SWORD | fast diagonal slash then a quick thrust, light footwork |
| HAMMER | two-handed overhead raise, slam to the ground, brief pause |
| SPEAR | step forward and long thrust, spear fully extended at the impact frame |
| WAND | quick flick of the wand with one hand, small spark at the tip |
| TOME | open spell book floating in one hand, other hand sweeping a magic circle |
| ORB | open palm, glowing orb floating above it that is pushed forward |

Normalizar (todas):

```
python tools/normalize_spritesheet.py art/{class}_{weapon}_sprites.png assets/sprites/{class}_{weapon}_sheet.png
```

Se a linha de andar tiver 5 frames em vez de 6, use `--counts 4,5,5,4,6,5,4,6,5,8 --pad-to 4,6,5,4,6,5,4,6,5,8`.

---

## 3. Partículas e efeitos por arma

Uma folha por tipo de arma. Grade de 6 colunas, células de 192x192, efeito centrado, desenhado virado para a direita (o jogo gira). Normalizar com `python tools/normalize_effects.py art/{weapon}_vfx.png assets/sprites/{weapon}_vfx.png --counts ...`.

Modelo:

```
[STYLE] Pixel art VFX sheet, transparent background, bright saturated colors, no character.
Canvas 1152x{HEIGHT} px, grid of 6 columns, each cell 192x192, effect centered in the cell.
{ROWS}
```

`{ROWS}` por arma:

| Arma | Linhas |
|---|---|
| GREATAXE | 1 wide spinning slash arc, gray-white, 6 frames. 2 heavy hit impact with dust, 4 frames. |
| SWORD | 1 thin fast slash arc, silver, 5 frames. 2 quick thrust streak, 4 frames. 3 small hit spark, 4 frames. |
| HAMMER | 1 overhead impact flash, 4 frames. 2 ground shockwave ring expanding with debris, 6 frames. 3 cracks on the floor fading, 5 frames. |
| SPEAR | 1 long thrust streak, 4 frames. 2 piercing hit spark, 4 frames. |
| WAND | 1 small magic bolt projectile, looping, 4 frames. 2 tiny impact sparkle, 5 frames. |
| TOME | 1 magic circle on the floor appearing and spinning, 6 frames. 2 rune burst, 6 frames. 3 floating runes dissipating, 5 frames. |
| ORB | 1 glowing orb projectile with trail, looping, 4 frames. 2 orb explosion, 6 frames. |

Variações elementares (opcional, por raridade): peça a mesma folha em fogo (laranja), gelo (azul claro), raio (amarelo/branco), veneno (verde) e sombra (roxo). O jogo aplica por raridade ou por encantamento.

---

## 4. Mapas

### 4.1 Como o jogo monta o mapa

O layout (salas e corredores) é gerado por código a cada partida. O que você gera é o **tileset**, a arte dos blocos. São 3 temas, e eu escolho um por partida (ou por andar).

Se preferir fases desenhadas à mão, o formato alternativo é um arquivo do Tiled (`.tmx`) com as camadas `GROUND`, `WALLS`, `DECOR` e uma camada de objetos `SPAWNS` (pontos `player`, `monster:<tipo>`, `boss`, `chest`). Posso adicionar o leitor se quiser.

### 4.2 Formato do tileset

**Formato do arquivo:** um único **PNG de 32 bits com transparência** (RGBA). Nada de JPG, nem fundo branco ou xadrez. Tamanho **1024x512 px**, tiles de **64x64 px** em grade exata, sem margem e sem espaçamento entre os tiles. O mapa em si (onde ficam salas e corredores) o jogo gera sozinho, então você **não** manda um arquivo de mapa pronto, só o tileset. Se a IA entregar JPG ou fundo opaco, me envie assim mesmo que eu limpo.

Tiles de 32x32 no jogo. Gere em 64x64 por tile (o dobro) e eu reduzo. Folha de **16 colunas x 8 linhas**, ou seja 1024x512 px.

| Linha | Colunas | Conteúdo |
|---|---|---|
| 0 | 0-7 | **FLOOR**: 8 variações de chão (liso, rachado, com musgo, etc.) |
| 0 | 8-11 | FLOOR_DECOR: ossos, poça, entulho, mancha |
| 0 | 12-15 | FLOOR_SHADOW: chão sombreado sob a parede (4 variações) |
| 1 | 0-3 | **WALL_FACE**: face frontal da parede (tijolos), 4 variações |
| 1 | 4-7 | WALL_FACE_TORCH: parede com tocha, 4 frames de animação |
| 1 | 8 | WALL_FACE_CRACKED |
| 1 | 9-10 | DOOR: fechada, aberta |
| 1 | 11-12 | BOSS_DOOR: fechada, aberta |
| 2-3 | 0-15 | **WALL_TOP**: topo da parede visto de cima, 16 tiles de autotile |
| 4 | 0-7 | props: barril, caixote, jarro, coluna (base e topo), estante, trono, altar |
| 5 | 0-7 | props: baú fechado, baú aberto, escada, runa de início, runa do chefe, grade, teia, tocha de chão |
| 6-7 | 0-15 | livre (variações extras do tema) |

**WALL_TOP (autotile de 16 tiles).** O índice é a soma de `N=1, L=2, S=4, O=8` para cada vizinho que também é parede. Exemplos: índice 0 é um bloco isolado (bordas em todos os lados), 15 é um miolo cercado de parede (sem borda), 5 é uma faixa vertical, 10 é uma faixa horizontal. Linha 2 tem os índices 0 a 7 e linha 3 os índices 8 a 15.

**Como o jogo desenha:** o chão usa FLOOR (com variação aleatória estável por posição). Uma parede com chão logo abaixo mostra WALL_FACE (a face de tijolos); as demais mostram WALL_TOP pelo índice. Tochas, decoração e props entram por cima.

Prompt (troque `{THEME}` pelo bloco do tema da seção 4.3):

```
[STYLE] Top-down 3/4 view dungeon tileset sheet. Canvas 1024x512 px, strict grid of 16 columns x 8 rows,
each tile exactly 64x64 px, tiles must be seamless and tile with their neighbors. Theme: {THEME}.
Row 1: 8 floor tile variations (columns 1-8), 4 floor decor tiles (bones, puddle, rubble, stain),
4 shaded floor tiles (darker, as if under a wall).
Row 2: 4 front wall face tiles (brick wall seen from the front), 4 frames of the same wall with an
animated torch, 1 cracked wall face, closed door, open door, closed boss door (large, ominous), open boss door.
Rows 3-4: the wall TOP 16-tile autotile set seen from above: tile 0 isolated block with edges on all
sides, tile 15 fully surrounded block with no edges, and the 14 edge combinations in between (bitmask
N=1, E=2, S=4, W=8).
Row 5: props: barrel, crate, jar, pillar base, pillar top, bookshelf, throne, altar.
Row 6: props: closed chest, open chest, stairs down, player start rune on the floor, boss rune on the floor,
iron grate, cobweb corner, standing torch.
Rows 7-8: 16 extra theme-specific decoration tiles.
Consistent lighting from the top-left, same palette across the sheet.
```

### 4.3 Os 3 temas

**1. Crypt**

```
Stone crypt: cold gray-blue brick walls, cracked flagstone floors, bones and skulls, cobwebs,
iron torches with warm orange light, coffins, faint green moss in the cracks.
Palette: slate blue, gray, bone, warm orange accents.
```

**2. Mossy Caverns**

```
Overgrown cave: earthy brown and gray rock walls, mossy dirt floors, tree roots, glowing blue-green
mushrooms, small puddles, crystals, wooden support beams, rope and lanterns.
Palette: brown, moss green, teal glow, soft yellow lantern light.
```

**3. Ember Forge**

```
Volcanic forge: dark basalt walls with glowing lava cracks, scorched floors with ember cracks,
iron grates over lava, anvils, braziers, chains, rusty metal plating.
Palette: black, charcoal, deep red, bright orange and yellow glow.
```

Normalizar: não precisa. Salve como `assets/maps/crypt_tileset.png`, `caverns_tileset.png` e `forge_tileset.png`.

---

## 5. Monstros com tiers

### 5.1 Elenco oficial

Cada monstro comum tem **3 tiers visuais**. Nomes das famílias por masmorra (os nomes dos tiers são os do jogo, em estilo RPG):

| Masmorra | Monstro 1 | Monstro 2 | Monstro 3 | Chefe |
|---|---|---|---|---|
| Forgotten Crypt | Skeleton Warrior (Bone Soldier, Bone Captain, Bone Tyrant) | Skeleton Archer (Bone Archer, Bone Ranger, Bone Sniper) | Necromancer (Grave Acolyte, Death Caller, Soul Reaver) | Lich King |
| Infested Caverns | Giant Spider (Cave Spider, Web Stalker, Venom Weaver) | Venom Broodling (Brood Larva, Venom Broodling, Plague Broodling) | Cultist Hunter (Cult Initiate, Cultist Hunter, Cult Executioner) | Spider Queen |
| Infernal Forge | Fire Imp (Ember Imp, Fire Imp, Inferno Imp) | Ember Knight (Ember Squire, Ember Knight, Ember Champion) | Magma Hound (Cinder Hound, Magma Hound, Hellfire Hound) | Magma Dragon |

Os atiradores têm projétil: Skeleton Archer (flechas), Necromancer (projétil mágico), Cultist Hunter (besta) e Fire Imp (bola de fogo).

**Tiers visuais**
- Tier 1: inimigo básico, equipamento mínimo, cores comuns.
- Tier 2: armadura ou marcas melhores, detalhes marcantes.
- Tier 3: aparência de elite, brilhos nos detalhes, postura mais agressiva.

**Chefes**

| Chefe | Ataques especiais | Lacaios |
|---|---|---|
| Lich King | Soul Barrage, Death Nova, Summon Undead | Death Knights, Soul Wraiths |
| Spider Queen | Venom Spit, Web Trap, Brood Summoning | Brood Guards, Egg Carriers |
| Magma Dragon | Dragon Breath, Meteor Rain, Lava Eruption | Forge Guardians, Flame Cultists |

### 5.2 Formato para o jogo carregar

O jogo carrega cada tier como **uma folha própria**, no mesmo layout das folhas do esqueleto e da aranha:

- **PNG com transparência**, grade de **8 colunas x 10 linhas**, células de **128x128 px** (folha de 1024x1280).
- Linhas: 1 frente parado (4 frames), 2 frente andando (6), 3 frente atacando (5), 4 costas parado (4), 5 costas andando (6), 6 costas atacando (5), 7 lado direito parado (4), 8 lado andando (6), 9 lado atacando (5), 10 dano (2) e morte (6). O lado esquerdo o jogo espelha.
- Criaturas sem direção (lodo, cogumelo): 8 colunas x 4 linhas (parado 4, mover 6, atacar 5, dano 2 + morte 6).
- **Atiradores:** acrescente a **linha 11** com o projétil em loop (4 frames) voando para a direita.
- **Chefes:** células de 192x192 e uma linha extra com o ataque especial.
- **Nome do arquivo:** `art/<monstro>_t<tier>_sprites.png`, por exemplo `skeleton_archer_t2_sprites.png`.

Normalizar (alinha os frames e gera a folha final):

```
python tools/normalize_spritesheet.py art/skeleton_archer_t2_sprites.png assets/sprites/skeleton_archer_t2_sheet.png
```

Para ativar a arte nova, troque o nome da folha da entrada correspondente em `MonsterType` (por exemplo, de `"skeleton"` para `"skeleton_t2"`). Hoje os tiers 2 e 3 reaproveitam a folha do tier 1 com uma cor diferente, só para o jogo já ter a progressão de dificuldade e de drops.

**As 3 folhas de conceito** que você gerou (`art/deadly_monsters_concept.png`, `cave_monsters_concept.png` e `fire_monsters_concept.png`) servem como referência visual, mas **não estão nesse layout**: misturam frames, projéteis e efeitos na mesma imagem, sem direções. Use-as como referência de estilo e peça cada monstro e tier de novo na folha padrão.

### 5.3 Prompts

Monstro com direções (um tier por folha):

```
[STYLE] {DESCRIPTION}, tier {N} ({TIER LOOK}). Sprite sheet: canvas 1024x1280 px, grid of 8 columns x 10 rows,
each cell 128x128, feet on the same baseline, centered. Rows: 1 front idle (4), 2 front walk (6),
3 front attack (5), 4 back idle (4), 5 back walk (6), 6 back attack (5), 7 right idle (4), 8 right walk (6),
9 right attack (5), 10 hurt (2) then death (6). {ATTACK DESCRIPTION}.
```

Atirador: o mesmo prompt mais esta frase no fim:

```
Add row 11: the projectile ({PROJECTILE}) as a 4-frame loop flying to the right, centered in each cell.
```

Criatura sem direção:

```
[STYLE] {DESCRIPTION}, tier {N}. Sheet 1024x512 px, grid of 8 columns x 4 rows, each cell 128x128, same baseline,
no facing directions. Row 1 idle (4), row 2 move (6), row 3 attack (5), row 4 hurt (2) then death (6).
```

Chefe:

```
[STYLE] {DESCRIPTION}, large boss. Canvas 1536x2112 px, grid of 8 columns x 11 rows, each cell 192x192.
Rows 1-10 as the monster layout above. Row 11: special attack facing the camera, 8 frames: {SPECIAL}.
```

Para o tier 2 e o tier 3, anexe o tier 1 e peça: `same creature as the attached image, upgraded: {TIER LOOK}`. `{TIER LOOK}` vale: tier 1 `basic gear, ordinary colors`; tier 2 `better armor or markings, distinctive details`; tier 3 `elite look, glowing accents, aggressive posture`.

Descrições sugeridas:

| Monstro | `{DESCRIPTION}` | `{ATTACK DESCRIPTION}` |
|---|---|---|
| Skeleton Archer | skeleton in leather scraps holding a short bow | draws the bow and releases an arrow |
| Necromancer | hooded skeletal mage with a staff topped by a skull | thrusts the staff, green magic bolt |
| Venom Broodling | pale larva-like spider with fangs and a venom sac | spits a glob of green venom |
| Cultist Hunter | hooded cultist with a crossbow and a spider-silk cloak | aims and fires a crossbow bolt |
| Fire Imp | small horned imp with ember skin | throws a fireball |
| Ember Knight | knight in blackened armor with glowing seams and a mace | heavy overhead mace strike |
| Magma Hound | lava-cracked hound with ember fur | pounce with open jaws |
| Lich King (boss) | skeletal king with a crown, dark robes and a bone staff | green soul bolts |
| Magma Dragon (boss) | wingless fire dragon, black scales with glowing cracks | tail whip and claw swipe |

Chefes, linha 11 (`{SPECIAL}`):

| Chefe | `{SPECIAL}` |
|---|---|
| Lich King | raises both arms, green souls rise from the ground |
| Spider Queen | rears up and spits a spray of venom |
| Magma Dragon | inhales and breathes a cone of fire |

---

## 6. Ordem sugerida

1. Tileset do Crypt, para o mapa deixar de ser retângulos coloridos.
2. Folhas das armas do bárbaro e do mago (uma de cada tipo), com os ícones.
3. Monstros e chefe do Crypt.
4. Drops e partículas.
5. Temas Caverns e Forge.

---

## 7. Itens: raridade e nomes

O jogo já usa os ícones de `weapon_icons.png`. Cada arma tem 8 colunas de evolução, e a coluna decide a raridade:

| Raridade | Colunas | Cor do brilho |
|---|---|---|
| Common | 0 e 1 | branco |
| Rare | 2 e 3 | azul |
| Epic | 4 e 5 | roxo |
| Legendary | 6 | laranja |
| Unique | 7 | vermelho e dourado |

Nomes: cada coluna tem um nome fixo por tipo de arma (por exemplo, o machado vai de *Rusted Hatchet* a *Bloodmoon Reaver*). Raro, épico e lendário ganham um epíteto pelo atributo do bônus: *of the Bear* (força), *of the Fox* (agilidade), *of the Bulwark* (defesa), *of the Sage* (inteligência) e *of Vigor* (vitalidade). O único tem só o nome próprio.

A chance de drop e de raridade por tier de monstro está em `LootTier`.

## 8. Efeito de level up (opcional)

O level up usa hoje os efeitos que já existem, tingidos de dourado. Se quiser um efeito próprio:

```
[STYLE] Pixel art VFX sheet, transparent background. Canvas 1536x384 px, grid of 8 columns x 2 rows, each cell 192x192.
Row 1: a golden light pillar rising from the ground with sparkles, 8 frames, bright and ascending then fading.
Row 2: an expanding golden ring on the floor with small stars, 8 frames.
```

Normalizar: `python tools/normalize_effects.py art/levelup_vfx.png assets/sprites/levelup_vfx.png --columns 8 --counts 8,8 --clean-alpha 70`.

## 9. Sons

Veja `art/SOUNDS.md`.

