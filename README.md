[![Downloads](http://cf.way2muchnoise.eu/1453554.svg)](https://www.curseforge.com/minecraft/mc-mods/ae2-improved-search)
[![MCVersion](http://cf.way2muchnoise.eu/versions/1453554.svg)](https://www.curseforge.com/minecraft/mc-mods/ae2-improved-search)
[![GitHub issues](https://img.shields.io/github/issues/NuanKi/ImprovedAE2Search.svg)](https://github.com/NuanKi/ImprovedAE2Search/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/NuanKi/ImprovedAE2Search.svg)](https://github.com/NuanKi/ImprovedAE2Search/pulls)

# AE2 Improved Search

Upgrades the Applied Energistics 2 terminal search with a smarter query parser and extra search targets, so you can find items faster without fighting the vanilla-style search limitations.

## CurseForge
https://www.curseforge.com/minecraft/mc-mods/ae2-improved-search

## Features

### Advanced search syntax
- **AND** terms by spaces
- **OR** groups using `|`
- **Quoted phrases** using `"like this"`
- **Negation** using `-word` or `!word`

### Extra prefixes
- `@` searches mod id / mod name (example: `@thermal`)
- `#` searches tooltip text (example: `#fortune`)
- `$` searches OreDictionary names (example: `$ingotCopper`)
- `&` or `*` searches registry name (example: `*minecraft:stone`)

## Examples
- `certus quartz` matches items containing both words (AND)
- `"fluix crystal"` matches the exact phrase
- `cable | conduit` matches either word (OR)
- `-damaged` or `!damaged` excludes matches containing "damaged"
- `@thermal` shows items from mods matching "thermal"
- `#fortune` finds items with "fortune" mentioned in the tooltip
- `$ingotCopper` finds items that match the `ingotCopper` OreDictionary entry
- `*minecraft:stone` finds items by registry name

## Advanced examples
- `#"silk touch"` tooltip search for the exact phrase "silk touch"
- `@"thermal expansion"` mod search with a quoted phrase (spaces allowed)
- `@thermal $ore | @ae2 $ore` ores from Thermal mods OR ores from AE2
- `@thermal $ore -nether` thermal ores excluding matches containing "nether"

## Configuration
All settings are available in the in-game Mod Config screen:
- **Max Search Length**: maximum characters allowed in the terminal search field
- **Include Tooltips in Search**: when enabled, normal searches also match tooltips (when disabled, only `#` searches tooltips)

## Requirements
- Minecraft **1.12.2**
- Forge
- Applied Energistics 2 (AE2)

## Building
Typical workflow:
1. Clone the repository
2. Import into your IDE (IntelliJ IDEA or Eclipse)
3. Run the ForgeGradle setup tasks for your environment
4. Build using Gradle

## Contributing
Issues and pull requests are welcome. If you submit a PR, keep changes focused and include a short description of what changed and why.

## License
MIT (see `LICENSE`).
