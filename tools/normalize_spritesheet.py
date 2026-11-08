"""Reorganiza uma spritesheet gerada por IA em uma grade regular.

Os frames gerados raramente ficam alinhados (colunas e linhas com espacamento diferente, personagem
deslocado de frame para frame). Este script detecta cada frame, centraliza no eixo X e mantem a linha
do chao fixa por animacao, devolvendo uma grade de celulas CELL x CELL.

Uso:
    python tools/normalize_spritesheet.py art/entrada.png assets/sprites/saida.png [--cell 160] [--counts 4,6,5,...]

Dependencias: pillow, numpy, scipy.
"""
import argparse

import numpy as np
from PIL import Image
from scipy import ndimage

ALPHA_THRESHOLD = 40
DILATION = 2
MIN_FRAME_AREA = 3000
# Dois frames colados (ex.: andar em cima de atacar) formam um blob com o dobro da area.
MERGED_FRAME_AREA = 14000
ROW_GAP = 40
BOTTOM_PADDING = 4


def detect_blobs(alpha):
    labels, count = ndimage.label(ndimage.binary_dilation(alpha, iterations=DILATION))
    blobs = []

    for index in range(1, count + 1):
        mask = labels == index
        area = int((mask & alpha).sum())

        if area >= MIN_FRAME_AREA:
            blobs.append({"mask": mask, "area": area})

    return blobs


def split_merged(blob, alpha):
    """Separa um blob com dois frames na linha horizontal com menos pixels perto do meio."""
    ys = np.where(blob["mask"].any(axis=1))[0]
    top, bottom = ys[0], ys[-1]
    middle = (top + bottom) // 2

    density = (blob["mask"] & alpha).sum(axis=1)
    split = middle - 25 + int(np.argmin(density[middle - 25:middle + 25]))

    upper = blob["mask"].copy()
    upper[split:] = False
    lower = blob["mask"].copy()
    lower[:split] = False

    return [
        {"mask": upper, "area": int((upper & alpha).sum())},
        {"mask": lower, "area": int((lower & alpha).sum())},
    ]


def bounds_of(blob):
    ys = np.where(blob["mask"].any(axis=1))[0]
    xs = np.where(blob["mask"].any(axis=0))[0]

    return xs[0], xs[-1] + 1, ys[0], ys[-1] + 1


def group_rows(blobs):
    rows = []

    for blob in sorted(blobs, key=lambda b: bounds_of(b)[3]):
        if rows and bounds_of(blob)[3] - bounds_of(rows[-1][-1])[3] <= ROW_GAP:
            rows[-1].append(blob)
        else:
            rows.append([blob])

    for row in rows:
        row.sort(key=lambda b: bounds_of(b)[0])

    return rows


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("source")
    parser.add_argument("output")
    parser.add_argument("--cell", type=int, default=160)
    parser.add_argument("--counts", default="4,6,5,4,6,5,4,6,5,8",
                        help="Quantidade de frames de cada linha, de cima para baixo.")
    args = parser.parse_args()

    expected = [int(value) for value in args.counts.split(",")]
    image = Image.open(args.source).convert("RGBA")
    pixels = np.array(image)
    alpha = pixels[:, :, 3] > ALPHA_THRESHOLD

    blobs = []
    for blob in detect_blobs(alpha):
        if blob["area"] >= MERGED_FRAME_AREA:
            blobs.extend(split_merged(blob, alpha))
        else:
            blobs.append(blob)

    rows = group_rows(blobs)
    counts = [len(row) for row in rows]

    if counts != expected:
        raise SystemExit(f"Frames detectados por linha {counts}, esperado {expected}.")

    cell = args.cell
    columns = max(expected)
    sheet = Image.new("RGBA", (columns * cell, len(rows) * cell), (0, 0, 0, 0))

    for row_index, row in enumerate(rows):
        baseline = int(np.median([bounds_of(blob)[3] for blob in row]))

        for column_index, blob in enumerate(row):
            x0, x1, y0, y1 = bounds_of(blob)
            ys, xs = np.where(blob["mask"] & alpha)
            center_x = int(round(xs.mean()))

            frame = pixels.copy()
            frame[~blob["mask"]] = 0
            frame = Image.fromarray(frame[y0:y1, x0:x1])

            left = cell // 2 - (center_x - x0)
            top = cell - BOTTOM_PADDING - (baseline - y0)

            if left < 0 or top < 0 or left + frame.width > cell or top + frame.height > cell:
                print(f"Aviso: frame {row_index},{column_index} ultrapassa a celula (esquerda={left}, topo={top}, direita={left + frame.width - cell}, base={top + frame.height - cell}).")

            tile = Image.new("RGBA", (cell, cell), (0, 0, 0, 0))
            tile.paste(frame, (left, top), frame)
            sheet.paste(tile, (column_index * cell, row_index * cell))

    sheet.save(args.output)
    print(f"Salvo {args.output}: {sheet.width}x{sheet.height}, celulas de {cell}px, linhas {counts}")


if __name__ == "__main__":
    main()
