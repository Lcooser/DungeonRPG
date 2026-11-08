from PIL import Image, ImageDraw, ImageOps, ImageEnhance, ImageFilter
from pathlib import Path
import json, shutil, math, zipfile, random, hashlib
random.seed(42)
root=Path('/mnt/data'); out=root/'DungeonRPG_complete_asset_bundle'; out.mkdir(exist_ok=True)
for sub in ['assets/sprites/monsters','assets/sprites/characters','assets/sprites/weapons','assets/sprites/vfx','assets/sprites/items','docs','references','sources']:(out/sub).mkdir(parents=True,exist_ok=True)
source=root/'DungeonRPG_animated_prototypes'
manifest=[]

def add(p,kind,cell=None,counts=None,quality='procedural-prototype',note=None):
 im=Image.open(p)
 assert im.mode=='RGBA' and (kind=='tileset' or im.getchannel('A').getextrema()[0]==0), p
 entry={'file':str(p.relative_to(out)),'category':kind,'size':list(im.size),'mode':'RGBA','quality':quality}
 if cell: entry['cell_size']=cell
 if counts: entry['frame_counts']=counts
 if note:entry['note']=note
 manifest.append(entry)

def save(im,p,kind,cell=None,counts=None,quality='procedural-prototype',note=None):
 im=im.convert('RGBA'); p.parent.mkdir(parents=True,exist_ok=True);im.save(p,optimize=True)
 add(p,kind,cell,counts,quality,note)

# Monsters: 27 tiered and 9 bosses/minions; correct pixel grids, frame counts. Existing source derived from static portraits.
for p in sorted(source.glob('*_sheet.png')):
 im=Image.open(p).convert('RGBA')
 name=p.name
 if name in ('lich_king_sheet.png','spider_queen_sheet.png','magma_dragon_sheet.png'):
  cell=192;counts=[4,6,5,4,6,5,4,6,5,8,8]
 else:
  cell=128; counts=[4,6,5,4,6,5,4,6,5,8]+([4] if im.height==1408 else [])
 expected=(8*cell,len(counts)*cell)
 assert im.size==expected,(name,im.size,expected)
 dst=out/'assets/sprites/monsters'/name
 save(im,dst,'monster-animation',cell,counts,note='Directional backs/sides and motion derived by software transforms from concept portraits, not hand-drawn')

# Main characters: normalize existing genuine character animation arts to strict 8x10 128px per frame.
for who,filename in [('barbarian','folha_de_sprites_do_bárbaro_pixel_art.png'),('mage','spritesheet_de_mago_arcano_em_pixel_art.png')]:
 im=Image.open(root/filename).convert('RGBA')
 im=im.resize((1024,1280),Image.Resampling.NEAREST)
 save(im,out/f'assets/sprites/characters/{who}_base_sheet.png','character-base',128,[4,6,5,4,6,5,4,6,5,8],quality='generated-art-rescaled',note='Original generated sheet resized to exact grid; visually confirm alignment and transparency for individual frames')

# Weapon inventory icons from existing 8x9 sprite atlas resampled to exact 8x9 160px.
iconorig=Image.open(root/'a_detailed_pixel_art_game_asset_sheet_a_1024_1152.png').convert('RGBA')
icon_sheet=iconorig.resize((8*160,9*160),Image.Resampling.NEAREST)
save(icon_sheet,out/'assets/sprites/items/weapon_icons_160.png','item-icons',160,[8]*9,quality='generated-art-rescaled',note='Grid resized from generated icon atlas, should visually inspect alignment')
weapon_names=['axe','greataxe','sword','hammer','spear','staff','wand','tome','orb']
for i,n in enumerate(weapon_names):
 icons=Image.new('RGBA',(8*160,160))
 for j in range(8): icons.alpha_composite(icon_sheet.crop((j*160,i*160,(j+1)*160,(i+1)*160)),(j*160,0))
 save(icons,out/f'assets/sprites/items/{n}_rarities.png','item-icons',160,[8],quality='generated-art-rescaled')

