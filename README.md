# PlayTimeRewards

PlayTimeRewards is a lightweight NeoForge server-side mod that tracks player playtime and automatically rewards them at hourly milestones.

- Server-side only (no client install required)
- Global hour announcements
- Hourly reward command
- Configurable specific-hour rewards (e.g. 1h, 5h, 16h)
- JSON config with live reload

---

## Features

- **Playtime tracking:** Tracks player playtime on the server in ticks, converted to hours.
- **Hour announcements:** Broadcasts a configurable message when a player reaches an hour milestone.
- **Hourly rewards:** Runs a configurable command every hour for each player.
- **Specific-hour rewards:** Extra commands for specific hour marks (e.g. 1 hour, 5 hours, 16 hours).
- **Config-based:** All behaviour controlled via `playtimerewards.json`.
- **Config reload:** Reload the config without restarting the server.

---

## Installation

1. Install **NeoForge** for your Minecraft version.
2. Download the `PlayTimeRewards` mod JAR.
3. Place the JAR into your server’s `mods/` folder.
4. Start the server once to generate the config:
   - `config/playtimerewards.json`
5. Edit the config to your liking.
6. Either restart the server or use the reload command:
   - `/playtimerewards reload`

---

## Configuration

The config file is created at:

```text
config/playtimerewards.json
