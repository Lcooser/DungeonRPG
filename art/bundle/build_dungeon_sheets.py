from PIL import Image,ImageDraw,ImageEnhance,ImageOps
from pathlib import Path
import math,json,zipfile,random
SRC=Path('/mnt/data/DungeonRPG_monster_portraits')
OUT=Path('/mnt/data/DungeonRPG_animated_prototypes')
OUT.mkdir(exist_ok=True)
COLS=8
names=['skeleton_warrior','skeleton_archer','necromancer','giant_spider','venom_broodling','cultist_hunter','fire_imp','ember_knight','magma_hound']
bosses=['lich_king','spider_queen','magma_dragon']
minions=['death_knight','soul_wraith','brood_guard','egg_carrier','forge_guardian','flame_cultist']
ranged=['skeleton_archer','necromancer','venom_broodling','cultist_hunter','fire_imp']
manifest=[]

def fit_image(img,size,scale=1.):
    bbox=img.getbbox()
    if not bbox:return Image.new('RGBA',(size,size))
    im=img.crop(bbox)
    maxwidth=int(size*(0.73 if size==128 else .86)*scale)
    maxheight=int(size*(.86 if size==128 else .84)*scale)
    ratio=min(maxwidth/im.width,maxheight/im.height)
    im=im.resize((max(1,round(im.width*ratio)),max(1,round(im.height*ratio))),Image.Resampling.NEAREST)
    return im

def draw_frame(original,size,row,frame,kind='normal'):
    # Deterministic pose variations with nearest-neighbor transforms and no blending.
    # Back/right views are approximated from the single front reference, not hand-drawn.
    out=Image.new('RGBA',(size,size))
    pose=frame/([4,6,5,4,6,5,4,6,5,8][row] if frame<8 else 8)
    im=fit_image(original,size)
    if row in (3,4,5):
        im=ImageOps.mirror(im)
        # darken for rear surrogate; no claim of authentic backside
        im=ImageEnhance.Brightness(im).enhance(0.83)
    elif row in (6,7,8):
        # side facing right: narrower body, shift skull/bow proportion visibly
        im=im.resize((max(1,round(im.width*.70)),im.height),Image.Resampling.NEAREST)
    if row in (0,3,6):
        delta=math.sin(frame*math.pi/2)
        im=im.resize((im.width,max(1,round(im.height*(1+.013*delta)))),Image.Resampling.NEAREST)
    elif row in (1,4,7):
        # Walk: stepping simulation based on subtle tilt / stride pivot
        angle=[-4,1,4,0,-4,2][frame]
        im=im.rotate(angle,resample=Image.Resampling.NEAREST,expand=True)
    elif row in (2,5,8):
        # attack with wind-up, slash, recoil: rotational and scale motion
        angle=[-9,-16,11,17,-3][frame]
        im=im.rotate(angle,resample=Image.Resampling.NEAREST,expand=True)
        if frame in (2,3):
            im=im.resize((round(im.width*1.04),round(im.height*1.025)),Image.Resampling.NEAREST)
    else:
        if frame<2:
            im=im.rotate([-10,10][frame],resample=Image.Resampling.NEAREST,expand=True)
        else:
            # death: shrinking, rotation, settling to ground near baseline
            i=frame-2
            angle=[-12,-28,-49,-69,-78,-84][i]
            im=im.rotate(angle,resample=Image.Resampling.NEAREST,expand=True)
            im=im.resize((max(1,round(im.width*(1-i*.025))),max(1,round(im.height*(1-i*.12)))),Image.Resampling.NEAREST)
    # Fit/crop inside cell always and leave 2px padding
    if im.width>size-4 or im.height>size-4:
        r=min((size-4)/im.width,(size-4)/im.height)
        im=im.resize((round(im.width*r),round(im.height*r)),Image.Resampling.NEAREST)
    x=(size-im.width)//2
    # baseline stable at fixed y, including attack except death which is deliberately grounded
    y=size-5-im.height
    out.alpha_composite(im,(x,y))
    return out

def draw_projectile(size,kind,frame):
    im=Image.new('RGBA',(size,size))
    d=ImageDraw.Draw(im)
    yy=size//2
    if kind in ['skeleton_archer','cultist_hunter']:
        color=(164,117,71,255)
        x0=38+frame*4;x1=95+frame*4
        d.line((x0,yy,x1,yy),fill=color,width=4)
        d.polygon([(x1,yy-8),(x1+11,yy),(x1,yy+8)],fill=(192,203,206,255))
        d.polygon([(x0,yy),(x0-8,yy-5),(x0-8,yy+5)],fill=(110,75,50,255))
    else:
        col={'necromancer':(70,240,141,255),'venom_broodling':(126,220,28,255),'fire_imp':(255,117,28,255)}.get(kind,(130,239,245,255))
        x=73+frame*3
        for j in range(4):
            delta=j*9
            d.ellipse((x-25-delta,yy-6+j,x-7-delta,yy+6-j),fill=(*col[:3],max(50,210-j*40)))
        d.ellipse((x-13,yy-13,x+13,yy+13),fill=col)
        d.ellipse((x-6,yy-6,x+5,yy+5),fill=(255,250,208,255))
    return im