# Character alternative weapon animations, same poses and quality as preexisting sheet, weapon visually switched through a cutout and overlay.
# A concise note in manifest explicitly clarifies these are composited placeholder weapons, not redrawn hand placements.
base_cache={who:Image.open(out/f'assets/sprites/characters/{who}_base_sheet.png').convert('RGBA') for who in ['barbarian','mage']}
weapons={'barbarian':{'axe':0,'greataxe':1,'sword':2,'hammer':3,'spear':4},'mage':{'staff':5,'wand':6,'tome':7,'orb':8}}
for who,group in weapons.items():
 base=base_cache[who]
 for weapon,weapon_row in group.items():
  # Keep base intact for default axe/staff, composite ornamental equipment variant at fixed relative location for alternatives.
  sheet=Image.new('RGBA',(1024,1280))
  raw_icon=icon_sheet.crop((3*160,weapon_row*160,4*160,(weapon_row+1)*160))
  box=raw_icon.getbbox()
  if box:raw_icon=raw_icon.crop(box)
  counts=[4,6,5,4,6,5,4,6,5,8]
  for row,n in enumerate(counts):
   for f in range(n):
    cell=base.crop((f*128,row*128,(f+1)*128,(row+1)*128))
    if not ((who=='barbarian' and weapon=='axe') or (who=='mage' and weapon=='staff')):
     # A recognizable class/equipment accent is added near the original weapon. No claim of independent wield animation.
     icon=raw_icon.copy(); w=max(16,round(26*icon.width/max(icon.height,1)));h=round(w*icon.height/max(icon.width,1))
     if h>56: h=56;w=max(12,round(h*icon.width/icon.height))
     icon=icon.resize((w,h),Image.Resampling.NEAREST)
     if row in (2,5,8):
      a=[-20,-8,11,30,8][f]; icon=icon.rotate(a,Image.Resampling.NEAREST,expand=True)
     x,y=(14,46) if who=='barbarian' else (84,42)
     if row in (3,4,5):x=128-x-icon.width
     x=max(0,min(128-icon.width,x));y=max(0,min(128-icon.height,y))
     cell.alpha_composite(icon,(x,y))
    sheet.alpha_composite(cell,(f*128,row*128))
  save(sheet,out/f'assets/sprites/characters/{who}_{weapon}_sheet.png','character-weapon-animation',128,counts,quality='composited-prototype',note='Original weapon remains in source animation; extra equipment variant is composited. Must be redrawn before release.')

# Extra weapon VFX; pixel-ish authored procedural effects with transparent tiles.
colors={'axe':(224,226,235),'greataxe':(214,222,232),'sword':(238,240,246),'hammer':(207,180,138),'spear':(221,235,243),'staff':(53,240,230),'wand':(73,235,255),'tome':(180,91,249),'orb':(124,61,237)}

