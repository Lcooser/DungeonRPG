# Sons e música

## O que o jogo já faz

O jogo usa o áudio do **libGDX** (`Gdx.audio`). Ele **toca** arquivos, mas não **gera** som. Por isso você precisa criar ou baixar os arquivos.

O `AudioManager` já está ligado nos eventos do jogo. Todo som é opcional: se o arquivo não existir, aquele evento fica em silêncio e nada quebra. Para adicionar um som, basta salvar o arquivo na pasta certa, sem mudar código.

- Efeitos: `assets/sounds/<nome>.ogg` (também aceita `.wav` e `.mp3`)
- Música: `assets/music/<nome>.ogg`

Cada efeito toca com uma variação aleatória de tom (6%) para não soar repetido.

## Efeitos que o jogo procura

| Arquivo | Quando toca | Duração | Como é |
|---|---|---|---|
| `swing.ogg` | golpe de arma ou giro | 0,2 a 0,4 s | chiado curto de lâmina cortando o ar |
| `cast.ogg` | projétil mágico lançado | 0,3 a 0,6 s | whoosh mágico com brilho |
| `blink.ogg` | Arcane Blink | 0,3 s | "zap" de teleporte, agudo |
| `hit.ogg` | qualquer dano sofrido | 0,15 a 0,3 s | impacto seco |
| `death.ogg` | morte de monstro ou jogador | 0,5 a 1 s | queda, ossos ou grito curto |
| `level_up.ogg` | subir de nível | 1 a 1,5 s | fanfarra curta ascendente e brilhante |
| `pickup.ogg` | reservado para coletar itens | 0,2 s | tilintar |
| `equip.ogg` | equipar arma | 0,3 s | metal sendo ajustado + brilho |
| `ui_click.ogg` | reservado para cliques de menu | 0,1 s | clique curto |

Dica: faça 2 ou 3 variações de `hit` e `swing` e me avise, que eu sorteio entre elas.

## Música

| Arquivo | Onde |
|---|---|
| `music/title.ogg` | tela de título |
| `music/crypt.ogg`, `caverns.ogg`, `forge.ogg` | cada masmorra |
| `music/boss.ogg` | sala do chefe |
| `music/victory.ogg`, `defeat.ogg` | fim de jogo (jingle curto, sem loop) |

Músicas de masmorra: loop de 60 a 120 s que emende sem estalo. Ainda falta eu ligar a música nas telas, e faço isso quando os arquivos existirem.

## Formato dos arquivos

- **OGG Vorbis** é o recomendado (pequeno e toca em todos os sistemas). WAV e MP3 também funcionam.
- Efeitos: mono, 44,1 kHz, até alguns segundos (o libGDX carrega tudo na memória).
- Música: estéreo, 44,1 kHz, transmitida do disco (pode ser longa).
- Volume: normalize os efeitos perto de -3 dB de pico, para ficarem parecidos entre si.

Converter qualquer arquivo para OGG com o ffmpeg:

```
ffmpeg -i entrada.wav -c:a libvorbis -q:a 4 saida.ogg
```

## Como gerar

**Efeitos estilo retro (grátis, rápido):**
- **jsfxr** (sfxr.me, no navegador), **Bfxr** e **ChipTone**: você escolhe um preset (Hit/Hurt, Pickup/Coin, Powerup, Laser/Shoot, Explosion), aperta Randomize e Export WAV. Combina bem com pixel art.

**Efeitos com IA:**
- **ElevenLabs Sound Effects** e **Stable Audio**: escrevem o som a partir de texto.

Prompts prontos (em inglês):

```
hit.ogg       short punchy impact hit, dry, 8-bit retro RPG style, 0.2 seconds
swing.ogg     fast sword swoosh whoosh, short, dry, fantasy RPG, 0.3 seconds
cast.ogg      magical energy spell cast whoosh with a sparkle tail, 0.5 seconds
blink.ogg     quick arcane teleport zap, bright, short, 0.3 seconds
death.ogg     monster death, bones collapsing and a short groan, 0.8 seconds
level_up.ogg  retro RPG level up fanfare, ascending bright notes, triumphant, 1.2 seconds
equip.ogg     metal weapon being equipped, short clink with a magical shimmer, 0.4 seconds
pickup.ogg    short sparkly item pickup chime, 0.2 seconds
ui_click.ogg  tiny clean UI click, 0.1 seconds
```

**Música com IA:** Suno e Udio. Prompts:

```
Dark fantasy dungeon crawler loop, slow tempo, low strings and distant choir, eerie, 90 seconds, seamless loop, instrumental
Epic boss battle, fast war drums, brass and distorted choir, intense, 90 seconds, seamless loop, instrumental
8-bit/16-bit chiptune RPG title theme, heroic and adventurous, 60 seconds, loop, instrumental
```

**Bibliotecas gratuitas:** Kenney.nl (pacotes CC0), OpenGameArt.org e Freesound.org. Confira a licença de cada arquivo, que pode exigir crédito.

## Partículas (sem arte)

O libGDX também tem um sistema de partículas (`ParticleEffect`) e uma ferramenta visual, o **gdx-particle-editor**, que gera arquivos `.p`. Dá para criar brilhos, faíscas e fumaça sem desenhar sprites. Hoje o level up usa os efeitos que você já gerou, tingidos de dourado, e posso trocar pelo sistema de partículas se você preferir.

## Licenças

Sons e músicas gerados por IA ou baixados têm termos de uso próprios. Para um jogo comercial, guarde a licença de cada arquivo.
