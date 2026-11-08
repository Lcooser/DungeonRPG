"""Reorganiza uma folha de efeitos visuais (golpes, faiscas, projeteis) em uma grade regular.

Diferente dos personagens, cada efeito e centralizado na celula (nao ha linha de chao). As linhas sao
detectadas pelas faixas verticais ocupadas, e as colunas pela grade nominal da imagem, porque os
frames de uma mesma linha costumam se tocar.

Uso:
    python tools/normalize_effects.py art/combat_effects_sprites.png assets/sprites/effects_sheet.png \\
        [--cell 192] [--columns 6] [--counts 5,5,4,4,4,6,4,6] [--clean-alpha 40] [--fixed-rows]

--clean-alpha  pixels with less alpha than this become fully transparent. Raise it for sheets that
               came with a faint glow field in the background.

--fixed-rows   split the image into evenly spaced rows instead of detecting them. Use it for icon
               grids, where neighbouring icons are too close for the row detection.

Dependencias: pillow, numpy.
"""
import argparse

import numpy as np
from PIL import Image

ROW_ALPHA = 100
CLEAN_ALPHA = 40
ROW_GAP = 6
ROW_PADDING = 14


def occupied_runs(values, gap):
    runs, start, last = [], None, None

    for index, filled in enumerate(values):
        if filled:
            if start is None:
                start = index

            last = index
        elif start is not None and index - last > gap:
            runs.append((start, last))
            start = None

    if start is not None:
        runs.append((start, last))

    return runs


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("source")
    parser.add_argument("output")
    parser.add_argument("--cell", type=int, default=192)
    parser.add_argument("--columns", type=int, default=6, help="Colunas da grade nominal da imagem.")
    parser.add_argument("--counts", default="5,5,4,4,4,6,4,6",
                        help="Quantidade de frames de cada linha, de cima para baixo.")
    parser.add_argument("--clean-alpha", type=int, default=CLEAN_ALPHA)
    parser.add_argument("--fixed-rows", action="store_true")
    args = parser.parse_args()

    expected = [int(value) for value in args.counts.split(",")]
    pixels = np.array(Image.open(args.source).convert("RGBA"))

    # O halo semitransparente da borda some, senao as faixas de cada linha se misturam.
    pixels[pixels[:, :, 3] < args.clean_alpha] = 0

    height, width = pixels.shape[:2]
    if args.fixed_rows:
        step = height / len(expected)
        bands = [(round(row * step), round((row + 1) * step) - 1) for row in range(len(expected))]
        row_padding = 0
    else:
        bands = occupied_runs((pixels[:, :, 3] > ROW_ALPHA).any(axis=1), ROW_GAP)
        row_padding = ROW_PADDING

    if len(bands) != len(expected):
        raise SystemExit(f"Linhas detectadas {len(bands)}, esperado {len(expected)}.")

    slot_width = width / args.columns
    cell = args.cell
    sheet = Image.new("RGBA", (args.columns * cell, len(bands) * cell), (0, 0, 0, 0))

    for row_index, (top, bottom) in enumerate(bands):
        y0 = max(0, top - row_padding)
        y1 = min(height, bottom + row_padding + 1)
        frames = []

        for column in range(args.columns):
            x0 = round(column * slot_width)
            x1 = round((column + 1) * slot_width)
            crop = pixels[y0:y1, x0:x1]
            alpha = crop[:, :, 3] > ROW_ALPHA

            if alpha.sum() > 30:
                frames.append((column, crop))

        if len(frames) != expected[row_index]:
            raise SystemExit(
                f"Linha {row_index}: {len(frames)} frames detectados, esperado {expected[row_index]}."
            )

        # Os frames sao empacotados em sequencia, mesmo que a imagem tenha deixado uma coluna vazia.
        for column, (_, crop) in enumerate(frames):
            ys, xs = np.where(crop[:, :, 3] > args.clean_alpha)
            frame = Image.fromarray(crop[ys.min():ys.max() + 1, xs.min():xs.max() + 1])

            if frame.width > cell or frame.height > cell:
                print(f"Aviso: frame {row_index},{column} maior que a celula ({frame.width}x{frame.height}).")

            tile = Image.new("RGBA", (cell, cell), (0, 0, 0, 0))
            tile.paste(frame, ((cell - frame.width) // 2, (cell - frame.height) // 2), frame)
            sheet.paste(tile, (column * cell, row_index * cell))

    sheet.save(args.output)
    print(f"Salvo {args.output}: {sheet.width}x{sheet.height}, celulas de {cell}px, linhas {expected}")


if __name__ == "__main__":
    main()