def vfx_frame(typ,seq,frame,n=6,size=192):
 img=Image.new('RGBA',(size,size));d=ImageDraw.Draw(img)
 cx,cy=size//2,size//2; col=colors.get(typ,(50,240,145));t=(frame+1)/n
 if seq=='impact':
  for k in range(10):
   a=2*math.pi*k/10
   radius=int(6+65*t)
   x=cx+math.cos(a)*radius;y=cy+math.sin(a)*radius
   l=round(3+12*(1-t))
   d.line((x,y,x+math.cos(a)*l,y+math.sin(a)*l),fill=(*col,round(245*(1-.65*t))),width=max(1,4-int(t*3)))
  d.ellipse((cx-11*(1-t),cy-11*(1-t),cx+11*(1-t),cy+11*(1-t)),fill=(*col,150))
 elif seq=='projectile':
  x=cx+int((t-.5)*9);y=cy
  for k in range(5):
   q=k*9
   d.ellipse((x-q-17,y-8+k,x-q+2,y+8-k),fill=(*col,max(30,210-k*38)))
  d.ellipse((x-12,y-12,x+12,y+12),fill=(*col,255)); d.ellipse((x-6,y-6,x+4,y+4),fill=(255,255,236,255))
 elif seq=='ring':
  r=round(9+65*t)
  for j in range(4):
   rr=r+j*2
   d.ellipse((cx-rr,cy-rr//3,cx+rr,cy+rr//3),outline=(*col,max(40,215-j*35)),width=2)
  for k in range(8):
   a=2*math.pi*k/8
   x=cx+math.cos(a)*r;y=cy+math.sin(a)*r/3
   d.ellipse((x-3,y-3,x+3,y+3),fill=(*col,250))
 elif seq=='slash':
  r=int(30+15*t)
  a0=-2.7+2.3*t
  for k in range(28):
   a=a0+(1.5*k/27)
   rr=r+int(3*math.sin(k/27*math.pi))
   x=cx+rr*math.cos(a); y=cy+rr*math.sin(a)
   d.ellipse((x-4,y-4,x+4,y+4),fill=(*col,round(255*(1-.45*t))))
 elif seq=='eruption':
  for k in range(9):
   x=cx-55+k*14
   h=int((10+50*math.sin((k+frame)*.8)**2)*(0.4+min(1,t*2)))
   d.polygon([(x,cy+30),(x+11,cy+30),(x+6,cy+30-h)],fill=(*col,220))
 else:return vfx_frame(typ,'impact',frame,n,size)
 return img

vfx_specs={
'axe':['slash','impact'],'greataxe':['slash','impact'],'sword':['slash','projectile','impact'],
'hammer':['impact','ring','eruption'],'spear':['projectile','impact'],'staff':['projectile','impact'],
'wand':['projectile','impact'],'tome':['ring','impact','eruption'],'orb':['projectile','impact']}
for typ,seqs in vfx_specs.items():
 counts=[6]*len(seqs)
 sheet=Image.new('RGBA',(6*192,len(seqs)*192))
 for row,seq in enumerate(seqs):
  for f in range(6):sheet.alpha_composite(vfx_frame(typ,seq,f),(f*192,row*192))
 save(sheet,out/f'assets/sprites/vfx/{typ}_vfx.png','weapon-vfx',192,counts,quality='procedurally-authored',note='Simple pixel-shaped effects generated deterministically with Pillow')

# Six player skills: explicit transparent pixel effects with 8 frames each.
skills={'whirlwind':('axe','slash'),'ground_slam':('hammer','ring'),'berserker_rage':('axe','eruption'),'fireball':('hammer','projectile'),'frost_nova':('staff','ring'),'arcane_blink':('orb','eruption')}
for name,(typ,seq) in skills.items():
 im=Image.new('RGBA',(8*192,192))
 for f in range(8):im.alpha_composite(vfx_frame(typ,seq,f,8),(f*192,0))
 save(im,out/f'assets/sprites/vfx/{name}_vfx.png','skill-vfx',192,[8],quality='procedurally-authored',note='Transparent pixel procedural effect; illustrative stand-in, not original previously generated FX')

# 9 boss VFX previously generated; copy exact 6x1 sprites
for p in source.glob('*_vfx.png'):
 im=Image.open(p).convert('RGBA')
 save(im,out/f'assets/sprites/vfx/{p.name}','boss-vfx',192,[6],quality='procedural-prototype')

# Bonus: ability projectile variants as one 4-frame row / 192px tile
for name,typ in {'arrow':'sword','necrotic_bolt':'staff','venom_glob':'tome','crossbow_bolt':'spear','imp_fireball':'axe','magma_breath':'hammer'}.items():
 im=Image.new('RGBA',(4*192,192))
 for i in range(4):
  tile=vfx_frame(typ,'projectile',i,4)
  if name in ('arrow','crossbow_bolt'):
   tile=Image.new('RGBA',(192,192));d=ImageDraw.Draw(tile);cy=96
   d.line((62,cy,122,cy),fill=(153,103,58,255),width=5)
   d.polygon([(120,cy-8),(141,cy),(120,cy+8)],fill=(210,213,218,255))
   d.polygon([(62,cy),(51,cy-7),(51,cy+7)],fill=(119,67,42,255))
  im.alpha_composite(tile,(i*192,0))
 save(im,out/f'assets/sprites/vfx/{name}_projectile.png','projectile-vfx',192,[4],quality='procedurally-authored')

# Reuse existing textures/tileset with precise normalizing, readme warns tile seams.
crypt=Image.open(root/'tileset_de_cripta_em_pixel_art.png').convert('RGBA').resize((1024,512),Image.Resampling.NEAREST)
save(crypt,out/'assets/sprites/crypt_tileset.png','tileset',64,[16]*8,quality='generated-art-rescaled',note='Layout resized to correct dimensions, seamless tiling and collision semantics not guaranteed')

# Save runnable manifest and QA
(out/'docs/asset_manifest.json').write_text(json.dumps({'project':'DungeonRPG','version':'development-asset-bundle-1','assets':manifest},indent=2,ensure_ascii=False))
counts={}
for e in manifest:counts[e['category']]=counts.get(e['category'],0)+1
readme=f'''# DungeonRPG — Development Asset Bundle\n\nTotal PNG assets: **{len(manifest)}**. Counts by category: {json.dumps(counts,indent=2)}\n\nAll files pass mechanical tests: 32-bit RGBA, exact dimensions for the declared grid, transparent pixels, expected frame slots. See `docs/asset_manifest.json`.\n\n## Important production-quality warning\n\nThis bundle provides **loadable development sprites, NOT final handcrafted animation**. The 36 monster sheets are based on previously provided single-view character portraits; attack/walk/death frames are programmatic transforms. Rear/side views are approximations. The 9 player weapon sheets reuse original character animation with overlaid equipment icons; original weapons remain visible, so they are **placeholders**, not proper weapon animations. Generated effect strips were resized to cell dimensions and need artistic QA. Generated tileset should be checked for tile continuity, logical adjacency and bitmask ordering.\n\nThe technical QA in this package does NOT prove that frame art is semantically correct, that foot baseline is perfect or that animations look natural. Do not call these finished commercial art.\n\n## Integration\n\nMonster files: `assets/sprites/monsters/<monster>_t1_sheet.png` etc. Normal mobs 8x10 grid (1024x1280); ranged and projectile mobs 8x11 (1024x1408). Bosses 1536x2112 (8x11 at 192 pixels). Characters: 1024x1280 (8x10 at 128 pixels). Effects: variable rows, each 192px tall; frame counts are in manifest.\n\nYou can test parsing, effects spawning, animation timing and resource loading now. To ship final animation, author genuine frame-by-frame directional sprite art, weapon-specific rigs, and pixel-aligned effects at the same dimensions.\n'''
(out/'README.md').write_text(readme,encoding='utf8')
shutil.copy2(root/'build_dungeon_sheets.py',out/'sources/build_dungeon_sheets.py')
shutil.copy2(root/'build_full_dungeon_pack.py',out/'sources/build_full_dungeon_pack.py')

# Preview sheet of core roster using saved individual reference images
refs=root/'DungeonRPG_monster_portraits'
preview=Image.new('RGBA',(1100,820),(22,19,30,255));draw=ImageDraw.Draw(preview)
families=[['giant_spider','venom_broodling','cultist_hunter','spider_queen'],['fire_imp','ember_knight','magma_hound','magma_dragon'],['skeleton_warrior','skeleton_archer','necromancer','lich_king']]
for ri,group in enumerate(families):
 for gi,name in enumerate(group):
  for tier in ([1,2,3] if gi<3 else [None]):
   file=refs/(f'{name}_t{tier}_reference.png' if tier else f'{name}_reference.png')
   if not file.exists():continue
   im=Image.open(file).convert('RGBA');bb=im.getbbox(); im=im.crop(bb) if bb else im
   maxw=65 if gi<3 else 180;maxh=185
   k=min(maxw/im.width,maxh/im.height,4)
   im=im.resize((max(1,int(im.width*k)),max(1,int(im.height*k))),Image.Resampling.NEAREST)
   x=20+gi*270+(tier-1)*84 if tier else 835
   y=45+ri*258+max(0,185-im.height)
   preview.alpha_composite(im,(x,y))
for y in [0,258,516]: draw.line((0,y,1100,y),fill=(66,60,77),width=2)
preview.convert('RGB').save(root/'DungeonRPG_full_pack_preview.jpg',quality=91)

# Comprehensive QA checks
qa=[]
for e in manifest:
 p=out/e['file'];im=Image.open(p)
 valid=im.mode=='RGBA' and (e['category']=='tileset' or im.getchannel('A').getextrema()[0]==0)
 if 'cell_size' in e:
  cell=e['cell_size']; valid=valid and im.width%cell==0 and im.height%cell==0
  if 'frame_counts' in e:valid=valid and len(e['frame_counts'])==im.height//cell and max(e['frame_counts'])<=im.width//cell
 qa.append((e['file'],valid))
assert all(x[1] for x in qa)
(out/'docs/qa_report.json').write_text(json.dumps({'passed':len(qa),'failed':0,'checks':'RGBA, transparency, exact grid dimensions and row frame capacity; not manual artistic inspection'},indent=2))
archive=root/'DungeonRPG_full_development_assets.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=5) as z:
 for p in out.rglob('*'):
  if p.is_file():z.write(p,p.relative_to(root))
print('CREATED',archive,archive.stat().st_size/1e6,'MB,',len(manifest),'PNG assets',counts)
