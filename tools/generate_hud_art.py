"""Gera a arte pixelada do HUD: fonte bitmap, atlas de molduras/barras e retrato do personagem.

Uso:
    python tools/generate_hud_art.py

Saidas (assets/ui): pixel-font.fnt/.png, hud.atlas/.png e, para cada classe,
portrait_<classe>.png e portrait_<classe>_icon.png (versao pequena para o minimapa).
Dependencia: pillow.
"""
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
UI_DIR = ROOT / "assets" / "ui"

# Cada glifo tem 5 colunas por 7 linhas.
GLYPHS = {
    "0": [".###.", "#...#", "#..##", "#.#.#", "##..#", "#...#", ".###."],
    "1": ["..#..", ".##..", "..#..", "..#..", "..#..", "..#..", ".###."],
    "2": [".###.", "#...#", "....#", "...#.", "..#..", ".#...", "#####"],
    "3": [".###.", "#...#", "....#", "..##.", "....#", "#...#", ".###."],
    "4": ["...#.", "..##.", ".#.#.", "#..#.", "#####", "...#.", "...#."],
    "5": ["#####", "#....", "####.", "....#", "....#", "#...#", ".###."],
    "6": ["..##.", ".#...", "#....", "####.", "#...#", "#...#", ".###."],
    "7": ["#####", "....#", "...#.", "..#..", ".#...", ".#...", ".#..."],
    "8": [".###.", "#...#", "#...#", ".###.", "#...#", "#...#", ".###."],
    "9": [".###.", "#...#", "#...#", ".####", "....#", "...#.", ".##.."],
    "A": [".###.", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"],
    "B": ["####.", "#...#", "#...#", "####.", "#...#", "#...#", "####."],
    "C": [".###.", "#...#", "#....", "#....", "#....", "#...#", ".###."],
    "D": ["###..", "#..#.", "#...#", "#...#", "#...#", "#..#.", "###.."],
    "E": ["#####", "#....", "#....", "####.", "#....", "#....", "#####"],
    "F": ["#####", "#....", "#....", "####.", "#....", "#....", "#...."],
    "G": [".###.", "#...#", "#....", "#.###", "#...#", "#...#", ".###."],
    "H": ["#...#", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"],
    "I": [".###.", "..#..", "..#..", "..#..", "..#..", "..#..", ".###."],
    "J": ["..###", "...#.", "...#.", "...#.", "...#.", "#..#.", ".##.."],
    "K": ["#...#", "#..#.", "#.#..", "##...", "#.#..", "#..#.", "#...#"],
    "L": ["#....", "#....", "#....", "#....", "#....", "#....", "#####"],
    "M": ["#...#", "##.##", "#.#.#", "#.#.#", "#...#", "#...#", "#...#"],
    "N": ["#...#", "##..#", "#.#.#", "#..##", "#...#", "#...#", "#...#"],
    "O": [".###.", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."],
    "P": ["####.", "#...#", "#...#", "####.", "#....", "#....", "#...."],
    "Q": [".###.", "#...#", "#...#", "#...#", "#.#.#", "#..#.", ".##.#"],
    "R": ["####.", "#...#", "#...#", "####.", "#.#..", "#..#.", "#...#"],
    "S": [".####", "#....", "#....", ".###.", "....#", "....#", "####."],
    "T": ["#####", "..#..", "..#..", "..#..", "..#..", "..#..", "..#.."],
    "U": ["#...#", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."],
    "V": ["#...#", "#...#", "#...#", "#...#", "#...#", ".#.#.", "..#.."],
    "W": ["#...#", "#...#", "#...#", "#.#.#", "#.#.#", "##.##", "#...#"],
    "X": ["#...#", "#...#", ".#.#.", "..#..", ".#.#.", "#...#", "#...#"],
    "Y": ["#...#", "#...#", ".#.#.", "..#..", "..#..", "..#..", "..#.."],
    "Z": ["#####", "....#", "...#.", "..#..", ".#...", "#....", "#####"],
    "/": ["....#", "....#", "...#.", "..#..", ".#...", "#....", "#...."],
    ":": [".....", "..#..", "..#..", ".....", "..#..", "..#..", "....."],
    ".": [".....", ".....", ".....", ".....", ".....", ".##..", ".##.."],
    "-": [".....", ".....", ".....", "#####", ".....", ".....", "....."],
    "%": ["##..#", "##..#", "...#.", "..#..", ".#...", "#..##", "#..##"],
    "+": [".....", "..#..", "..#..", "#####", "..#..", "..#..", "....."],
}

GLYPH_WIDTH = 5
GLYPH_HEIGHT = 7
ADVANCE = 6
SPACE_ADVANCE = 4

OUTLINE = (22, 16, 12, 255)
BRASS_DARK = (110, 74, 30, 255)
BRASS = (181, 135, 58, 255)
BRASS_LIGHT = (232, 196, 104, 255)
STONE_LIGHT = (92, 80, 72, 255)
STONE_DARK = (40, 34, 30, 255)
BAR_BACKGROUND = (30, 24, 22, 255)
CLEAR = (0, 0, 0, 0)

BAR_COLORS = {
    # topo, corpo, sombra, sombra escura
    "red": ((235, 110, 100), (190, 40, 40), (130, 25, 30), (85, 15, 20)),
    "blue": ((120, 170, 245), (45, 100, 210), (30, 65, 150), (20, 40, 100)),
    "yellow": ((250, 225, 120), (214, 170, 40), (160, 115, 25), (105, 75, 15)),
    "green": ((150, 235, 140), (70, 180, 80), (40, 125, 55), (25, 80, 40)),
}

SKULL = [
    "..#####..",
    ".#######.",
    "#########",
    "##oo#oo##",
    "##oo#oo##",
    "#########",
    ".###.###.",
    "..#.#.#..",
    "..#.#.#..",
]
BONE = (236, 226, 200, 255)
SOCKET = (150, 20, 20, 255)
MARKER_OUTLINE = (60, 8, 8, 255)
ARROW_FILL = (250, 214, 90, 255)


def generate_font():
    chars = list(GLYPHS.keys())
    sheet = Image.new("RGBA", (len(chars) * ADVANCE, GLYPH_HEIGHT), CLEAR)
    lines = []

    for index, char in enumerate(chars):
        x = index * ADVANCE

        for row, pattern in enumerate(GLYPHS[char]):
            for column, cell in enumerate(pattern):
                if cell == "#":
                    sheet.putpixel((x + column, row), (255, 255, 255, 255))

        lines.append(
            f"char id={ord(char)} x={x} y=0 width={GLYPH_WIDTH} height={GLYPH_HEIGHT} "
            f"xoffset=0 yoffset=1 xadvance={ADVANCE} page=0 chnl=15"
        )

    lines.append(
        f"char id=32 x=0 y=0 width=0 height=0 xoffset=0 yoffset=0 xadvance={SPACE_ADVANCE} page=0 chnl=15"
    )

    header = [
        'info face="pixel-font" size=7 bold=0 italic=0 charset="" unicode=0 stretchH=100 '
        'smooth=0 aa=1 padding=0,0,0,0 spacing=1,1',
        f"common lineHeight=9 base=8 scaleW={sheet.width} scaleH={sheet.height} pages=1 packed=0",
        'page id=0 file="pixel-font.png"',
        f"chars count={len(lines)}",
    ]

    sheet.save(UI_DIR / "pixel-font.png")
    (UI_DIR / "pixel-font.fnt").write_text("\n".join(header + lines) + "\n", encoding="utf-8", newline="\n")


def ring(draw, inset, size, top_left, bottom_right):
    last = size - 1 - inset
    draw.line([(inset, inset), (last, inset)], fill=top_left)
    draw.line([(inset, inset), (inset, last)], fill=top_left)
    draw.line([(inset, last), (last, last)], fill=bottom_right)
    draw.line([(last, inset), (last, last)], fill=bottom_right)


def portrait_frame():
    size = 40
    image = Image.new("RGBA", (size, size), CLEAR)
    draw = ImageDraw.Draw(image)

    ring(draw, 0, size, OUTLINE, OUTLINE)
    ring(draw, 1, size, BRASS_LIGHT, BRASS_DARK)
    ring(draw, 2, size, BRASS, BRASS)
    ring(draw, 3, size, OUTLINE, OUTLINE)

    # Rebites nos cantos.
    for point in ((2, 2), (size - 3, 2), (2, size - 3), (size - 3, size - 3)):
        draw.point(point, fill=BRASS_LIGHT)

    return image


def bar_frame():
    size = 8
    image = Image.new("RGBA", (size, size), BAR_BACKGROUND)
    draw = ImageDraw.Draw(image)

    ring(draw, 0, size, OUTLINE, OUTLINE)
    ring(draw, 1, size, STONE_LIGHT, STONE_DARK)

    return image


def minimap_frame():
    size = 12
    image = Image.new("RGBA", (size, size), CLEAR)
    draw = ImageDraw.Draw(image)

    ring(draw, 0, size, OUTLINE, OUTLINE)
    ring(draw, 1, size, BRASS_LIGHT, BRASS_DARK)
    ring(draw, 2, size, BRASS, BRASS)

    return image


def panel_frame():
    size = 16
    image = Image.new("RGBA", (size, size), BAR_BACKGROUND)
    draw = ImageDraw.Draw(image)

    ring(draw, 0, size, OUTLINE, OUTLINE)
    ring(draw, 1, size, BRASS_LIGHT, BRASS_DARK)
    ring(draw, 2, size, BRASS, BRASS)
    ring(draw, 3, size, OUTLINE, OUTLINE)

    # Rebites nos cantos.
    for point in ((2, 2), (size - 3, 2), (2, size - 3), (size - 3, size - 3)):
        draw.point(point, fill=BRASS_LIGHT)

    return image


def panel_selected():
    size = 16
    image = Image.new("RGBA", (size, size), (52, 42, 34, 255))
    draw = ImageDraw.Draw(image)

    ring(draw, 0, size, OUTLINE, OUTLINE)
    ring(draw, 1, size, (255, 240, 170, 255), BRASS)
    ring(draw, 2, size, BRASS_LIGHT, BRASS_LIGHT)
    ring(draw, 3, size, OUTLINE, OUTLINE)

    return image


def outlined(image, color):
    """Adds a 1px outline around the opaque pixels, growing the image by 2px."""
    size = (image.width + 2, image.height + 2)
    base = Image.new("RGBA", size, CLEAR)
    base.paste(image, (1, 1))
    result = base.copy()

    for x in range(size[0]):
        for y in range(size[1]):
            if base.getpixel((x, y))[3] != 0:
                continue

            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy

                if 0 <= nx < size[0] and 0 <= ny < size[1] and base.getpixel((nx, ny))[3] != 0:
                    result.putpixel((x, y), color)
                    break

    return result


def boss_marker():
    image = Image.new("RGBA", (9, 9), CLEAR)

    for y, row in enumerate(SKULL):
        for x, cell in enumerate(row):
            if cell == "#":
                image.putpixel((x, y), BONE)
            elif cell == "o":
                image.putpixel((x, y), SOCKET)

    return outlined(image, MARKER_OUTLINE)


def arrow():
    image = Image.new("RGBA", (9, 9), CLEAR)
    draw = ImageDraw.Draw(image)
    draw.polygon([(0, 3), (4, 3), (4, 0), (8, 4), (4, 8), (4, 5), (0, 5)], fill=ARROW_FILL)

    return outlined(image, OUTLINE)


def button(hover):
    size = 12
    body = (74, 62, 52, 255) if not hover else (104, 86, 62, 255)
    image = Image.new("RGBA", (size, size), body)
    draw = ImageDraw.Draw(image)

    ring(draw, 0, size, OUTLINE, OUTLINE)
    ring(draw, 1, size, BRASS_LIGHT, BRASS_DARK)
    ring(draw, 2, size, STONE_LIGHT if not hover else BRASS, STONE_DARK)

    return image


def bar_fill(colors):
    light, body, shadow, darkest = colors
    rows = [light, body, body, body, shadow, darkest]
    image = Image.new("RGBA", (2, len(rows)), CLEAR)

    for y, color in enumerate(rows):
        for x in range(2):
            image.putpixel((x, y), color + (255,))

    return image


def generate_atlas():
    sheet = Image.new("RGBA", (128, 64), CLEAR)
    regions = []

    def place(name, image, x, y, split=None):
        sheet.paste(image, (x, y))
        regions.append((name, x, y, image.width, image.height, split))

    place("portrait-frame", portrait_frame(), 0, 0)
    place("minimap-frame", minimap_frame(), 42, 0, split=(3, 3, 3, 3))
    place("bar-frame", bar_frame(), 56, 0, split=(2, 2, 2, 2))
    place("bar-red", bar_fill(BAR_COLORS["red"]), 66, 0)
    place("bar-blue", bar_fill(BAR_COLORS["blue"]), 70, 0)
    place("bar-yellow", bar_fill(BAR_COLORS["yellow"]), 74, 0)
    place("pixel", Image.new("RGBA", (1, 1), (255, 255, 255, 255)), 80, 0)
    place("bar-green", bar_fill(BAR_COLORS["green"]), 82, 0)
    place("panel", panel_frame(), 0, 42, split=(5, 5, 5, 5))
    place("panel-selected", panel_selected(), 46, 42, split=(5, 5, 5, 5))
    place("boss-marker", boss_marker(), 64, 42)
    place("arrow", arrow(), 78, 42)
    place("button", button(False), 18, 42, split=(3, 3, 3, 3))
    place("button-over", button(True), 32, 42, split=(3, 3, 3, 3))

    lines = ["hud.png", f"size: {sheet.width}, {sheet.height}", "format: RGBA8888",
             "filter: Nearest, Nearest", "repeat: none"]

    for name, x, y, width, height, split in regions:
        lines += [name, "  rotate: false", f"  xy: {x}, {y}", f"  size: {width}, {height}"]

        if split:
            lines.append("  split: " + ", ".join(str(value) for value in split))

        lines += [f"  orig: {width}, {height}", "  offset: 0, 0", "  index: -1"]

    sheet.save(UI_DIR / "hud.png")
    (UI_DIR / "hud.atlas").write_text("\n".join(lines) + "\n", encoding="utf-8", newline="\n")


# Recorte quadrado do rosto na arte de referencia de 1254x1254 de cada classe.
PORTRAIT_CROPS = {
    "barbarian": (415, 75, 835, 495),
    "mage": (430, 80, 850, 500),
}


def generate_portraits():
    for name, crop in PORTRAIT_CROPS.items():
        source = Image.open(ROOT / "art" / f"{name}.png").convert("RGBA")
        face = source.crop(crop)
        face.resize((32, 32), Image.LANCZOS).save(UI_DIR / f"portrait_{name}.png")
        face.resize((10, 10), Image.LANCZOS).save(UI_DIR / f"portrait_{name}_icon.png")


if __name__ == "__main__":
    generate_font()
    generate_atlas()
    generate_portraits()
    print("HUD art gerada em", UI_DIR)
