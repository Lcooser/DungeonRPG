"""Reorganiza uma spritesheet gerada por IA em uma grade regular.

Os frames gerados raramente ficam alinhados (colunas e linhas com espacamento diferente, personagem
deslocado de frame para frame). Este script detecta cada frame, centraliza no eixo X e mantem a linha
do chao fixa por animacao, devolvendo uma grade de celulas CELL x CELL.

Uso:
    python tools/normalize_spritesheet.py art/entrada.png assets/sprites/saida.png \\
        [--cell 160] [--counts 4,6,5,...] [--pad-to 4,6,5,...] [--scale 0.5] [--remove-background] [--background-threshold 215] [--ignore-last-rows 1] [--row-by-center] [--min-frame-ratio 0.06] [--alpha-threshold 200]

--counts      quantidade de frames de cada linha da imagem, de cima para baixo.
--pad-to      completa linhas curtas repetindo o frame do meio, para chegar a esse numero.
--scale        reduz (ou amplia) a imagem antes de processar, para casar o tamanho com as outras folhas.
--background-threshold  with --remove-background, the lowest channel value that counts as background (default 215).
--ignore-last-rows  detected rows at the bottom (for example a projectile row) that are dropped from the output.
--row-by-center  group frames into rows by their vertical center instead of their ground line. Use it when frames of
               one row sit at different heights (jumps, collapsing deaths).
--min-frame-ratio  smallest piece, relative to a typical frame, that still counts as a frame (default 0.2). Lower it
               when a death animation ends in a small collapsed body.
--alpha-threshold  pixels with less alpha than this are ignored when finding frames (default 40). Raise it to skip
               the faint grid lines some generators leave on a transparent sheet.
--remove-background  torna transparente o fundo claro ligado as bordas, para folhas sem transparencia.

Os limites de area sao relativos ao tamanho tipico dos frames, entao funciona em folhas de qualquer
resolucao.

Dependencias: pillow, numpy, scipy.
"""
import argparse

import numpy as np
from PIL import Image
from scipy import ndimage

ALPHA_THRESHOLD = 40
DILATION = 2
# Fragmentos pequenos (bolhas, respingos) sao encaixados no frame mais proximo.
SMALL_BLOB_RATIO = 0.2
ATTACH_DISTANCE = 60
# Dois frames colados (ex.: andar em cima de atacar) formam um blob com o dobro da area.
MERGED_BLOB_RATIO = 1.65
ROW_GAP_RATIO = 0.3
CENTER_GAP_RATIO = 0.5
BACKGROUND_MIN_CHANNEL = 215
HOLE_AREA_RATIO = 0.0002
FRINGE_PASSES = 2
FRINGE_MIN_CHANNEL = 190
BOTTOM_PADDING = 4


def remove_light_background(pixels, threshold=BACKGROUND_MIN_CHANNEL):
    """Apaga o fundo claro: o que toca a borda e tambem os vaos grandes dentro da silhueta.

    Brilhos pequenos do personagem (olhos, metal) ficam, porque so regioes claras grandes saem.
    """
    rgb = pixels[:, :, :3].astype(int)
    light = (rgb.min(axis=2) >= threshold) | (pixels[:, :, 3] == 0)

    labels, count = ndimage.label(light)
    border = set(np.unique(np.concatenate([labels[0], labels[-1], labels[:, 0], labels[:, -1]])))
    sizes = ndimage.sum(light, labels, index=range(1, count + 1))
    min_hole = pixels.shape[0] * pixels.shape[1] * HOLE_AREA_RATIO

    remove = [index for index, size in enumerate(sizes, start=1) if index in border or size >= min_hole]

    pixels = pixels.copy()
    background = np.isin(labels, remove)
    pixels[background] = 0

    # Tira o halo claro que sobra na beirada da silhueta.
    for _ in range(FRINGE_PASSES):
        transparent = pixels[:, :, 3] == 0
        touching = ndimage.binary_dilation(transparent) & ~transparent
        fringe = touching & (pixels[:, :, :3].astype(int).min(axis=2) >= FRINGE_MIN_CHANNEL)
        pixels[fringe] = 0

    return pixels


def bounds_of(mask):
    ys = np.where(mask.any(axis=1))[0]
    xs = np.where(mask.any(axis=0))[0]

    return xs[0], xs[-1] + 1, ys[0], ys[-1] + 1


def box_distance(a, b):
    dx = max(a[0] - b[1], b[0] - a[1], 0)
    dy = max(a[2] - b[3], b[2] - a[3], 0)

    return max(dx, dy)


def detect_blobs(alpha, main_ratio=SMALL_BLOB_RATIO):
    labels, count = ndimage.label(ndimage.binary_dilation(alpha, iterations=DILATION))
    blobs = []

    for index in range(1, count + 1):
        mask = labels == index
        blobs.append({"mask": mask, "area": int((mask & alpha).sum())})

    largest = max(blob["area"] for blob in blobs)
    typical = float(np.median([blob["area"] for blob in blobs if blob["area"] >= largest * 0.25]))

    main = [blob for blob in blobs if blob["area"] >= typical * main_ratio]
    small = [blob for blob in blobs if blob["area"] < typical * main_ratio]

    for fragment in small:
        box = bounds_of(fragment["mask"])
        nearest = min(main, key=lambda blob: box_distance(box, bounds_of(blob["mask"])))

        if box_distance(box, bounds_of(nearest["mask"])) <= ATTACH_DISTANCE:
            nearest["mask"] = nearest["mask"] | fragment["mask"]
            nearest["area"] += fragment["area"]

    return main, typical