def boss_effect(size,kind,frame):
    im=Image.new('RGBA',(size,size));d=ImageDraw.Draw(im)
    center=(size//2,size//2);r=15+frame*9
    color={'lich_king':(70,240,140,255),'spider_queen':(172,82,221,255),'magma_dragon':(255,100,22,255)}[kind]
    if kind=='lich_king':
        for j in range(5):
            a=math.tau*(j/5+frame/17)
            x=center[0]+math.cos(a)*r;y=center[1]+math.sin(a)*r*.6
            d.ellipse((x-9,y-9,x+9,y+9),fill=color)
            d.rectangle((x-3,y-3,x+3,y+3),fill=(230,255,237,255))
    elif kind=='spider_queen':
        pts=[]
        for j in range(8):
            a=math.tau*j/8
            pts.append((center[0]+math.cos(a)*r,center[1]+math.sin(a)*r*.62))
        for i in range(8):d.line((*pts[i],*pts[(i+1)%8]),fill=color,width=3)
        for p in pts:d.line((*center,*p),fill=(224,198,233,255),width=2)
    else:
        for j in range(3+frame):
            x=center[0]-25+j*12
            h=10+(j*17+frame*9)%70
            d.polygon([(x,center[1]+43),(x+8,center[1]+43),(x+5,center[1]+43-h)],fill=color)
    return im

for name in names+bosses+minions:
 for tier in ([1,2,3] if name in names else [None]):
    stem=f'{name}_t{tier}' if tier else name
    source=SRC/(stem+'_reference.png')
    if not source.exists():print('MISSING',source);continue
    im=Image.open(source).convert('RGBA')
    size=192 if name in bosses else 128
    rows=11 if name in bosses or name in ranged else 10
    sheet=Image.new('RGBA',(size*COLS,size*rows))
    counts=[4,6,5,4,6,5,4,6,5,8]
    for row,n in enumerate(counts):
        for f in range(n):sheet.alpha_composite(draw_frame(im,size,row,f),(f*size,row*size))
    if rows==11:
        n=8 if name in bosses else 4
        for f in range(n):
            fx=boss_effect(size,name,f) if name in bosses else draw_projectile(size,name,f)
            sheet.alpha_composite(fx,(f*size,10*size))
        counts.append(n)
    fp=OUT/(stem+'_sheet.png')
    sheet.save(fp,optimize=True)
    manifest.append({'name':stem,'path':fp.name,'dimensions':list(sheet.size),'cell':size,'rows':rows,'counts':counts,'facing_up':'mirrored placeholder, not authentic rear artwork','side':'compressed placeholder','animation':'procedural prototype; not hand-animated'})

# Boss ability FX: 6 frames each, 192 pixels
for boss in bosses:
 for power in {'lich_king':['soul_barrage','death_nova','summon_undead'], 'spider_queen':['venom_spit','web_trap','brood_summoning'], 'magma_dragon':['dragon_breath','meteor_rain','lava_eruption']}[boss]:
    fx=Image.new('RGBA',(192*6,192))
    for i in range(6):
        tile=boss_effect(192,boss,(i+len(power))%8)
        fx.alpha_composite(tile,(i*192,0))
    fp=OUT/f'{power}_vfx.png';fx.save(fp,optimize=True)
    manifest.append({'name':power,'path':fp.name,'dimensions':list(fx.size),'cell':192,'rows':1,'counts':[6],'animation':'placeholder visual variations'})
(OUT/'manifest.json').write_text(json.dumps(manifest,indent=2),encoding='utf-8')
(OUT/'README.md').write_text('''# DungeonRPG — Procedural animation prototypes\n\nAll PNG sheets are genuine RGBA with exact 8-column grids. Standard mobs and minions: 1024x1280, 128px cells; ranged mobs: 1024x1408, 11 rows; bosses: 1536x2112, 192px cells.\n\n**Limitations:** These are temporary sprite-sheet prototypes generated from static front-facing concept portraits; walk, attack, hurt and death use deterministic sprite transformations, not genuine articulated animation. Back-facing frames are mirrored/darkened stand-ins, not actual back views, and side-facing frames are narrowed stand-ins. Boss VFX are variations, not fully authored attacks. Do not ship as finished art. They are loadable prototypes for animation-engine integration, collision testing and frame-index validation.\n\nFor true production animation, draw or regenerate directional views and independently articulated frames, retain these exact dimensions and counts, and substitute the PNGs one-by-one.\n''',encoding='utf-8')
zipfile_path=Path('/mnt/data/DungeonRPG_animation_prototypes.zip')
with zipfile.ZipFile(zipfile_path,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
 for p in sorted(OUT.iterdir()):z.write(p,'DungeonRPG_animation_prototypes/'+p.name)
print('files',len(manifest),'zip',zipfile_path,'mb',round(zipfile_path.stat().st_size/1e6,1))