def split_merged(blob, alpha):
    """Separa um blob com dois frames na linha horizontal com menos pixels perto do meio."""
    _, _, top, bottom = bounds_of(blob["mask"])
    middle = (top + bottom) // 2

    density = (blob["mask"] & alpha).sum(axis=1)
    window = max(10, (bottom - top) // 5)
    split = middle - window + int(np.argmin(density[middle - window:middle + window]))

    upper = blob["mask"].copy()
    upper[split:] = False
    lower = blob["mask"].copy()
    lower[:split] = False

    return [
        {"mask": upper, "area": int((upper & alpha).sum())},
        {"mask": lower, "area": int((lower & alpha).sum())},
    ]


def row_key(blob, by_center):
    _, _, top, bottom = bounds_of(blob["mask"])

    return (top + bottom) / 2 if by_center else bottom


def group_rows(blobs, by_center=False):
    heights = [bounds_of(blob["mask"])[3] - bounds_of(blob["mask"])[2] for blob in blobs]
    gap = float(np.median(heights)) * (CENTER_GAP_RATIO if by_center else ROW_GAP_RATIO)
    rows = []

    for blob in sorted(blobs, key=lambda b: row_key(b, by_center)):
        if rows and row_key(blob, by_center) - row_key(rows[-1][-1], by_center) <= gap:
            rows[-1].append(blob)
        else:
            rows.append([blob])

    for row in rows:
        row.sort(key=lambda b: bounds_of(b["mask"])[0])

    return rows


def pad_row(row, target):
    """Completa uma linha curta repetindo o frame do meio, o que mantem o ciclo suave."""
    row = list(row)

    while len(row) < target:
        middle = len(row) // 2
        row.insert(middle + 1, row[middle])

    return row


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("source")
    parser.add_argument("output")
    parser.add_argument("--cell", type=int, default=160)
    parser.add_argument("--counts", default="4,6,5,4,6,5,4,6,5,8",
                        help="Quantidade de frames de cada linha, de cima para baixo.")
    parser.add_argument("--pad-to", default=None,
                        help="Completa linhas curtas ate esta quantidade por linha.")
    parser.add_argument("--scale", type=float, default=1.0,
                        help="Fator de escala aplicado a imagem inteira antes de processar.")
    parser.add_argument("--background-threshold", type=int, default=BACKGROUND_MIN_CHANNEL)
    parser.add_argument("--ignore-last-rows", type=int, default=0)
    parser.add_argument("--alpha-threshold", type=int, default=ALPHA_THRESHOLD)
    parser.add_argument("--row-by-center", action="store_true")
    parser.add_argument("--min-frame-ratio", type=float, default=SMALL_BLOB_RATIO)
    parser.add_argument("--remove-background", action="store_true",
                        help="Remove o fundo claro ligado as bordas da imagem.")
    args = parser.parse_args()

    expected = [int(value) for value in args.counts.split(",")]
    pad_to = [int(value) for value in args.pad_to.split(",")] if args.pad_to else expected

    image = Image.open(args.source).convert("RGBA")
    pixels = np.array(image)

    if args.remove_background:
        pixels = remove_light_background(pixels, args.background_threshold)

    if args.scale != 1.0:
        scaled = Image.fromarray(pixels).resize(
            (round(pixels.shape[1] * args.scale), round(pixels.shape[0] * args.scale)),
            Image.LANCZOS,
        )
        pixels = np.array(scaled)

    alpha = pixels[:, :, 3] > args.alpha_threshold

    detected, typical = detect_blobs(alpha, args.min_frame_ratio)
    blobs = []

    for blob in detected:
        if blob["area"] > typical * MERGED_BLOB_RATIO:
            blobs.extend(split_merged(blob, alpha))
        else:
            blobs.append(blob)

    rows = group_rows(blobs, args.row_by_center)
    counts = [len(row) for row in rows]

    if counts != expected:
        raise SystemExit(f"Frames detectados por linha {counts}, esperado {expected}.")

    if args.ignore_last_rows:
        rows = rows[:-args.ignore_last_rows]

    rows = [pad_row(row, target) for row, target in zip(rows, pad_to)]

    cell = args.cell
    columns = max(pad_to)
    sheet = Image.new("RGBA", (columns * cell, len(rows) * cell), (0, 0, 0, 0))

    for row_index, row in enumerate(rows):
        baseline = int(np.median([bounds_of(blob["mask"])[3] for blob in row]))

        for column_index, blob in enumerate(row):
            x0, x1, y0, y1 = bounds_of(blob["mask"])
            ys, xs = np.where(blob["mask"] & alpha)
            center_x = int(round(xs.mean()))

            frame = pixels.copy()
            frame[~blob["mask"]] = 0
            frame = Image.fromarray(frame[y0:y1, x0:x1])

            left = cell // 2 - (center_x - x0)
            top = cell - BOTTOM_PADDING - (baseline - y0)

            # Se o frame nao cabe na posicao ideal, encosta na borda da celula em vez de cortar.
            if frame.width <= cell:
                left = min(max(left, 0), cell - frame.width)
            if frame.height <= cell:
                top = min(max(top, 0), cell - frame.height)

            if frame.width > cell or frame.height > cell:
                print(f"Aviso: frame {row_index},{column_index} e maior que a celula ({frame.width}x{frame.height}) e sera cortado.")

            tile = Image.new("RGBA", (cell, cell), (0, 0, 0, 0))
            tile.paste(frame, (left, top), frame)
            sheet.paste(tile, (column_index * cell, row_index * cell))

    sheet.save(args.output)
    print(f"Salvo {args.output}: {sheet.width}x{sheet.height}, celulas de {cell}px, linhas {[len(r) for r in rows]}")


if __name__ == "__main__":
    main()
